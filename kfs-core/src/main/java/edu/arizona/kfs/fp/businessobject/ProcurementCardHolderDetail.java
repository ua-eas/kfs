package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kim.impl.group.GroupBo;

/**
 * This class is used to represent a procurement card holder, or the individual whose name is on the card.
 */
public class ProcurementCardHolderDetail extends PersistableBusinessObjectBase {

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
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String organizationCode;
    private String cardHolderSystemId;
    private String cardGroupId;
    private String cardCancelCode;
    private Date cardOpenDate;
    private Date cardCancelDate;
    private Date cardExpireDate;
    private String cardApprovalOfficial;

    private Account account;
    private Chart chartOfAccounts;
    private SubAccount subAccount;
    private ObjectCode objectCode;
    private Organization organization;
    private SubObjectCode subObjectCode;
    private Person cardholderUser;
    private GroupBo reconcilerGroup;


    public ProcurementCardHolderDetail() {

    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardHolderAlternateName() {
        return cardHolderAlternateName;
    }

    public void setCardHolderAlternateName(String cardHolderAlternateName) {
        this.cardHolderAlternateName = cardHolderAlternateName;
    }

    public String getCardHolderLine1Address() {
        return cardHolderLine1Address;
    }

    public void setCardHolderLine1Address(String cardHolderLine1Address) {
        this.cardHolderLine1Address = cardHolderLine1Address;
    }

    public String getCardHolderLine2Address() {
        return cardHolderLine2Address;
    }

    public void setCardHolderLine2Address(String cardHolderLine2Address) {
        this.cardHolderLine2Address = cardHolderLine2Address;
    }

    public String getCardHolderCityName() {
        return cardHolderCityName;
    }

    public void setCardHolderCityName(String cardHolderCityName) {
        this.cardHolderCityName = cardHolderCityName;
    }

    public String getCardHolderStateCode() {
        return cardHolderStateCode;
    }

    public void setCardHolderStateCode(String cardHolderStateCode) {
        this.cardHolderStateCode = cardHolderStateCode;
    }

    public String getCardHolderZipCode() {
        return cardHolderZipCode;
    }

    public void setCardHolderZipCode(String cardHolderZipCode) {
        this.cardHolderZipCode = cardHolderZipCode;
    }

    public String getCardHolderWorkPhoneNumber() {
        return cardHolderWorkPhoneNumber;
    }

    public void setCardHolderWorkPhoneNumber(String cardHolderWorkPhoneNumber) {
        this.cardHolderWorkPhoneNumber = cardHolderWorkPhoneNumber;
    }

    public KualiDecimal getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(KualiDecimal cardLimit) {
        this.cardLimit = cardLimit;
    }

    public KualiDecimal getCardCycleAmountLimit() {
        return cardCycleAmountLimit;
    }

    public void setCardCycleAmountLimit(KualiDecimal cardCycleAmountLimit) {
        this.cardCycleAmountLimit = cardCycleAmountLimit;
    }

    public Integer getCardCycleVolumeLimit() {
        return cardCycleVolumeLimit;
    }

    public void setCardCycleVolumeLimit(Integer cardCycleVolumeLimit) {
        this.cardCycleVolumeLimit = cardCycleVolumeLimit;
    }

    public Integer getCardMonthlyNumber() {
        return cardMonthlyNumber;
    }

    public void setCardMonthlyNumber(Integer cardMonthlyNumber) {
        this.cardMonthlyNumber = cardMonthlyNumber;
    }

    public String getCardStatusCode() {
        return cardStatusCode;
    }

    public void setCardStatusCode(String cardStatusCode) {
        this.cardStatusCode = cardStatusCode;
    }

    public String getCardNoteText() {
        return cardNoteText;
    }

    public void setCardNoteText(String cardNoteText) {
        this.cardNoteText = cardNoteText;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getCardHolderSystemId() {
        return cardHolderSystemId;
    }

    public void setCardHolderSystemId(String cardHolderSystemId) {
        this.cardHolderSystemId = cardHolderSystemId;
    }

    public String getCardGroupId() {
        return cardGroupId;
    }

    public void setCardGroupId(String cardGroupId) {
        this.cardGroupId = cardGroupId;
    }

    public String getCardCancelCode() {
        return cardCancelCode;
    }

    public void setCardCancelCode(String cardCancelCode) {
        this.cardCancelCode = cardCancelCode;
    }

    public Date getCardOpenDate() {
        return cardOpenDate;
    }

    public void setCardOpenDate(Date cardOpenDate) {
        this.cardOpenDate = cardOpenDate;
    }

    public Date getCardCancelDate() {
        return cardCancelDate;
    }

    public void setCardCancelDate(Date cardCancelDate) {
        this.cardCancelDate = cardCancelDate;
    }

    public Date getCardExpireDate() {
        return cardExpireDate;
    }

    public void setCardExpireDate(Date cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }

    public String getCardApprovalOfficial() {
        return cardApprovalOfficial;
    }

    public void setCardApprovalOfficial(String cardApprovalOfficial) {
        this.cardApprovalOfficial = cardApprovalOfficial;
    }

    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account
     *            The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts
     *            The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute.
     * 
     * @param subAccount
     *            The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public ObjectCode getObjectCode() {
        return objectCode;
    }

    /**
     * Sets the objectCode attribute.
     * 
     * @param objectCode
     *            The objectCode to set.
     * @deprecated
     */
    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization
     *            The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public SubObjectCode getSubObjectCode() {
        return subObjectCode;
    }

    /**
     * Sets the subObjectCode attribute.
     * 
     * @param subObjectCode
     *            The subObjectCode to set.
     * @deprecated
     */
    public void setSubObjectCode(SubObjectCode subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    public Person getCardholderUser() {
        cardholderUser = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(cardHolderSystemId, cardholderUser);
        return cardholderUser;
    }

    /**
     * Sets the cardholderUser attribute.
     * 
     * @param cardholderUser
     *            The cardholderUser to set.
     * @deprecated
     */
    public void setCardholderUser(Person cardholderUser) {
        this.cardholderUser = cardholderUser;
    }

    public GroupBo getReconcilerGroup() {
        return reconcilerGroup;
    }

    /**
     * Sets the reconcilerGroup attribute.
     * 
     * @param reconcilerGroup
     *            The reconcilerGroup to set.
     * @deprecated
     */
    public void setReconcilerGroup(GroupBo reconcilerGroup) {
        this.reconcilerGroup = reconcilerGroup;
    }

    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(KFSPropertyConstants.TRANSACTION_CREDIT_CARD_NUMBER, this.creditCardNumber);
        return m;
    }
}
