/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.krad.maintenance;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataObjectAuthorizationService;
import org.kuali.kfs.krad.service.DataObjectMetaDataService;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.LookupService;
import org.kuali.kfs.krad.service.MaintenanceDocumentService;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.web.form.MaintenanceForm;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the <code>Maintainable</code> interface
 *
 * 
 */
public class MaintainableImpl extends ViewHelperServiceImpl implements Maintainable {
    private static final long serialVersionUID = 9125271369161634992L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintainableImpl.class);

    private String documentNumber;
    private Object dataObject;
    private Class<?> dataObjectClass;
    private String maintenanceAction;

    private transient LookupService lookupService;
    private transient DataObjectAuthorizationService dataObjectAuthorizationService;
    private transient DataObjectMetaDataService dataObjectMetaDataService;
    private transient DocumentDictionaryService documentDictionaryService;
    private transient EncryptionService encryptionService;
    private transient BusinessObjectService businessObjectService;
    private transient MaintenanceDocumentService maintenanceDocumentService;

    /**
     * @see Maintainable#retrieveObjectForEditOrCopy(MaintenanceDocument, java.util.Map)
     */
    @Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {
        Object dataObject = null;

        try {
            dataObject = getLookupService().findObjectBySearch(getDataObjectClass(), dataObjectKeys);
        } catch (ClassNotPersistenceCapableException ex) {
            if (!document.getOldMaintainableObject().isExternalBusinessObject()) {
                throw new RuntimeException("Data Object Class: "
                        + getDataObjectClass()
                        + " is not persistable and is not externalizable - configuration error");
            }
            // otherwise, let fall through
        }

        return dataObject;
    }

    /**
     * @see Maintainable#setDocumentNumber
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @see Maintainable#getDocumentTitle
     */
    @Override
    public String getDocumentTitle(MaintenanceDocument document) {
        // default implementation is to allow MaintenanceDocumentBase to
        // generate the doc title
        return "";
    }

    /**
     * @see Maintainable#getDataObject
     */
    @Override
    public Object getDataObject() {
        return dataObject;
    }

    /**
     * @see Maintainable#setDataObject
     */
    @Override
    public void setDataObject(Object object) {
        this.dataObject = object;
    }

    /**
     * @see Maintainable#getDataObjectClass
     */
    @Override
    public Class getDataObjectClass() {
        return dataObjectClass;
    }

    /**
     * @see Maintainable#setDataObjectClass
     */
    @Override
    public void setDataObjectClass(Class dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * Persistable business objects are lockable
     *
     * @see Maintainable#isLockable
     */
    @Override
    public boolean isLockable() {
        return KRADServiceLocator.getPersistenceStructureService().isPersistable(getDataObject().getClass());
    }

    /**
     * Returns the data object if its persistable, null otherwise
     *
     * @see Maintainable#getPersistableBusinessObject
     */
    @Override
    public PersistableBusinessObject getPersistableBusinessObject() {
        if (KRADServiceLocator.getPersistenceStructureService().isPersistable(getDataObject().getClass())) {
            return (PersistableBusinessObject) getDataObject();
        } else {
            return null;
        }

    }

    /**
     * @see Maintainable#getMaintenanceAction
     */
    @Override
    public String getMaintenanceAction() {
        return maintenanceAction;
    }

    /**
     * @see Maintainable#setMaintenanceAction
     */
    @Override
    public void setMaintenanceAction(String maintenanceAction) {
        this.maintenanceAction = maintenanceAction;
    }

    /**
     * Note: as currently implemented, every key field for a given
     * data object class must have a visible getter
     *
     * @see Maintainable#generateMaintenanceLocks
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        StringBuffer lockRepresentation = new StringBuffer(dataObjectClass.getName());
        lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_CLASS_DELIM);

        Object bo = getDataObject();
        List keyFieldNames = getDocumentDictionaryService().getLockingKeys(getDocumentTypeName());

        for (Iterator i = keyFieldNames.iterator(); i.hasNext(); ) {
            String fieldName = (String) i.next();
            Object fieldValue = ObjectUtils.getPropertyValue(bo, fieldName);
            if (fieldValue == null) {
                fieldValue = "";
            }

            // check if field is a secure
            if (getDataObjectAuthorizationService()
                    .attributeValueNeedsToBeEncryptedOnFormsAndLinks(dataObjectClass, fieldName)) {
                try {
                    if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                        fieldValue = getEncryptionService().encrypt(fieldValue);
                    }
                } catch (GeneralSecurityException e) {
                    LOG.error("Unable to encrypt secure field for locking representation " + e.getMessage());
                    throw new RuntimeException(
                            "Unable to encrypt secure field for locking representation " + e.getMessage());
                }
            }

            lockRepresentation.append(fieldName);
            lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_FIELDNAME_DELIM);
            lockRepresentation.append(String.valueOf(fieldValue));
            if (i.hasNext()) {
                lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_VALUE_DELIM);
            }
        }

        MaintenanceLock maintenanceLock = new MaintenanceLock();
        maintenanceLock.setDocumentNumber(documentNumber);
        maintenanceLock.setLockingRepresentation(lockRepresentation.toString());
        maintenanceLocks.add(maintenanceLock);

        return maintenanceLocks;
    }

    /**
     * Retrieves the document type name from the data dictionary based on
     * business object class
     */
    protected String getDocumentTypeName() {
        return getDocumentDictionaryService().getMaintenanceDocumentTypeName(dataObjectClass);
    }

    /**
     * @see Maintainable#saveDataObject
     */
    @Override
    public void saveDataObject() {
        if (dataObject instanceof PersistableBusinessObject) {
            getBusinessObjectService().linkAndSave((PersistableBusinessObject) dataObject);
        } else {
            throw new RuntimeException(
                    "Cannot save object of type: " + dataObjectClass + " with business object service");
        }
    }

    /**
     * @see Maintainable#deleteDataObject
     */
    @Override
    public void deleteDataObject() {
        if (dataObject == null) {
            return;
        }

        if (dataObject instanceof PersistableBusinessObject) {
            getBusinessObjectService().delete((PersistableBusinessObject) dataObject);
            dataObject = null;
        } else {
            throw new RuntimeException(
                    "Cannot delete object of type: " + dataObjectClass + " with business object service");
        }
    }

    /**
     * @see Maintainable#doRouteStatusChange
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        // no default implementation
    }

    /**
     * @see Maintainable#getLockingDocumentId
     */
    @Override
    public String getLockingDocumentId() {
        return getMaintenanceDocumentService().getLockingDocumentId(this, documentNumber);
    }

    /**
     * @see Maintainable#getWorkflowEngineDocumentIdsToLock
     */
    @Override
    public List<String> getWorkflowEngineDocumentIdsToLock() {
        return null;
    }

    /**
     * @see Maintainable#isNotesEnabled
     */
    @Override
    public boolean isNotesEnabled() {
        return getDataObjectMetaDataService().areNotesSupported(dataObjectClass);
    }

    /**
     * @see MaintainableImpl#isExternalBusinessObject
     */
    @Override
    public boolean isExternalBusinessObject() {
        return false;
    }

    /**
     * @see MaintainableImpl#prepareExternalBusinessObject
     */
    @Override
    public void prepareExternalBusinessObject(BusinessObject businessObject) {
        // by default do nothing
    }

    /**
     * Checks whether the data object is not null and has its primary key values populated
     *
     * @see MaintainableImpl#isOldDataObjectInDocument
     */
    @Override
    public boolean isOldDataObjectInDocument() {
        boolean isOldDataObjectInExistence = true;

        if (getDataObject() == null) {
            isOldDataObjectInExistence = false;
        } else {
            Map<String, ?> keyFieldValues = getDataObjectMetaDataService().getPrimaryKeyFieldValues(getDataObject());
            for (Object keyValue : keyFieldValues.values()) {
                if (keyValue == null) {
                    isOldDataObjectInExistence = false;
                } else if ((keyValue instanceof String) && StringUtils.isBlank((String) keyValue)) {
                    isOldDataObjectInExistence = false;
                }

                if (!isOldDataObjectInExistence) {
                    break;
                }
            }
        }

        return isOldDataObjectInExistence;
    }

    /**
     * @see Maintainable#prepareForSave
     */
    @Override
    public void prepareForSave() {
        // by default do nothing
    }

    /**
     * @see Maintainable#processAfterRetrieve
     */
    @Override
    public void processAfterRetrieve() {
        // by default do nothing
    }

    /**
     * @see MaintainableImpl#setupNewFromExisting
     */
    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        // by default do nothing
    }

    /**
     * @see Maintainable#processAfterCopy
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        // by default do nothing
    }

    /**
     * @see Maintainable#processAfterEdit
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        // by default do nothing
    }

    /**
     * @see Maintainable#processAfterNew
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        // by default do nothing
    }

    /**
     * @see Maintainable#processAfterPost
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        // by default do nothing
    }

    /**
     * In the case of edit maintenance adds a new blank line to the old side
     *
     * TODO: should this write some sort of missing message on the old side
     * instead?
     *
     * @see ViewHelperServiceImpl#processAfterAddLine(View,
     *      CollectionGroup, java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    protected void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
        super.processAfterAddLine(view, collectionGroup, model, addLine);
        
        // Check for maintenance documents in edit but exclude notes
        if (model instanceof MaintenanceForm && KRADConstants.MAINTENANCE_EDIT_ACTION.equals(((MaintenanceForm)model).getMaintenanceAction()) && !(addLine instanceof Note)) {
            MaintenanceForm maintenanceForm = (MaintenanceForm) model;
            MaintenanceDocument document = maintenanceForm.getDocument();

            // get the old object's collection
            //KULRICE-7970 support multiple level objects
            String bindingPrefix = collectionGroup.getBindingInfo().getBindByNamePrefix();
            String propertyPath = collectionGroup.getPropertyName();
            if(bindingPrefix!=""&&bindingPrefix!= null)     {
                propertyPath = bindingPrefix + "." + propertyPath;
            }

            Collection<Object> oldCollection = ObjectPropertyUtils
                    .getPropertyValue(document.getOldMaintainableObject().getDataObject(),
                            propertyPath);

            try {
                Object blankLine = collectionGroup.getCollectionObjectClass().newInstance();
                oldCollection.add(blankLine);
            } catch (Exception e) {
                throw new RuntimeException("Unable to create new line instance for old maintenance object", e);
            }
        }
    }
    
    /**
     * In the case of edit maintenance deleted the item on the old side
     *
     *
     * @see ViewHelperServiceImpl#processAfterDeleteLine(View,
     *      CollectionGroup, java.lang.Object,  int)
     */
    @Override
    protected void processAfterDeleteLine(View view, CollectionGroup collectionGroup, Object model, int lineIndex) {
        super.processAfterDeleteLine(view, collectionGroup, model, lineIndex);
        
        // Check for maintenance documents in edit but exclude notes
        if (model instanceof MaintenanceForm && KRADConstants.MAINTENANCE_EDIT_ACTION.equals(((MaintenanceForm)model).getMaintenanceAction()) 
                && !collectionGroup.getCollectionObjectClass().getName().equals(Note.class.getName())) {
            MaintenanceForm maintenanceForm = (MaintenanceForm) model;
            MaintenanceDocument document = maintenanceForm.getDocument();

            // get the old object's collection
            Collection<Object> oldCollection = ObjectPropertyUtils
                    .getPropertyValue(document.getOldMaintainableObject().getDataObject(),
                            collectionGroup.getPropertyName());
            try {
                // Remove the object at lineIndex from the collection
                oldCollection.remove(oldCollection.toArray()[lineIndex]);
            } catch (Exception e) {
                throw new RuntimeException("Unable to delete line instance for old maintenance object", e);
            }
        }
    }    

    /**
     * Retrieves the document number configured on this maintainable
     *
     * @return String document number
     */
    protected String getDocumentNumber() {
        return this.documentNumber;
    }

    protected LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = KRADServiceLocatorWeb.getLookupService();
        }
        return this.lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    protected DataObjectAuthorizationService getDataObjectAuthorizationService() {
        if (dataObjectAuthorizationService == null) {
            this.dataObjectAuthorizationService = KRADServiceLocatorWeb.getDataObjectAuthorizationService();
        }
        return dataObjectAuthorizationService;
    }

    public void setDataObjectAuthorizationService(DataObjectAuthorizationService dataObjectAuthorizationService) {
        this.dataObjectAuthorizationService = dataObjectAuthorizationService;
    }

    protected DataObjectMetaDataService getDataObjectMetaDataService() {
        if (dataObjectMetaDataService == null) {
            this.dataObjectMetaDataService = KRADServiceLocatorWeb.getDataObjectMetaDataService();
        }
        return dataObjectMetaDataService;
    }

    public void setDataObjectMetaDataService(DataObjectMetaDataService dataObjectMetaDataService) {
        this.dataObjectMetaDataService = dataObjectMetaDataService;
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    protected EncryptionService getEncryptionService() {
        if (encryptionService == null) {
            encryptionService = CoreApiServiceLocator.getEncryptionService();
        }
        return encryptionService;
    }

    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected MaintenanceDocumentService getMaintenanceDocumentService() {
        if (maintenanceDocumentService == null) {
            maintenanceDocumentService = KRADServiceLocatorWeb.getMaintenanceDocumentService();
        }
        return maintenanceDocumentService;
    }

    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }
}
