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

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Prorate Journal Entry Set BO
 *
 * @author tbradford
 */
public class PRJESet 
        extends PersistableBusinessObjectBase
        implements Serializable {

    private Integer setId;          // PRJE_SET_ID
    private Integer fiscalYear;     // UNIV_FISCAL_YEAR
    private String setName;         // PRJE_SET_NAME
    private String setDescription;  // PRJE_SET_DESC
    private Timestamp lastUpdate;   // LST_UPDT_TS    
    private Boolean active;         // ACTV_CD

    // Uncomment in next feature step
//    private transient List<PRJEType> types = new ArrayList<PRJEType>();
    
    /**
     * Constructs a PRJESet business object.
     */
    public PRJESet() {
        super();
    }

    /**
     * @return the setId
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
     * @return the fiscalYear
     */
    public Integer getFiscalYear() {
        return fiscalYear;
    }

    /**
     * @param fiscalYear the fiscalYear to set
     */
    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * @return the setName
     */
    public String getSetName() {
        return setName;
    }

    /**
     * @param setName the setName to set
     */
    public void setSetName(String setName) {
        this.setName = setName;
    }

    /**
     * @return the setDescription
     */
    public String getSetDescription() {
        return setDescription;
    }

    /**
     * @param setDescription the setDescription to set
     */
    public void setSetDescription(String setDescription) {
        this.setDescription = setDescription;
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
    
    // Uncomment in next feature step
//    public List<PRJEType> getTypes() {
//        return types;
//    }
//
//    public void setTypes(List<PRJEType> types) {
//        this.types = types;
//    }
    
}
