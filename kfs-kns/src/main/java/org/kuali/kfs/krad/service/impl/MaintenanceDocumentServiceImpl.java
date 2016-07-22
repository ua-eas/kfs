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
package org.kuali.kfs.krad.service.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.kns.service.BusinessObjectMetaDataService;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.rice.core.framework.persistence.jta.TransactionalNoValidationExceptionRollback;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.dao.MaintenanceDocumentDao;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.maintenance.MaintenanceLock;
import org.kuali.kfs.krad.exception.DocumentTypeAuthorizationException;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.kfs.krad.service.DataObjectAuthorizationService;
import org.kuali.kfs.krad.service.DataObjectMetaDataService;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.MaintenanceDocumentService;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.springframework.beans.PropertyAccessorUtils;

/**
 * Service implementation for the MaintenanceDocument structure. This is the
 * default implementation, that is delivered with Kuali
 *
 * 
 */
@TransactionalNoValidationExceptionRollback
public class MaintenanceDocumentServiceImpl implements MaintenanceDocumentService {
    protected static final Logger LOG = Logger.getLogger(MaintenanceDocumentServiceImpl.class);

    private MaintenanceDocumentDao maintenanceDocumentDao;
    private DataObjectAuthorizationService dataObjectAuthorizationService;
    private DocumentService documentService;
    private DataObjectMetaDataService dataObjectMetaDataService;
    private DocumentDictionaryService documentDictionaryService;
    protected BusinessObjectMetaDataService businessObjectMetaDataService;
    protected IdentityService identityService;
    protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

    /**
     * @see MaintenanceDocumentService#setupNewMaintenanceDocument(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public MaintenanceDocument setupNewMaintenanceDocument(String objectClassName, String documentTypeName,
            String maintenanceAction) {
        if (StringUtils.isEmpty(objectClassName) && StringUtils.isEmpty(documentTypeName)) {
            throw new IllegalArgumentException("Document type name or bo class not given!");
        }

        // get document type if not passed
        if (StringUtils.isEmpty(documentTypeName)) {
            try {
                documentTypeName =
                        getDocumentDictionaryService().getMaintenanceDocumentTypeName(Class.forName(objectClassName));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (StringUtils.isEmpty(documentTypeName)) {
                throw new RuntimeException(
                        "documentTypeName is empty; does this Business Object have a maintenance document definition? " +
                                objectClassName);
            }
        }

        // check doc type allows new or copy if that action was requested
        if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction) ||
                KRADConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
            Class<?> boClass =
                    getDocumentDictionaryService().getMaintenanceDataObjectClass(documentTypeName);
            boolean allowsNewOrCopy = getDataObjectAuthorizationService()
                    .canCreate(boClass, GlobalVariables.getUserSession().getPerson(), documentTypeName);
            if (!allowsNewOrCopy) {
                LOG.error("Document type " + documentTypeName + " does not allow new or copy actions.");
                throw new DocumentTypeAuthorizationException(
                        GlobalVariables.getUserSession().getPerson().getPrincipalId(), "newOrCopy", documentTypeName);
            }
        }

        // get new document from service
        try {
            return (MaintenanceDocument) getDocumentService().getNewDocument(documentTypeName);
        } catch (WorkflowException e) {
            LOG.error("Cannot get new maintenance document instance for doc type: " + documentTypeName, e);
            throw new RuntimeException("Cannot get new maintenance document instance for doc type: " + documentTypeName,
                    e);
        }
    }

    /**
     * @see MaintenanceDocumentServiceImpl#setupMaintenanceObject
     */
    @Override
    public void setupMaintenanceObject(MaintenanceDocument document, String maintenanceAction,
            Map<String, String[]> requestParameters) {
        document.getNewMaintainableObject().setMaintenanceAction(maintenanceAction);
        document.getOldMaintainableObject().setMaintenanceAction(maintenanceAction);

        // if action is edit or copy first need to retrieve the old record
        if (!KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction) &&
                !KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
            Object oldDataObject = retrieveObjectForMaintenance(document, requestParameters);

            // TODO should we be using ObjectUtils? also, this needs dictionary
            // enhancement to indicate fields to/not to copy
            Object newDataObject = ObjectUtils.deepCopy((Serializable) oldDataObject);

            // set object instance for editing
            document.getOldMaintainableObject().setDataObject(oldDataObject);
            document.getNewMaintainableObject().setDataObject(newDataObject);

            // process further object preparations for copy action
            if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
                processMaintenanceObjectForCopy(document, newDataObject, requestParameters);
            } else {
                checkMaintenanceActionAuthorization(document, oldDataObject, maintenanceAction, requestParameters);
            }
        }

        // if new with existing we need to populate with passed in parameters
        if (KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
            Object newBO = document.getNewMaintainableObject().getDataObject();
            Map<String, String> parameters =
                    buildKeyMapFromRequest(requestParameters, document.getNewMaintainableObject().getDataObjectClass());
            ObjectPropertyUtils.copyPropertiesToObject(parameters, newBO);
            if (newBO instanceof PersistableBusinessObject) {
                ((PersistableBusinessObject) newBO).refresh();
            }

            document.getNewMaintainableObject().setupNewFromExisting(document, requestParameters);
        } else if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
            document.getNewMaintainableObject().processAfterNew(document, requestParameters);
        }
    }

    /**
     * For the edit and delete maintenance actions checks with the
     * <code>BusinessObjectAuthorizationService</code> to check whether the
     * action is allowed for the record data. In action is allowed invokes the
     * custom processing hook on the <code>Maintainble</code>.
     *
     * @param document - document instance for the maintenance object
     * @param oldBusinessObject - the old maintenance record
     * @param maintenanceAction - type of maintenance action requested
     * @param requestParameters - map of parameters from the request
     */
    protected void checkMaintenanceActionAuthorization(MaintenanceDocument document, Object oldBusinessObject,
            String maintenanceAction, Map<String, String[]> requestParameters) {
        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction)) {
            boolean allowsEdit = getDataObjectAuthorizationService()
                    .canMaintain(oldBusinessObject, GlobalVariables.getUserSession().getPerson(),
                            document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            if (!allowsEdit) {
                LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName() +
                        " does not allow edit actions.");
                throw new DocumentTypeAuthorizationException(
                        GlobalVariables.getUserSession().getPerson().getPrincipalId(), "edit",
                        document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            }

            // invoke custom processing method
            document.getNewMaintainableObject().processAfterEdit(document, requestParameters);
        }
        // 3070
        else if (KRADConstants.MAINTENANCE_DELETE_ACTION.equals(maintenanceAction)) {
            boolean allowsDelete = getDataObjectAuthorizationService()
                    .canMaintain((BusinessObject) oldBusinessObject, GlobalVariables.getUserSession().getPerson(),
                            document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            if (!allowsDelete) {
                LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName() +
                        " does not allow delete actions.");
                throw new DocumentTypeAuthorizationException(
                        GlobalVariables.getUserSession().getPerson().getPrincipalId(), "delete",
                        document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            }
        }
    }

    /**
     * For the edit or copy actions retrieves the record that is to be
     * maintained
     *
     * <p>
     * Based on the persistence metadata for the maintenance object class
     * retrieves the primary key values from the given request parameters map
     * (if the class is persistable). With those key values attempts to find the
     * record using the <code>LookupService</code>.
     * </p>
     *
     * @param document - document instance for the maintenance object
     * @param requestParameters - Map of parameters from the request
     * @return Object the retrieved old object
     */
    protected Object retrieveObjectForMaintenance(MaintenanceDocument document,
            Map<String, String[]> requestParameters) {
        Map<String, String> keyMap =
                buildKeyMapFromRequest(requestParameters, document.getNewMaintainableObject().getDataObjectClass());

        Object oldDataObject = document.getNewMaintainableObject().retrieveObjectForEditOrCopy(document, keyMap);

        if (oldDataObject == null && !document.getOldMaintainableObject().isExternalBusinessObject()) {
            throw new RuntimeException(
                    "Cannot retrieve old record for maintenance document, incorrect parameters passed on maint url: " +
                            requestParameters);
        }

        if (document.getOldMaintainableObject().isExternalBusinessObject()) {
            if (oldDataObject == null) {
                try {
                    oldDataObject = document.getOldMaintainableObject().getDataObjectClass().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(
                            "External BO maintainable was null and unable to instantiate for old maintainable object.",
                            ex);
                }
            }

            populateMaintenanceObjectWithCopyKeyValues(KRADUtils.translateRequestParameterMap(requestParameters),
                    oldDataObject, document.getOldMaintainableObject());
            document.getOldMaintainableObject().prepareExternalBusinessObject((PersistableBusinessObject) oldDataObject);
            oldDataObject = document.getOldMaintainableObject().getDataObject();
        }

        return oldDataObject;
    }

    /**
     * For the copy action clears out primary key values for the old record and
     * does authorization checks on the remaining fields. Also invokes the
     * custom processing method on the <code>Maintainble</code>
     *
     * @param document - document instance for the maintenance object
     * @param maintenanceObject - the object instance being maintained
     * @param requestParameters - map of parameters from the request
     */
    protected void processMaintenanceObjectForCopy(MaintenanceDocument document, Object maintenanceObject,
            Map<String, String[]> requestParameters) {
        if (!document.isFieldsClearedOnCopy()) {
            Maintainable maintainable = document.getNewMaintainableObject();
            if (!getDocumentDictionaryService().getPreserveLockingKeysOnCopy(maintainable.getDataObjectClass())) {
                clearPrimaryKeyFields(maintenanceObject, maintainable.getDataObjectClass());
            }

            clearUnauthorizedNewFields(document);

            maintainable.processAfterCopy(document, requestParameters);

            // mark so that this clearing does not happen again
            document.setFieldsClearedOnCopy(true);
        }
    }

    /**
     * Clears the value of the primary key fields on the maintenance object
     *
     * @param document - document to clear the pk fields on
     * @param dataObjectClass - class to use for retrieving primary key metadata
     */
    protected void clearPrimaryKeyFields(Object maintenanceObject, Class<?> dataObjectClass) {
        List<String> keyFieldNames = getDataObjectMetaDataService().listPrimaryKeyFieldNames(dataObjectClass);
        for (String keyFieldName : keyFieldNames) {
            ObjectPropertyUtils.setPropertyValue(maintenanceObject, keyFieldName, null);
        }
    }

    /**
     * Used as part of the Copy functionality, to clear any field values that
     * the user making the copy does not have permissions to modify. This will
     * prevent authorization errors on a copy.
     *
     * @param document - document to be adjusted
     */
    protected void clearUnauthorizedNewFields(MaintenanceDocument document) {
        // get a reference to the current user
        Person user = GlobalVariables.getUserSession().getPerson();

        // get a new instance of MaintenanceDocumentAuthorizations for context
        // TODO: rework for KRAD
//        MaintenanceDocumentRestrictions maintenanceDocumentRestrictions =
//                getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(document, user);
//
//        clearBusinessObjectOfRestrictedValues(maintenanceDocumentRestrictions);
    }

    /**
     * Based on the maintenance object class retrieves the key field names from
     * the <code>BusinessObjectMetaDataService</code> (or alternatively from the
     * request parameters), then retrieves any matching key value pairs from the
     * request parameters
     *
     * @param requestParameters - map of parameters from the request
     * @param dataObjectClass - class to use for checking security parameter restrictions
     * @return Map<String, String> key value pairs
     */
    protected Map<String, String> buildKeyMapFromRequest(Map<String, String[]> requestParameters,
            Class<?> dataObjectClass) {
        List<String> keyFieldNames = null;

        // translate request parameters
        Map<String, String> parameters = KRADUtils.translateRequestParameterMap(requestParameters);

        // are override keys listed in the request? If so, then those need to be
        // our keys, not the primary key fields for the BO
        if (!StringUtils.isBlank(parameters.get(KRADConstants.OVERRIDE_KEYS))) {
            String[] overrideKeys =
                    parameters.get(KRADConstants.OVERRIDE_KEYS).split(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            keyFieldNames = Arrays.asList(overrideKeys);
        } else {
            keyFieldNames = getDataObjectMetaDataService().listPrimaryKeyFieldNames(dataObjectClass);
        }

        return KRADUtils.getParametersFromRequest(keyFieldNames, dataObjectClass, parameters);
    }

    /**
     * Looks for a special request parameters giving the names of the keys that
     * should be retrieved from the request parameters and copied to the
     * maintenance object
     *
     * @param parameters - map of parameters from the request
     * @param oldBusinessObject - the old maintenance object
     * @param oldMaintainableObject - the old maintainble object (used to get object class for
     * security checks)
     */
    protected void populateMaintenanceObjectWithCopyKeyValues(Map<String, String> parameters, Object oldBusinessObject,
            Maintainable oldMaintainableObject) {
        List<String> keyFieldNamesToCopy = null;
        Map<String, String> parametersToCopy = null;

        if (!StringUtils.isBlank(parameters.get(KRADConstants.COPY_KEYS))) {
            String[] copyKeys =
                    parameters.get(KRADConstants.COPY_KEYS).split(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            keyFieldNamesToCopy = Arrays.asList(copyKeys);
            parametersToCopy = KRADUtils
                    .getParametersFromRequest(keyFieldNamesToCopy, oldMaintainableObject.getDataObjectClass(),
                            parameters);
        }

        if (parametersToCopy != null) {
            // TODO: make sure we are doing formatting here eventually
            ObjectPropertyUtils.copyPropertiesToObject(parametersToCopy, oldBusinessObject);
        }
    }

    /**
     * @see MaintenanceDocumentService#getLockingDocumentId(MaintenanceDocument)
     */
    public String getLockingDocumentId(MaintenanceDocument document) {
        return getLockingDocumentId(document.getNewMaintainableObject(), document.getDocumentNumber());
    }

    /**
     * @see MaintenanceDocumentService#getLockingDocumentId(Maintainable,
     *      java.lang.String)
     */
    public String getLockingDocumentId(Maintainable maintainable, String documentNumber) {
        String lockingDocId = null;
        List<MaintenanceLock> maintenanceLocks = maintainable.generateMaintenanceLocks();
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = maintenanceDocumentDao
                    .getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(), documentNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }
        return lockingDocId;
    }

    /**
     * @see MaintenanceDocumentService#deleteLocks(String)
     */
    public void deleteLocks(String documentNumber) {
        maintenanceDocumentDao.deleteLocks(documentNumber);
    }

    /**
     * @see MaintenanceDocumentService#saveLocks(List)
     */
    public void storeLocks(List<MaintenanceLock> maintenanceLocks) {
        maintenanceDocumentDao.storeLocks(maintenanceLocks);
    }

    /**
     * This was copied from PersonService and changed a bit - so that we had access to cf's data dictionary
     * @see org.kuali.rice.kim.api.identity.PersonService#resolvePrincipalNamesToPrincipalIds(org.kuali.rice.krad.bo.BusinessObject, java.util.Map)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String,String> resolvePrincipalNamesToPrincipalIds(BusinessObject businessObject, Map<String,String> fieldValues) {
        if ( fieldValues == null ) {
            return null;
        }
        if ( businessObject == null ) {
            return fieldValues;
        }
        StringBuffer resolvedPrincipalIdPropertyName = new StringBuffer();
        // save off all criteria which are not references to Person properties
        // leave person properties out so they can be resolved and replaced by this method
        Map<String,String> processedFieldValues = getNonPersonSearchCriteria(businessObject, fieldValues);
        for ( String propertyName : fieldValues.keySet() ) {
            if (	!StringUtils.isBlank(fieldValues.get(propertyName))  // property has a value
                    && isPersonProperty(businessObject, propertyName) // is a property on a Person object
                    ) {
                // strip off the prefix on the property
                int lastPropertyIndex = PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(propertyName);
                String personPropertyName = lastPropertyIndex != -1 ? StringUtils.substring(propertyName, lastPropertyIndex + 1) : propertyName;
                // special case - the user ID
                if ( StringUtils.equals( KIMPropertyConstants.Person.PRINCIPAL_NAME, personPropertyName) ) {
                    Class targetBusinessObjectClass = null;
                    BusinessObject targetBusinessObject = null;
                    resolvedPrincipalIdPropertyName.setLength( 0 ); // clear the buffer without requiring a new object allocation on each iteration
                    // get the property name up until the ".principalName"
                    // this should be a reference to the Person object attached to the BusinessObject
                    String personReferenceObjectPropertyName =  lastPropertyIndex != -1 ? StringUtils.substring(propertyName, 0, lastPropertyIndex) : StringUtils.EMPTY;
                    // check if the person was nested within another BO under the master BO.  If so, go up one more level
                    // otherwise, use the passed in BO class as the target class
                    if ( ObjectUtils.isNestedAttribute( personReferenceObjectPropertyName ) ) {
                        String targetBusinessObjectPropertyName = ObjectUtils.getNestedAttributePrefix( personReferenceObjectPropertyName );
                        targetBusinessObject = (BusinessObject)ObjectUtils.getPropertyValue( businessObject, targetBusinessObjectPropertyName );
                        if (targetBusinessObject != null) {
                            targetBusinessObjectClass = targetBusinessObject.getClass();
                            resolvedPrincipalIdPropertyName.append(targetBusinessObjectPropertyName).append(".");
                        } else {
                            LOG.error("Could not find target property '"+propertyName+"' in class "+businessObject.getClass().getName()+". Property value was null.");
                        }
                    } else { // not a nested Person property
                        targetBusinessObjectClass = businessObject.getClass();
                        targetBusinessObject = businessObject;
                    }

                    if (targetBusinessObjectClass != null) {
                        // use the relationship metadata in the KNS to determine the property on the
                        // host business object to put back into the map now that the principal ID
                        // (the value stored in application tables) has been resolved
                        int lastIndex = PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(personReferenceObjectPropertyName);
                        String propName = lastIndex != -1 ? StringUtils.substring(personReferenceObjectPropertyName, lastIndex + 1) : personReferenceObjectPropertyName;
                        DataObjectRelationship rel = getBusinessObjectMetaDataService().getBusinessObjectRelationship( targetBusinessObject, propName );
                        if ( rel != null ) {
                            String sourcePrimitivePropertyName = rel.getParentAttributeForChildAttribute(KIMPropertyConstants.Person.PRINCIPAL_ID);
                            resolvedPrincipalIdPropertyName.append(sourcePrimitivePropertyName);
                            // get the principal - for translation of the principalName to principalId
                            String principalName = fieldValues.get( propertyName );
                            Principal principal = getIdentityService().getPrincipalByPrincipalName( principalName );
                            if (principal != null ) {
                                processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), principal.getPrincipalId());
                            } else {
                                processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), null);
                                try {
                                    // if the principalName is bad, then we need to clear out the Person object
                                    // and base principalId property
                                    // so that their values are no longer accidentally used or re-populate
                                    // the object
                                    ObjectUtils.setObjectProperty(targetBusinessObject,
                                            resolvedPrincipalIdPropertyName.toString(), null);
                                    ObjectUtils.setObjectProperty(targetBusinessObject, propName, null );
                                    ObjectUtils.setObjectProperty(targetBusinessObject, propName + ".principalName", principalName );
                                } catch ( Exception ex ) {
                                    LOG.error( "Unable to blank out the person object after finding that the person with the given principalName does not exist.", ex );
                                }
                            }
                        } else {
                            LOG.error( "Missing relationship for " + propName + " on " + targetBusinessObjectClass.getName() );
                        }
                    } else { // no target BO class - the code below probably will not work
                        processedFieldValues.put(resolvedPrincipalIdPropertyName.toString(), null);
                    }
                }
                // if the property does not seem to match the definition of a Person property but it
                // does end in principalName then...
                // this is to handle the case where the user ID is on an ADD line - a case excluded from isPersonProperty()
            } else if (propertyName.endsWith("." + KIMPropertyConstants.Person.PRINCIPAL_NAME)){
                // if we're adding to a collection and we've got the principalName; let's populate universalUser
                String principalName = fieldValues.get(propertyName);
                if ( StringUtils.isNotEmpty( principalName ) ) {
                    String containerPropertyName = propertyName;
                    if (containerPropertyName.startsWith(KRADConstants.MAINTENANCE_ADD_PREFIX)) {
                        containerPropertyName = StringUtils.substringAfter( propertyName, KRADConstants.MAINTENANCE_ADD_PREFIX );
                    }
                    // get the class of the object that is referenced by the property name
                    // if this is not true then there's a principalName collection or primitive attribute
                    // directly on the BO on the add line, so we just ignore that since something is wrong here
                    if ( PropertyAccessorUtils.isNestedOrIndexedProperty(containerPropertyName) ) {
                        // the first part of the property is the collection name
                        String collectionName = StringUtils.substringBefore( containerPropertyName, "." );
                        // what is the class held by that collection?
                        // JHK: I don't like this.  This assumes that this method is only used by the maintenance
                        // document service.  If that will always be the case, this method should be moved over there.
                        Class<? extends BusinessObject> collectionBusinessObjectClass = getMaintenanceDocumentDictionaryService()
                                .getCollectionBusinessObjectClass(
                                        getMaintenanceDocumentDictionaryService()
                                                .getDocumentTypeName(businessObject.getClass()), collectionName);
                        if (collectionBusinessObjectClass != null) {
                            // we are adding to a collection; get the relationships for that object;
                            // is there one for personUniversalIdentifier?
                            List<DataObjectRelationship> relationships =
                                    getBusinessObjectMetaDataService().getBusinessObjectRelationships( collectionBusinessObjectClass );
                            // JHK: this seems like a hack - looking at all relationships for a BO does not guarantee that we get the right one
                            // JHK: why not inspect the objects like above?  Is it the property path problems because of the .add. portion?
                            for ( DataObjectRelationship rel : relationships ) {
                                String parentAttribute = rel.getParentAttributeForChildAttribute( KIMPropertyConstants.Person.PRINCIPAL_ID );
                                if ( parentAttribute == null ) {
                                    continue;
                                }
                                // there is a relationship for personUserIdentifier; use that to find the universal user
                                processedFieldValues.remove( propertyName );
                                String fieldPrefix = StringUtils.substringBeforeLast( StringUtils.substringBeforeLast( propertyName, "." + KIMPropertyConstants.Person.PRINCIPAL_NAME ), "." );
                                String relatedPrincipalIdPropertyName = fieldPrefix + "." + parentAttribute;
                                // KR-683 Special handling for extension objects
                                if(KRADPropertyConstants.EXTENSION.equals(StringUtils.substringAfterLast(fieldPrefix, ".")) && KRADPropertyConstants.EXTENSION.equals(StringUtils.substringBefore(parentAttribute, ".")))
                                {
                                    relatedPrincipalIdPropertyName = fieldPrefix + "." + StringUtils.substringAfter(parentAttribute, ".");
                                }
                                String currRelatedPersonPrincipalId = processedFieldValues.get(relatedPrincipalIdPropertyName);
                                if ( StringUtils.isBlank( currRelatedPersonPrincipalId ) ) {
                                    Principal principal = getIdentityService().getPrincipalByPrincipalName( principalName );
                                    if ( principal != null ) {
                                        processedFieldValues.put(relatedPrincipalIdPropertyName, principal.getPrincipalId());
                                    } else {
                                        processedFieldValues.put(relatedPrincipalIdPropertyName, null);
                                    }
                                }
                            } // relationship loop
                        } else {
                            if ( LOG.isDebugEnabled() ) {
                                LOG.debug( "Unable to determine class for collection referenced as part of property: " + containerPropertyName + " on " + businessObject.getClass().getName() );
                            }
                        }
                    } else {
                        if ( LOG.isDebugEnabled() ) {
                            LOG.debug( "Non-nested property ending with 'principalName': " + containerPropertyName + " on " + businessObject.getClass().getName() );
                        }
                    }
                }
            }
        }
        return processedFieldValues;
    }

    /**
     * copied from PersonService, here where we can access the cf data dictionary
     * Builds a map containing entries from the passed in Map that do NOT represent properties on an embedded
     * Person object.
     */
    protected Map<String,String> getNonPersonSearchCriteria( Object bo, Map<String,String> fieldValues) {
        Map<String,String> nonUniversalUserSearchCriteria = new HashMap<String,String>();
        for ( String propertyName : fieldValues.keySet() ) {
            if (!isPersonProperty(bo, propertyName)) {
                nonUniversalUserSearchCriteria.put(propertyName, fieldValues.get(propertyName));
            }
        }
        return nonUniversalUserSearchCriteria;
    }

    /**
     * copied from PersonService, here where we can access the cf data dictionary
     * @param bo
     * @param propertyName
     * @return
     */
    protected boolean isPersonProperty(Object bo, String propertyName) {
        try {
            if (PropertyAccessorUtils.isNestedOrIndexedProperty( propertyName ) // is a nested property
                    && !StringUtils.contains(propertyName, "add.") ) {// exclude add line properties (due to path parsing problems in PropertyUtils.getPropertyType)
                int lastIndex = PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(propertyName);
                String propertyTypeName = lastIndex != -1 ? StringUtils.substring(propertyName, 0, lastIndex) : StringUtils.EMPTY;
                Class<?> type = PropertyUtils.getPropertyType(bo, propertyTypeName);
                // property type indicates a Person object
                if ( type != null ) {
                    return Person.class.isAssignableFrom(type);
                }
                LOG.warn( "Unable to determine type of nested property: " + bo.getClass().getName() + " / " + propertyName );
            }
        } catch (Exception ex) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Unable to determine if property on " + bo.getClass().getName() + " to a person object: " + propertyName, ex );
            }
        }
        return false;
    }


    public MaintenanceDocumentDao getMaintenanceDocumentDao() {
        return maintenanceDocumentDao;
    }

    public void setMaintenanceDocumentDao(MaintenanceDocumentDao maintenanceDocumentDao) {
        this.maintenanceDocumentDao = maintenanceDocumentDao;
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

    protected DocumentService getDocumentService() {
        return this.documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    protected DataObjectMetaDataService getDataObjectMetaDataService() {
        if (dataObjectMetaDataService == null) {
            dataObjectMetaDataService = KRADServiceLocatorWeb.getDataObjectMetaDataService();
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

    public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        if (businessObjectMetaDataService == null) {
            this.businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
        }
        return businessObjectMetaDataService;
    }

    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        this.businessObjectMetaDataService = businessObjectMetaDataService;
    }

    public IdentityService getIdentityService() {
        if (identityService == null) {
            this.identityService = KimApiServiceLocator.getIdentityService();
        }
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        if (maintenanceDocumentDictionaryService == null) {
            maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        }
        return maintenanceDocumentDictionaryService;
    }

    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }
}
