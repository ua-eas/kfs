package edu.arizona.kfs.fp.batch.dataaccess.impl;

import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * This class converts a BankTransaction object into a row of data for the Check Reconciliation file
 * depending on the field order from the BANK_TFILE_FIELD_POSIITON parameter
 * <p>
 * Created by nataliac on 5/10/17.
 */
public class CheckReconciliationAdapter {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationAdapter.class);

    /**
     * Convert the Bank Transaction into a row of data for Check Recon file
     *
     * @param Map<String, Integer> fieldPositions - map of string to determine the order of fields in the data row
     * @return freshly built BankTransactionBO
     */
    public static String[] buildCheckReconRecord(BankTransaction bankTransaction, Map<String, Integer> fieldPositions) {
        String[] checkReconRowData = new String[8];
        checkReconRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.ACCOUNT_NUMBER) ] = bankTransaction.getAccountNumber().toString();
        checkReconRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.BAI_TYPE) ] = bankTransaction.getBaiType().toString();
        checkReconRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.AMOUNT) ] = bankTransaction.getAmount().abs().toString();
        checkReconRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.CUST_REF_NO) ] = bankTransaction.getCustRefNo();
        checkReconRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.VALUE_DATE) ] = convertValueDate(bankTransaction.getValueDate());
        checkReconRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.BANK_REFERENCE) ] = bankTransaction.getBankReference();
        checkReconRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.DESC_TEXT6) ] = bankTransaction.getDescriptiveTxt6();
        checkReconRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.DESC_TEXT7) ] = bankTransaction.getDescriptiveTxt7();

        return checkReconRowData;
    }

    private static String convertValueDate(java.sql.Date date){
       return DateFormatUtils.format(date.getTime(), KFSConstants.BankTransactionConstants.TFILE_DATE_FORMAT);
    }

}
