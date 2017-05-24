
package edu.arizona.kfs.module.prje.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.module.prje.PRJEConstants;
import edu.arizona.kfs.module.prje.PRJEPropertyConstants;

/**
 * Prorate Journal Entry Account Line BO
 *
 * @author tbradford
 */
public class PRJEAccountLine
        extends PersistableBusinessObjectBase
        implements Serializable {

	private static final long serialVersionUID = -7415061476983190024L;
	private Integer rateAccountId;        // JE_RATE_ACCT_ID
    private Integer typeId;               // PRJE_TYPE_ID
    private String overrideProrateType;   // AMT_OR_PCT
    private KualiDecimal overrideAmount;  // PRORATE_AMOUNT
    private KualiDecimal overridePercent; // PRORATE_PCT
    private Timestamp effectiveDateFrom;  // EFFECTIVE_DT_FROM
    private Timestamp effectiveDateTo;    // EFFECTIVE_DT_TO
    private String chartCode;             // FIN_COA_CD
    private String accountNumber;         // ACCOUNT_NBR
    private String subAccountNumber;      // SUB_ACCOUNT_NBR
    private String objectCode;            // OBJECT_CD
    private String projectCode;           // PROJECT_CD
    private Timestamp lastUpdate;         // LST_UPDT_TS
    private Boolean active;               // ACTV_CD

    private PRJEType type;
    private ProjectCode project;
    
    private Chart accountLineChart;
    private Account accountLineAccount;
    private ObjectCode accountLineObjectCode;
    private SubAccount accountLineSubAccount;
    
    /**
     * Constructs a PRJEBaseObject business object.
     */
    public PRJEAccountLine() {
        super();
    }

    public Integer getRateAccountId() {
        return rateAccountId;
    }

    public void setRateAccountId(Integer rateAccountId) {
        this.rateAccountId = rateAccountId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getOverrideProrateType() {
        if ( overrideAmount != null )
            return overrideProrateType;
        else
            return PRJEConstants.ProrateCreditType.NO_OVERRIDE.getKey();
    }

    public void setOverrideProrateType(String overrideProrateType) {
        this.overrideProrateType = overrideProrateType;
    }

    public KualiDecimal getOverrideAmount() {
        return overrideAmount;
    }

    public void setOverrideAmount(KualiDecimal overrideAmount) {
        this.overrideAmount = overrideAmount;
    }

    public KualiDecimal getOverridePercent() {
        return overridePercent;
    }

    public void setOverridePercent(KualiDecimal overridePercent) {
        this.overridePercent = overridePercent;
    }

    public Timestamp getEffectiveDateFrom() {
        return effectiveDateFrom;
    }

    public void setEffectiveDateFrom(Timestamp effectiveDateFrom) {
        this.effectiveDateFrom = effectiveDateFrom;
    }

    public Timestamp getEffectiveDateTo() {
        return effectiveDateTo;
    }

    public void setEffectiveDateTo(Timestamp effectiveDateTo) {
        this.effectiveDateTo = effectiveDateTo;
    }

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

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public PRJEType getType() {
        return type;
    }

    public void setType(PRJEType type) {
        this.type = type;
    }

    public Chart getAccountLineChart() {
        return accountLineChart;
    }
    
    public void setAccountLineChart(Chart accountLineChart) {
        this.accountLineChart = accountLineChart;        
    }
    
    public Account getAccountLineAccount() {
        return accountLineAccount;
    }

    public void setAccountLineAccount(Account accountLineAccount) {
        this.accountLineAccount = accountLineAccount;
    }

    public ObjectCode getAccountLineObjectCode() {
        return accountLineObjectCode;
    }

    public void setAccountLineObjectCode(ObjectCode accountLineObjectCode) {
        this.accountLineObjectCode = accountLineObjectCode;
    }

    public SubAccount getAccountLineSubAccount() {
        return accountLineSubAccount;
    }

    public void setAccountLineSubAccount(SubAccount accountLineSubAccount) {
        this.accountLineSubAccount = accountLineSubAccount;
    }

    public ProjectCode getProject() {
        return project;
    }

    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> retVal = new LinkedHashMap<String, String>(); 

        retVal.put(PRJEPropertyConstants.JE_RATE_ACCT_ID, getRateAccountId() == null ? null : getRateAccountId().toString());
        retVal.put(PRJEPropertyConstants.PRJE_TYPE_ID, getTypeId() == null ? null : getTypeId().toString());
        retVal.put(PRJEPropertyConstants.AMT_OR_PCT, getOverrideProrateType());
        retVal.put(PRJEPropertyConstants.PRORATE_AMOUNT, getOverrideAmount() == null ? null : getOverrideAmount().toString());
        retVal.put(PRJEPropertyConstants.PRORATE_PCT, getOverridePercent() == null ? null : getOverridePercent().toString());
        retVal.put(PRJEPropertyConstants.EFFECTIVE_DT_FROM, getEffectiveDateFrom() == null ? null : getEffectiveDateFrom().toString());
        retVal.put(PRJEPropertyConstants.EFFECTIVE_DT_TO, getEffectiveDateTo() == null ? null :getEffectiveDateTo().toString());
        retVal.put(PRJEPropertyConstants.FIN_COA_CD, getChartCode());
        retVal.put(PRJEPropertyConstants.ACCOUNT_NBR, getAccountNumber());
        retVal.put(PRJEPropertyConstants.SUB_ACCOUNT_NBR, getSubAccountNumber());
        retVal.put(PRJEPropertyConstants.OBJECT_CD, getObjectCode());
        retVal.put(PRJEPropertyConstants.PROJECT_CD, getProjectCode());
        retVal.put(PRJEPropertyConstants.ACTV_CD, getActive() == null ? null : getActive().toString());
        return retVal;
    }
}
