package edu.arizona.kfs.gl.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;


public class GlobalTransactionEditDetail extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String originCode;
    private String fundGroupCode;
    private String subFundGroupCode;
    private String documentTypeCode;
    private String objectTypeCode;
    private String objectSubTypeCode;
    private String objectCodeRulePurpose;
    private boolean active;
    private DocumentTypeEBO documentType;
    private ObjectType objectType;
    private ObjectSubType objectSubType;
    private GlobalTransactionEdit globalTransactionEdit;
    private BusinessObjectService boService;


    public GlobalTransactionEditDetail() {
        super();
    }


    public String getOriginCodeFullText() {
        OriginationCode origin = getBoService().findBySinglePrimaryKey(OriginationCode.class, originCode);
        return origin.getFinancialSystemOriginationCode() + "-" + origin.getFinancialSystemServerName();
    }

    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    public String getFundGroupCodeFullText() {
        FundGroup fundGroup = getBoService().findBySinglePrimaryKey(FundGroup.class, fundGroupCode);
        return fundGroup.getCode() + "-" + fundGroup.getName();
    }

    public String getFundGroupCode() {
        return fundGroupCode;
    }

    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }

    public String getSubFundGroupCodeFullText() {
        SubFundGroup subFundGroup = getBoService().findBySinglePrimaryKey(SubFundGroup.class, subFundGroupCode);
        return subFundGroup.getSubFundGroupCode() + "-" + subFundGroup.getSubFundGroupDescription();
    }

    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    // UA KFS7 upgrade
    public String getDocumentTypeCodeFullText() {
        if (documentType == null || !StringUtils.equals(documentType.getName(), documentTypeCode)) {
        	documentType = null;
            if (StringUtils.isNotBlank(documentTypeCode)) {
                DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeCode);
                if (docType != null) {
                	documentType = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                }
            }
        }
        return documentType.getName() + "-" + documentType.getLabel();
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(String documentType) {
        this.documentTypeCode = documentType;
    }

    public String getObjectTypeCodeFullText() {
        ObjectType objectType = getBoService().findBySinglePrimaryKey(ObjectType.class, objectTypeCode);
        return objectType.getName() + "-" + objectType.getName();
    }

    public String getObjectTypeCode() {
        return objectTypeCode;
    }

    public void setObjectTypeCode(String objectType) {
        this.objectTypeCode = objectType;
    }

    public String getObjectSubTypeCodeFullText() {
        ObjectSubType objectSubType = getBoService().findBySinglePrimaryKey(ObjectSubType.class, objectSubTypeCode);
        return objectSubType.getName() + "-" + objectSubType.getName();
    }

    public String getObjectSubTypeCode() {
        return objectSubTypeCode;
    }

    public void setObjectSubTypeCode(String objectSubType) {
        this.objectSubTypeCode = objectSubType;
    }

    // UA KFS7 upgrade
    public DocumentTypeEBO getDocumentType() {
    	 if (documentType == null || !StringUtils.equals(documentType.getName(), documentTypeCode)) {
         	documentType = null;
             if (StringUtils.isNotBlank(documentTypeCode)) {
                 DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeCode);
                 if (docType != null) {
                 	documentType = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                 }
             }
         }
        return documentType;
    }

    public void setDocumentType(DocumentTypeEBO documentType) {
        this.documentType = documentType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public ObjectSubType getObjectSubType() {
        return objectSubType;
    }

    public void setObjectSubType(ObjectSubType objectSubType) {
        this.objectSubType = objectSubType;
    }

    public GlobalTransactionEdit getGlobalTransactionEdit() {
        return globalTransactionEdit;
    }

    public void setGlobalTransactionEdit(GlobalTransactionEdit globalTransactionEdit) {
        this.globalTransactionEdit = globalTransactionEdit;
    }

    public String getObjectCodeRulePurpose() {
        return objectCodeRulePurpose;
    }

    public void setObjectCodeRulePurpose(String objectCodeRulePurpose) {
        this.objectCodeRulePurpose = objectCodeRulePurpose;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    private BusinessObjectService getBoService() {
        if (boService == null) {
            boService = SpringContext.getBean(BusinessObjectService.class);
        }
        return boService;
    }

}
