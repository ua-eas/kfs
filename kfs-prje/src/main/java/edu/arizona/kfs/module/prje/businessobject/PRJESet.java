
package edu.arizona.kfs.module.prje.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Prorate Journal Entry Set BO
 *
 * @author tbradford
 */
public class PRJESet 
        extends PersistableBusinessObjectBase
        implements Serializable {

	private static final long serialVersionUID = -3330759186876159319L;
	private Integer setId;          // PRJE_SET_ID
    private Integer fiscalYear;     // UNIV_FISCAL_YEAR
    private String setName;         // PRJE_SET_NAME
    private String setDescription;  // PRJE_SET_DESC
    private Timestamp lastUpdate;   // LST_UPDT_TS    
    private Boolean active;         // ACTV_CD

    private transient List<PRJEType> types = new ArrayList<PRJEType>();
    
    /**
     * Constructs a PRJESet business object.
     */
    public PRJESet() {
        super();
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetDescription() {
        return setDescription;
    }

    public void setSetDescription(String setDescription) {
        this.setDescription = setDescription;
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
    
    public List<PRJEType> getTypes() {
        return types;
    }

    public void setTypes(List<PRJEType> types) {
        this.types = types;
    }
    
}
