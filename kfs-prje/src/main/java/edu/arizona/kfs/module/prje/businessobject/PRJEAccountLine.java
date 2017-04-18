/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * @return the rateAccountId
     */
    public Integer getRateAccountId() {
        return rateAccountId;
    }

    /**
     * @param rateAccountId the rateAccountId to set
     */
    public void setRateAccountId(Integer rateAccountId) {
        this.rateAccountId = rateAccountId;
    }

    /**
     * @return the typeId
     */
    public Integer getTypeId() {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    /**
     * @return the overrideProrateType
     */
    public String getOverrideProrateType() {
        if ( overrideAmount != null )
            return overrideProrateType;
        else
            return PRJEConstants.ProrateCreditType.NO_OVERRIDE.getKey();
    }

    /**
     * @param overrideProrateType the overrideProrateType to set
     */
    public void setOverrideProrateType(String overrideProrateType) {
        this.overrideProrateType = overrideProrateType;
    }

    /**
     * @return the overrideAmount
     */
    public KualiDecimal getOverrideAmount() {
        return overrideAmount;
    }

    /**
     * @param overrideAmount the overrideAmount to set
     */
    public void setOverrideAmount(KualiDecimal overrideAmount) {
        this.overrideAmount = overrideAmount;
    }

    /**
     * @return the overridePercent
     */
    public KualiDecimal getOverridePercent() {
        return overridePercent;
    }

    /**
     * @param overridePercent the overridePercent to set
     */
    public void setOverridePercent(KualiDecimal overridePercent) {
        this.overridePercent = overridePercent;
    }

    /**
     * @return the effectiveDateFrom
     */
    public Timestamp getEffectiveDateFrom() {
        return effectiveDateFrom;
    }

    /**
     * @param effectiveDateFrom the effectiveDateFrom to set
     */
    public void setEffectiveDateFrom(Timestamp effectiveDateFrom) {
        this.effectiveDateFrom = effectiveDateFrom;
    }

    /**
     * @return the effectiveDateTo
     */
    public Timestamp getEffectiveDateTo() {
        return effectiveDateTo;
    }

    /**
     * @param effectiveDateTo the effectiveDateTo to set
     */
    public void setEffectiveDateTo(Timestamp effectiveDateTo) {
        this.effectiveDateTo = effectiveDateTo;
    }

    /**
     * @return the chartCode
     */
    public String getChartCode() {
        return chartCode;
    }

    /**
     * @param chartCode the chartCode to set
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber the accountNumber to set
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @return the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber the subAccountNumber to set
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @return the objectCode
     */
    public String getObjectCode() {
        return objectCode;
    }

    /**
     * @param objectCode the objectCode to set
     */
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * @return the projectCode
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @param projectCode the projectCode to set
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the lastUpdate
     */  
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */    
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
        LinkedHashMap m = new LinkedHashMap();

        m.put("rateAccountId", getRateAccountId());
        m.put("typeId", getTypeId());
        m.put("overrideProrateType", getOverrideProrateType());
        m.put("overrideAmount", getOverrideAmount());
        m.put("overridePercent", getOverridePercent());
        m.put("effectiveDateFrom", getEffectiveDateFrom());
        m.put("effectiveDateTo", getEffectiveDateTo());
        m.put("chartCode", getChartCode());
        m.put("accountNumber", getAccountNumber());
        m.put("subAccountNumber", getSubAccountNumber());
        m.put("objectCode", getObjectCode());
        m.put("projectCode", getProjectCode());
        m.put("active", getActive());

        return m;
    }
}
