package edu.arizona.kfs.fp.batch.dataaccess.impl;

import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VaRecordNotesMasker {

    private BankParametersAccessService bankParametersAccessService;


    private String accountNumber;
    private String baiTypeCode;
    private String custRefNum;
    private String notesFieldSix;
    private String notesFieldSeven;



    public VaRecordNotesMasker(){
        throw new UnsupportedOperationException("VaRecordNotesMasker can only be instantiated by the 'VaRecordNotesMasker(String accountNumber, String baiTypeCode, String custRefNum, String notesFieldSix, String notesFieldSeven)' constructor.");
    }


    public VaRecordNotesMasker(String accountNumber, String baiTypeCode, String custRefNum, String notesFieldSix, String notesFieldSeven) {
        this.accountNumber = accountNumber;
        this.baiTypeCode = baiTypeCode;
        this.custRefNum = custRefNum;
        this.notesFieldSix = notesFieldSix;
        this.notesFieldSeven = notesFieldSeven;
    }



    /**
     * If the fields of this record follow the defintions set forth by FSO and
     * encoded in KFS parms, then "Notes Field Seven" will have its
     * "VA Case File Number" component masked and the modified field returned.
     *
     *  If the conditions indicate this is not a VA record, then the original
     *  notesFieldSeven is returned, unmodified.
     *
     * @return If this.isValidVaRecord() returns true, then a masked version of
     *         notesFieldSeven is returned. Otherwise, return the unmodified
     *         notesFieldSeven.
     */
    public String getAppropriateNotesFieldSeven(){
        if(isValidVaRecord()){
            return getMaskedFieldSeven();
        }else{
            return notesFieldSeven;
        }
    }

    /*
     * Mask:
     * " 123456789  *TERM"
     * 
     * Into:
     * " XXX456789  *TERM"
     */
    private String getMaskedFieldSeven(){
        /*
         * Algorithm for masking:
         * 
         * We use regex to split this field into three groups:
         *     1. Group one is from beginning of the field to the start of numerical: " "
         *     2. Group two is the first three digits of the VA Case File Number: "123"
         *     3. Group three are the remaining digits, plus the remainder of the field: "456789  *TERM"
         *
         * We then reassemble these groups, substituting group 2 with "XXX":
         * " " + "XXX" + "456789  *TERM" = " XXX456789  *TERM"
         * 
         * The result is then returned.
         * 
         */
        Pattern maskingPattern =  getBankParametersAccessService().getVaNotesSevenMaskingPattern();
        Matcher maskingMatcher = maskingPattern.matcher(notesFieldSeven);
        maskingMatcher.find(); // safe because we already checked with isValidNotesFieldSeven()
        String groupOne = maskingMatcher.group(1); // => " "
        String groupTwo = KFSConstants.THREE_CHAR_MASK; // maskingMatcher.group(2) => "123"
        String groupThree = maskingMatcher.group(3); // => "456789  *TERM"

        String maskedNotesFieldSeven = groupOne + groupTwo + groupThree;
        return maskedNotesFieldSeven;

    }


    /*
     * Test if all fields follow the definition of a VA record
     */
    public boolean isValidVaRecord(){
        return isValidBankAccountNumber()
               && isValidBaiTypeCode()
               && isValidCustRefNum()
               && isValidNotesFieldSix()
               && isValidNotesFieldSeven();
    }


    /*
     * All VA records should have an account number of "529788309"
     */
    private boolean isValidBankAccountNumber(){
        Set<String> validVaAccountNumberSet = getBankParametersAccessService().getVaAccountNumbers();
        return validVaAccountNumberSet.contains(accountNumber);
    }


    /*
     * Currently, this is just the one value of "169", lets leave room for more
     */
    private boolean isValidBaiTypeCode(){
        Set<String> validBaiTypeCodeSet = getBankParametersAccessService().getVaBaiTypeCodes();
        return validBaiTypeCodeSet.contains(baiTypeCode);
    }

    /*
     * The "Customer Reference Number" of all VA records is of the form "00000000000".
     * This will parse to zero. Again, leave room to change.
     */
    private boolean isValidCustRefNum() {
        // Test against the parms
        Set<String> validCustRefNumSet = getBankParametersAccessService().getVaCustRefNumSet();
        return validCustRefNumSet.contains(custRefNum);
    }

    /*
     * All VA records should have a notesFieldSix of the form:
     * "VAED TREAS 310   XXVA CH33  100713       XXXXX3600 REF*48*CH33 TF VA FILE NO"
     * 
     * The pattern built from regex pulled from parms will match the above if this
     * is a VA record.
     * 
     */
    private boolean isValidNotesFieldSix(){
        // Perform regex matching to determine if this is a VA formatted field
        Pattern pattern =  getBankParametersAccessService().getVaNotesSixPattern();
        return patternMatchesValue(pattern, notesFieldSix);
    }


    /*
     * All VA records should have a notesFieldSeven of the form:
     * " 123456789  *TERM"
     * 
     * Important: the above field is literal, including the leading space and kleene star.
     * 
     * The pattern built from regex pulled from parms will match the above if this
     * is a VA record.
     * 
     */
    private boolean isValidNotesFieldSeven(){
        // Perform regex matching to determine if this is a VA formatted field
        Pattern pattern =  getBankParametersAccessService().getVaNotesSevenPattern();
        return patternMatchesValue(pattern, notesFieldSeven);
    }


    /*
     * Generically pattern match against a string value
     */
    private boolean patternMatchesValue(Pattern pattern, String value){
        // Short circuit if this field is missing, it can't be a VA record
        if(StringUtils.isBlank(value)){
            return false;
        }

        // Perform regex matching to determine if this is a VA formatted field
        Matcher matcher = pattern.matcher(value);
        return matcher.find();
    }


    protected BankParametersAccessService getBankParametersAccessService() {
        if (bankParametersAccessService == null) {
            bankParametersAccessService = SpringContext.getBean(BankParametersAccessService.class);
        }
        return bankParametersAccessService;
    }


}
