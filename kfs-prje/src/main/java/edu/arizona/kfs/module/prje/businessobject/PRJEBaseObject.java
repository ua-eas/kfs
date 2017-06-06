
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

    public Integer getRateObjectCodeId() {
        return rateObjectCodeId;
    }

    public void setRateObjectCodeId(Integer rateObjectCodeId) {
        this.rateObjectCodeId = rateObjectCodeId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getObjectCodeRangeName() {
        return objectCodeRangeName;
    }

    public void setObjectCodeRangeName(String objectCodeRangeName) {
        this.objectCodeRangeName = objectCodeRangeName;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getBaseChartCode() {
        return baseChartCode;
    }

    public void setBaseChartCode(String baseChartCode) {
        this.baseChartCode = baseChartCode;
    }

    public String getBaseObjectCodeLow() {
        return baseObjectCodeLow;
    }

    public void setBaseObjectCodeLow(String baseObjectCodeLow) {
        this.baseObjectCodeLow = baseObjectCodeLow;
    }

    public String getBaseObjectCodeHigh() {
        return baseObjectCodeHigh;
    }

    public void setBaseObjectCodeHigh(String baseObjectCodeHigh) {
        this.baseObjectCodeHigh = baseObjectCodeHigh;
    }

    public String getSubObjectCodeLow() {
        return subObjectCodeLow;
    }

    public void setSubObjectCodeLow(String subObjectCodeLow) {
        this.subObjectCodeLow = subObjectCodeLow;
    }

    public String getSubObjectCodeHigh() {
        return subObjectCodeHigh;
    }

    public void setSubObjectCodeHigh(String subObjectCodeHigh) {
        this.subObjectCodeHigh = subObjectCodeHigh;
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
