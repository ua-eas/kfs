 
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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    /**
     * @return entryName
     */    
    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getProrateOptions() {
        return prorateOptions;
    }

    public void setProrateOptions(String prorateOptions) {
        this.prorateOptions = prorateOptions;
    }
    
    public Boolean getActive() {
        return active;
    }

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
    
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
}
