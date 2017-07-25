/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.kns.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.authorization.BusinessObjectAuthorizer;
import org.kuali.kfs.kns.bo.authorization.InquiryOrMaintenanceDocumentAuthorizer;
import org.kuali.kfs.kns.bo.authorization.InquiryOrMaintenanceDocumentPresentationController;
import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.kns.datadictionary.FieldDefinition;
import org.kuali.kfs.kns.datadictionary.InquiryCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.InquirySectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictionsBase;
import org.kuali.kfs.kns.document.authorization.InquiryOrMaintenanceDocumentRestrictions;
import org.kuali.kfs.kns.document.authorization.InquiryOrMaintenanceDocumentRestrictionsBase;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentPresentationController;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentRestrictionsBase;
import org.kuali.kfs.kns.inquiry.InquiryAuthorizer;
import org.kuali.kfs.kns.inquiry.InquiryPresentationController;
import org.kuali.kfs.kns.inquiry.InquiryRestrictions;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.kns.service.BusinessObjectDictionaryService;
import org.kuali.kfs.kns.service.DocumentHelperService;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.kfs.krad.bo.DataObjectAuthorizer;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.kfs.krad.datadictionary.DataObjectEntry;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.impl.DataObjectAuthorizationServiceImpl;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.BusinessObject;

public class BusinessObjectAuthorizationServiceImpl extends DataObjectAuthorizationServiceImpl implements BusinessObjectAuthorizationService {
    private DataDictionaryService dataDictionaryService;
    private PermissionService permissionService;
    private BusinessObjectDictionaryService businessObjectDictionaryService;
    private DocumentHelperService documentHelperService;
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    private ConfigurationService kualiConfigurationService;

    public BusinessObjectRestrictions getLookupResultRestrictions(
        Object dataObject, Person user) {
        BusinessObjectRestrictions businessObjectRestrictions = new BusinessObjectRestrictionsBase();
        considerBusinessObjectFieldUnmaskAuthorization(dataObject, user,
            businessObjectRestrictions, "", null);
        return businessObjectRestrictions;
    }

    public InquiryRestrictions getInquiryRestrictions(
        BusinessObject businessObject, Person user) {
        InquiryRestrictions inquiryRestrictions = new InquiryOrMaintenanceDocumentRestrictionsBase();
        BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry) getDataDictionaryService()
            .getDataDictionary().getBusinessObjectEntry(
                businessObject.getClass().getName());
        InquiryPresentationController inquiryPresentationController = getBusinessObjectDictionaryService()
            .getInquiryPresentationController(businessObject.getClass());
        InquiryAuthorizer inquiryAuthorizer = getBusinessObjectDictionaryService()
            .getInquiryAuthorizer(businessObject.getClass());
        considerBusinessObjectFieldUnmaskAuthorization(businessObject, user,
            inquiryRestrictions, "", null);
        considerBusinessObjectFieldViewAuthorization(businessObjectEntry,
            businessObject, null, user, inquiryAuthorizer, inquiryRestrictions,
            "");
        considerInquiryOrMaintenanceDocumentPresentationController(
            inquiryPresentationController, businessObject,
            inquiryRestrictions);
        considerInquiryOrMaintenanceDocumentAuthorizer(inquiryAuthorizer,
            businessObject, user, inquiryRestrictions);
        for (InquirySectionDefinition inquirySectionDefinition : businessObjectEntry.getInquiryDefinition().getInquirySections()) {
            if (inquirySectionDefinition.getInquiryCollections() != null) {
                addInquirableItemRestrictions(inquirySectionDefinition.getInquiryCollections().values(), inquiryAuthorizer,
                    inquiryRestrictions, businessObject, businessObject, "", user);
            }
            // Collections may also be stored in the inquiry fields, so we need to parse through that
            List<FieldDefinition> inquiryFields = inquirySectionDefinition.getInquiryFields();
            if (inquiryFields != null) {
                for (FieldDefinition fieldDefinition : inquiryFields) {
                    addInquirableItemRestrictions(inquiryFields, inquiryAuthorizer,
                        inquiryRestrictions, businessObject, businessObject, "", user);
                }
            }
        }

        return inquiryRestrictions;
    }

    public MaintenanceDocumentRestrictions getMaintenanceDocumentRestrictions(
        MaintenanceDocument maintenanceDocument, Person user) {

        MaintenanceDocumentRestrictions maintenanceDocumentRestrictions = new MaintenanceDocumentRestrictionsBase();
        DataObjectEntry dataObjectEntry = getDataDictionaryService()
            .getDataDictionary().getDataObjectEntry(
                maintenanceDocument.getNewMaintainableObject()
                    .getDataObject().getClass().getName());
        MaintenanceDocumentPresentationController maintenanceDocumentPresentationController = (MaintenanceDocumentPresentationController) getDocumentHelperService()
            .getDocumentPresentationController(maintenanceDocument);
        MaintenanceDocumentAuthorizer maintenanceDocumentAuthorizer = (MaintenanceDocumentAuthorizer) getDocumentHelperService()
            .getDocumentAuthorizer(maintenanceDocument);
        considerBusinessObjectFieldUnmaskAuthorization(maintenanceDocument
                .getNewMaintainableObject().getDataObject(), user,
            maintenanceDocumentRestrictions, "", maintenanceDocument);
        considerBusinessObjectFieldViewAuthorization(dataObjectEntry,
            maintenanceDocument.getNewMaintainableObject().getDataObject(),
            null, user, maintenanceDocumentAuthorizer,
            maintenanceDocumentRestrictions, "");
        considerBusinessObjectFieldModifyAuthorization(dataObjectEntry,
            maintenanceDocument.getNewMaintainableObject().getDataObject(),
            null, user, maintenanceDocumentAuthorizer,
            maintenanceDocumentRestrictions, "");
        considerCustomButtonFieldAuthorization(dataObjectEntry,
            maintenanceDocument.getNewMaintainableObject().getDataObject(),
            null, user, maintenanceDocumentAuthorizer,
            maintenanceDocumentRestrictions, "");
        considerInquiryOrMaintenanceDocumentPresentationController(
            maintenanceDocumentPresentationController, maintenanceDocument,
            maintenanceDocumentRestrictions);
        considerInquiryOrMaintenanceDocumentAuthorizer(
            maintenanceDocumentAuthorizer, maintenanceDocument, user,
            maintenanceDocumentRestrictions);
        considerMaintenanceDocumentPresentationController(
            maintenanceDocumentPresentationController, maintenanceDocument,
            maintenanceDocumentRestrictions);
        considerMaintenanceDocumentAuthorizer(maintenanceDocumentAuthorizer,
            maintenanceDocument, user, maintenanceDocumentRestrictions);

        MaintenanceDocumentEntry maintenanceDocumentEntry = getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(maintenanceDocument
            .getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        for (MaintainableSectionDefinition maintainableSectionDefinition : maintenanceDocumentEntry.getMaintainableSections()) {
            addMaintainableItemRestrictions(maintainableSectionDefinition.getMaintainableItems(), maintenanceDocumentAuthorizer, maintenanceDocumentRestrictions,
                maintenanceDocument, maintenanceDocument.getNewMaintainableObject().getBusinessObject(), "", user);
        }
        return maintenanceDocumentRestrictions;
    }

    protected void considerBusinessObjectFieldUnmaskAuthorization(Object dataObject, Person user, BusinessObjectRestrictions businessObjectRestrictions, String propertyPrefix, Document document) {
        // UAF-6.0 upgrade - null data object causes the application to throw NullPointerException
        if (dataObject != null) {
            final DataDictionaryEntryBase objectEntry = (dataObject instanceof Document) ?
                    getDataDictionaryService().getDataDictionary().getDocumentEntry(getDataDictionaryService().getDocumentTypeNameByClass(dataObject.getClass())) :
                    getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(dataObject.getClass().getName());

            BusinessObject permissionTarget = (dataObject instanceof BusinessObject) ? (BusinessObject)dataObject : document;

            for (String attributeName : objectEntry.getAttributeNames()) {
                AttributeDefinition attributeDefinition = objectEntry.getAttributeDefinition(attributeName);
                if (attributeDefinition.getAttributeSecurity() != null) {
                    if (attributeDefinition.getAttributeSecurity().isMask() &&
                            !canFullyUnmaskFieldForBusinessObject(user, dataObject.getClass(), attributeName, permissionTarget, document)) {
                        businessObjectRestrictions.addFullyMaskedField(propertyPrefix + attributeName, attributeDefinition.getAttributeSecurity().getMaskFormatter());
                    }
                    if (attributeDefinition.getAttributeSecurity().isPartialMask() &&
                            !canPartiallyUnmaskFieldForBusinessObject(user, dataObject.getClass(), attributeName, permissionTarget, document)) {
                        businessObjectRestrictions.addPartiallyMaskedField(propertyPrefix + attributeName, attributeDefinition.getAttributeSecurity().getPartialMaskFormatter());
                    }
                }
            }
        }
    }

    /**
     * Determines if the given field on the given business object can be unmasked, using the business object to build role qualifiers if possible
     *
     * @param user            the person to check if there is full unmask field permission for
     * @param dataObjectClass the class of the data object being checked
     * @param fieldName       the name of the field to potentially unmask
     * @param businessObject  the business object containing sensitive information
     * @param document        the document we are acting on, or null if no document is known
     * @return true if the field on the business object can be fully unmasked, false otherwise
     */
    protected boolean canFullyUnmaskFieldForBusinessObject(Person user, Class<?> dataObjectClass, String fieldName, BusinessObject businessObject, Document document) {
        if (isNonProductionEnvAndUnmaskingTurnedOff()) {
            return false;
        }

        if (user == null || StringUtils.isEmpty(user.getPrincipalId())) {
            return false;
        }

        DataObjectAuthorizer authorizer = null;
        BusinessObject boForAuthorization = null;
        if (document != null) {
            authorizer = findDocumentAuthorizerForBusinessObject(document);
            boForAuthorization = document;
        }
        if (authorizer == null) {
            authorizer = findDocumentAuthorizerForBusinessObject(businessObject);
            if (authorizer == null) {
                authorizer = findInquiryAuthorizerForBusinessObject(businessObject);
            }
            boForAuthorization = businessObject;
        }
        if (authorizer == null) {
            return getPermissionService().isAuthorizedByTemplate(user.getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD, new HashMap<String, String>(
                    getFieldPermissionDetails(dataObjectClass, fieldName)), Collections.<String, String>emptyMap());
        }
        return authorizer
            .isAuthorizedByTemplate(boForAuthorization,
                KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD,
                user.getPrincipalId(), getFieldPermissionDetails(dataObjectClass, fieldName), Collections.<String, String>emptyMap());
    }

    /**
     * Determines if the given field on the given business object can be unmasked, using the business object to build role qualifiers if possible
     *
     * @param user            the person to check if there is partial unmask field permission for
     * @param dataObjectClass the class of the data object being checked
     * @param fieldName       the name of the field to potentially unmask
     * @param businessObject  the business object containing sensitive information
     * @param document        the document we are acting on, or null if no document is known
     * @return true if the field on the business object can be partially unmasked, false otherwise
     */
    protected boolean canPartiallyUnmaskFieldForBusinessObject(Person user, Class<?> dataObjectClass, String fieldName, BusinessObject businessObject, Document document) {
        if (isNonProductionEnvAndUnmaskingTurnedOff()) {
            return false;
        }

        if (user == null || StringUtils.isEmpty(user.getPrincipalId())) {
            return false;
        }

        DataObjectAuthorizer authorizer = null;
        BusinessObject boForAuthorization = null;
        if (document != null) {
            authorizer = findDocumentAuthorizerForBusinessObject(document);
            boForAuthorization = document;
        }
        if (authorizer == null) {
            authorizer = findDocumentAuthorizerForBusinessObject(businessObject);
            if (authorizer == null) {
                authorizer = findInquiryAuthorizerForBusinessObject(businessObject);
            }
            boForAuthorization = businessObject;
        }
        if (authorizer == null) {
            return getPermissionService().isAuthorizedByTemplate(user.getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.PARTIAL_UNMASK_FIELD, new HashMap<String, String>(
                    getFieldPermissionDetails(dataObjectClass, fieldName)), Collections.<String, String>emptyMap());
        } else { // if a document was passed, evaluate the permission in the context of a document
            return authorizer
                .isAuthorizedByTemplate(boForAuthorization,
                    KRADConstants.KNS_NAMESPACE,
                    KimConstants.PermissionTemplateNames.PARTIAL_UNMASK_FIELD,
                    user.getPrincipalId(), getFieldPermissionDetails(dataObjectClass, fieldName), Collections.<String, String>emptyMap());
        }
    }

    /**
     * Attempts to find a DocumentAuthorizer for the given business object.  If the business object is a document, simply looks up
     * its associated authorizer.  Otherwise, it checks to see if there's a maintenance document associated with the business object and uses
     * the document authorizer associated with that
     *
     * @param businessObject the business object to attempt to find a DocumentAuthorizer for
     * @return an instantiated DocumentAuthorizer associated with the business object, or null if none could be found
     */
    protected DocumentAuthorizer findDocumentAuthorizerForBusinessObject(BusinessObject businessObject) {
        if (businessObject == null) {
            return null;
        }
        if (businessObject instanceof Document) {
            return getDocumentHelperService().getDocumentAuthorizer((Document) businessObject);
        }
        final String maintDocType = getMaintenanceDocumentDictionaryService().getDocumentTypeName(businessObject.getClass());
        if (StringUtils.isBlank(maintDocType)) {
            return null;
        }
        return getDocumentHelperService().getDocumentAuthorizer(maintDocType);
    }

    /**
     * Attempts to find an InquiryAuthorizer for the given business object, by looking at the inquiry definition
     *
     * @param businessObject the business object to attempt to find a InquiryAuthorizer for
     * @return an instantiated InquiryAuthorizer associated with the business object, or null if none could be found
     */
    protected DataObjectAuthorizer findInquiryAuthorizerForBusinessObject(BusinessObject businessObject) {
        if (businessObject == null) {
            return null;
        }
        final BusinessObjectEntry boEntry = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObject.getClass().getName());
        if (boEntry != null && boEntry.getInquiryDefinition() != null && boEntry.getInquiryDefinition().getAuthorizerClass() != null) {
            try {
                return (DataObjectAuthorizer) boEntry.getInquiryDefinition().getAuthorizerClass().newInstance();
            } catch (InstantiationException ie) {
                throw new RuntimeException("Could not instantiate authorizer for inquiry of " + businessObject.getClass().getName(), ie);
            } catch (IllegalAccessException iae) {
                throw new RuntimeException("Could not instantiate authorizer for inquiry of " + businessObject.getClass().getName(), iae);
            }
        }
        return null;
    }

    /**
     * @param dataObjectEntry                          if collectionItemBusinessObject is not null, then it is the DD entry for collectionItemBusinessObject.
     *                                                 Otherwise, it is the entry for primaryBusinessObject
     * @param primaryDataObject                        the top-level BO that is being inquiried or maintained
     * @param collectionItemBusinessObject             an element of a collection under the primaryBusinessObject that we are evaluating view auths for
     * @param user                                     the logged in user
     * @param businessObjectAuthorizer
     * @param inquiryOrMaintenanceDocumentRestrictions
     * @param propertyPrefix
     */
    protected void considerBusinessObjectFieldViewAuthorization(
        DataObjectEntry dataObjectEntry,
        Object primaryDataObject,
        BusinessObject collectionItemBusinessObject,
        Person user,
        BusinessObjectAuthorizer businessObjectAuthorizer,
        InquiryOrMaintenanceDocumentRestrictions inquiryOrMaintenanceDocumentRestrictions,
        String propertyPrefix) {
        for (String attributeName : dataObjectEntry.getAttributeNames()) {
            AttributeDefinition attributeDefinition = dataObjectEntry
                .getAttributeDefinition(attributeName);
            if (attributeDefinition.getAttributeSecurity() != null) {
                if (attributeDefinition.getAttributeSecurity().isHide()) {
                    Map<String, String> collectionItemPermissionDetails = new HashMap<String, String>();
                    Map<String, String> collectionItemRoleQualifications = null;
                    if (ObjectUtils.isNotNull(collectionItemBusinessObject)) {
                        collectionItemPermissionDetails.putAll(getFieldPermissionDetails(collectionItemBusinessObject, attributeName));
                        collectionItemPermissionDetails.putAll(businessObjectAuthorizer.
                            getCollectionItemPermissionDetails(collectionItemBusinessObject));
                        collectionItemRoleQualifications = new HashMap<String, String>(businessObjectAuthorizer.
                            getCollectionItemRoleQualifications(collectionItemBusinessObject));
                    } else {
                        collectionItemPermissionDetails.putAll(getFieldPermissionDetails(primaryDataObject, attributeName));
                    }
                    if (!businessObjectAuthorizer
                        .isAuthorizedByTemplate(
                            primaryDataObject,
                            KRADConstants.KNS_NAMESPACE,
                            KimConstants.PermissionTemplateNames.VIEW_MAINTENANCE_INQUIRY_FIELD,
                            user.getPrincipalId(),
                            collectionItemPermissionDetails,
                            collectionItemRoleQualifications)) {
                        inquiryOrMaintenanceDocumentRestrictions
                            .addHiddenField(propertyPrefix + attributeName);
                    }
                }
            }
        }
    }

    /**
     * @param dataObjectEntry                 if collectionItemBusinessObject is not null, then it is the DD entry for collectionItemBusinessObject.
     *                                        Otherwise, it is the entry for primaryBusinessObject
     * @param primaryDataObject               the top-level BO that is being inquiried or maintained
     * @param collectionItemBusinessObject    an element of a collection under the primaryBusinessObject that we are evaluating view auths for
     * @param user                            the logged in user
     * @param businessObjectAuthorizer
     * @param maintenanceDocumentRestrictions
     * @param propertyPrefix
     */
    protected void considerBusinessObjectFieldModifyAuthorization(
        DataObjectEntry dataObjectEntry,
        Object primaryDataObject,
        BusinessObject collectionItemBusinessObject, Person user,
        BusinessObjectAuthorizer businessObjectAuthorizer,
        MaintenanceDocumentRestrictions maintenanceDocumentRestrictions,
        String propertyPrefix) {
        for (String attributeName : dataObjectEntry.getAttributeNames()) {
            AttributeDefinition attributeDefinition = dataObjectEntry
                .getAttributeDefinition(attributeName);
            if (attributeDefinition.getAttributeSecurity() != null) {
                Map<String, String> collectionItemPermissionDetails = new HashMap<String, String>();
                Map<String, String> collectionItemRoleQualifications = null;
                if (ObjectUtils.isNotNull(collectionItemBusinessObject)) {
                    collectionItemPermissionDetails.putAll(getFieldPermissionDetails(collectionItemBusinessObject, attributeName));
                    collectionItemPermissionDetails.putAll(businessObjectAuthorizer.
                        getCollectionItemPermissionDetails(collectionItemBusinessObject));
                    collectionItemRoleQualifications = new HashMap<String, String>(businessObjectAuthorizer.
                        getCollectionItemRoleQualifications(collectionItemBusinessObject));
                } else {
                    collectionItemPermissionDetails.putAll(getFieldPermissionDetails(primaryDataObject, attributeName));
                }
                if (attributeDefinition.getAttributeSecurity().isReadOnly()) {
                    if (!businessObjectAuthorizer
                        .isAuthorizedByTemplate(
                            primaryDataObject,
                            KRADConstants.KNS_NAMESPACE,
                            KimConstants.PermissionTemplateNames.MODIFY_FIELD,
                            user.getPrincipalId(),
                            collectionItemPermissionDetails,
                            collectionItemRoleQualifications)) {
                        maintenanceDocumentRestrictions
                            .addReadOnlyField(propertyPrefix + attributeName);
                    }
                }
            }
        }
    }

    /**
     * @param dataObjectEntry                 if collectionItemBusinessObject is not null, then it is the DD entry for collectionItemBusinessObject.
     *                                        Otherwise, it is the entry for primaryBusinessObject
     * @param primaryDataObject               the top-level BO that is being inquiried or maintained
     * @param collectionItemBusinessObject    an element of a collection under the primaryBusinessObject that we are evaluating view auths for
     * @param user                            the logged in user
     * @param businessObjectAuthorizer
     * @param maintenanceDocumentRestrictions
     * @param propertyPrefix
     */
    protected void considerCustomButtonFieldAuthorization(
        DataObjectEntry dataObjectEntry,
        Object primaryDataObject,
        BusinessObject collectionItemBusinessObject,
        Person user,
        BusinessObjectAuthorizer businessObjectAuthorizer,
        MaintenanceDocumentRestrictions maintenanceDocumentRestrictions,
        String propertyPrefix) {
        for (String attributeName : dataObjectEntry.getAttributeNames()) {
            AttributeDefinition attributeDefinition = dataObjectEntry
                .getAttributeDefinition(attributeName);
            // TODO what is the equivalent of control.isButton in KRAD
            if (attributeDefinition.getControl() != null &&
                attributeDefinition.getControl().isButton()) {
                Map<String, String> collectionItemPermissionDetails = new HashMap<String, String>();
                Map<String, String> collectionItemRoleQualifications = null;
                if (ObjectUtils.isNotNull(collectionItemBusinessObject)) {
                    collectionItemPermissionDetails.putAll(getButtonFieldPermissionDetails(collectionItemBusinessObject, attributeName));
                    collectionItemPermissionDetails.putAll(businessObjectAuthorizer.
                        getCollectionItemPermissionDetails(collectionItemBusinessObject));
                    collectionItemRoleQualifications = new HashMap<String, String>(businessObjectAuthorizer.
                        getCollectionItemRoleQualifications(collectionItemBusinessObject));
                } else {
                    getButtonFieldPermissionDetails(primaryDataObject, attributeName);
                }

                if (!businessObjectAuthorizer
                    .isAuthorizedByTemplate(
                        primaryDataObject,
                        KRADConstants.KNS_NAMESPACE,
                        KimConstants.PermissionTemplateNames.PERFORM_CUSTOM_MAINTENANCE_DOCUMENT_FUNCTION,
                        user.getPrincipalId(),
                        collectionItemPermissionDetails,
                        collectionItemRoleQualifications)) {
                    maintenanceDocumentRestrictions
                        .addHiddenField(propertyPrefix + attributeName);
                }
            }
        }
    }

    protected void considerInquiryOrMaintenanceDocumentPresentationController(
        InquiryOrMaintenanceDocumentPresentationController businessObjectPresentationController,
        BusinessObject businessObject,
        InquiryOrMaintenanceDocumentRestrictions inquiryOrMaintenanceDocumentRestrictions) {
        for (String attributeName : businessObjectPresentationController
            .getConditionallyHiddenPropertyNames(businessObject)) {
            inquiryOrMaintenanceDocumentRestrictions
                .addHiddenField(attributeName);
        }
        for (String sectionId : businessObjectPresentationController
            .getConditionallyHiddenSectionIds(businessObject)) {
            inquiryOrMaintenanceDocumentRestrictions
                .addHiddenSectionId(sectionId);
        }
    }

    protected void considerInquiryOrMaintenanceDocumentAuthorizer(
        InquiryOrMaintenanceDocumentAuthorizer authorizer,
        BusinessObject businessObject, Person user,
        InquiryOrMaintenanceDocumentRestrictions restrictions) {
        for (String sectionId : authorizer
            .getSecurePotentiallyHiddenSectionIds()) {
            Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
            additionalPermissionDetails
                .put(KimConstants.AttributeConstants.SECTION_ID, sectionId);
            if (!authorizer.isAuthorizedByTemplate(businessObject,
                KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.VIEW_SECTION, user
                    .getPrincipalId(), additionalPermissionDetails,
                null)) {
                restrictions.addHiddenSectionId(sectionId);
            }
        }
    }

    protected void considerMaintenanceDocumentPresentationController(
        MaintenanceDocumentPresentationController presentationController,
        MaintenanceDocument document,
        MaintenanceDocumentRestrictions restrictions) {
        for (String attributeName : presentationController
            .getConditionallyReadOnlyPropertyNames(document)) {
            restrictions.addReadOnlyField(attributeName);
        }
        for (String sectionId : presentationController
            .getConditionallyReadOnlySectionIds(document)) {
            restrictions.addReadOnlySectionId(sectionId);
        }
    }

    protected void considerMaintenanceDocumentAuthorizer(
        MaintenanceDocumentAuthorizer authorizer,
        MaintenanceDocument document, Person user,
        MaintenanceDocumentRestrictions restrictions) {
        for (String sectionId : authorizer
            .getSecurePotentiallyReadOnlySectionIds()) {
            Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
            additionalPermissionDetails
                .put(KimConstants.AttributeConstants.SECTION_ID, sectionId);
            if (!authorizer.isAuthorizedByTemplate(document,
                KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.MODIFY_SECTION, user
                    .getPrincipalId(), additionalPermissionDetails,
                null)) {
                restrictions.addReadOnlySectionId(sectionId);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void addInquirableItemRestrictions(Collection sectionDefinitions,
                                                 InquiryAuthorizer authorizer, InquiryRestrictions restrictions,
                                                 BusinessObject primaryBusinessObject,
                                                 BusinessObject businessObject, String propertyPrefix, Person user) {
        for (Object inquirableItemDefinition : sectionDefinitions) {
            if (inquirableItemDefinition instanceof InquiryCollectionDefinition) {
                InquiryCollectionDefinition inquiryCollectionDefinition = (InquiryCollectionDefinition) inquirableItemDefinition;
                BusinessObjectEntry collectionBusinessObjectEntry = (BusinessObjectEntry) getDataDictionaryService()
                    .getDataDictionary().getBusinessObjectEntry(
                        inquiryCollectionDefinition.getBusinessObjectClass().getName());

                try {
                    Collection<BusinessObject> collection = (Collection<BusinessObject>) PropertyUtils
                        .getProperty(businessObject,
                            inquiryCollectionDefinition.getName());
                    int i = 0;
                    for (Iterator<BusinessObject> iterator = collection.iterator(); iterator
                        .hasNext(); ) {
                        String newPropertyPrefix = propertyPrefix + inquiryCollectionDefinition.getName() + "[" + i + "].";
                        BusinessObject collectionItemBusinessObject = iterator.next();
                        considerBusinessObjectFieldUnmaskAuthorization(
                            collectionItemBusinessObject, user, restrictions,
                            newPropertyPrefix, null);
                        considerBusinessObjectFieldViewAuthorization(
                            collectionBusinessObjectEntry, primaryBusinessObject, collectionItemBusinessObject,
                            user, authorizer, restrictions, newPropertyPrefix);
                        addInquirableItemRestrictions(
                            inquiryCollectionDefinition
                                .getInquiryCollections(),
                            authorizer,
                            restrictions,
                            primaryBusinessObject,
                            collectionItemBusinessObject,
                            newPropertyPrefix,
                            user);
                        i++;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                        "Unable to resolve collection property: "
                            + businessObject.getClass() + ":"
                            + inquiryCollectionDefinition.getName(), e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void addMaintainableItemRestrictions(List<? extends MaintainableItemDefinition> itemDefinitions,
                                                   MaintenanceDocumentAuthorizer authorizer,
                                                   MaintenanceDocumentRestrictions restrictions,
                                                   MaintenanceDocument maintenanceDocument,
                                                   BusinessObject businessObject, String propertyPrefix, Person user) {
        for (MaintainableItemDefinition maintainableItemDefinition : itemDefinitions) {
            if (maintainableItemDefinition instanceof MaintainableCollectionDefinition) {
                try {
                    MaintainableCollectionDefinition maintainableCollectionDefinition = (MaintainableCollectionDefinition) maintainableItemDefinition;

                    Collection<BusinessObject> collection = (Collection<BusinessObject>) ObjectUtils
                        .getNestedValue(businessObject,
                            maintainableItemDefinition.getName());
                    BusinessObjectEntry collectionBusinessObjectEntry = (BusinessObjectEntry) getDataDictionaryService()
                        .getDataDictionary().getBusinessObjectEntry(
                            maintainableCollectionDefinition.getBusinessObjectClass().getName());
                    if (CollectionUtils.isNotEmpty(collection)) {
                        //if (collection != null && !collection.isEmpty()) {
                        int i = 0;
                        for (Iterator<BusinessObject> iterator = collection.iterator(); iterator
                            .hasNext(); ) {
                            String newPropertyPrefix = propertyPrefix + maintainableItemDefinition.getName() + "[" + i + "].";
                            BusinessObject collectionBusinessObject = iterator.next();
                            considerBusinessObjectFieldUnmaskAuthorization(
                                collectionBusinessObject, user, restrictions,
                                newPropertyPrefix, maintenanceDocument);
                            considerBusinessObjectFieldViewAuthorization(
                                collectionBusinessObjectEntry, maintenanceDocument, collectionBusinessObject, user,
                                authorizer, restrictions, newPropertyPrefix);
                            considerBusinessObjectFieldModifyAuthorization(
                                collectionBusinessObjectEntry, maintenanceDocument, collectionBusinessObject, user,
                                authorizer, restrictions, newPropertyPrefix);
                            addMaintainableItemRestrictions(
                                ((MaintainableCollectionDefinition) maintainableItemDefinition)
                                    .getMaintainableCollections(),
                                authorizer, restrictions, maintenanceDocument,
                                collectionBusinessObject, newPropertyPrefix,
                                user);
                            addMaintainableItemRestrictions(
                                ((MaintainableCollectionDefinition) maintainableItemDefinition)
                                    .getMaintainableFields(), authorizer,
                                restrictions, maintenanceDocument,
                                collectionBusinessObject, newPropertyPrefix,
                                user);
                            i++;
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                        "Unable to resolve collection property: "
                            + businessObject.getClass() + ":"
                            + maintainableItemDefinition.getName(), e);
                }
            }
        }
    }

    public boolean canFullyUnmaskField(Person user,
                                       Class<?> dataObjectClass, String fieldName, Document document) {
        return canFullyUnmaskFieldForBusinessObject(user, dataObjectClass, fieldName, document, null);
    }

    public boolean canPartiallyUnmaskField(
        Person user, Class<?> dataObjectClass, String fieldName, Document document) {
        return canPartiallyUnmaskFieldForBusinessObject(user, dataObjectClass, fieldName, document, null);
    }

    protected Map<String, String> getFieldPermissionDetails(
        Class<?> dataObjectClass, String attributeName) {
        try {
            return getFieldPermissionDetails(dataObjectClass.newInstance(),
                attributeName);
        } catch (Exception e) {
            throw new RuntimeException(
                "The getPermissionDetails method of BusinessObjectAuthorizationServiceImpl was unable to instantiate the dataObjectClass"
                    + dataObjectClass, e);
        }
    }

    protected Map<String, String> getFieldPermissionDetails(
        Object dataObject, String attributeName) {
        Map<String, String> permissionDetails = null;
        String namespaceCode = null;
        String componentName = null;
        String propertyName = null;
        // JHK: commenting out for KFSMI-2398 - permission checks need to be done at the level specified
        // that is, if the parent object specifies the security, that object should be used for the
        // component
//		if (attributeName.contains(".")) {
//			try {
//				permissionDetails = KimCommonUtils
//						.getNamespaceAndComponentSimpleName(PropertyUtils
//								.getPropertyType(businessObject, attributeName
//										.substring(0, attributeName
//												.lastIndexOf("."))));
//			} catch (Exception e) {
//				throw new RuntimeException(
//						"Unable to discover nested business object class: "
//								+ businessObject.getClass() + " : "
//								+ attributeName, e);
//			}

//			permissionDetails.put(KimAttributes.PROPERTY_NAME, attributeName
//					.substring(attributeName.indexOf(".") + 1));
//		} else {
        permissionDetails = KRADUtils
            .getNamespaceAndComponentSimpleName(dataObject.getClass());
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, attributeName);
//		}
        return permissionDetails;
    }

    protected Map<String, String> getButtonFieldPermissionDetails(
        Object businessObject, String attributeName) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        if (attributeName.contains(".")) {
            permissionDetails.put(KimConstants.AttributeConstants.BUTTON_NAME, attributeName);
        } else {
            permissionDetails.put(KimConstants.AttributeConstants.BUTTON_NAME, attributeName);
        }
        return permissionDetails;
    }

    private PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = KimApiServiceLocator
                .getPermissionService();
        }
        return permissionService;
    }

    private BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        if (businessObjectDictionaryService == null) {
            businessObjectDictionaryService = KNSServiceLocator
                .getBusinessObjectDictionaryService();
        }
        return businessObjectDictionaryService;
    }

    private MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        if (maintenanceDocumentDictionaryService == null) {
            maintenanceDocumentDictionaryService = KNSServiceLocator
                .getMaintenanceDocumentDictionaryService();
        }
        return maintenanceDocumentDictionaryService;
    }

    private ConfigurationService getKualiConfigurationService() {
        if (kualiConfigurationService == null) {
            kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
        }
        return kualiConfigurationService;
    }

    @Override
    public boolean isNonProductionEnvAndUnmaskingTurnedOff() {
        return !getKualiConfigurationService().getPropertyValueAsString(KRADConstants.PROD_ENVIRONMENT_CODE_KEY)
            .equalsIgnoreCase(
                getKualiConfigurationService().getPropertyValueAsString(KRADConstants.ENVIRONMENT_KEY)) &&
            !getKualiConfigurationService().getPropertyValueAsBoolean(KRADConstants.ENABLE_NONPRODUCTION_UNMASKING);
    }

    protected DocumentHelperService getDocumentHelperService() {
        return KNSServiceLocator.getDocumentHelperService();
    }

}
