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
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

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

    /**
     * @return baseAccountId
     */
    public Integer getBaseAccountId() {
        return baseAccountId;
    }

    /**
     * @param baseAccountId the baseAccountId to set
     */    
    public void setBaseAccountId(Integer baseAccountId) {
        this.baseAccountId = baseAccountId;
    }

    /**
     * @return typeId
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
     * @return the baseChart
     */
    public String getBaseChart() {
        return baseChart;
    }

    /**
     * @param baseChart the baseChart to set
     */
    public void setBaseChart(String baseChart) {
        this.baseChart = baseChart;
    }

    /**
     * @return the baseAccount
     */
    public String getBaseAccount() {
        return baseAccount;
    }

    /**
     * @param baseAccount the baseAccount to set
     */
    public void setBaseAccount(String baseAccount) {
        this.baseAccount = baseAccount;
    }

    /**
     * @return the baseSubAccount
     */
    public String getBaseSubAccount() {
        return baseSubAccount;
    }

    /**
     * @param baseSubAccount the baseSubAccount to set
     */
    public void setBaseSubAccount(String baseSubAccount) {
        this.baseSubAccount = baseSubAccount;
    }

    /**
     * @return the fromChart
     */    
    public String getFromChart() {
        return fromChart;
    }

    /**
     * @param fromChart the fromChart to set
     */    
    public void setFromChart(String fromChart) {
        this.fromChart = fromChart;
    }

    /**
     * @return the fromAccount
     */    
    public String getFromAccount() {
        return fromAccount;
    }

    /**
     * @param fromAccount the fromAccount to set
     */    
    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    /**
     * @return the fromSubAccount
     */    
    public String getFromSubAccount() {
        return fromSubAccount;
    }

    /**
     * @param fromSubAccount the fromSubAccount to set
     */    
    public void setFromSubAccount(String fromSubAccount) {
        this.fromSubAccount = fromSubAccount;
    }

    /**
     * @return the fromObjectCode
     */    
    public String getFromObjectCode() {
        return fromObjectCode;
    }
    
    /**
     * @param fromObjectCode the fromObjectCode to set
     */    
    public void setFromObjectCode(String fromObjectCode) {
        this.fromObjectCode = fromObjectCode;
    }
    
    /**
     * @return the frequency
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the prorateType
     */
    public String getProrateType() {
        return prorateType;
    }

    /**
     * @param prorateType the prorateType to set
     */
    public void setProrateType(String prorateType) {
        this.prorateType = prorateType;
    }

    /**
     * @param prorateAmount the prorateAmount to set
     */    
    public KualiDecimal getProrateAmount() {
        return prorateAmount;
    }

    /**
     * @param prorateAmount the prorateAmount to set
     */    
    public void setProrateAmount(KualiDecimal prorateAmount) {
        this.prorateAmount = prorateAmount;
    }

    /**
     * @return the proratePercent
     */
    public KualiDecimal getProratePercent() {
        return proratePercent;
    }

    /**
     * @param proratePercent the proratePercent to set
     */
    public void setProratePercent(KualiDecimal proratePercent) {
        this.proratePercent = proratePercent;
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
    
    public PRJEType getType() {
        return type;
    }

    public void setSet(PRJEType type) {
        this.type = type;
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
        LinkedHashMap m = new LinkedHashMap();
        
        m.put("baseAccountId", getBaseAccountId());
        m.put("typeId", getTypeId());
        
        m.put("baseChart", getBaseChart());
        m.put("baseAccount", getBaseAccount());
        m.put("baseSubAccount", getBaseSubAccount());

        m.put("fromChart", getFromChart());
        m.put("fromAccount", getFromAccount());
        m.put("fromSubAccount", getFromSubAccount());
        
        m.put("fromObjectCode", getFromObjectCode());
        
        m.put("frequency", getFrequency());
        m.put("prorateType", getProrateType());
        m.put("proratePercent", getProratePercent());

        m.put("active", getActive());
        
        return m;
    }
}
