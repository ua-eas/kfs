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
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Prorate Journal Entry Type BO
 *
 * @author tbradford
 */
public class PRJEType 
        extends PersistableBusinessObjectBase
        implements Serializable {
    
	private static final long serialVersionUID = 4519160828267112230L;
	private Integer typeId;              // PRJE_TYPE_ID
    private Integer setId;               // PRJE_SET_ID
    private String entryName;            // PRORATE_JE_NM
    private String prorateOptions;       // PRORATE_JE_OPTION;
    
    private Timestamp lastUpdate;        // LST_UPDT_TS    
    private Boolean active;              // ACTV_CD
    
    private PRJESet set;
    private List<PRJEBaseAccount> baseAccounts = new ArrayList<PRJEBaseAccount>();
    private List<PRJEBaseObject> baseObjects = new ArrayList<PRJEBaseObject>();
    private List<PRJEAccountLine> accountLines = new ArrayList<PRJEAccountLine>();

    /**
     * Constructs a PRJEType business object.
     */    
    public PRJEType() {
        super();
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
     * @return setId
     */    
    public Integer getSetId() {
        return setId;
    }

    /**
     * @param setId the setId to set
     */    
    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    /**
     * @return entryName
     */    
    public String getEntryName() {
        return entryName;
    }

    /**
     * @param entryName the entryName to set
     */    
    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    /**
     * @return prorateOptions
     */
    public String getProrateOptions() {
        return prorateOptions;
    }

    /**
     * @param prorateOptions the prorateOptions to set
     */    
    public void setProrateOptions(String prorateOptions) {
        this.prorateOptions = prorateOptions;
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
    
    public PRJESet getSet() {
        return set;
    }

    public void setSet(PRJESet set) {
        this.set = set;
    }

    public List<PRJEBaseAccount> getBaseAccounts() {
        return baseAccounts;
    }

    public void setBaseAccounts(List<PRJEBaseAccount> baseAccounts) {
        this.baseAccounts = baseAccounts;
    }
    
    public List<PRJEBaseObject> getBaseObjects() {
        return baseObjects;
    }

    public void setBaseObjects(List<PRJEBaseObject> baseObjects) {
        this.baseObjects = baseObjects;
    }

    public List<PRJEAccountLine> getAccountLines() {
        return accountLines;
    }

    public void setAccountLines(List<PRJEAccountLine> accountLines) {
        this.accountLines = accountLines;
    }

    public String getSetName() {
        return getSet().getSetName();
    }
    
    public void setSetName(String setName) {
        getSet().setSetName(setName);
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
    
}
