
package edu.arizona.kfs.module.prje.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.module.prje.PRJEPropertyConstants;

/**
 * Prorate Journal Entry Base Object BO
 *
 * @author tbradford
 */
public class PRJEBaseObject
        extends PersistableBusinessObjectBase
        implements Serializable {

	private static final long serialVersionUID = 662506739988139207L;
	private Integer rateObjectCodeId;   // PRJE_RATE_OBJECT_CD_ID
    private Integer typeId;             // PRJE_TYPE_ID
    private String objectCodeRangeName; // OBJ_CD_RG_NM
    private String include;             // INCLUDE_EXCLUDE_FLAG
    private String baseChartCode;       // FIN_COA_CD
    private String baseObjectCodeLow;   // FIN_OBJECT_CD_LOW
    private String baseObjectCodeHigh;  // FIN_OBJECT_CD_HIGH
    private String subObjectCodeLow;    // SUB_OBJECT_CD_LOW
    private String subObjectCodeHigh;   // SUB_OBJECT_CD_HIGH
    private Timestamp lastUpdate;       // LST_UPDT_TS    
    private Boolean active;             // ACTV_CD

    private PRJEType type;
    
    private Chart baseObjectChart;
    private ObjectCodeCurrent baseObjectObjectCodeLow;
    private ObjectCodeCurrent baseObjectObjectCodeHigh;
    
    /**
     * Constructs a PRJEBaseObject business object.
     */
    public PRJEBaseObject() {
        super();
    }

    /**
     * @return the rateObjectCodeId
     */
    public Integer getRateObjectCodeId() {
        return rateObjectCodeId;
    }

    /**
     * @param rateObjectCodeId the rateObjectCodeId to set
     */
    public void setRateObjectCodeId(Integer rateObjectCodeId) {
        this.rateObjectCodeId = rateObjectCodeId;
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
     * @return the objectCodeRangeName
     */
    public String getObjectCodeRangeName() {
        return objectCodeRangeName;
    }

    /**
     * @param objectCodeRangeName the objectCodeRangeName to set
     */
    public void setObjectCodeRangeName(String objectCodeRangeName) {
        this.objectCodeRangeName = objectCodeRangeName;
    }

    /**
     * @return the include
     */
    public String getInclude() {
        return include;
    }

    /**
     * @param include the include to set
     */
    public void setInclude(String include) {
        this.include = include;
    }

    /**
     * @return the baseChartCode
     */
    public String getBaseChartCode() {
        return baseChartCode;
    }

    /**
     * @param baseChartCode the baseChartCode to set
     */
    public void setBaseChartCode(String baseChartCode) {
        this.baseChartCode = baseChartCode;
    }

    /**
     * @return the baseObjectCodeLow
     */
    public String getBaseObjectCodeLow() {
        return baseObjectCodeLow;
    }

    /**
     * @param baseObjectCodeLow the baseObjectCodeLow to set
     */
    public void setBaseObjectCodeLow(String baseObjectCodeLow) {
        this.baseObjectCodeLow = baseObjectCodeLow;
    }

    /**
     * @return the baseObjectCodeHigh
     */
    public String getBaseObjectCodeHigh() {
        return baseObjectCodeHigh;
    }

    /**
     * @param baseObjectCodeHigh the baseObjectCodeHigh to set
     */
    public void setBaseObjectCodeHigh(String baseObjectCodeHigh) {
        this.baseObjectCodeHigh = baseObjectCodeHigh;
    }

    /**
     * @return the subObjectCodeLow
     */
    public String getSubObjectCodeLow() {
        return subObjectCodeLow;
    }

    /**
     * @param subObjectCodeLow the subObjectCodeLow to set
     */
    public void setSubObjectCodeLow(String subObjectCodeLow) {
        this.subObjectCodeLow = subObjectCodeLow;
    }

    /**
     * @return the subObjectCodeHigh
     */
    public String getSubObjectCodeHigh() {
        return subObjectCodeHigh;
    }

    /**
     * @param subObjectCodeHigh the subObjectCodeHigh to set
     */
    public void setSubObjectCodeHigh(String subObjectCodeHigh) {
        this.subObjectCodeHigh = subObjectCodeHigh;
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

    public Chart getBaseObjectChart() {
        return baseObjectChart;
    }

    public void setBaseObjectChart(Chart baseObjectChart) {
        this.baseObjectChart = baseObjectChart;
    }

    public ObjectCodeCurrent getBaseObjectObjectCodeLow() {
        return baseObjectObjectCodeLow;
    }

    public void setBaseObjectObjectCodeLow(ObjectCodeCurrent baseObjectObjectCodeLow) {
        this.baseObjectObjectCodeLow = baseObjectObjectCodeLow;
    }

    public ObjectCodeCurrent getBaseObjectObjectCodeHigh() {
        return baseObjectObjectCodeHigh;
    }

    public void setBaseObjectObjectCodeHigh(ObjectCodeCurrent baseObjectObjectCodeHigh) {
        this.baseObjectObjectCodeHigh = baseObjectObjectCodeHigh;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> retVal = new LinkedHashMap<String, String>();

        retVal.put(PRJEPropertyConstants.PRJE_RATE_OBJECT_CD_ID, getRateObjectCodeId() == null ? null : getRateObjectCodeId().toString());
        retVal.put(PRJEPropertyConstants.PRJE_TYPE_ID, getTypeId() == null ? null : getTypeId().toString());
        retVal.put(PRJEPropertyConstants.OBJ_CD_RG_NM, getObjectCodeRangeName());
        retVal.put(PRJEPropertyConstants.INCLUDE_EXCLUDE_FLAG, getInclude());
        retVal.put(PRJEPropertyConstants.BASE_FIN_COA_CD, getBaseChartCode());
        retVal.put(PRJEPropertyConstants.BASE_FIN_COA_CD_LOW, getBaseObjectCodeLow());
        retVal.put(PRJEPropertyConstants.BASE_FIN_COA_CD_HIGH, getBaseObjectCodeHigh());
        retVal.put(PRJEPropertyConstants.SUB_OBJECT_CD_LOW, getSubObjectCodeLow());
        retVal.put(PRJEPropertyConstants.SUB_OBJECT_CD_HIGH, getSubObjectCodeHigh());
        retVal.put(PRJEPropertyConstants.ACTV_CD, getActive() == null ? null : getActive().toString());


        return retVal;
    }
}
