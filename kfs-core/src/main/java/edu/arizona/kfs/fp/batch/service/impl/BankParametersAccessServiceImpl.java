package edu.arizona.kfs.fp.batch.service.impl;


import edu.arizona.kfs.fp.batch.DocumentCreationStep;
import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import edu.arizona.kfs.fp.businessobject.ChartBankObjectCode;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Service that facilitates access to Bank Parameters for Bank Transaction loading and Document Creation processes
 * <p>
 * Created by nataliac on 3/25/17.
 */
public class BankParametersAccessServiceImpl implements BankParametersAccessService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankParametersAccessServiceImpl.class);

    private static String COLON=":";
    private static String COMMA=",";
    private static String EQUAL="=";

    private ParameterService parameterService = null;

    private char bankTFileDelimiter;
    private Map<String, Integer> bankTFileFieldMap;
    private String defaultBank;
    private String keyChartOfElectronicFunds;
    private String keyAccountOfElectronicFunds;
    private Map<String, String> defaultBanksByDocTypeMap;
    private List<Integer> checkReconBaiTypes;
    private List<Integer> excludeBaiTypes;
    private List<String> excludedByBaiAndAccount;
    private List<Integer> specialBaiTypes;
    private List<String> excludeCustomerRefNbr;
    private Map<Integer, String> documentTypeBaiMap;
    private Map<Integer, String> debitCreditMap;
    private Set<Integer> allowedBaiTypes;
    private Map<String, ChartBankObjectCode> descriptionSearchChartBankObjectCodeMap;
    private String objectCodeForDefaultAD;
    private Map<String, ChartBankObjectCode> baiChartBankObjectCodeMap;
    private String checkReconStatus;

    private String bofaDelimiter;
    private Pattern bofaCustRefNumPattern;
    private Pattern bofaNotesPattern;
    private Integer bofaCustRefNumLength;
    private String bofaCustRefNumRegexFormat;
    private Integer bofaCustRefNumGroupIndex;
    private String bofaNotesIndicator;
    private String bofaNotesRegexFormat;
    private Set<String> bofaCcAccountNumbers;
    private Set<String> bofaCcBaiTypeCodes;

    private String vaDelimiter;
    private Set<String> vaAccountNumbers;
    private Set<String> vaBaiTypeCodes;
    private Set<String> vaCustRefNumSet;
    private String vaNotesSixRegex;
    private String vaNotesSevenRegex;
    private String vaNotesSevenMaskingRegex;
    private Pattern vaNotesSixPattern;
    private Pattern vaNotesSevenPattern;
    private Pattern vaNotesSevenMaskingPattern;


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#clearCached()
     */
    public void clearCached() {
        bankTFileDelimiter = Character.MIN_VALUE;
        bankTFileFieldMap = null;
        defaultBank = null;
        keyChartOfElectronicFunds = null;
        keyAccountOfElectronicFunds = null;
        defaultBanksByDocTypeMap = null;
        checkReconBaiTypes = null;
        excludeBaiTypes = null;
        excludedByBaiAndAccount = null;
        specialBaiTypes = null;
        excludeCustomerRefNbr = null;
        documentTypeBaiMap = null;
        debitCreditMap = null;
        allowedBaiTypes = null;
        descriptionSearchChartBankObjectCodeMap = null;
        objectCodeForDefaultAD = null;
        baiChartBankObjectCodeMap = null;

        bofaDelimiter = null;
        bofaCustRefNumPattern = null;
        bofaNotesPattern = null;
        bofaCustRefNumLength = null;
        bofaCustRefNumRegexFormat = null;
        bofaCustRefNumGroupIndex = null;
        bofaNotesIndicator = null;
        bofaNotesRegexFormat = null;
        bofaCcAccountNumbers = null;
        bofaCcBaiTypeCodes = null;

        vaDelimiter = null;
        vaAccountNumbers = null;
        vaBaiTypeCodes = null;
        vaCustRefNumSet = null;
        vaNotesSixRegex = null;
        vaNotesSevenRegex = null;
        vaNotesSevenMaskingRegex = null;
        vaNotesSixPattern = null;
        vaNotesSevenPattern = null;
        vaNotesSevenMaskingPattern = null;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBankFileDelimiter()
     */
    public char getBankFileDelimiter() {
        if (Character.MIN_VALUE == bankTFileDelimiter) {
            String delimiter = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.BANK_TFILE_DELIMITER);
            bankTFileDelimiter = delimiter.trim().charAt(0);
        }
        return bankTFileDelimiter;
    }


    /**
     * TODO: field names are stored in KFSConstants, use them to build the map keys
     *
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getFieldPositions()
     */
    public Map<String, Integer> getFieldPositions() {
        if (bankTFileFieldMap == null) {
            bankTFileFieldMap = new HashMap<String, Integer>();
            String fieldPositions = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.BANK_TFILE_FIELD_POSITION);
            String[] keyPairs = fieldPositions.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            for (String keyValues : keyPairs) {
                String fieldName = keyValues.split(EQUAL)[0];
                Integer fieldPosition = Integer.parseInt(keyValues.split(EQUAL)[1]);
                bankTFileFieldMap.put(fieldName, fieldPosition);
            }
        }
        return bankTFileFieldMap;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getFieldPosition(String)
     */
    public int getFieldPosition(String fieldName) {
        return getFieldPositions().get(fieldName);
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getDefaultBank()
     */
    public String getDefaultBank() {
        if (defaultBank == null) {
            defaultBank = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.DEFAULT_BANK);
        }
        return defaultBank;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getKeyChartOfElectronicFunds()
     */
    public String getKeyChartOfElectronicFunds() {
        if (keyChartOfElectronicFunds == null) {
            setElectronicFundsAccounts();
        }
        return keyChartOfElectronicFunds;

    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getKeyAccountOfElectronicFunds()
     */
    public String getKeyAccountOfElectronicFunds() {
        if (keyAccountOfElectronicFunds == null) {
            setElectronicFundsAccounts();
        }
        return keyAccountOfElectronicFunds;
    }


    protected void setElectronicFundsAccounts() {
        String electronicFundsAccounts = parameterService.getParameterValueAsString(AdvanceDepositDocument.class, KFSConstants.BankTransactionsParameters.ELECTRONIC_FUNDS_ACCOUNTS);
        keyChartOfElectronicFunds = electronicFundsAccounts.split(EQUAL)[0];
        keyAccountOfElectronicFunds = electronicFundsAccounts.split(EQUAL)[1];
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getDefaultBankByDocType(KFSConstants.BankTransactionDocumentType)
     */
    public String getDefaultBankByDocType(KFSConstants.BankTransactionDocumentType docType ){
        return getDefaultBanks().get(docType.name());
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getDefaultBankByDocType(String)
     */
    public String getDefaultBankByDocType(String docType) {
        return getDefaultBanks().get(docType);
    }


    protected Map<String, String> getDefaultBanks() {
        if (defaultBanksByDocTypeMap == null) {
            defaultBanksByDocTypeMap = new HashMap<String, String>();
            String defaultBanksByDocTypeParam = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.DEFAULT_BANK_BY_DOCUMENT_TYPE);
            String[] keyPairs = defaultBanksByDocTypeParam.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            for (String keyValues : keyPairs) {
                String[] token = keyValues.split(EQUAL);
                String docType = token[0];
                String defaultBank = token[1];
                defaultBanksByDocTypeMap.put(docType, defaultBank);
            }
        }
        return defaultBanksByDocTypeMap;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getCheckReconBaiTypes()
     */
    public List<Integer> getCheckReconBaiTypes() {
        if (checkReconBaiTypes == null) {
            checkReconBaiTypes = new ArrayList<Integer>();
            String checkReconBaiTypesParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.CHECK_RECONCILIATION_BAI_TYPE_CODES);
            String[] crBaiTypesLocal = checkReconBaiTypesParm.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            for (String str : crBaiTypesLocal) {
                checkReconBaiTypes.add(Integer.parseInt(str));
            }
        }
        return checkReconBaiTypes;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getExcludedBaiTypes()
     */
    public List<Integer> getExcludedBaiTypes() {
        if (excludeBaiTypes == null) {
            excludeBaiTypes = new ArrayList<Integer>();
            String excludeBaiTypesParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.EXCLUDE_BAI_TYPE_CODES);
            String[] excludeBaiTypesList = excludeBaiTypesParm.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            for (String excludedBai : excludeBaiTypesList) {
                excludeBaiTypes.add(Integer.parseInt(excludedBai));
            }
        }
        return excludeBaiTypes;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getExcludedByBaiAndAccount()
     */
    public List<String> getExcludedByBaiAndAccount() {
        if (excludedByBaiAndAccount == null) {
            excludedByBaiAndAccount = new ArrayList<String>();
            String excludedByBaiAcctParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.EXCLUDE_BY_BAI_TYPE_AND_ACCOUNT);
            String[] excludedByBaiAcctList = excludedByBaiAcctParm.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            excludedByBaiAndAccount = Arrays.asList(excludedByBaiAcctList);
        }
        return excludedByBaiAndAccount;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getExcludedCustomerRefNbr()
     */
    public List<String> getExcludedCustomerRefNbr() {
        if (excludeCustomerRefNbr == null) {
            excludeCustomerRefNbr = new ArrayList<String>();
            String excludeCustomerRefNbrParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, "EXCLUDE_BY_CUSTOMER_REFERENCE_NUMBER");
            String[] excludeCustomerRefNbrList = excludeCustomerRefNbrParm.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            excludeCustomerRefNbr = Arrays.asList(excludeCustomerRefNbrList);
        }
        return excludeCustomerRefNbr;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getSpecialBaiTypes()
     */
    public List<Integer> getSpecialBaiTypes() {
        if (specialBaiTypes == null) {
            populateChartBankObjectCodeMap();
        }
        return specialBaiTypes;
    }


    public Map<String, ChartBankObjectCode> getBaiChartBankObjectCodeMap() {
        if (baiChartBankObjectCodeMap == null) {
            populateChartBankObjectCodeMap();
        }
        return baiChartBankObjectCodeMap;
    }


    /*
     * Local method that populates both specialBaiTypes and baiChartBankObjectCodeMap from the Bank Parameter: ACCOUNT_AND_OBJECT_CODE_BY_BAI_AND_BANK_ACCOUNT
     */
    protected void populateChartBankObjectCodeMap() {
        specialBaiTypes = new ArrayList<Integer>();
        baiChartBankObjectCodeMap = new HashMap<String, ChartBankObjectCode>();

        String accountObjCdByBaiTypeParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.ACCOUNT_AND_OBJECT_CODE_BY_BAI_AND_BANK_ACCOUNT);
        String[] accountObjCdByBaiTypeList = accountObjCdByBaiTypeParm.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);

        for (String currentAccountObjCd : accountObjCdByBaiTypeList) {
            String[] tokens = currentAccountObjCd.split(EQUAL);
            String[] keyCombo = tokens[0].split(COLON);
            Integer specialBaiType = Integer.parseInt(keyCombo[0]);
            specialBaiTypes.add(specialBaiType);

            String key = tokens[0];
            String value = tokens[1];
            String[] valueTokens = value.split(COLON);
            ChartBankObjectCode cbo = new ChartBankObjectCode();
            cbo.setChartCode(valueTokens[0]);
            cbo.setAccountNumber(valueTokens[1]);
            cbo.setObjectCode(valueTokens[2]);

            baiChartBankObjectCodeMap.put(key, cbo);
        }
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getChartBankObjectCodeForSpecialBai
     */
    public ChartBankObjectCode getChartBankObjectCodeForSpecialBai(Long bai, Long bankAccountNumber) {
        String key = bai.toString() + COLON + bankAccountNumber.toString();
        if (getBaiChartBankObjectCodeMap().containsKey(key)) {
            return getBaiChartBankObjectCodeMap().get(key);
        }
        return null;
    }


    protected Map<Integer, String> getBaiMapOfDocTypes() {
        if (documentTypeBaiMap == null) {
            documentTypeBaiMap = new HashMap<Integer, String>();
            String documentTypeBaiParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.DOCUMENT_TYPE_BY_BAI_TYPE_CODE);
            String[] documentTypeBaiList = documentTypeBaiParm.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            for (String documentTypeBaiStr : documentTypeBaiList) {
                String[] tokens = documentTypeBaiStr.split(EQUAL);
                String docTypeValue = tokens[0];
                String values = tokens[1];
                String[] valueTokens = values.split(COMMA);
                for (String str : valueTokens) {
                    Integer baiTypeKey = Integer.parseInt(str);
                    documentTypeBaiMap.put(baiTypeKey, docTypeValue);
                }
            }
        }
        return documentTypeBaiMap;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getDocumentTypeByBai(int)
     */
    public String getDocumentTypeByBai(int bai) {
        return getBaiMapOfDocTypes().get(bai);
    }


    protected Map<Integer, String> getDebitCreditByBaiType() {
        if (debitCreditMap == null) {
            debitCreditMap = new HashMap<Integer, String>();
            String debitCredityByBaiParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.DEBIT_CREDIT_BY_BAI_TYPE_CODE);
            String[] debitCredityByBaiList = debitCredityByBaiParm.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            for (String debitCreditStr : debitCredityByBaiList) {
                String[] tokens = debitCreditStr.split(EQUAL);
                String debitOrCredit = tokens[0];
                String values = tokens[1];
                String[] valueTokens = values.split(COMMA);
                for (String str : valueTokens) {
                    Integer baiTypeKey = Integer.parseInt(str);
                    debitCreditMap.put(baiTypeKey, debitOrCredit);
                }
            }
        }
        return debitCreditMap;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getDebitCreditByBai(int)
     */
    public String getDebitCreditByBai(int baiType) {
        return getDebitCreditByBaiType().get(baiType);
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#isDebitByBai(int)
     */
    public boolean isDebitByBai(int baiType) {
        String debitOrCredit = getDebitCreditByBai(baiType);
        if (debitOrCredit.trim().equalsIgnoreCase("D")) {
            return true;
        }
        //assume credit by default
        return false;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getAllowedBaiTypes()
     */
    public Set<Integer> getAllowedBaiTypes() {
        if (allowedBaiTypes == null) {
            allowedBaiTypes = new HashSet<Integer>();
            allowedBaiTypes.addAll(getCheckReconBaiTypes());
            allowedBaiTypes.addAll(getExcludedBaiTypes());
            allowedBaiTypes.addAll(getBaiMapOfDocTypes().keySet());
            allowedBaiTypes.addAll(getSpecialBaiTypes());
        }
        return allowedBaiTypes;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#isCheckReconBai(int)
     */
    public boolean isCheckReconBai(int bai) {
        return getCheckReconBaiTypes().contains(bai);
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#isExcludedBai(int)
     */
    public boolean isExcludedBai(int bai) {
        return getExcludedBaiTypes().contains(bai);
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#isExcludedCustomerRefNbr(String)
     */
    public boolean isExcludedCustomerRefNbr(String custRefNumb) {
        return getExcludedCustomerRefNbr().contains(custRefNumb);
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#isExcludedByBaiAndAccount(int, String)
     */
    public boolean isExcludedByBaiAndAccount(int bai, String account) {
        return getExcludedByBaiAndAccount().contains(bai + COLON + account);
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#isSpecialBai(int)
     */
    public boolean isSpecialBai(int bai) {
        return getSpecialBaiTypes().contains(bai);
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#isAllowedBai(int)
     */
    public boolean isAllowedBai(int bai) {
        return getAllowedBaiTypes().contains(bai);
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getChartBankObjectCodeForDescriptionSearch(String)
     */
    public ChartBankObjectCode getChartBankObjectCodeForDescriptionSearch(String description) {

        for (String descKey : getDescriptionSearchChartBankObjectCodeMap().keySet()) {
            if (description.contains(descKey)) {
                return descriptionSearchChartBankObjectCodeMap.get(descKey);
            }
        }
        //not found, returning null;
        return null;
    }

    /*
     * Internal method that populates a Map with description as key and ChartBankObjectCode as values from the ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION parameter
     */
    protected Map<String, ChartBankObjectCode> getDescriptionSearchChartBankObjectCodeMap() {
        if (descriptionSearchChartBankObjectCodeMap == null) {
            descriptionSearchChartBankObjectCodeMap = new HashMap<String, ChartBankObjectCode>();
            String accountObjByDescrParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION);
            if ( StringUtils.isEmpty( accountObjByDescrParm )) {
                LOG.error("Parameter [ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION] has an empty value.  The process cannot be continued without this value.");
                throw new RuntimeException("Parameter [ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION] has an empty value.  The process cannot be continued without this value.");
            }
            String[] accountObjByDescrList = accountObjByDescrParm.split(KFSConstants.BankTransactionsParameters.BANK_PARAMETER_DELIM);
            if ( accountObjByDescrList.length == 0) {
                LOG.error("Parameter [ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION] could not be parsed.  The process cannot be continued without this value.");
                throw new RuntimeException("Parameter [ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION] could not be parsed.  The process cannot be continued without this value.");
            }

            int index = 0;
            for (String chartBankObjectCodeMapStr : accountObjByDescrList) {
                String[] tokens = chartBankObjectCodeMapStr.split(EQUAL);
                if ( tokens.length != 2) {
                    LOG.error("Parameter [ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION] value [" + index + "] has a misconfigured value.  Expecting a string delimeted by equals-sign, got none.");
                    throw new RuntimeException("Parameter [ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION] value [" + index + "] has a misconfigured value.  Expecting a string delimeted by equals-sign, got none.");
                }
                String descrKey = tokens[0];
                String value = tokens[1];
                ChartBankObjectCode cbo = new ChartBankObjectCode();
                String[] valueTokens = value.split(COLON);
                if ( valueTokens.length != 3) {
                    LOG.error("Parameter [ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION] value [" + index + "] has a misconfigured value.  Expecting a 3-part string delimited by a colon [:], got none.");
                    throw new RuntimeException("Parameter [ACCOUNT_AND_OBJECT_CODE_BY_DESCRIPTION] value [" + index + "] has a misconfigured value.  Expecting a 3-part string delimited by a colon [:], got none.");
                }
                cbo.setChartCode(valueTokens[0]);
                cbo.setAccountNumber(valueTokens[1]);
                cbo.setObjectCode(valueTokens[2]);
                descriptionSearchChartBankObjectCodeMap.put(descrKey, cbo);
                index++;
            }
        }
        return descriptionSearchChartBankObjectCodeMap;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getObjectCodeForDefaultAD()
     */
    public String getObjectCodeForDefaultAD() {
        if (objectCodeForDefaultAD == null) {
            objectCodeForDefaultAD = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.DEFAULT_AD_OBJECT_CODE);
        }
        return objectCodeForDefaultAD;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getCheckReconClearedStatusCode()
     */
    public String getCheckReconClearedStatusCode() {
        if ( checkReconStatus == null) {
            checkReconStatus = parameterService.getParameterValueAsString(KFSConstants.BankTransactionsParameters.CR_STATUS_NAMESPACE, KFSConstants.BankTransactionsParameters.CR_STATUS_COMPONENT_CODE, KFSConstants.BankTransactionsParameters.CR_STATUS_CLEARED_CODES);
        }
        return checkReconStatus;
    }


    /*--------------------------------- Bank of America specific parameters -----------------------------*/

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaDelimiter()
     */
    public String getBofaDelimiter() {
        if (bofaDelimiter == null) {
            String delimiter = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.BOFA_PARM_DELIMITER);
            bofaDelimiter = delimiter.trim();
        }
        return bofaDelimiter;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaCustRefNumPattern()
     */
    public Pattern getBofaCustRefNumPattern() {
        if (bofaCustRefNumPattern == null) {
            String custRefNumRegexString = String.format(getBofaCustRefNumRegexFormat(), getBofaCustRefNumLength());
            bofaCustRefNumPattern = Pattern.compile(custRefNumRegexString);
        }
        return bofaCustRefNumPattern;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaNotesPattern()
     */
    public Pattern getBofaNotesPattern() {
        if (bofaNotesPattern == null) {
            String bofaNotesRegex = String.format(getBofaNotesRegexFormat(), getBofaNotesIndicator());
            bofaNotesPattern = Pattern.compile(bofaNotesRegex);
        }
        return bofaNotesPattern;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaCustRefNumLength()
     */
    public Integer getBofaCustRefNumLength() {
        if (bofaCustRefNumLength == null) {
            String bofaCustRefNumLengthParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.BOFA_CUST_REF_NUM_LENGTH);
            bofaCustRefNumLength = Integer.parseInt(bofaCustRefNumLengthParm);
        }
        return bofaCustRefNumLength;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaCustRefNumRegexFormat()
     */
    public String getBofaCustRefNumRegexFormat() {
        if (bofaCustRefNumRegexFormat == null) {
            bofaCustRefNumRegexFormat = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.BOFA_CUST_REF_NUM_REGEX_FORMAT);
        }
        return bofaCustRefNumRegexFormat;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaCustRefNumGroupIndex()
     */
    public Integer getBofaCustRefNumGroupIndex() {
        if (bofaCustRefNumGroupIndex == null) {
            String bofaCustRefNumGroupIndexParm = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.BOFA_CUST_REF_NUM_GROUP_INDEX);
            bofaCustRefNumGroupIndex = Integer.parseInt(bofaCustRefNumGroupIndexParm);
        }
        return bofaCustRefNumGroupIndex;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaNotesRegexFormat()
     */
    public String getBofaNotesRegexFormat() {
        if (bofaNotesRegexFormat == null) {
            bofaNotesRegexFormat = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.BOFA_NOTES_REGEX_FORMAT);
        }
        return bofaNotesRegexFormat;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaNotesIndicator()
     */
    public String getBofaNotesIndicator() {
        if (bofaNotesIndicator == null) {
            bofaNotesIndicator = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.BOFA_NOTES_INDICATOR);
        }
        return bofaNotesIndicator;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaCcAccountNumbers()
     */
    public Set<String> getBofaCcAccountNumbers() {
        if (bofaCcAccountNumbers == null) {
            bofaCcAccountNumbers = getValueSetForParameter(KFSConstants.BankTransactionsParameters.BOFA_CC_ACCOUNT_NUM_SET, getBofaDelimiter());
        }
        return bofaCcAccountNumbers;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getBofaCcBaiTypeCodes()
     */
    public Set<String> getBofaCcBaiTypeCodes() {
        if (bofaCcBaiTypeCodes == null) {
            bofaCcBaiTypeCodes = getValueSetForParameter(KFSConstants.BankTransactionsParameters.BOFA_CC_BAI_TYPE_CODE_SET, getBofaDelimiter());
        }
        return bofaCcBaiTypeCodes;
    }


    /*------------------------------- VA specific parameters -----------------------------*/

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getVaDelimiter()
     */
    public String getVaDelimiter() {
        if (vaDelimiter == null) {
            String delimiter = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.VA_PARAM_DELIMITER);
            vaDelimiter = delimiter.trim();
        }
        return vaDelimiter;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getVaAccountNumbers()
     */
    public Set<String> getVaAccountNumbers() {
        if (vaAccountNumbers == null) {
            vaAccountNumbers = getValueSetForParameter(KFSConstants.BankTransactionsParameters.VA_ACCOUNT_NUM_SET, getVaDelimiter());
        }
        return vaAccountNumbers;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getVaBaiTypeCodes()
     */
    public Set<String> getVaBaiTypeCodes() {
        if (vaBaiTypeCodes == null) {
            vaBaiTypeCodes = getValueSetForParameter(KFSConstants.BankTransactionsParameters.VA_BAI_TYPE_CODE_SET, getVaDelimiter());
        }
        return vaBaiTypeCodes;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getVaCustRefNumSet()
     */
    public Set<String> getVaCustRefNumSet() {
        if (vaCustRefNumSet == null) {
            vaCustRefNumSet = getValueSetForParameter(KFSConstants.BankTransactionsParameters.VA_CUST_REF_NUM_SET, getVaDelimiter());
        }
        return vaCustRefNumSet;
    }

    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getVaNotesSixRegex()
     */
    public String getVaNotesSixRegex() {
        if (vaNotesSixRegex == null) {
            vaNotesSixRegex = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.VA_NOTES_SIX_REGEX);
        }
        return vaNotesSixRegex;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getVaNotesSixPattern()
     */
    public Pattern getVaNotesSixPattern() {
        if (vaNotesSixPattern == null) {
            vaNotesSixPattern = Pattern.compile(getVaNotesSixRegex());
        }
        return vaNotesSixPattern;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getVaNotesSevenPattern()
     */
    public Pattern getVaNotesSevenPattern() {
        if (vaNotesSevenPattern == null) {
            vaNotesSevenPattern = Pattern.compile(getVaNotesSevenRegex());
        }
        return vaNotesSevenPattern;
    }


    /**
     * @see edu.arizona.kfs.fp.batch.service.BankParametersAccessService#getVaNotesSevenMaskingPattern()
     */
    public Pattern getVaNotesSevenMaskingPattern() {
        if (vaNotesSevenMaskingPattern == null) {
            vaNotesSevenMaskingPattern = Pattern.compile(getVaNotesSevenMaskingRegex());
        }
        return vaNotesSevenMaskingPattern;
    }


    public String getVaNotesSevenRegex() {
        if (vaNotesSevenRegex == null) {
            vaNotesSevenRegex = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.VA_NOTES_SEVEN_REGEX);
        }
        return vaNotesSevenRegex;
    }

    public String getVaNotesSevenMaskingRegex() {
        if (vaNotesSevenMaskingRegex == null) {
            vaNotesSevenMaskingRegex = parameterService.getParameterValueAsString(DocumentCreationStep.class, KFSConstants.BankTransactionsParameters.VA_NOTES_SEVEN_MASKING_REGEX);
        }
        return vaNotesSevenMaskingRegex;
    }


    /*
   * Several of the parameters are delimited strings that should be
   * placed into a data structure. A set was chosen, since all of
   * the pulled values within one parm should be unique. Also, this
   * allows us to use Set.contains(...) rather than looping through
   * ourselves.
   */
    private Set<String> getValueSetForParameter(String parameterKey, String delimiter) {
        String parameter = parameterService.getParameterValueAsString(DocumentCreationStep.class, parameterKey);
        String[] listAsArray = parameter.split(delimiter);
        Set<String> resultSet = new HashSet<String>();
        Collections.addAll(resultSet, listAsArray);

        return resultSet;
    }

}
