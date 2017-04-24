package edu.arizona.kfs.fp.businessobject;

/**
 * Non persistable POJO that encapsulates the Bank Chart, Account and Object code for Bank Transaction Loading and Posting
 *
 * Created by nataliac on 4/5/17.
 */
public class ChartBankObjectCode {
    private String chartCode;
    private String accountNumber;
    private String objectCode;

    public String getChartCode() {
        return chartCode;
    }

    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

}
