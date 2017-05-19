package edu.arizona.kfs.fp.batch.dataaccess.impl;

import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * This class converts a BankTranactionDigesterVO object to a standard BankTransaction object.
 * <p>
 * Created by nataliac on 4/6/17.
 */
public class BankTransactionDigesterAdapter {

    private DateTimeService dateTimeService;
    private BankParametersAccessService bankParametersAccessService;


    /**
     * Converts a BankTranactionDigesterVO to a real BankTransaction BO.
     * Validates all fields depending on types and adds conversion errors to the errorList
     *
     * @param bankTransactionVO The VO full of String values to convert from.
     * @param errorList         An list of errors to add new conversion errors to
     * @return A populated BankTransaction object, from the VO.
     */
    public BankTransaction convert(BankTransactionDigesterVO bankTransactionVO, List<String> errorList) {
        BankTransaction bt = new BankTransaction();

        //parse account number into a Long
        bt.setAccountNumber(convertAccountNumber(bankTransactionVO, errorList));

        //parse BAI Type into a Long
        bt.setBaiType(convertBaiType(bankTransactionVO, errorList));

        //parse Amount into a Kuali Decimal
        bt.setAmount(convertAmount(bankTransactionVO, bt.getBaiType(), errorList));

        //parse Customer Reference Number
        bt.setCustRefNo(convertCustomerRefNum(bankTransactionVO, errorList));

        bt.setBankReference(bankTransactionVO.getBankReference());
        bt.setDescriptiveTxt6(bankTransactionVO.getDescriptiveTxt6());

        //if this is a VA record, this will Mask the "VA Case File Number"
        bt.setDescriptiveTxt7(convertDescriptiveText7(bankTransactionVO, errorList));

        //parse Value Date
        bt.setValueDate(convertValueDate(bankTransactionVO, errorList));

        return bt;
    }


    private Long convertAccountNumber(BankTransactionDigesterVO bankTransactionVO, List<String> errorList) {
        Long accountNumber = new Long(0);
        if (StringUtils.isNotEmpty(bankTransactionVO.getAccountNumber())) {
            try {
                accountNumber = Long.parseLong(bankTransactionVO.getAccountNumber());
            } catch (NumberFormatException e) {
                errorList.add("ERROR: Account number is non numeric: " + bankTransactionVO.getAccountNumber());
            }
        } else {
            errorList.add("ERROR: missing Account Number :" + bankTransactionVO.toString());
        }
        return accountNumber;
    }

    private Long convertBaiType(BankTransactionDigesterVO bankTransactionVO, List<String> errorList) {
        Long baiType = new Long(0);
        if (StringUtils.isNotEmpty(bankTransactionVO.getBaiType())) {
            try {
                baiType = Long.parseLong(bankTransactionVO.getBaiType());
            } catch (NumberFormatException e) {
                errorList.add("ERROR: Bai Type is non numeric: " + bankTransactionVO.getBaiType());
            }
        } else {
            errorList.add("ERROR: missing Bai Type :" + bankTransactionVO.toString());
        }
        return baiType;
    }

    private KualiDecimal convertAmount(BankTransactionDigesterVO bankTransactionVO, Long baiType, List<String> errorList) {
        KualiDecimal amount = KualiDecimal.ZERO;
        if (StringUtils.isNotEmpty(bankTransactionVO.getAmount())) {
            try {
                //insuring .00 precision by multiplication
                amount = new KualiDecimal(bankTransactionVO.getAmount()).multiply(new KualiDecimal(0.01));
                try {
                    //if is Debit, should be Negative amount
                    if (getBankParametersAccessService().isDebitByBai(baiType.intValue())) {
                        amount = amount.multiply(new KualiDecimal(-1));
                    }
                } catch (Exception e) {
                    errorList.add("ERROR: BAI Type Code of [" + baiType + "] is not set up in the DEBIT_CREDIT_BY_BAI_TYPE_CODE parameter.");
                }
            } catch (Exception e) {
                errorList.add("ERROR: Not a valid transaction Amount : " + bankTransactionVO.getAmount());
            }
        } else {
            errorList.add("ERROR: missing AMOUNT :" + bankTransactionVO.toString());
        }

        return amount;
    }

    /*
     *  Bank of America special rule:
     *  If the accountNbr,BaiType and custRefNum indicate this is a BofA record,
     *  then the "Customer Reference Number" is extracted from the description 6 field.
     *  For all other CC types, the original custRefNum is returned back.
     */
    private String convertCustomerRefNum(BankTransactionDigesterVO bankTransactionVO, List<String> errorList) {
        String custRefNum = null;
        if (StringUtils.isNotEmpty(bankTransactionVO.getCustRefNo())) {
            custRefNum = bankTransactionVO.getCustRefNo();
            try {
                // If the fields passed in indicate this is a BofA record, then the "Customer Reference Number"
                // is extracted from the notes field. For all other CC types, the original custRefNun is returned back.
                BofaCcRecordChecker bofaRecordChecker = new BofaCcRecordChecker(bankTransactionVO.getAccountNumber(), bankTransactionVO.getBaiType(), custRefNum, bankTransactionVO.getDescriptiveTxt6());
                if (bofaRecordChecker.isBofaCreditCardRecord()) {
                    custRefNum = bofaRecordChecker.getCustRefNum();
                    if (StringUtils.isNotEmpty(custRefNum)) {
                        //something went wrong with the identification of BofA customer reference number
                        errorList.add("ERROR: Could not parse Bank of America Customer Reference Number :" + bankTransactionVO.toString());
                        return null;
                    }
                }

                // All CustRefNumbers must be ten digits EXACTLY - if longer, truncate to first ten digits which is all that KFS needs
                if (custRefNum.length() > 10) {
                    custRefNum = custRefNum.substring(custRefNum.length() - 10);
                } else if (custRefNum.length() != 10) {
                    // If less than 10 digits, indicate an error.
                    errorList.add("ERROR: VendorNumber of " + custRefNum + " is less than 10 digits:" + bankTransactionVO.toString());
                }

            } catch (Exception e) {
                errorList.add("ERROR: Could not extract BofA customer reference nbr: " + e.getMessage());
            }

        } else {
            errorList.add("ERROR: missing Customer Reference Number :" + bankTransactionVO.toString());
        }

        return custRefNum;
    }


    /**
     * If the fields of this record follow the defintions set forth by FSO and
     * encoded in Bank parms, then "Notes Field Seven" will have its
     * "VA Case File Number" component masked and the modified field returned.
     * <p>
     * If the conditions indicate this is not a VA record, then the original
     * notesFieldSeven is returned, unmodified.
     */
    private String convertDescriptiveText7(BankTransactionDigesterVO bankTransactionVO, List<String> errorList) {
        String descText7 = null;

        try {
            // Mask the "VA Case File Number" if this is a VA record, otherwise no-op assign original value back in
            VaRecordNotesMasker vaNotesMasker = new VaRecordNotesMasker(bankTransactionVO.getAccountNumber(), bankTransactionVO.getBaiType(), bankTransactionVO.getCustRefNo(), bankTransactionVO.getDescriptiveTxt6(), bankTransactionVO.getDescriptiveTxt7());

            descText7 = vaNotesMasker.getAppropriateNotesFieldSeven();
        } catch (Exception e) {
            errorList.add("ERROR:" + e.getMessage() + " When trying to mask description field 7 for VA:" + bankTransactionVO.toString());
        }

        return descText7;
    }


    private Date convertValueDate(BankTransactionDigesterVO bankTransactionVO, List<String> errorList) {
        java.sql.Date date = null;
        if (StringUtils.isNotEmpty(bankTransactionVO.getAccountNumber())) {
            SimpleDateFormat df = new SimpleDateFormat(KFSConstants.BankTransactionConstants.TFILE_DATE_FORMAT);
            try {
                java.util.Date dt = df.parse(bankTransactionVO.getValueDate());
                date = new java.sql.Date(dt.getTime());
            } catch (ParseException e) {
                errorList.add("ERROR: [" + bankTransactionVO.getValueDate() + "] is not a valid Date. ");
            }
        } else {
            errorList.add("ERROR: missing Value Date :" + bankTransactionVO.toString());
        }
        return date;
    }


    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    public BankParametersAccessService getBankParametersAccessService() {
        if (bankParametersAccessService == null) {
            bankParametersAccessService = SpringContext.getBean(BankParametersAccessService.class);
        }
        return bankParametersAccessService;
    }
}