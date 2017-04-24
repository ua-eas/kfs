package edu.arizona.kfs.fp.batch.dataaccess.impl;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;




/**
 * A class to encapsulate components of a credit card record, and methods to
 * determine if the components pass the tests of being a Bank of America credit
 * card record. All input but the notes are guarded upon instantiation, and even
 * the notes are checked as part of isBofaNotesField().
 *
 * If the passed-in fields indicate that this is a BofA CC record, then a
 * call to getCustRefNum() will extract the custRefNum from the notesField.
 *
 * If this is not a BofA CC record, then getCustRefNum() will return the
 * originally supplied custRefNum.
 *
 *
 * @author sskinner
 *
 */
public class BofaCcRecordChecker {

    private BankParametersAccessService bankParametersAccessService;

    // Instance variables
    private String accountNumber;
    private String baiCode;
    private String custRefNum;
    private String notesField;




    /**
     * Constructor that requires all necessary components up-front.
     *
     * @param accountNumber This is currently field[0] of a valid BofA CC record
     * @param baiCode This is currently field[1] of a valid BofA CC record
     * @param custRefNum This was field[3] of a BofA CC record, but is now zeroed out for BofA CC records
     * @param notesField This is field[6] of a BofA CC record, and is where the needed "Customer Reference Number" now resides.
     */
    public BofaCcRecordChecker(final String accountNumber, final String baiCode, final String custRefNum, final String notesField) {
        this.accountNumber = accountNumber;
        this.custRefNum = custRefNum;
        this.baiCode = baiCode;
        this.notesField = notesField;
    }


    /**
     * If the encapsulated record is not a BofA CC record, return the original
     * Customer Reference Number passed-in during instantiation.
     *
     * If this is a BofA record, use regular expressions to attempt and extract
     * n-digit Customer Reference Number from the notes field. If this the extraction
     * fails, throw a detailed exception.
     *
     * @return Customer Reference Number -- when this record is a BofA CC record,
     *         the custRefNum is extracted from the notesField. Otherwise, return the
     *         originally passed-in custRefNum.
     */
    public String getCustRefNum() {

        // If this is not a BofA CC record, simply return the original custRefNum
        if(!isBofaCreditCardRecord()){
            return custRefNum;
        }

        // This record has all the elements of a BofA CC record, try to
        // extract custRefNum from the notesField
        Pattern custRefNumPattern = getBankParametersAccessService().getBofaCustRefNumPattern();
        Matcher custRefNumMatcher = custRefNumPattern.matcher(notesField);
        if(custRefNumMatcher.find()){
            int groupIndex = getBankParametersAccessService().getBofaCustRefNumGroupIndex();
            return custRefNumMatcher.group(groupIndex);
        }

        // If we get here, this is a BofA CC record, but we still couldn't match a custRefNum from the notes field
        return null;
    }




    /*
     * Tests if input values line up with that of a Bank of America credit card record.
     *
     * @return True if BofA record, otherwise false
     */
    public boolean isBofaCreditCardRecord() {
        return isCorrectBankAccountNum()
                && isCorrectBaiType()
                && isZeroedCustRefNum()
                && isBofaNotesField();
    }


    /*
     * The BofA CC records should only ever have the account number of "529788341",
     * but is parameterized in case the number changes, or additional numbers are needed.
     */
    private boolean isCorrectBankAccountNum() {
        Set<String> ccAccountNumSet = getBankParametersAccessService().getBofaCcAccountNumbers();
        return ccAccountNumSet.contains(accountNumber);
    }


    /*
     * This field went away when BofA switched vendors that produces files
     * passed on to Wells Fargo (UofA's bank). So, any BofA CC record should
     * have a 'zeroed' custRefNum, of the form "00000000000". This would parse
     * as a '0'.
     */
    private boolean isZeroedCustRefNum() {
        long testCustRefNum = Long.MIN_VALUE;
        try{
            testCustRefNum = Long.parseLong(custRefNum);
        } catch(Exception e){
            throw new RuntimeException("Could not parse '" + custRefNum + "' to type Long.");
        }
        return testCustRefNum == 0;
    }


    /*
     * These will only have two codes for bankcards (including credit cards):
     * 1.) 169 - Income Account Number
     * 2.) 469 - Expense Account Number
     *
     * Note: these are not exclusive to Bofa CC records, but all BofA CC
     * records will necessarily have one of these BAI type codes.
     */
    private boolean isCorrectBaiType() {
        Set<String> ccBaiTypeCodeSet = getBankParametersAccessService().getBofaCcBaiTypeCodes();
        return ccBaiTypeCodeSet.contains(baiCode);
    }


    /*
     * Verify that the notes field contains an indicator specific to BofA.
     * Currently, this free-form field for BofA CC records has the following form:
     *
     * "BANK OF AMERICA  DEPOSIT    131001 345536769887    UOFA SOUVENIR SALES"
     *
     * This method picks up regex from the parms, and applies it to this field. If
     * the matcher finds a hit, return true, otherwise false.
     */
    private boolean isBofaNotesField() {

        if(StringUtils.isBlank(notesField)){
            // All BofA CC records will have this field. The other CC types might not,
            // so short circuit when we were passed nothing upon instantiation.
            return false;
        }

        Pattern bofaNotesPattern = getBankParametersAccessService().getBofaNotesPattern();
        Matcher notesMatcher = bofaNotesPattern.matcher(notesField);
        return notesMatcher.find();
    }



    protected BankParametersAccessService getBankParametersAccessService() {
        if (bankParametersAccessService == null) {
            bankParametersAccessService = SpringContext.getBean(BankParametersAccessService.class);
        }
        return bankParametersAccessService;
    }


}
