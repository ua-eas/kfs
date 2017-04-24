package edu.arizona.kfs.fp.batch.service;


import edu.arizona.kfs.fp.businessobject.ChartBankObjectCode;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * This service provides access to the Bank Parameters needed to process Bank Transaction Files in Document Creation Job
 * Tech note:
 * As it uses lazy instantiation for most of the values, use clearCached() at the begining of each batch job that uses the service
 * to insure latest values for the params are retrieved from the DB.
 * <p>
 * TODO: some of the maps/lists are exposed in the service unnecessarily - refactor and remove them
 * <p>
 * Created by nataliac on 3/25/17.
 */
public interface BankParametersAccessService {

    /**
     * Clears all the parameter values from the service insuring new values will be retrieved from the DB.
     */
    public void clearCached();

    /**
     * Returns the char used as a delimiter in the Bank Transaction File
     */
    public char getBankFileDelimiter();

    /**
     * Returns a map of the fields and their indexes in each row of the Bank Transaction File
     */
    public Map<String, Integer> getFieldPositions();

    /**
     * Returns the position index of the given field in a row the Bank Transaction File
     */
    public int getFieldPosition(String fieldName);


    /**
     * Returns the default bank account to be used in the DIE on the FROM side
     */
    public String getDefaultBank();


    /**
     * Returns the chart of accounts from EFT (Param: ELECTRONIC_FUNDS_ACCOUNTS)
     */
    public String getKeyChartOfElectronicFunds();


    /**
     * Returns the key of accounts from EFT (Param: ELECTRONIC_FUNDS_ACCOUNTS)
     */
    public String getKeyAccountOfElectronicFunds();


    /**
     * This method returns the Check Recon Bais
     *
     * @return List<Integer>
     */
    public List<Integer> getCheckReconBaiTypes();

    /**
     * This method returns the default bank corresponding to the given doc type
     *
     * @param String docType
     * @return String bank
     */
    public String getDefaultBankByDocType(String docType);

    /**
     * This method returns the BAI type codes to exclude from processing in the Bank Transaction File
     *
     * @return List<Integer>
     */
    public List<Integer> getExcludedBaiTypes();


    /**
     * This method returns the BAI type codes AND account numbers that will be excluded from processing in the Bank Transaction File
     *
     * @return List<String>
     */
    public List<String> getExcludedByBaiAndAccount();


    /**
     * This method returns the Customer reference numbers that will be excluded from processing in the Bank Transaction File
     *
     * @return List<String>
     */
    public List<String> getExcludedCustomerRefNbr();

    /**
     * This method returns the specialBaiTypes from the Bank Parameter: ACCOUNT_AND_OBJECT_CODE_BY_BAI_AND_BANK_ACCOUNT
     *
     * @return List<Integer>
     */
    public List<Integer> getSpecialBaiTypes();


    /**
     * This method returns the ChartBankObjectCode corresponding to the given special Bai Type and Bank Account
     *
     * @return ChartBankObjectCode
     */
    public ChartBankObjectCode getChartBankObjectCodeForSpecialBai(int bai, String bankAccount);


    /**
     * TODO should be protected!
     * This method returns the map of Doc types corresponding to each Bai type from the Bank Parameter: DOCUMENT_TYPE_BY_BAI_TYPE_CODE
     *
     * Tech note: in the DOCUMENT_TYPE_BY_BAI_TYPE_CODE param, each Bai Type has to correspond to ONE document Type and one only, otherwise the second one WILL BE INGORED!
     *
     * @return Map<Integer, String>

    public Map<Integer, String> getBaiMapOfDocTypes();
     */

    /**
     * This method returns the document type to be created corresponding to given Bai code
     *
     * @param int baiType
     * @return String - document Type
     */
    public String getDocumentTypeByBai(int baiType);

    /**
     * This method returns true if the given baiType is associated with a Debit Type
     *
     * @return String
     */
    public boolean isDebitByBai(int baiType);


    /**
     * This method returns either Credit or Debit corresponding to the given baiType
     *
     * @return String
     */
    public String getDebitCreditByBai(int baiType);


    /**
     * This method returns the set of ALL Allowed Bai Types in the Bank Transactions Files.
     *
     * @return Set<Integer>
     */
    public Set<Integer> getAllowedBaiTypes();

    /**
     * This method checks whether a bai is of Check Recon bai
     *
     * @param bai
     * @return
     */
    public boolean isCheckReconBai(int bai);

    /**
     * This method checks whether a bai is of excluded type
     *
     * @param bai
     * @return
     */
    public boolean isExcludedBai(int bai);


    /**
     * This method tests whether a bai is of special type
     *
     * @param bai
     * @return
     */
    public boolean isSpecialBai(int bai);

    /**
     * This method tests whether a Customer Reference Number is excluded
     *
     * @param custRefNumb
     * @return
     */
    public boolean isExcludedCustomerRefNbr(String custRefNumb);

    /**
     * This method tests whether bai:account should be excluded
     */
    public boolean isExcludedByBaiAndAccount(int bai, String account);


    /**
     * This method tests whether the Bai provided is allowed in the Bank Transaction Files
     */
    public boolean isAllowedBai(int bai);


    /**
     * Returns the Chart, account in object code to be used for a bank transaction record that matches this description.
     * If there's none that matches, returns null;
     * <p>
     * Bank Parameter that holds all the values: ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION
     */
    public ChartBankObjectCode getChartBankObjectCodeForDescriptionSearch(String description);


    /**
     * Returns the object code for the Default Advance Deposit
     */
    public String getObjectCodeForDefaultAD();

    /**
     * ******************************************************
     * ************Bank of America specific parameters
     * ******************************************************
     */

    /**
     * Returns the delimiter character for the other Bank of America parameters
     */
    public String getBofaDelimiter();

    /**
     * Returns a pattern to match BofA Customer Reference Number in a BofA notes field.
     * params: BOFA_CUST_REF_NUM_LENGTH_KEY, BOFA_CUST_REF_NUM_REGEX_FORMAT_KEY
     */
    public Pattern getBofaCustRefNumPattern();

    /**
     * Pattern that should match only on BofA note fields. This is split between
     * parameters: BOFA_NOTES_REGEX_FORMAT_KEY and BOFA_NOTES_INDICATOR_KEY
     */
    public Pattern getBofaNotesPattern();

    /**
     * Returns the number of digits that are allowed for the BofA Customer Reference Number
     * Param: BOFA_CUST_REF_NUM_LENGTH
     */
    public Integer getBofaCustRefNumLength();

    /**
     * Returns the regular expression that mathches the CustRefNbr of BofA records notes field
     * Param: BOFA_CUST_REF_NUM_REGEX_FORMAT
     */
    public String getBofaCustRefNumRegexFormat();

    /**
     * Returns the matched group index after applying the regex format to a BofA record notes
     * This tells which of the groups from getCustRefNumPattern().find() that
     * will contain the matched Customer Reference Number.
     * <p>
     * Param: BOFA_CUST_REF_NUM_GROUP_INDEX
     */
    public Integer getBofaCustRefNumGroupIndex();


    /**
     * Returns the regular expression that mathches the BofA records notes field
     * Param: BOFA_NOTES_REGEX_FORMAT
     */
    public String getBofaNotesRegexFormat();


    /**
     * Value indicating that notes field originates from Bank of America
     * Param: BOFA_NOTES_INDICATOR
     */
    public String getBofaNotesIndicator();

    /**
     * Returns the set of Account Numbers that can be used in Credit Card records from Bank of America
     * Param: BOFA_CC_ACCOUNT_NUM_SET
     */
    public Set<String> getBofaCcAccountNumbers();

    /**
     * Returns the set of BAI Codes that are associated with Credit Card records from Bank of America
     * Param: BOFA_CC_BAI_TYPE_CODE_SET
     */
    public Set<String> getBofaCcBaiTypeCodes();


    /**
     * ******************************************************
     * ************VA specific parameters
     * ******************************************************
     */

    /**
     * Returns the delimiter character for the other VA parameters
     */
    public String getVaDelimiter();

    /**
     * Retrieve all of the possible account numbers a VA record can have.
     * Parameter: VA_ACCOUNT_NUM_SET
     *
     * @return A set of all possible VA account numbers.
     */
    public Set<String> getVaAccountNumbers();


    /**
     * This method provides all of the BAI Type Codes that can be set on a VA
     * record. This set is not mutually exclusive to VA records.
     * Parameter: VA_BAI_TYPE_CODE_SET
     *
     * @return A complete set of all baiTypeCode tha a VA record can have.
     */
    public Set<String> getVaBaiTypeCodes();


    /**
     * Set of all possible Customer Reference Numbers a VA record can possess.
     *
     * @return Set of all possible Customer Reference Numbers a VA record can possess.
     */
    public Set<String> getVaCustRefNumSet();


    /**
     * Returns the regular expression that satisfies the VA format for Desc6 field
     * Param: VA_NOTES_SIX_REGEX
     */
    public String getVaNotesSixRegex();


    /**
     * Pattern matching  VA format for Desc6 field
     */
    public Pattern getVaNotesSixPattern();

    /**
     * Returns the regular expression that mathches the VA Notes7 field
     * Param: VA_NOTES_SEVEN_REGEX
     */
    public String getVaNotesSevenRegex();

    /**
     * Pattern matching  VA format for notesFieldSeven
     */
    public Pattern getVaNotesSevenPattern();


    /**
     * Returns the regular expression that for masking the VA Notes7 field
     * Param: VA_NOTES_SEVEN_MASKING_REGEX
     */
    public String getVaNotesSevenMaskingRegex();

    /**
     * Pull a pattern from the parms suitable for splitting this field
     * into parts, and apply a mask.
     */
    public Pattern getVaNotesSevenMaskingPattern();

}
