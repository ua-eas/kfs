package edu.arizona.kfs.fp.batch.dataaccess.impl;

import edu.arizona.kfs.sys.KFSConstants;

import java.util.Map;


/**
 * CSVBuilder convert one data row from the Bank Transaction file into a into a temporary BankTransactionVO,
 * that will ulteriorly be validated and converted into a legitimate BankTransaction object
 *
 * Created by nataliac on 4/2/17.
 */
public class BankTransactionCSVBuilder {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankTransactionCSVBuilder.class);

    /**
     * Convert the row of data from Bank Transaction File into a temporary BankTransactionVO object
     *
     * Tech Note: field order is stored in the BANK_TFILE_FIELD_POSIITON parameter
     *
     * @param String[] currentRowData from bank file
     * @param Map<String, Integer> fieldPositions - map of string to determine the order of fields in the data row
     * @return freshly built BankTransactionBO
     */
    public static BankTransactionDigesterVO buildBankTransactionVO(String[] currentRowData, Map<String, Integer> fieldPositions) {
        BankTransactionDigesterVO btv = new BankTransactionDigesterVO();
        btv.setAccountNumber( currentRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.ACCOUNT_NUMBER) ] );
        btv.setBaiType( currentRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.BAI_TYPE) ] );
        btv.setAmount( currentRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.AMOUNT) ] );
        btv.setCustRefNo( currentRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.CUST_REF_NO) ] );
        btv.setValueDate( currentRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.VALUE_DATE) ] );
        btv.setBankReference( currentRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.BANK_REFERENCE) ] );
        btv.setDescriptiveTxt6( currentRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.DESC_TEXT6) ].toUpperCase() );
        btv.setDescriptiveTxt7( currentRowData[ fieldPositions.get(KFSConstants.BankTransactionFields.DESC_TEXT7) ].toUpperCase() );

        return btv;
    }
}
