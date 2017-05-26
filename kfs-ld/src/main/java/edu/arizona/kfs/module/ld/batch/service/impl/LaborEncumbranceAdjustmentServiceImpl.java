package edu.arizona.kfs.module.ld.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.module.ld.batch.dataaccess.LaborEncumbranceAdjustmentDao;
import edu.arizona.kfs.module.ld.batch.dataaccess.LaborEncumbranceDao;
import edu.arizona.kfs.module.ld.batch.report.LaborEncumbranceJobReportData;
import edu.arizona.kfs.module.ld.batch.report.LaborEncumbranceReportWriterService;
import edu.arizona.kfs.module.ld.batch.service.LaborEncumbranceAdjustmentService;

/**
 * KITT-933 / FP-INT0008-01 - Main service which calculates the needed adjustments.  Used by the related batch steps. 
 * UAF-4010 MOD-FP0008-01 Accounting for Personnel Encumbrances - Code Feature
 * 
 * @author Jonathan Keller
 */
@Transactional
public class LaborEncumbranceAdjustmentServiceImpl implements LaborEncumbranceAdjustmentService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborEncumbranceAdjustmentServiceImpl.class);

    protected LaborEncumbranceAdjustmentDao laborEncumbranceAdjustmentDao;
    protected LaborEncumbranceDao laborEncumbranceDao;
    protected DateTimeService dateTimeService;
    protected LaborEncumbranceReportWriterService reportWriterService;
    protected PersonService personService;
    protected AccountingCycleCachingService accountingCycleCachingService;
    protected ParameterService paramSrv;
        

    /**
     * @see edu.arizona.kfs.module.ld.batch.service.LaborEncumbranceAdjustmentService#buildBalanceFile(java.lang.Integer, java.io.File)
     */
    public boolean buildBalanceFile(Integer fiscalYear, File outputFile) {
        int outputRecords = laborEncumbranceAdjustmentDao.buildFileForEncumbranceBalances(fiscalYear, outputFile);

        return true; // building the balance file should never stop the batch
    }

    /**
     * @see edu.arizona.kfs.module.ld.batch.service.LaborEncumbranceAdjustmentService#buildEncumbranceDifferenceFile(java.io.File, java.io.File, java.io.File, java.io.File)
     */
    public boolean buildEncumbranceDifferenceFile(File inputFile, File balanceFile, File outputFile, File errorFile, File reconFile) {
        accountingCycleCachingService.initialize();
        BufferedReader inputReader = null;
        BufferedReader balanceReader = null;
        PrintStream outputStream = null;
        PrintStream errorStream = null;
        PrintStream reconStream = null;
        Date currentDate = dateTimeService.getCurrentSqlDate();
        LaborOriginEntry lastOriginEntry = null;
        LaborOriginEntryToBalanceRecordComparator lineComparator = new LaborOriginEntryToBalanceRecordComparator();
        String finOriginCode = null;
        
        // field length map used for parsing the balance file
        Map<String, Integer> lMap = new LaborOriginEntryFieldUtil().getFieldLengthMap();

        // counters for reporting
        LaborEncumbranceJobReportData jobData = new LaborEncumbranceJobReportData();
        
        int lineNumber = 1;

        try {
            // open all the files
            inputReader = new BufferedReader(new FileReader(inputFile));
            balanceReader = new BufferedReader(new FileReader(balanceFile));
            outputStream = new PrintStream(outputFile);
            errorStream = new PrintStream(errorFile);

            LOG.info("All Files Opened - starting comparisons");
            // ok, now start up the iterators
            String encumbranceLine = null;
            String balanceLine = null;
            LaborOriginEntry encumbranceEntryLine = new LaborOriginEntry();
            KualiDecimal reconAmount = KualiDecimal.ZERO;

            // get the first line of each
            encumbranceLine = inputReader.readLine();
            balanceLine = balanceReader.readLine();
            
            finOriginCode = paramSrv.getParameterValueAsString("KFS-GL", "Batch", "MANUAL_FEED_ORIGINATION"); 
                        
            // keep processing until *both* have been exhausted
            // the encumbrance file is assumed to be a comprehensive list of
            // the current balances, so any "leftovers" in the balance file will
            // need to be removed
            while (encumbranceLine != null || balanceLine != null) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Processing Lines:\nHCM: "+encumbranceLine + "\nBAL: "+balanceLine);
                }
                boolean encumbranceLineReadError = false;
                boolean advanceEncumbranceFile = false;
                boolean advanceBalanceFile = false;
                if ( encumbranceLine != null ) {
                    try {
                    	// first remove the invalid characters that's being sent from HCM.
                    	encumbranceLine = encumbranceLine.replaceAll("[^\\p{ASCII}]", " ");
                        List<Message> parsingMessages = encumbranceEntryLine.setFromTextFileForBatch(encumbranceLine, lineNumber);
                        // if the record does not parse, skip, log to the error file and continue
                        if (!parsingMessages.isEmpty()) {
                            jobData.errorMessages.add("Line parsing returned error messages\n**LINE: " + encumbranceLine + "\n**MESSAGES: " + parsingMessages);
                            jobData.errorCount++;
                            LOG.error(jobData.errorMessages.getLast());
                            errorStream.println(encumbranceLine);
                            encumbranceLineReadError = true;
                            advanceEncumbranceFile = true;
                        }
                    }
                    catch (Exception ex) {
                        // the parsing blew up - write to the error file
                        jobData.errorMessages.add("Unable to parse line - writing to error file.\n**LINE: " + encumbranceLine + "\n**Exception: " + ex.getClass().getName() + " : " + ex.getMessage() );
                        jobData.errorCount++;
                        LOG.error(jobData.errorMessages.getLast());
                        errorStream.println(encumbranceLine);
                        encumbranceLineReadError = true;
                        advanceEncumbranceFile = true;
                    }
                } else { // encumbrance file is exhausted, clear out the origin entry so that it is not accidentally used                	
                    if ( encumbranceEntryLine != null ) {
                        encumbranceEntryLine = null;
                        LOG.debug("Encumbrance file exhausted, blanking out entry line variable for rest of process");
                    }
                }
                if (!encumbranceLineReadError) {
                    if ( encumbranceEntryLine != null ) {
                        lastOriginEntry = encumbranceEntryLine;                            
                    }
                    // OK, the line has been parsed - so we can now compare the records
                    // assemble a string which matches the beginning of the balance file
                    // so we can do a simple string comparison
                    int compareResult = 0;
                    if (encumbranceEntryLine == null) {
                        compareResult = 1; // encumbrance file is exhausted, clear any remaining balance entries
                    } else if ( balanceLine == null ) {
                        compareResult = -1; // balance file is exhausted, create balances for all remaining records in the file
                    } else { // records present in both files, run the normal compare
                        compareResult = lineComparator.compare(encumbranceEntryLine, balanceLine);
                    }
                    //1st record is in the encumbrance file and also in ld_ldgr_bal_t  
                    if (compareResult == 0) { // if the strings are equal, compare the amounts
                        LOG.debug("Key Match: Proceeding with amount comparison");
                        /////// calculate the difference
                        KualiDecimal newEncumbranceBalance = encumbranceEntryLine.getTransactionLedgerEntryAmount();
                        // parse the amount from the balance line
                        KualiDecimal existingEncumbranceBalance = KualiDecimal.ZERO;
                        String amountString = balanceLine.substring(lineComparator.BALANCE_AMOUNT_POS, lineComparator.BALANCE_AMOUNT_POS+lineComparator.BALANCE_AMOUNT_LEN).trim();
                        try {
                            existingEncumbranceBalance = new KualiDecimal(amountString);
                        } catch (NumberFormatException ex) {
                            LOG.fatal("Unable to parse amount from balance file.  Amount string was: " + amountString, ex);
                            throw new RuntimeException("Unable to parse balance amount from a system generated file.  There's a problem here!");
                        }
                        // if the amounts are equal, do nothing
                        if ( LOG.isDebugEnabled() ) {
                            LOG.debug( "Comparing Amounts: BalanceFile=" + existingEncumbranceBalance + " / InputFileAmount=" + newEncumbranceBalance );
                        }
                        if (existingEncumbranceBalance.equals(newEncumbranceBalance)) {
                            LOG.debug( "Balances Match - no line created");
                            jobData.matchingBalanceCount++;
                        } else { // if not, create an adjusting entry
                            KualiDecimal difference;
                            String debitCreditCode;
                            if (existingEncumbranceBalance.isGreaterThan(newEncumbranceBalance)) {
                                LOG.debug( "New Balance Less - Creating a credit");
                                // existing > new - reduce the balance
                                difference = existingEncumbranceBalance.subtract(newEncumbranceBalance);
                                debitCreditCode = KFSConstants.GL_CREDIT_CODE;
                                jobData.decreasedBalanceCount++;
                            } else {
                                // new > existing, increase the amount encumbered
                                LOG.debug( "New Balance More - Creating a debit to increase the amount");
                                difference = newEncumbranceBalance.subtract(existingEncumbranceBalance);
                                debitCreditCode = KFSConstants.GL_DEBIT_CODE;
                                jobData.increasedBalanceCount++;
                            }
                            // Add Amount for Recon file
                            reconAmount = reconAmount.add(difference);
                            
                            // copy fields per requirements
                            adjustEncumbranceOriginEntryLine(encumbranceEntryLine, difference, debitCreditCode, currentDate);

                            String financialObjectTypeCode = balanceLine.substring(lineComparator.FINANCIAL_OBJECT_TYPE_CODE_POS, lineComparator.FINANCIAL_OBJECT_TYPE_CODE_POS + lineComparator.FINANCIAL_OBJECT_CODE_LEN);
                            encumbranceEntryLine.setFinancialObjectTypeCode(financialObjectTypeCode);
                            
                            //**START AZ** KFSI-5726 KevinMcO
                            encumbranceEntryLine.setFinancialSystemOriginationCode(finOriginCode);
                            //**END AZ**
                            
                            guaranteeObjectTypeCodeInOutputLine(encumbranceEntryLine);
                            
                            // write the line to the output file
                            outputStream.println( encumbranceEntryLine.getLine() );
                            if ( LOG.isDebugEnabled() ) {
                                LOG.debug( "Writing line to output stream:\n" + encumbranceEntryLine.getLine() );
                            }
                            jobData.outputCount++;
                        }
                        // advance both iterators
                        advanceBalanceFile = true;
                        advanceEncumbranceFile = true;
                      //2nd record is in encumbrance file but not in balance table  
                    } else if (compareResult < 0) { // if the encumbrance line is less than the balance line,
                        LOG.debug("Encr < Bal Keys: Create new encumbrance for encumbrance line");
                        // then the encumbrance line does not have a match
                        // add a new line for the full amount
                        KualiDecimal amount = encumbranceEntryLine.getTransactionLedgerEntryAmount();
                        adjustEncumbranceOriginEntryLine(encumbranceEntryLine, amount.abs(), amount.isPositive() ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE,currentDate);
                        guaranteeObjectTypeCodeInOutputLine(encumbranceEntryLine);
                        
                        //**START AZ** KFSI-5726 KevinMcO
                        String OriginCode = encumbranceEntryLine.getFinancialSystemOriginationCode();
                        encumbranceEntryLine.setFinancialSystemOriginationCode(encumbranceEntryLine.getFinancialSystemOriginationCode());
                        //**END AZ**
                        
                        //Add amount for Recon file
                        reconAmount = reconAmount.add(encumbranceEntryLine.getTransactionLedgerEntryAmount());
                        
                        outputStream.println( encumbranceEntryLine.getLine() );
                        jobData.outputCount++;
                        jobData.newBalanceCount++;
                        // and advance the encumbrance line iterator
                        advanceEncumbranceFile = true;
                        //3rd Record is in balance but not encumbrance file
                    } else { // if the encumbrance line is greater than the balance line,
                        LOG.debug("Encr > Bal Keys: Reverse balance line");
                        // then the balance line does not have a match
                        // add a line to reverse the entire balance
                        // create a new origin entry instance, based on the balance line
                        LaborOriginEntry reversingOriginEntry = createOriginEntryToReverseBalanceEntry( balanceLine, lastOriginEntry, currentDate, lMap );
                        guaranteeObjectTypeCodeInOutputLine(reversingOriginEntry);
                        
                        // however the description on the previous entry may be misleading (as in, have the employee's name)
                        // so, we need to replace it.  Attempt to do a lookup from the person table using the employee ID.
                        // If that fails, put in a stock description
                        String newDescription = "Unknown Employee ID: " + reversingOriginEntry.getEmplid();
                        try {
                            Person employeePerson = getPersonService().getPersonByEmployeeId(reversingOriginEntry.getEmplid());
                            if ( employeePerson == null ) {
                                LOG.warn( "No Person found for employee ID: " + reversingOriginEntry.getEmplid() );
                            } else {
                                newDescription = (employeePerson.getLastNameUnmasked() + ", " + employeePerson.getFirstNameUnmasked() + " " + employeePerson.getMiddleNameUnmasked()).trim();
                            }
                        } catch ( Exception ex ) {
                            LOG.warn( "Error Obtaining Person for employee ID: " + reversingOriginEntry.getEmplid(), ex );
                        }
                        reversingOriginEntry.setTransactionLedgerEntryDescription(newDescription);
                        
                        // Update the end of the document number to "00000"
                        // save the first 9 characters (YYYYMMDD#)
                        String newDocNum = StringUtils.left(reversingOriginEntry.getDocumentNumber(), 9);
                        // append the new suffix
                        newDocNum += "00000";
                        reversingOriginEntry.setDocumentNumber(newDocNum);
                        
                        //**START AZ** KFSI-5726 KevinMcO
                        reversingOriginEntry.setFinancialSystemOriginationCode(finOriginCode);
                        //**END AZ**
                        
                        //Add amount for Recon file
                        reconAmount = reconAmount.add(reversingOriginEntry.getTransactionLedgerEntryAmount());
                        outputStream.println( reversingOriginEntry.getLine() );
                        jobData.outputCount++;
                        jobData.removedBalanceCount++;
                        // and advance the balance line iterator
                        advanceBalanceFile = true;
                    }
                    
                    
                }
                // depending on comparison, increment the appropriate counters
                if (advanceEncumbranceFile) {
                    jobData.inputLinesProcessed++;
                    // move to the next line
                    encumbranceLine = inputReader.readLine();
                    lineNumber++;
                }
                if (advanceBalanceFile) {
                    jobData.balanceLinesProcessed++;
                    balanceLine = balanceReader.readLine();
                }
            }
            outputStream.flush();
            
            try{
            	//Create recon file.
            	reconStream = new PrintStream(reconFile);
            	reconStream.println("c ld_ldgr_entr_t " + jobData.outputCount +";");
            	reconStream.println("s trn_ldgr_entr_amt " + reconAmount +";");
            	reconStream.print("e 02;");            	
            } catch ( IOException ex ) {
                LOG.error( "Unable to create recon file for labor encumbrance adjustment output file.", ex );
                throw new RuntimeException("Unable to create recon file for labor encumbrance adjustment output file.", ex);
            }
            
            try {
                String doneFileName = StringUtils.removeEnd(outputFile.getAbsolutePath(), ".data") + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
                File doneFile = new File( doneFileName );
                doneFile.createNewFile();
            } catch ( IOException ex ) {
                LOG.error( "Unable to create done file for labor encumbrance adjustment output file.", ex );
                throw new RuntimeException("Unable to create done file for labor encumbrance adjustment output file.", ex);
            }
        }
        catch (FileNotFoundException ex) {
            LOG.warn("Missing Input file:" + ex.getMessage(), ex);
            return false;
        }
        catch (IOException ex) {
            LOG.warn("Problem reading input files:" + ex.getMessage(), ex);
            throw new RuntimeException("Error processing encumbrance balance file", ex);
        }
        finally {
            IOUtils.closeQuietly(inputReader);
            IOUtils.closeQuietly(balanceReader);
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(errorStream);
            IOUtils.closeQuietly(reconStream);
            if ( reportWriterService != null ) {
                ((WrappingBatchService)reportWriterService).initialize();
                reportWriterService.writeStatisticLine("HCM File Input Lines      %,20d", jobData.inputLinesProcessed);
                reportWriterService.writeStatisticLine("Balance File Lines        %,20d", jobData.balanceLinesProcessed);
                reportWriterService.writeStatisticLine("Output Lines              %,20d", jobData.outputCount);
                reportWriterService.writeStatisticLine("Input Line Errors         %,20d", jobData.errorCount);
                reportWriterService.writeNewLines(2);
                reportWriterService.writeStatisticLine("New Encumbrance Balances  %,20d", jobData.newBalanceCount);
                reportWriterService.writeStatisticLine("Increased Balances        %,20d", jobData.increasedBalanceCount);
                reportWriterService.writeStatisticLine("Decreased Balances        %,20d", jobData.decreasedBalanceCount);
                reportWriterService.writeStatisticLine("Removed Balances          %,20d", jobData.removedBalanceCount);
                reportWriterService.writeStatisticLine("Matching Balances         %,20d", jobData.matchingBalanceCount);
                if ( !jobData.errorMessages.isEmpty() ) {
                    for ( String errorMessage : jobData.errorMessages ) {
                        reportWriterService.writeErrorLine( errorMessage );
                    }
                }
                ((WrappingBatchService)reportWriterService).destroy();
            }
            accountingCycleCachingService.destroy();
        }
        return true;
    }

    protected void guaranteeObjectTypeCodeInOutputLine(LaborOriginEntry outputLine) {
        if (StringUtils.isNotBlank(outputLine.getFinancialObjectTypeCode())) {
            return;
        }
        ObjectCode objectCode = accountingCycleCachingService.getObjectCode(outputLine.getUniversityFiscalYear(), 
                outputLine.getChartOfAccountsCode(), 
                outputLine.getFinancialObjectCode());
        outputLine.setFinancialObjectTypeCode((objectCode == null ? "  " : objectCode.getFinancialObjectTypeCode()));
    }
    
    /**
     * Converts an origin entry line from the HCM system and sets or clears fields as needed.
     * 
     * @param originEntry The "template" origin entry to modify.
     * @param amount Amount of the transaction.
     * @param debitCreditCode 
     * @param currentDate Transaction date of the entry.
     */
    protected void adjustEncumbranceOriginEntryLine( LaborOriginEntry originEntry, KualiDecimal amount, String debitCreditCode, Date currentDate ) {
        // set the amount
        originEntry.setTransactionLedgerEntryAmount(amount);
        originEntry.setTransactionDebitCreditCode(debitCreditCode);
        // set document type and other fields per the spec
        // only set the document type if not present in the file
        if ( StringUtils.isBlank(originEntry.getFinancialDocumentTypeCode() ) ) {
            originEntry.setFinancialDocumentTypeCode(LABOR_PERSONNEL_ENCUMBRANCE_DOC_TYPE);
        }
        originEntry.setTransactionPostingDate( currentDate );
        // ensure a number of other fields are blank per the spec
        originEntry.setOrganizationDocumentNumber("");
        originEntry.setOrganizationReferenceId("");
        originEntry.setReferenceFinancialDocumentTypeCode("");
        originEntry.setReferenceFinancialDocumentNumber("");
        originEntry.setReferenceFinancialSystemOriginationCode("");
        originEntry.setReversalDate(null);
        // not blanking this field out - the scrubber doesn't like it
        //originEntry.setTransactionEncumbranceUpdateCode("");
        originEntry.setPayPeriodEndDate(null);
        originEntry.setTransactionTotalHours(null);
        originEntry.setPayrollEndDateFiscalPeriodCode("");
        originEntry.setPayrollEndDateFiscalYear(null);
        originEntry.setEarnCode("");
        originEntry.setPayGroup("");
        originEntry.setSalaryAdministrationPlan("");
        originEntry.setGrade("");
        originEntry.setRunIdentifier("");
        originEntry.setLaborLedgerOriginalChartOfAccountsCode("");
        originEntry.setLaborLedgerOriginalAccountNumber("");
        originEntry.setLaborLedgerOriginalFinancialSubObjectCode("");
        originEntry.setLaborLedgerOriginalFinancialObjectCode("");
        originEntry.setLaborLedgerOriginalSubAccountNumber("");
    }
    
    /**
     * Takes a template origin entry (not modified) and merges with the key information in the given balance line.
     */
    protected LaborOriginEntry createOriginEntryToReverseBalanceEntry( String balanceLine, LaborOriginEntry lastOriginEntry, Date currentDate, Map<String, Integer> fieldLengthMap ) {
        LaborOriginEntry originEntry = new LaborOriginEntry(lastOriginEntry); // makes a copy
        // parse the file for FAU attribs and amount
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Parsing balance line to make origin entry:\n" + balanceLine);
        }
        int startPos = 0; 
     
        int fieldLen = fieldLengthMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        originEntry.setFinancialBalanceTypeCode(balanceLine.substring(startPos,startPos+fieldLen).trim());
        
        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.EMPLID);
        originEntry.setEmplid( balanceLine.substring(startPos,startPos+fieldLen).trim());

        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.POSITION_NUMBER);
        originEntry.setPositionNumber( balanceLine.substring(startPos,startPos+fieldLen).trim());

        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        originEntry.setChartOfAccountsCode( balanceLine.substring(startPos,startPos+fieldLen).trim());

        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        originEntry.setAccountNumber( balanceLine.substring(startPos,startPos+fieldLen).trim());

        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        originEntry.setSubAccountNumber( balanceLine.substring(startPos,startPos+fieldLen).trim());

        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        originEntry.setFinancialObjectTypeCode( balanceLine.substring(startPos,startPos+fieldLen).trim());

        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        originEntry.setFinancialObjectCode( balanceLine.substring(startPos,startPos+fieldLen).trim());

        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        originEntry.setFinancialSubObjectCode( balanceLine.substring(startPos,startPos+fieldLen).trim());

        startPos += fieldLen;
        fieldLen = fieldLengthMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT) + 1;

        KualiDecimal amount = new KualiDecimal(balanceLine.substring(startPos,startPos+fieldLen).trim());
            
        Integer employeeRecord = laborEncumbranceDao.getEmployeeRecord(originEntry.getEmplid(), originEntry.getPositionNumber(), originEntry.getFinancialBalanceTypeCode(), originEntry.getAccountNumber(), originEntry.getFinancialObjectCode());
        originEntry.setEmployeeRecord(employeeRecord.intValue() == -1 ? null : employeeRecord);
        
        // blank out the project code
        originEntry.setProjectCode(KFSConstants.getDashProjectCode());
        // null out the sequence number, we don't have a source
        originEntry.setTransactionLedgerEntrySequenceNumber(null);
        adjustEncumbranceOriginEntryLine(originEntry, amount.abs(), amount.isPositive()?KFSConstants.GL_CREDIT_CODE:KFSConstants.GL_DEBIT_CODE, currentDate);
        return originEntry;
    }
        
    
    /**
     * Special comparator for labor origin entry records and the encumbrance balance file.
     * It compares the keys in the balance file with the appropriate attributes of the origin entry. 
     */
    protected static class LaborOriginEntryToBalanceRecordComparator {
        protected LaborOriginEntryFieldUtil loefu = new LaborOriginEntryFieldUtil();
        protected Map<String, Integer> lMap = loefu.getFieldLengthMap();

        // Decode all of these up front to avoid the decoding of the information on
        // *every* comparison, which could happen quite a number of times
        protected int FINANCIAL_BALANCE_TYPE_CODE_LEN = lMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        protected int EMPLID_LEN = lMap.get(KFSPropertyConstants.EMPLID);
        protected int POSITION_NUMBER_LEN = lMap.get(KFSPropertyConstants.POSITION_NUMBER);
        protected int CHART_OF_ACCOUNTS_CODE_LEN = lMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        protected int ACCOUNT_NUMBER_LEN = lMap.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        protected int SUB_ACCOUNT_NUMBER_LEN = lMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        protected int FINANCIAL_OBJECT_TYPE_CODE_LEN = lMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        protected int FINANCIAL_OBJECT_CODE_LEN = lMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        protected int FINANCIAL_SUB_OBJECT_CODE_LEN = lMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        protected int TOTAL_KEY_LEN = FINANCIAL_BALANCE_TYPE_CODE_LEN + EMPLID_LEN + POSITION_NUMBER_LEN + CHART_OF_ACCOUNTS_CODE_LEN + ACCOUNT_NUMBER_LEN + SUB_ACCOUNT_NUMBER_LEN + FINANCIAL_OBJECT_TYPE_CODE_LEN + FINANCIAL_OBJECT_CODE_LEN + FINANCIAL_SUB_OBJECT_CODE_LEN;

        protected int BALANCE_AMOUNT_POS = TOTAL_KEY_LEN;
        protected int BALANCE_AMOUNT_LEN = lMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT) + 1; // account for the
                                                                                                               // plus/minus
        protected int FINANCIAL_OBJECT_TYPE_CODE_POS =  FINANCIAL_BALANCE_TYPE_CODE_LEN + EMPLID_LEN + POSITION_NUMBER_LEN + CHART_OF_ACCOUNTS_CODE_LEN + ACCOUNT_NUMBER_LEN + SUB_ACCOUNT_NUMBER_LEN;
        protected int FINANCIAL_OBJECT_CODE_POS = FINANCIAL_OBJECT_TYPE_CODE_POS + FINANCIAL_OBJECT_TYPE_CODE_LEN;

        // set up the needed string buffer for use by this sort
        protected StringBuffer sb = new StringBuffer(TOTAL_KEY_LEN);

        public int compare(LaborOriginEntry originEntry, String balanceRecord) {
            // build string from origin entry record (include padding)
            sb.setLength(0);
            sb.append(StringUtils.rightPad(originEntry.getFinancialBalanceTypeCode(), FINANCIAL_BALANCE_TYPE_CODE_LEN, ' '));            
            sb.append(StringUtils.rightPad(originEntry.getEmplid(), EMPLID_LEN, ' '));
            sb.append(StringUtils.rightPad(originEntry.getPositionNumber(), POSITION_NUMBER_LEN, ' '));
            sb.append(StringUtils.rightPad(originEntry.getChartOfAccountsCode(), CHART_OF_ACCOUNTS_CODE_LEN, ' '));
            sb.append(StringUtils.rightPad(originEntry.getAccountNumber(), ACCOUNT_NUMBER_LEN, ' '));
            sb.append(StringUtils.rightPad(originEntry.getSubAccountNumber(), SUB_ACCOUNT_NUMBER_LEN, ' '));
            sb.append(StringUtils.rightPad(originEntry.getFinancialObjectTypeCode(), FINANCIAL_OBJECT_TYPE_CODE_LEN, ' '));
            sb.append(StringUtils.rightPad(originEntry.getFinancialObjectCode(), FINANCIAL_OBJECT_CODE_LEN, ' '));
            sb.append(StringUtils.rightPad(originEntry.getFinancialSubObjectCode(), FINANCIAL_SUB_OBJECT_CODE_LEN, ' '));
            String originEntryKeyString = sb.toString();

            // pull substring from balance record
            String balanceRecordKeyString = StringUtils.substring(balanceRecord, 0, TOTAL_KEY_LEN);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Comparing Strings: \nENC: " + originEntryKeyString + "\nBAL: " + balanceRecordKeyString);
            }
            // KFSI-3563: don't use the obj type code to compare for a match, so we need to split the keys into chunks and compare each
            String originEntrySubKey = originEntryKeyString.substring(0, FINANCIAL_OBJECT_TYPE_CODE_POS);
            String balanceRecordSubKey = balanceRecordKeyString.substring(0, FINANCIAL_OBJECT_TYPE_CODE_POS);
            
            int compareResult = originEntrySubKey.compareTo(balanceRecordSubKey);
            if (compareResult != 0) {
                // if unequal, we know the strings aren't equal, but if equal, we need to test the rest of the key
                return compareResult;
            }
            
            originEntrySubKey = originEntryKeyString.substring(FINANCIAL_OBJECT_CODE_POS);
            balanceRecordSubKey = balanceRecordKeyString.substring(FINANCIAL_OBJECT_CODE_POS);
            
            return originEntrySubKey.compareTo(balanceRecordSubKey);
        }
    }

    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected LaborEncumbranceReportWriterService getReportWriterService() {
        return reportWriterService;
    }

    public void setReportWriterService(LaborEncumbranceReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }

    protected PersonService getPersonService() {
        if ( personService == null ) {
            personService = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class);
        }
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }
    
    /**
     * Sets the paramSrv attribute value.
     * @param paramSrv The paramSrv to set.
     */
    public void setParamSrv(ParameterService paramSrv) {
        this.paramSrv = paramSrv;
    }

    /**
     * Gets the laborEncumbranceAdjustmentDao attribute. 
     * @return Returns the laborEncumbranceAdjustmentDao.
     */
    public LaborEncumbranceAdjustmentDao getLaborEncumbranceAdjustmentDao() {
        return laborEncumbranceAdjustmentDao;
    }

    /**
     * Sets the laborEncumbranceAdjustmentDao attribute value.
     * @param laborEncumbranceAdjustmentDao The laborEncumbranceAdjustmentDao to set.
     */
    public void setLaborEncumbranceAdjustmentDao(LaborEncumbranceAdjustmentDao laborEncumbranceAdjustmentDao) {
        this.laborEncumbranceAdjustmentDao = laborEncumbranceAdjustmentDao;
    }

    /**
     * Gets the laborEncumbranceDao attribute. 
     * @return Returns the laborEncumbranceDao.
     */
    public LaborEncumbranceDao getLaborEncumbranceDao() {
        return laborEncumbranceDao;
    }

    /**
     * Sets the laborEncumbranceDao attribute value.
     * @param laborEncumbranceDao The laborEncumbranceDao to set.
     */
    public void setLaborEncumbranceDao(LaborEncumbranceDao laborEncumbranceDao) {
        this.laborEncumbranceDao = laborEncumbranceDao;
    }


    

}
 