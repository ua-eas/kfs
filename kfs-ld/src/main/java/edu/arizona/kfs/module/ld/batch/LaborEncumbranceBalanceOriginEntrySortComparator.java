package edu.arizona.kfs.module.ld.batch;

import java.util.Comparator;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LaborOriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * Special sort comparator used for sorting the input origin entry file.  It sorts by the same
 * keys as the generated balance file. 
 */
public class LaborEncumbranceBalanceOriginEntrySortComparator implements Comparator<String> {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborEncumbranceBalanceOriginEntrySortComparator.class);
    
    protected LaborOriginEntryFieldUtil loefu = new LaborOriginEntryFieldUtil();
    protected Map<String, Integer> pMap = loefu.getFieldBeginningPositionMap();
    protected Map<String, Integer> lMap = loefu.getFieldLengthMap();
    
    // Decode all of these up front to avoid the decoding of the information on
    // *every* comparison, which could happen quite a number of times
    protected int FIN_BALANCE_TYP_CD_POS = pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
    protected int FIN_BALANCE_TYP_CD_LEN = lMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
    protected int EMPLID_POS = pMap.get(KFSPropertyConstants.EMPLID);
    protected int EMPLID_LEN = lMap.get(KFSPropertyConstants.EMPLID);
    protected int POSITION_NUMBER_POS = pMap.get(KFSPropertyConstants.POSITION_NUMBER);
    protected int POSITION_NUMBER_LEN = lMap.get(KFSPropertyConstants.POSITION_NUMBER);
    protected int CHART_OF_ACCOUNTS_CODE_POS = pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
    protected int CHART_OF_ACCOUNTS_CODE_LEN = lMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
    protected int ACCOUNT_NUMBER_POS = pMap.get(KFSPropertyConstants.ACCOUNT_NUMBER);
    protected int ACCOUNT_NUMBER_LEN = lMap.get(KFSPropertyConstants.ACCOUNT_NUMBER);
    protected int SUB_ACCOUNT_NUMBER_POS = pMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
    protected int SUB_ACCOUNT_NUMBER_LEN = lMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
    protected int FINANCIAL_OBJECT_TYPE_CODE_POS = pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
    protected int FINANCIAL_OBJECT_TYPE_CODE_LEN = lMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
    protected int FINANCIAL_OBJECT_CODE_POS = pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    protected int FINANCIAL_OBJECT_CODE_LEN = lMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    protected int FINANCIAL_SUB_OBJECT_CODE_POS = pMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
    protected int FINANCIAL_SUB_OBJECT_CODE_LEN = lMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

    // set up the needed string buffer for use by this sort
    protected StringBuffer sb = new StringBuffer(FIN_BALANCE_TYP_CD_LEN+EMPLID_LEN+POSITION_NUMBER_LEN+CHART_OF_ACCOUNTS_CODE_LEN
                    +ACCOUNT_NUMBER_LEN+SUB_ACCOUNT_NUMBER_LEN+FINANCIAL_OBJECT_TYPE_CODE_LEN
                    +FINANCIAL_OBJECT_CODE_LEN+FINANCIAL_SUB_OBJECT_CODE_LEN);
    
    public int compare(String originEntryLine1, String originEntryLine2) {
        sb.setLength(0); // clear out the string buffer for the next use
            
//        System.out.println( "Entry Line: " + originEntryLine1);
            
        try {
            sb.append(originEntryLine1.substring(FIN_BALANCE_TYP_CD_POS,FIN_BALANCE_TYP_CD_POS+FIN_BALANCE_TYP_CD_LEN).trim());
            sb.append(originEntryLine1.substring(EMPLID_POS,EMPLID_POS+EMPLID_LEN).trim());
            sb.append(originEntryLine1.substring(POSITION_NUMBER_POS,POSITION_NUMBER_POS+POSITION_NUMBER_LEN));
            sb.append(originEntryLine1.substring(CHART_OF_ACCOUNTS_CODE_POS,CHART_OF_ACCOUNTS_CODE_POS+CHART_OF_ACCOUNTS_CODE_LEN));
            sb.append(originEntryLine1.substring(ACCOUNT_NUMBER_POS,ACCOUNT_NUMBER_POS+ACCOUNT_NUMBER_LEN));
            sb.append(originEntryLine1.substring(SUB_ACCOUNT_NUMBER_POS,SUB_ACCOUNT_NUMBER_POS+SUB_ACCOUNT_NUMBER_LEN));
            sb.append(originEntryLine1.substring(FINANCIAL_OBJECT_CODE_POS,FINANCIAL_OBJECT_CODE_POS+FINANCIAL_OBJECT_CODE_LEN));
            sb.append(originEntryLine1.substring(FINANCIAL_SUB_OBJECT_CODE_POS,FINANCIAL_SUB_OBJECT_CODE_POS+FINANCIAL_SUB_OBJECT_CODE_LEN));
            sb.append(originEntryLine1.substring(FINANCIAL_OBJECT_TYPE_CODE_POS,FINANCIAL_OBJECT_TYPE_CODE_POS+FINANCIAL_OBJECT_TYPE_CODE_LEN));
            originEntryLine1 = sb.toString();
        } catch ( Exception ex ) {
            LOG.error("Exception while attempting to sort Encumbrance origin entry record: \n" + originEntryLine1, ex);
            return 1; // make this one sort to the end
        }
        try {
            sb.setLength(0); // clear out the string buffer for the next use
            sb.append(originEntryLine2.substring(FIN_BALANCE_TYP_CD_POS,FIN_BALANCE_TYP_CD_POS+FIN_BALANCE_TYP_CD_LEN).trim());
            sb.append(originEntryLine2.substring(EMPLID_POS,EMPLID_POS+EMPLID_LEN).trim());
            sb.append(originEntryLine2.substring(POSITION_NUMBER_POS,POSITION_NUMBER_POS+POSITION_NUMBER_LEN));
            sb.append(originEntryLine2.substring(CHART_OF_ACCOUNTS_CODE_POS,CHART_OF_ACCOUNTS_CODE_POS+CHART_OF_ACCOUNTS_CODE_LEN));
            sb.append(originEntryLine2.substring(ACCOUNT_NUMBER_POS,ACCOUNT_NUMBER_POS+ACCOUNT_NUMBER_LEN));
            sb.append(originEntryLine2.substring(SUB_ACCOUNT_NUMBER_POS,SUB_ACCOUNT_NUMBER_POS+SUB_ACCOUNT_NUMBER_LEN));
            sb.append(originEntryLine2.substring(FINANCIAL_OBJECT_CODE_POS,FINANCIAL_OBJECT_CODE_POS+FINANCIAL_OBJECT_CODE_LEN));
            sb.append(originEntryLine2.substring(FINANCIAL_SUB_OBJECT_CODE_POS,FINANCIAL_SUB_OBJECT_CODE_POS+FINANCIAL_SUB_OBJECT_CODE_LEN));
            sb.append(originEntryLine2.substring(FINANCIAL_OBJECT_TYPE_CODE_POS,FINANCIAL_OBJECT_TYPE_CODE_POS+FINANCIAL_OBJECT_TYPE_CODE_LEN));
            originEntryLine2 = sb.toString();
                
        } catch ( Exception ex ) {
            LOG.error("Exception while attempting to sort Encumbrance origin entry record: \n" + originEntryLine2, ex);
            return -1; // make the other line sort before this one
        }
        return originEntryLine1.compareTo(originEntryLine2);
    }
}