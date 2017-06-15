package edu.arizona.kfs.module.prje.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.prje.PRJEConstants;
import edu.arizona.kfs.module.prje.ProrateJournalEntry;
import edu.arizona.kfs.module.prje.businessobject.PRJEAccountLine;
import edu.arizona.kfs.module.prje.businessobject.PRJEBaseAccount;
import edu.arizona.kfs.module.prje.businessobject.PRJEBaseObject;
import edu.arizona.kfs.module.prje.businessobject.PRJESet;
import edu.arizona.kfs.module.prje.businessobject.PRJEType;
import edu.arizona.kfs.module.prje.dataaccess.PRJEDao;
import edu.arizona.kfs.module.prje.businessobject.PRJETransferRecord;
import edu.arizona.kfs.module.prje.service.PRJEReportsService;
import edu.arizona.kfs.module.prje.service.PRJEService;
import edu.arizona.kfs.module.prje.service.PRJEServiceBaseImpl;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class PRJEServiceImpl extends PRJEServiceBaseImpl implements PRJEService {
    private static Logger LOG = Logger.getLogger(PRJEServiceImpl.class);
    private static final KualiDecimal ZERO = new KualiDecimal(0.0);
    
    private PRJEReportsServiceImpl prjeReports;
    protected OptionsService optionsService;
    
    public boolean process() {
        LOG.info("Beginning PRJE Service Processing");
        
        try {
            new WorkContext().run();
            return true;
        }
        // an exception is now a failure condition
        catch ( RuntimeException e ) {
            LOG.error("Error running PRJE Service Proccessing", e);
            throw e;
        }
    }

    public PRJEReportsServiceImpl getPrjeReports() {
        return prjeReports;
    }

    public void setPrjeReports(PRJEReportsServiceImpl prjeReports) {
        this.prjeReports = prjeReports;
    }
    
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
    
    public SystemOptions getSystemOptions(Integer universityFiscalYear) {
        SystemOptions options = null;
        options = optionsService.getOptions(universityFiscalYear);
        if (ObjectUtils.isNull(options)) {
            options = optionsService.getCurrentYearOptions();
        } 
        return options;
    }

    //---------------------------------------------------------------------------
    
    /**
     * WorkContext
     */
    private class WorkContext implements Runnable {
        // Per-Type Variables
        public int matchingBalances;
        public KualiDecimal availableBalance;

        // Property Strings
        public String propFiscalYear;
        public String propCurrentPeriod;
        public String propObjectTypes;
        public String propProcessOrder;
        
        // Other Parameters
        public List<PRJETransferRecord> processedTransfers = new ArrayList<PRJETransferRecord>();
        public Map<PRJEType, Integer> typeSequences = new HashMap<PRJEType, Integer>();
        public int lastTypeSequence = 1;
        public int lastOriginSequence = 1;
        
        public List<String> objectTypes;
        public Integer fiscalYear;
        public Map<Integer, String> processOrder;
        public String currentPeriod;
        public Date transactionDate;
        
        public WorkContext() {
        }
        
        public void run() {
            configure();
            
            PRJEDao prjeDataAccess = getPrjeDataAccess();
            Collection<PRJESet> sets = prjeDataAccess.getPRJESets(true);
            Map<String, PRJESet> setMap = new HashMap<String, PRJESet>();
            
            List<String> setOrder;
            if ( propProcessOrder != null ) {
                setOrder = Arrays.asList(propProcessOrder.split(";"));
            }
            else {
                setOrder = new ArrayList<String>();
            }
            
            // Add the Set to a Name Mapping, filtering by Fiscal Year
            for ( PRJESet set : sets ) {
                if ( set.getFiscalYear().equals(fiscalYear) ) {
                    setMap.put(set.getSetName(), set);

                    LOG.info("Adding to Set Map: "+set.getSetName());
                }
            }
            
            // get rid of exceptions for some error conditions 
            if ( setMap.isEmpty() ) {
                LOG.error("No Fiscal Year PRJE Sets to Process for FY: " + fiscalYear);
            }
            else {
                // Process the set in PROCESS_ORDER
                for ( String id : setOrder ) {
                    PRJESet set = setMap.get(id.trim());
                    
                    if ( set == null ) {
                        LOG.error("PRJE SetID " + id.trim() + " was not included in the processing - invalid value provided.");
                        continue;
                    }
                    processSet(set);
                }
    
                // Write the Origin Entries output file
                writeStagingEntries(generateOriginEntries(processedTransfers));
             
                // Write the Reports
                PRJEReportsService reports = getPrjeReports();
                reports.writeReports(processedTransfers);
            }
        }
        
        private void configure() {
            ParameterService parameterService = getParameterService();
            
            List<String> configuration = new ArrayList<String>(parameterService.getParameterValuesAsString(ProrateJournalEntry.class, PRJEConstants.CONFIGURATION));
            
            for ( String type : configuration ) {
                if ( PRJEConstants.PROPERTIES.equalsIgnoreCase(type.trim())) {
                    configureFromProperties();
                }
                else if ( PRJEConstants.PARAMETERS.equalsIgnoreCase(type.trim())) {
                    configureFromParameters();
                }
                else {
                    LOG.warn("Invalid Configuration type: "+type);
                }
            }
            
            // Display gathered Properties
            LOG.info("Property: " + PRJEConstants.OBJECT_TYPE + "='" + propObjectTypes + "'");
            LOG.info("Property: " + PRJEConstants.TABLE_FISCAL_YEAR + "='" + propFiscalYear + "'");
            LOG.info("Property: " + PRJEConstants.TABLE_CURRENT_PERIOD + "='" + propCurrentPeriod + "'");
            LOG.info("Property: " + PRJEConstants.PROCESS_ORDER + "='" + propProcessOrder + "'");
            
            DateTimeService dts = getDateTimeService();
            UniversityDateService uds = getUniversityDateService();
            transactionDate = new Date(dts.getCurrentDate().getTime());
            
            if ( StringUtils.isNotBlank(propObjectTypes) ) {
                objectTypes = Arrays.asList(propObjectTypes.split(";"));
            }
            else {
                objectTypes = new ArrayList<String>();
            }
            
            if ( StringUtils.isNotBlank(propFiscalYear) ) {
                try {
                    fiscalYear = Integer.parseInt(propFiscalYear.trim());
                }
                catch ( Exception e ) {
                    LOG.warn("Could not parse Fiscal Year");
                }
            }

            if ( fiscalYear == null ) {
                fiscalYear = uds.getCurrentFiscalYear();
            }

            if ( StringUtils.isNotBlank(propCurrentPeriod) ) {
                currentPeriod = propCurrentPeriod;
            }
            else {
                currentPeriod = uds.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod();
            }
            
            LOG.info("Processing Fiscal Year: "+fiscalYear);
            LOG.info("Processing Fiscal Period: "+currentPeriod);
        }
        
        private void configureFromProperties() {
            // check the available Properties object for configuration
            Properties properties = getProperties();
            
            if ( propObjectTypes == null && properties.containsKey(PRJEConstants.OBJECT_TYPE) ) {
                propObjectTypes = properties.getProperty(PRJEConstants.OBJECT_TYPE);
            }
            
            if ( propFiscalYear == null && properties.containsKey(PRJEConstants.TABLE_FISCAL_YEAR) ) {
                propFiscalYear = properties.getProperty(PRJEConstants.TABLE_FISCAL_YEAR);
            }
            
            if ( propCurrentPeriod == null && properties.containsKey(PRJEConstants.TABLE_CURRENT_PERIOD) ) {
                propCurrentPeriod = properties.getProperty(PRJEConstants.TABLE_CURRENT_PERIOD);
            }
            
            if ( propProcessOrder == null && properties.containsKey(PRJEConstants.PROCESS_ORDER) ) {
                propProcessOrder = properties.getProperty(PRJEConstants.PROCESS_ORDER);
            }
        }
        
        private void configureFromParameters() {
            // Retrieve Parameters
            ParameterService parameterService = getParameterService();
            
            if ( propObjectTypes == null ) {
                propObjectTypes = parameterService.getParameterValueAsString(ProrateJournalEntry.class, PRJEConstants.OBJECT_TYPE);
            }
            
            if ( propFiscalYear == null ) {
                propFiscalYear = parameterService.getParameterValueAsString(ProrateJournalEntry.class, PRJEConstants.TABLE_FISCAL_YEAR);
            }

            if ( propCurrentPeriod == null ) {
                propCurrentPeriod = parameterService.getParameterValueAsString(ProrateJournalEntry.class, PRJEConstants.TABLE_CURRENT_PERIOD);
            }
            
            if ( propProcessOrder == null ) {
                propProcessOrder = parameterService.getParameterValueAsString(ProrateJournalEntry.class, PRJEConstants.PROCESS_ORDER);
            }
        }

        private void processSet(PRJESet set) {
            LOG.info("Processing Set: " + set.getSetName());
            
            // For each From (Base) account entry in the JE Prorate Type Table:
            Collection<PRJEType> types = set.getTypes();

            // This requires two passes, a first to calculate all available
            for ( PRJEType type : types ) {
                if ( Boolean.TRUE.equals(type.getActive()) ) {
                    // Read a row from the Prorate JE Type Table
                    processType(type);
                }
            }
        }

        private void processType(PRJEType type) {
            LOG.info("- Processing Type: "+type.getEntryName());
            
            // Read the Prorate Account table and calculate the amount money 
            // to distribute from the From (Base) Account based on the amount
            // or percent type.  If the type resides at the account level, use
            // the account level type. If the type does not reside at the
            // account, then use the type stored at the Prorate JE Type level.

            // In dealing with multiple accounts, the calculation should be 
            // done by first adding the balance amounts, multiply by the
            // rate, then round the total.
            
            // Perform the transfer for this Type
            
            performTransfer(type);
        }
        
        private void performTransfer(PRJEType type) {
            LOG.info("-- Performing Transfer");
            
            List<PRJETransferRecord> transferRecords = generatePRJETransferRecords(type);
            
            KualiDecimal amountTransferred = ZERO;
            for ( PRJETransferRecord transferRecord : transferRecords ) {
                KualiDecimal amount = processPRJETransferRecord(transferRecord);
                amountTransferred = amountTransferred.add(amount.abs());
            }

            if ( amountTransferred.isZero() ) {
                LOG.warn("--- Total Amount Transferred is Zero");
            }
        }
        
        private KualiDecimal processPRJETransferRecord(PRJETransferRecord transferRecord) {
            KualiDecimal availableBalance = this.calculateBaseAccountAmount(transferRecord.getBaseAccount());
            KualiDecimal amount = calculateTransfer(transferRecord, availableBalance);
            
            if ( amount.isNonZero() ) {
                transferRecord.setBalance(availableBalance);
                transferRecord.setAmount(amount);
                
                // Create the Origin Entries based on the Transfer Record Amount
                OriginEntryFull debitEntry = generateDebitEntry(transferRecord);
                OriginEntryFull creditEntry = generateCreditEntry(transferRecord);
                
                transferRecord.setDebitEntry(debitEntry);
                transferRecord.setCreditEntry(creditEntry);
                
                processedTransfers.add(transferRecord);
            }
            else {
                LOG.warn("--- Attempting to Transfer Zero");
            }
            
            return amount;
        }

        private OriginEntryFull generateDebitEntry(PRJETransferRecord transferRecord) {
            PRJEBaseAccount baseAccount = transferRecord.getBaseAccount();
            KualiDecimal amount = transferRecord.getAmount();
            KualiDecimal balance = transferRecord.getBalance();
            
            // From (Base) Side:
            
            // Debit the From (Base) Account for expense object code, 
            // or credit the From (Base) Account for revenue object 
            // code.  However, if the override chart and accounts are 
            // defined use the amount under the From (Base) chart-account
            // for calculation, but move the money from the override 
            // chart-account.
            
            String account;
            String subAccount;
            String chartCode;

            // Decide The Source of the Debit (From or Base)
            if ( StringUtils.isNotBlank(baseAccount.getFromAccount()) ) {
                account = baseAccount.getFromAccount();
                subAccount = baseAccount.getFromSubAccount();
                chartCode = baseAccount.getFromChart();
            }
            else {
                account = baseAccount.getBaseAccount();
                subAccount = baseAccount.getBaseSubAccount();
                chartCode = baseAccount.getBaseChart();
            }
            
            LOG.info("--- Debit " + amount + " from Base Account " + account);
            
            PRJEType type = baseAccount.getType();
            
            // Create the origin entry
            OriginEntryFull debitEntry = new OriginEntryFull(PRJEConstants.ENTRY_DOCTYPE, PRJEConstants.ENTRY_ORIGIN);
            SystemOptions options = getSystemOptions(getUniversityDateService().getCurrentFiscalYear());
            debitEntry.setFinancialBalanceTypeCode(options.getActualFinancialBalanceTypeCd());
            
            debitEntry.setOrganizationDocumentNumber(getOrganizationDocumentNumber(type));
            debitEntry.setDocumentNumber(getDocumentNumber(type));
            debitEntry.setAccountNumber(account);
            debitEntry.setChartOfAccountsCode(chartCode);
            
            if ( subAccount != null ) {
                debitEntry.setSubAccountNumber(subAccount);
            }

            String objectCode = baseAccount.getFromObjectCode();
            if ( objectCode != null ) {
                ObjectCodeService ocs = getObjectCodeService();
                ObjectCode oc = ocs.getByPrimaryId(fiscalYear, chartCode, objectCode);
                
                if ( oc != null ) {
                    debitEntry.setFinancialObjectTypeCode(oc.getFinancialObjectTypeCode());
                    debitEntry.setFinancialObjectCode(oc.getFinancialObjectCode());
                    ObjectType ot = oc.getFinancialObjectType();
                    debitEntry.setTransactionDebitCreditCode(deriveDebitSign(ot, balance));
                }
                else {
                    LOG.warn("---- No Object Code " + objectCode + " in " + fiscalYear);
                }
            }
            else {
                LOG.warn("---- No Object Code -- Can't Derive Debit/Credit Code");
            }
            
            debitEntry.setUniversityFiscalPeriodCode(currentPeriod);
            debitEntry.setUniversityFiscalYear(fiscalYear);
            
            debitEntry.setTransactionDate(transactionDate);
            debitEntry.setTransactionLedgerEntryAmount(amount.abs());
            debitEntry.setTransactionLedgerEntrySequenceNumber(lastOriginSequence++);
            debitEntry.setTransactionScrubberOffsetGenerationIndicator(true);

            String description = generateDebitDescription(transferRecord);
            debitEntry.setTransactionLedgerEntryDescription(description);
            
            return debitEntry;
        }
        
        private OriginEntryFull generateCreditEntry(PRJETransferRecord transferRecord) {
            PRJEAccountLine accountLine = transferRecord.getAccountLine();
            KualiDecimal amount = transferRecord.getAmount();
            KualiDecimal balance = transferRecord.getBalance();
            
            LOG.info("--- Credit " + amount + " to Account " + accountLine.getAccountNumber());
            
            // If Sub-Account and Sub-Objects exist, then use them. If 
            // Override accounts and objects do not have sub account or 
            // sub object, then post them at the override account and 
            // object.
            
            PRJEType type = accountLine.getType();
            
            // Calculate Amount to Credit
            String prorateType = accountLine.getOverrideProrateType();
            
            String account = accountLine.getAccountNumber();
            String subAccount = accountLine.getSubAccountNumber();
            String chartCode = accountLine.getChartCode();
            String objectCode = accountLine.getObjectCode();
            String projectCode = accountLine.getProjectCode();
          
            // To Side:

            // Credit the departmental Account for expense object codes,
            // or debit the departmental account for revenue object 
            // codes.

            // Always post the prorated amounts to the account and object
            // code (no sub-account and sub-object codes) since they are
            // not guaranteed to exist.

            // Create the origin entry
            OriginEntryFull creditEntry = new OriginEntryFull(PRJEConstants.ENTRY_DOCTYPE, PRJEConstants.ENTRY_ORIGIN);
            SystemOptions options = getSystemOptions(getUniversityDateService().getCurrentFiscalYear());
            creditEntry.setFinancialBalanceTypeCode(options.getActualFinancialBalanceTypeCd());
            
            creditEntry.setOrganizationDocumentNumber(getOrganizationDocumentNumber(type));
            creditEntry.setDocumentNumber(getDocumentNumber(type));
            creditEntry.setAccountNumber(account);
            creditEntry.setSubAccountNumber(subAccount);
            creditEntry.setChartOfAccountsCode(chartCode);
            creditEntry.setProjectCode(projectCode);
            
            if ( objectCode != null ) {
                ObjectCodeService ocs = getObjectCodeService();
                ObjectCode oc = ocs.getByPrimaryId(fiscalYear, chartCode, objectCode);
                
                if ( oc != null ) {
                    creditEntry.setFinancialObjectTypeCode(oc.getFinancialObjectTypeCode());
                    creditEntry.setFinancialObjectCode(oc.getFinancialObjectCode());
                    ObjectType ot = oc.getFinancialObjectType();
                    creditEntry.setTransactionDebitCreditCode(deriveCreditSign(ot, balance));
                }
                else {
                    LOG.warn("---- No Object Code "+objectCode+" in "+fiscalYear);
                }
            }
            else {
                LOG.warn("---- No Object Code -- Can't Derive Debit/Credit Code");
            }
            
            creditEntry.setUniversityFiscalPeriodCode(currentPeriod);
            creditEntry.setUniversityFiscalYear(fiscalYear);

            creditEntry.setTransactionDate(transactionDate);
            creditEntry.setTransactionLedgerEntryAmount(amount.abs());
            creditEntry.setTransactionLedgerEntrySequenceNumber(lastOriginSequence++);
            creditEntry.setTransactionScrubberOffsetGenerationIndicator(true);

            String description = generateCreditDescription(transferRecord);
            creditEntry.setTransactionLedgerEntryDescription(description);
            
            return creditEntry;
        }
        
        private void writeStagingEntries(List<OriginEntryFull> originEntries) {
            LOG.info("Writing Origin Entries to Staging");
            
            String dataFilename = getBatchFileDirectoryName() + File.separator + DATA_FILE;
            String doneFilename = getBatchFileDirectoryName() + File.separator + DONE_FILE;
            
            try {
                PrintStream ps = new PrintStream(dataFilename);
            
                for ( OriginEntryFull originEntry : originEntries ) {
                    
                    // Do a little bit of house cleaning of entries
                    if ( StringUtils.isEmpty(originEntry.getSubAccountNumber()) ) {
                        originEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
                    }
                    if ( StringUtils.isEmpty(originEntry.getProjectCode()) ) {
                        originEntry.setProjectCode(KFSConstants.getDashProjectCode());
                    }
                    if ( StringUtils.isEmpty(originEntry.getFinancialSubObjectCode()) ) {
                        originEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                    }
                    
                    ps.printf("%s\n", originEntry.getLine());
                    
                    LOG.debug("- FLAT FILE: "+originEntry.getLine());
                }
                
                ps.close();
                
                // Touch the DONE file
                File doneFile = new File(doneFilename);
                if ( !doneFile.exists() ) {
                    doneFile.createNewFile();
                }
            }
            catch ( IOException e ) {
                throw new RuntimeException(e);
            }
        }
        
        private KualiDecimal calculateBaseAccountAmount(PRJEBaseAccount baseAccount) {
            KualiDecimal curBalance = ZERO;
            LOG.debug("-- Processing Base Account "+baseAccount.getBaseAccount());
            
            // Retrieve the balances for this baseAccount
            BusinessObjectService bos = getBusinessObjectService();
            Map<String, String> args = new HashMap<String, String>();
            args.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, Integer.toString(fiscalYear));
            args.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, baseAccount.getBaseChart());
            args.put(KFSPropertyConstants.ACCOUNT_NUMBER, baseAccount.getBaseAccount());
            
            if ( baseAccount.getBaseSubAccount() != null ) {
                args.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, baseAccount.getBaseSubAccount());
            }
            
            List<Balance> balances = (List<Balance>)bos.findMatching(Balance.class, args);
            if ( balances != null && balances.size() > 0 ) {
                // Calculate the total available balance to leverage
                KualiDecimal balance;
                if ( PRJEConstants.Frequency.YEARLY.getKey().equals(baseAccount.getFrequency()) ) {
                    balance = calculateYearly(balances, baseAccount);
                }
                else {
                    balance = calculateMonthly(balances, baseAccount);
                }
                curBalance = curBalance.add(balance);
                
                LOG.debug("--- Balance for Base Account is "+balance);
            }
            else {
                LOG.warn("--- No Balances for Base Account "+baseAccount.getBaseAccount());
            }
            
            return curBalance;
        }
        
        private List<PRJETransferRecord> generatePRJETransferRecords(PRJEType type) {
            // This can technically create a complete cross join for 
            // many-to-many processing, but that would probably be rather 
            // frightening.
            
            List<PRJEBaseAccount> baseAccounts = type.getBaseAccounts();
            List<PRJEAccountLine> accountLines = type.getAccountLines();

            List<PRJETransferRecord> transferRecords = new ArrayList<PRJETransferRecord>();
            
            for ( PRJEBaseAccount baseAccount : baseAccounts ) {
                for ( PRJEAccountLine accountLine : accountLines ) {
                    if ( isBaseAccountValid(baseAccount) && isAccountLineValid(accountLine) ) {
                        transferRecords.add(new PRJETransferRecord(baseAccount, accountLine));
                    }
                }
            }
            
            if ( transferRecords.size() == 0 ) {
                LOG.error("--- No Transfer Records to be processed");
            }
            
            return transferRecords;
        }
        
        private List<OriginEntryFull> generateOriginEntries(List<PRJETransferRecord> transferRecords) {
            List<OriginEntryFull> originEntries = new ArrayList<OriginEntryFull>();
            
            for ( PRJETransferRecord transferRecord : transferRecords ) {
                originEntries.add(transferRecord.getDebitEntry());
                originEntries.add(transferRecord.getCreditEntry());
            }
            
            return originEntries;
        }
        
        private String generateDebitDescription(PRJETransferRecord transferRecord) {
            PRJEBaseAccount baseAccount = transferRecord.getBaseAccount();
            PRJEType type = baseAccount.getType();
            
            StringBuffer description = new StringBuffer();
            description.append(type.getEntryName());
            description.append(" (Base)");
            
            return description.toString();
        }
        
        private String generateCreditDescription(PRJETransferRecord transferRecord) {
            PRJEBaseAccount baseAccount = transferRecord.getBaseAccount();
            PRJEAccountLine accountLine = transferRecord.getAccountLine();
            PRJEType type = baseAccount.getType();
            
            StringBuffer description = new StringBuffer();
            description.append(type.getEntryName());
            description.append(" (To)");
            
            return description.toString();
        }
        
        private KualiDecimal calculateTransfer(PRJETransferRecord transferRecord, KualiDecimal amount) {
            PRJEBaseAccount baseAccount = transferRecord.getBaseAccount();
            PRJEAccountLine accountLine = transferRecord.getAccountLine();
            
            String prorateType = baseAccount.getProrateType();
            if ( PRJEConstants.ProrateDebitType.AMOUNT.getKey().equals(prorateType) ) {
                amount = baseAccount.getProrateAmount();
            }
            else if ( PRJEConstants.ProrateDebitType.PERCENTAGE.getKey().equals(prorateType) ) {
                BigDecimal pct = baseAccount.getProratePercent().bigDecimalValue();
                BigDecimal bal = amount.bigDecimalValue();
                amount = new KualiDecimal(pct.multiply(bal).divide(new BigDecimal(100.0)));
            }
            
            // See if there's an override
            prorateType = accountLine.getOverrideProrateType();
            if ( PRJEConstants.ProrateCreditType.AMOUNT.getKey().equals(prorateType) ) {
                amount = accountLine.getOverrideAmount();
            }
            else if ( PRJEConstants.ProrateCreditType.PERCENTAGE.getKey().equals(prorateType) ) {
                BigDecimal pct = accountLine.getOverridePercent().bigDecimalValue();
                BigDecimal bal = amount.bigDecimalValue();
                amount = new KualiDecimal(pct.multiply(bal).divide(new BigDecimal(100.0)));
            }
            
            return amount;
        }
        
        private boolean isBaseAccountValid(PRJEBaseAccount baseAccount) {
            LOG.debug("--- Validating Base Account "+baseAccount.getBaseAccount());
            
            // Is Base Account Active?
            if ( !Boolean.TRUE.equals(baseAccount.getActive()) ) {
                return false;
            }
            
            // Does it have a proper ProrateType?
            String prorateType = baseAccount.getProrateType();
            if ( !(PRJEConstants.ProrateDebitType.AMOUNT.getKey().equals(prorateType) || 
                    PRJEConstants.ProrateDebitType.PERCENTAGE.getKey().equals(prorateType)) ) {
                return false;
            }
            
            return true;
        }
        
        private boolean isAccountLineValid(PRJEAccountLine accountLine) {
            LOG.debug("--- Validating Account Line "+accountLine.getAccountNumber());
            
            if ( !Boolean.TRUE.equals(accountLine.getActive()) ) {
                return false;
            }
            
            // Check to see if we're in the effective date range
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp from = accountLine.getEffectiveDateFrom();
            Timestamp to = accountLine.getEffectiveDateTo();
            
            if ( from != null && to != null && to.before(from) ) {
                LOG.warn("---- Effective From is After To");
                return false;
            }
            
            if ( (from != null && now.before(from)) || (to != null && now.after(to)) ) {
                LOG.warn("---- Out of Account Effective Date Range");
                return false;
            }
            
            return true;
        }
        
        private String getOrganizationDocumentNumber(PRJEType type) {
            StringBuffer sb = new StringBuffer();
            
            sb.append(type.getSet().getSetId());
            sb.append("-");
            sb.append(type.getTypeId());
            
            return sb.toString();
        }
        
        private String getDocumentNumber(PRJEType type) {
            Integer seq = typeSequences.get(type);
            if ( seq == null ) {
                // Associate it with a Sequence Number
                seq = new Integer(lastTypeSequence++);
                typeSequences.put(type, seq);
            }
            
            StringBuffer sb = new StringBuffer();
            
            sb.append(type.getTypeId());
            sb.append("-");
            sb.append(seq);
            
            return sb.toString();
        }
        
        private KualiDecimal calculateYearly(List<Balance> balances, PRJEBaseAccount baseAccount) {
            // If the Use Annual Balance Amt option is selected in the
            // type, then calculate the amount based on the 
            // ACLN_ANNL_BAL_AMT for the specified object code ranges.

            KualiDecimal result = ZERO;
            for ( Balance balance : balances ) {
                PRJEType type = baseAccount.getType();
                if ( balanceMatchesBaseObjects(type, balance) ) {
                    KualiDecimal amount = balance.getAccountLineAnnualBalanceAmount();
                    result = result.add(normalizeBalance(balance, amount));
                }
            }
            
            LOG.debug("-- Returning "+result+" for Yearly");
            
            return result;
        }

        private KualiDecimal calculateMonthly(List<Balance> balances, 
                                              PRJEBaseAccount baseAccount) {
            // Otherwise select the MO_ACCT_LN_AMT line corresponding to the 
            // accounting period of the GL Balance Table for the specified 
            // object code ranges.
            
            KualiDecimal result = ZERO;
            for ( Balance balance : balances ) {
                PRJEType type = baseAccount.getType();
                if ( balanceMatchesBaseObjects(type, balance) ) {
                    KualiDecimal amount = balance.getAmount(currentPeriod);
                    result = result.add(normalizeBalance(balance, amount));
                }
            }
            
            LOG.debug("-- Returning "+result+" for Monthly");
            
            return result;
        }
        
        private KualiDecimal normalizeBalance(Balance balance, KualiDecimal amount) {
            return amount;
        }
        
        private boolean balanceMatchesBaseObjects(PRJEType type, Balance balance) {
            SystemOptions options = getSystemOptions(getUniversityDateService().getCurrentFiscalYear());
            if ( !options.getActualFinancialBalanceTypeCd().equals(balance.getBalanceTypeCode()) ) {
                LOG.debug("--- Balance Type is not Actual");
                return false;
            }
            
            if ( objectTypes.size() > 0 && !objectTypes.contains(balance.getObjectTypeCode()) ) {
                LOG.debug("--- Object Types do not match");
                return false;
            }

            // Note: The object code selection should be made based on exclude
            //       or include flag.
            
            List<PRJEBaseObject> baseObjects = type.getBaseObjects();
            boolean inIncludeBaseObject = false;
            boolean inExcludeBaseObject = false;
            
            for ( PRJEBaseObject base : baseObjects) {
                if ( !Boolean.TRUE.equals(base.getActive()) ) {
                    continue;
                }
                
                if ( !base.getBaseChartCode().equals(balance.getChartOfAccountsCode()) ) {
                    LOG.debug("--- Chart Codes do not match");
                    continue;
                }
                
                boolean include = PRJEConstants.Containment.INCLUDE.getKey().equals(base.getInclude());
                
                boolean useSubObject = StringUtils.isNotBlank(base.getSubObjectCodeLow())  
                                    && StringUtils.isNotBlank(base.getSubObjectCodeHigh());
                
                int objectCode = Integer.parseInt(balance.getObjectCode());
                int lowCode = Integer.parseInt(base.getBaseObjectCodeLow());
                int highCode = Integer.parseInt(base.getBaseObjectCodeHigh());
                boolean inSet = (objectCode >= lowCode && objectCode <= highCode);
                
                if ( useSubObject ) {
                    
                    if (StringUtils.isBlank(balance.getSubObjectCode())) {
                        LOG.debug("--- Balance does not have a Sub Object Code");
                        continue;
                    }
                    
                    // Fields in the GL Balance table have a value of "---" (dashes) instead of blank or null
                    String balanceSubObjectCode = balance.getSubObjectCode().trim();
                    if (KFSConstants.BLANK_SUBOBJECT.equals(balanceSubObjectCode)) {
                        LOG.debug("--- Balance does not have a Sub Object Code");
                        continue;
                    }
                    
                    // Not all schools stick to pure numbers for sub-object codes, so have to handle that case (alpha)
                    // where the concept of low/high doesn't make any sense.
                    boolean allNumerics = false;
                    if (StringUtils.isNumeric(balanceSubObjectCode) 
                            && StringUtils.isNumeric(base.getSubObjectCodeLow()) 
                            && StringUtils.isNumeric(base.getSubObjectCodeHigh())) {
                        allNumerics = true;
                    }
                    
                    // Handle the comparisons differently, depending if we're working with all 
                    // numbers, or with alphanumerics.
                    if (allNumerics) {
                        int subObjectCode = Integer.parseInt(balanceSubObjectCode);
                        int subLowCode = Integer.parseInt(base.getSubObjectCodeLow());
                        int subHighCode = Integer.parseInt(base.getSubObjectCodeHigh());
                        inSet &= (subObjectCode >= subLowCode && subObjectCode <= subHighCode);
                    } else {
                        // Use lexicographical comparison to determine if the balance subobjectcode is equals to, 
                        // or between the low/high.  
                        inSet &= (balanceSubObjectCode.compareToIgnoreCase(base.getSubObjectCodeLow()) >= 0
                                && balanceSubObjectCode.compareToIgnoreCase(base.getSubObjectCodeHigh()) <= 0);
                    }
                }
                
                // KITT-1043 begin changes
                if (inSet) {
                    if (include) {
                        inIncludeBaseObject = true;
                    } else {
                        inExcludeBaseObject = true;
                    }
                }
                
            }
            
            boolean matches = inIncludeBaseObject && !inExcludeBaseObject;
            
            return matches;
        }
        
        private String deriveDebitSign(ObjectType objectType, KualiDecimal amount) {
            return amount.isNegative() ? "C" : "D";
        }
        
        private String deriveCreditSign(ObjectType objectType, KualiDecimal amount) {
            return amount.isNegative() ? "D" : "C";
        }
    }
}
