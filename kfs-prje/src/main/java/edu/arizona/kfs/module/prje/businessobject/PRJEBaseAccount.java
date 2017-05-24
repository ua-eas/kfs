
package edu.arizona.kfs.module.prje.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.module.prje.PRJEPropertyConstants;

/**
 * Prorate Journal Entry From BO
 *
 * @author tbradford
 */
public class PRJEBaseAccount 
        extends PersistableBusinessObjectBase
        implements Serializable {
    
	private static final long serialVersionUID = 1956214248016165748L;
	private Integer baseAccountId;       // PRJE_BASE_ACCOUNT_ID
    private Integer typeId;              // PRJE_TYPE_ID
    
    private String baseChart;            // BASE_COA_CD
    private String baseAccount;          // BASE_ACCOUNT_NBR
    private String baseSubAccount;       // BASE_SUB_ACCOUNT_NBR

    private String fromChart;            // FRM_COA_CD
    private String fromAccount;          // FRM_ACCOUNT_NBR
    private String fromSubAccount;       // FRM_SUB_ACCT
 
    private String fromObjectCode;       // FRM_OBJECT_CD
    
    private String frequency;            // MON_OR_YRLY    
    private String prorateType;          // AMT_OR_PCT
    private KualiDecimal prorateAmount;  // PRORATE_AMOUNT
    private KualiDecimal proratePercent; // PRORATE_PCT
    
    private Timestamp lastUpdate;        // LST_UPDT_TS    
    private Boolean active;              // ACTV_CD
    
    private PRJEType type;
    
    private Chart baseAccountFromChart;
    private Chart baseAccountBaseChart;
    private Account baseAccountFromAccount;
    private Account baseAccountBaseAccount;
    private ObjectCodeCurrent baseAccountFromObjectCode;
    private SubAccount baseAccountFromSubAccount;
    private SubAccount baseAccountBaseSubAccount;

    /**
     * Constructs a PRJEBaseAccount business object.
     */    
    public PRJEBaseAccount() {
        super();
    }

    public Integer getBaseAccountId() {
        return baseAccountId;
    }

    public void setBaseAccountId(Integer baseAccountId) {
        this.baseAccountId = baseAccountId;
    }


    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getBaseChart() {
        return baseChart;
    }

    public void setBaseChart(String baseChart) {
        this.baseChart = baseChart;
    }

    public String getBaseAccount() {
        return baseAccount;
    }

    public void setBaseAccount(String baseAccount) {
        this.baseAccount = baseAccount;
    }

    public String getBaseSubAccount() {
        return baseSubAccount;
    }

    public void setBaseSubAccount(String baseSubAccount) {
        this.baseSubAccount = baseSubAccount;
    }

    public String getFromChart() {
        return fromChart;
    }

    public void setFromChart(String fromChart) {
        this.fromChart = fromChart;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getFromSubAccount() {
        return fromSubAccount;
    }

    public void setFromSubAccount(String fromSubAccount) {
        this.fromSubAccount = fromSubAccount;
    }


    public String getFromObjectCode() {
        return fromObjectCode;
    }
    
    public void setFromObjectCode(String fromObjectCode) {
        this.fromObjectCode = fromObjectCode;
    }
    
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getProrateType() {
        return prorateType;
    }

    public void setProrateType(String prorateType) {
        this.prorateType = prorateType;
    }


    public KualiDecimal getProrateAmount() {
        return prorateAmount;
    }

    public void setProrateAmount(KualiDecimal prorateAmount) {
        this.prorateAmount = prorateAmount;
    }

    /**
     * @return the proratePercent
     */
    public KualiDecimal getProratePercent() {
        return proratePercent;
    }

    public void setProratePercent(KualiDecimal proratePercent) {
        this.proratePercent = proratePercent;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public PRJEType getType() {
        return type;
    }

    public void setSet(PRJEType type) {
        this.type = type;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public Chart getBaseAccountFromChart() {
        return baseAccountFromChart;
    }

    public void setBaseAccountFromChart(Chart baseAccountFromChart) {
        this.baseAccountFromChart = baseAccountFromChart;
    }

    public Chart getBaseAccountBaseChart() {
        return baseAccountBaseChart;
    }

    public void setBaseAccountBaseChart(Chart baseAccountBaseChart) {
        this.baseAccountBaseChart = baseAccountBaseChart;
    }

    public Account getBaseAccountFromAccount() {
        return baseAccountFromAccount;
    }

    public void setBaseAccountFromAccount(Account baseAccountFromAccount) {
        this.baseAccountFromAccount = baseAccountFromAccount;
    }

    public Account getBaseAccountBaseAccount() {
        return baseAccountBaseAccount;
    }

    public void setBaseAccountBaseAccount(Account baseAccountBaseAccount) {
        this.baseAccountBaseAccount = baseAccountBaseAccount;
    }

    public ObjectCodeCurrent getBaseAccountFromObjectCode() {
        return baseAccountFromObjectCode;
    }

    public void setBaseAccountFromObjectCode(ObjectCodeCurrent baseAccountFromObjectCode) {
        this.baseAccountFromObjectCode = baseAccountFromObjectCode;
    }

    public SubAccount getBaseAccountFromSubAccount() {
        return baseAccountFromSubAccount;
    }

    public void setBaseAccountFromSubAccount(SubAccount baseAccountFromSubAccount) {
        this.baseAccountFromSubAccount = baseAccountFromSubAccount;
    }

    public SubAccount getBaseAccountBaseSubAccount() {
        return baseAccountBaseSubAccount;
    }

    public void setBaseAccountBaseSubAccount(SubAccount baseAccountBaseSubAccount) {
        this.baseAccountBaseSubAccount = baseAccountBaseSubAccount;
    }

    public void setType(PRJEType type) {
        this.type = type;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> retVal = new LinkedHashMap<String, String>();
        retVal.put(PRJEPropertyConstants.PRJE_BASE_ACCOUNT_ID, getBaseAccountId() == null ? null : getBaseAccountId().toString());
        retVal.put(PRJEPropertyConstants.PRJE_TYPE_ID, getTypeId() == null ? null : getTypeId().toString());
        
        retVal.put(PRJEPropertyConstants.BASE_COA_CD, getBaseChart());
        retVal.put(PRJEPropertyConstants.BASE_ACCOUNT_NBR, getBaseAccount());
        retVal.put(PRJEPropertyConstants.BASE_SUB_ACCOUNT_NBR, getBaseSubAccount());

        retVal.put(PRJEPropertyConstants.FRM_COA_CD, getFromChart());
        retVal.put(PRJEPropertyConstants.FRM_ACCOUNT_NBR, getFromAccount());
        retVal.put(PRJEPropertyConstants.FRM_SUB_ACCT, getFromSubAccount());
        
        retVal.put(PRJEPropertyConstants.FRM_OBJECT_CD, getFromObjectCode());
        
        retVal.put(PRJEPropertyConstants.MON_OR_YRLY, getFrequency());
        retVal.put(PRJEPropertyConstants.PRORATE_TYPE, getProrateType());
        retVal.put(PRJEPropertyConstants.PRORATE_PCT, getProratePercent() == null ? null : getProratePercent().toString());

        retVal.put(PRJEPropertyConstants.ACTV_CD, getActive() == null ? null : getActive().toString());

        
        return retVal;
    }
}
