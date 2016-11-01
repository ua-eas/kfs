package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent the procurement cardholder data from the bank. It is loaded through an
 *  XML ingestion process.
 */
public class ProcurementCardHolderLoad extends PersistableBusinessObjectBase {
    private String creditCardNumber;
    private String cardHolderName;
    private String cardHolderAlternateName;
    private String cardHolderLine1Address;
    private String cardHolderLine2Address;
    private String cardHolderCityName;
    private String cardHolderStateCode;
    private String cardHolderZipCode;
    private String cardHolderWorkPhoneNumber;
    private KualiDecimal cardLimit;
    private KualiDecimal cardCycleAmountLimit;
    private Integer cardCycleVolumeLimit;
    private Integer cardMonthlyNumber;
    private String cardStatusCode;
    private String cardNoteText;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String organizationCode;
    private String cardHolderSystemId;
    private Date cardOpenDate;
    private Date cardCancelDate;
    private String cardExpireDate;
    
    /**
     * Gets the creditCardNumber attribute. 
     * @return Returns the creditCardNumber.
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Sets the creditCardNumber attribute value.
     * @param creditCardNumber The creditCardNumber to set.
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * Gets the cardHolderName attribute. 
     * @return Returns the cardHolderName.
     */
    public String getCardHolderName() {
        return cardHolderName;
    }

    /**
     * Sets the cardHolderName attribute value.
     * @param cardHolderName The cardHolderName to set.
     */
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    /**
     * Gets the cardHolderAlternateName attribute. 
     * @return Returns the cardHolderAlternateName.
     */
    public String getCardHolderAlternateName() {
        return cardHolderAlternateName;
    }

    /**
     * Sets the cardHolderAlternateName attribute value.
     * @param cardHolderAlternateName The cardHolderAlternateName to set.
     */
    public void setCardHolderAlternateName(String cardHolderAlternateName) {
        this.cardHolderAlternateName = cardHolderAlternateName;
    }

    /**
     * Gets the cardHolderLine1Address attribute. 
     * @return Returns the cardHolderLine1Address.
     */
    public String getCardHolderLine1Address() {
        return cardHolderLine1Address;
    }

    /**
     * Sets the cardHolderLine1Address attribute value.
     * @param cardHolderLine1Address The cardHolderLine1Address to set.
     */
    public void setCardHolderLine1Address(String cardHolderLine1Address) {
        this.cardHolderLine1Address = cardHolderLine1Address;
    }

    /**
     * Gets the cardHolderLine2Address attribute. 
     * @return Returns the cardHolderLine2Address.
     */
    public String getCardHolderLine2Address() {
        return cardHolderLine2Address;
    }

    /**
     * Sets the cardHolderLine2Address attribute value.
     * @param cardHolderLine2Address The cardHolderLine2Address to set.
     */
    public void setCardHolderLine2Address(String cardHolderLine2Address) {
        this.cardHolderLine2Address = cardHolderLine2Address;
    }

    /**
     * Gets the cardHolderCityName attribute. 
     * @return Returns the cardHolderCityName.
     */
    public String getCardHolderCityName() {
        return cardHolderCityName;
    }

    /**
     * Sets the cardHolderCityName attribute value.
     * @param cardHolderCityName The cardHolderCityName to set.
     */
    public void setCardHolderCityName(String cardHolderCityName) {
        this.cardHolderCityName = cardHolderCityName;
    }

    /**
     * Gets the cardHolderStateCode attribute. 
     * @return Returns the cardHolderStateCode.
     */
    public String getCardHolderStateCode() {
        return cardHolderStateCode;
    }

    /**
     * Sets the cardHolderStateCode attribute value.
     * @param cardHolderStateCode The cardHolderStateCode to set.
     */
    public void setCardHolderStateCode(String cardHolderStateCode) {
        this.cardHolderStateCode = cardHolderStateCode;
    }

    /**
     * Gets the cardHolderZipCode attribute. 
     * @return Returns the cardHolderZipCode.
     */
    public String getCardHolderZipCode() {
        return cardHolderZipCode;
    }

    /**
     * Sets the cardHolderZipCode attribute value.
     * @param cardHolderZipCode The cardHolderZipCode to set.
     */
    public void setCardHolderZipCode(String cardHolderZipCode) {
        this.cardHolderZipCode = cardHolderZipCode;
    }

    /**
     * Gets the cardHolderWorkPhoneNumber attribute. 
     * @return Returns the cardHolderWorkPhoneNumber.
     */
    public String getCardHolderWorkPhoneNumber() {
        return cardHolderWorkPhoneNumber;
    }

    /**
     * Sets the cardHolderWorkPhoneNumber attribute value.
     * @param cardHolderWorkPhoneNumber The cardHolderWorkPhoneNumber to set.
     */
    public void setCardHolderWorkPhoneNumber(String cardHolderWorkPhoneNumber) {
        this.cardHolderWorkPhoneNumber = cardHolderWorkPhoneNumber;
    }

    /**
     * Gets the cardLimit attribute. 
     * @return Returns the cardLimit.
     */
    public KualiDecimal getCardLimit() {
        return cardLimit;
    }

    /**
     * Sets the cardLimit attribute value.
     * @param cardLimit The cardLimit to set.
     */
    public void setCardLimit(KualiDecimal cardLimit) {
        this.cardLimit = cardLimit;
    }

    /**
     * Sets the cardLimit attribute value.
     * @param cardLimit The cardLimit to set.
     */
    public void setCardLimit(String cardLimit) {
        if (StringUtils.isNotBlank(cardLimit)) {
            this.cardLimit = new KualiDecimal(cardLimit);
        }
        else {
            this.cardLimit = KualiDecimal.ZERO;
        }    
    }    
    
    /**
     * Gets the cardCycleAmountLimit attribute. 
     * @return Returns the cardCycleAmountLimit.
     */
    public KualiDecimal getCardCycleAmountLimit() {
        return cardCycleAmountLimit;
    }

    /**
     * Sets the cardCycleAmountLimit attribute value.
     * @param cardCycleAmountLimit The cardCycleAmountLimit to set.
     */
    public void setCardCycleAmountLimit(KualiDecimal cardCycleAmountLimit) {
        this.cardCycleAmountLimit = cardCycleAmountLimit;
    }

    /**
     * Sets the cardCycleAmountLimit attribute value.
     * @param cardCycleAmountLimit The cardCycleAmountLimit to set.
     */
    public void setCardCycleAmountLimit(String cardCycleAmountLimit) {
        if (StringUtils.isNotBlank(cardCycleAmountLimit)) {
            this.cardCycleAmountLimit = new KualiDecimal(cardCycleAmountLimit);
        }
        else {
            this.cardCycleAmountLimit = KualiDecimal.ZERO;
        }   
    }    
    
    /**
     * Gets the cardCycleVolumeLimit attribute. 
     * @return Returns the cardCycleVolumeLimit.
     */
    public Integer getCardCycleVolumeLimit() {
        return cardCycleVolumeLimit;
    }

    /**
     * Sets the cardCycleVolumeLimit attribute.
     * 
     * @param cardCycleVolumeLimit The cardCycleVolumeLimit to set.
     */
    public void setCardCycleVolumeLimit(Integer cardCycleVolumeLimit) {
        this.cardCycleVolumeLimit = cardCycleVolumeLimit;
    }
    
    /**
     * Sets the cardCycleVolumeLimit attribute.
     * 
     * @param cardCycleVolumeLimit The cardCycleVolumeLimit to set.
     */
    public void setCardCycleVolumeLimit(String cardCycleVolumeLimit) {
        if (StringUtils.isNotBlank(cardCycleVolumeLimit)) {
            this.cardCycleVolumeLimit = new Integer(cardCycleVolumeLimit);
        }
        else {
            this.cardCycleVolumeLimit = 0;
        }   
    }      
    
    /**
     * Gets the cardMonthlyNumber attribute. 
     * @return Returns the cardMonthlyNumber.
     */
    public Integer getCardMonthlyNumber() {
        return cardMonthlyNumber;
    }

    /**
     * Sets the cardMonthlyNumber attribute value.
     * @param cardMonthlyNumber The cardMonthlyNumber to set.
     */
    public void setCardMonthlyNumber(Integer cardMonthlyNumber) {
        this.cardMonthlyNumber = cardMonthlyNumber;
    }

    /**
     * Sets the cardMonthlyNumber attribute value.
     * @param cardMonthlyNumber The cardMonthlyNumber to set.
     */
    public void setCardMonthlyNumber(String cardMonthlyNumber) {
        if (StringUtils.isNotBlank(cardMonthlyNumber)) {
            this.cardMonthlyNumber = new Integer(cardMonthlyNumber);
        }
        else {
            this.cardMonthlyNumber = 0;
        }
    }    
    
    /**
     * Gets the cardStatusCode attribute. 
     * @return Returns the cardStatusCode.
     */
    public String getCardStatusCode() {
        return cardStatusCode;
    }

    /**
     * Sets the cardStatusCode attribute value.
     * @param cardStatusCode The cardStatusCode to set.
     */
    public void setCardStatusCode(String cardStatusCode) {
        this.cardStatusCode = cardStatusCode;
    }

    /**
     * Gets the cardNoteText attribute. 
     * @return Returns the cardNoteText.
     */
    public String getCardNoteText() {
        return cardNoteText;
    }

    /**
     * Sets the cardNoteText attribute value.
     * @param cardNoteText The cardNoteText to set.
     */
    public void setCardNoteText(String cardNoteText) {
        this.cardNoteText = cardNoteText;
    }

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute. 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the financialObjectCode attribute. 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the organizationCode attribute. 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute value.
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the cardHolderSystemId attribute. 
     * @return Returns the cardHolderSystemId.
     */
    public String getCardHolderSystemId() {
        return cardHolderSystemId;
    }

    /**
     * Sets the cardHolderSystemId attribute value.
     * @param cardHolderSystemId The cardHolderSystemId to set.
     */
    public void setCardHolderSystemId(String cardHolderSystemId) {
        this.cardHolderSystemId = cardHolderSystemId;
    }

    /**
     * Gets the cardOpenDate attribute. 
     * @return Returns the cardOpenDate.
     */
    public Date getCardOpenDate() {
        return cardOpenDate;
    }

    /**
     * Sets the cardOpenDate attribute value.
     * @param cardOpenDate The cardOpenDate to set.
     */
    public void setCardOpenDate(Date cardOpenDate) {
        this.cardOpenDate = cardOpenDate;
    }
    
    /**
     * Sets the cardOpenDate attribute value.
     * @param cardOpenDate The cardOpenDate to set.
     */
    public void setCardOpenDate(String cardOpenDate) {
        if (StringUtils.isNotBlank(cardOpenDate)) {
            this.cardOpenDate = (Date) (new SqlDateConverter()).convert(Date.class, cardOpenDate);
        } 
    }    

    /**
     * Gets the cardCancelDate attribute. 
     * @return Returns the cardCancelDate.
     */
    public Date getCardCancelDate() {
        return cardCancelDate;
    }

    /**
     * Sets the cardCancelDate attribute value.
     * @param cardCancelDate The cardCancelDate to set.
     */
    public void setCardCancelDate(Date cardCancelDate) {
        this.cardCancelDate = cardCancelDate;
    }

    /**
     * Sets the cardCancelDate attribute value.
     * @param cardCancelDate The cardCancelDate to set.
     */
    public void setCardCancelDate(String cardCancelDate) {
        if (StringUtils.isNotBlank(cardCancelDate)) {
            this.cardCancelDate = (Date) (new SqlDateConverter()).convert(Date.class, cardCancelDate);
        } 
    }    
    
    /**
     * Gets the cardExpireDate attribute. 
     * @return Returns the cardExpireDate.
     */
    public String getCardExpireDate() {
        return cardExpireDate;
    }

    /**
     * Sets the cardExpireDate attribute value.
     * @param cardExpireDate The cardExpireDate to set.
     */
    public void setCardExpireDate(String cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.TRANSACTION_CREDIT_CARD_NUMBER, this.creditCardNumber);
        return m;
    }
}