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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.lookup.Lookupable;
import org.kuali.kfs.krad.uif.service.AttributeQueryService;
import org.kuali.kfs.krad.uif.service.ExpressionEvaluatorService;
import org.kuali.kfs.krad.uif.service.ViewDictionaryService;
import org.kuali.kfs.krad.uif.service.ViewService;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

/**
 * Service locator for the KRAD Web module
 */
public class KRADServiceLocatorWeb {

    public static final class Namespaces {
        public static final String MODULE_NAME = "krad";
        public static final String KRAD_NAMESPACE_PREFIX =
            CoreConstants.Namespaces.ROOT_NAMESPACE_PREFIX + "/" + MODULE_NAME;

        /**
         * Namespace for the krad module which is compatible with Kuali Rice 2.0.x.
         */
        public static final String KRAD_NAMESPACE_2_0 =
            KRAD_NAMESPACE_PREFIX + "/" + CoreConstants.Versions.VERSION_2_0;

    }

    public static final String DATA_DICTIONARY_REMOTE_FIELD_SERVICE = "dataDictionaryRemoteFieldService";
    public static final String DOCUMENT_DICTIONARY_SERVICE = "cf.documentDictionaryService";
    public static final String SESSION_DOCUMENT_SERVICE = "cf.sessionDocumentService";
    public static final String DATA_OBJECT_AUTHORIZATION_SERVICE = "cf.dataObjectAuthorizationService";
    public static final String MAINTENANCE_DOCUMENT_SERVICE = "cf.maintenanceDocumentService";
    public static final String WORKFLOW_DOCUMENT_SERVICE = "cf.workflowDocumentService";
    public static final String EXCEPTION_INCIDENT_REPORT_SERVICE = "cf.kradExceptionIncidentService";
    public static final String DATA_DICTIONARY_SERVICE = "cf.dataDictionaryService";
    public static final String DOCUMENT_HEADER_SERVICE = "cf.documentHeaderService";
    public static final String PERSISTENCE_SERVICE_OJB = "cf.persistenceServiceOjb";
    public static final String KUALI_MODULE_SERVICE = "cf.kualiModuleService";
    public static final String KUALI_RULE_SERVICE = "cf.kualiRuleService";
    public static final String DOCUMENT_SERVICE = "cf.documentService";
    public static final String DOCUMENT_SERIALIZER_SERVICE = "cf.documentSerializerService";
    public static final String LOOKUP_SERVICE = "cf.lookupService";
    public static final String DICTIONARY_VALIDATION_SERVICE = "cf.dictionaryValidationService";
    public static final String DEFAULT_INACTIVATION_BLOCKING_DETECTION_SERVICE = "cf.inactivationBlockingDetectionService";
    public static final String DATA_OBJECT_METADATA_SERVICE = "cf.dataObjectMetaDataService";
    public static final String EXPRESSION_EVALUATOR_SERVICE = "expressionEvaluatorService";
    public static final String VIEW_SERVICE = "viewService";
    public static final String VIEW_DICTIONARY_SERVICE = "viewDictionaryService";
    public static final String VIEW_VALIDATION_SERVICE = "viewValidationService";
    public static final String ATTRIBUTE_QUERY_SERVICE = "attributeQueryService";
    public static final String MAINTAINABLE_XML_CONVERSION_SERVICE = "cf.maintainableXMLConversionService";
    public static final String CSRF_SERVICE = "cf.csrfService";

    public static <T extends Object> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static DocumentDictionaryService getDocumentDictionaryService() {
        return getService(DOCUMENT_DICTIONARY_SERVICE);
    }

    public static SessionDocumentService getSessionDocumentService() {
        return getService(SESSION_DOCUMENT_SERVICE);
    }

    public static DataObjectAuthorizationService getDataObjectAuthorizationService() {
        return getService(DATA_OBJECT_AUTHORIZATION_SERVICE);
    }

    public static MaintenanceDocumentService getMaintenanceDocumentService() {
        return getService(MAINTENANCE_DOCUMENT_SERVICE);
    }

    public static WorkflowDocumentService getWorkflowDocumentService() {
        return getService(WORKFLOW_DOCUMENT_SERVICE);
    }

    public static final KualiExceptionIncidentService getKualiExceptionIncidentService() {
        return getService(EXCEPTION_INCIDENT_REPORT_SERVICE);
    }

    public static DataDictionaryService getDataDictionaryService() {
        return getService(DATA_DICTIONARY_SERVICE);
    }

    public static DocumentHeaderService getDocumentHeaderService() {
        return getService(DOCUMENT_HEADER_SERVICE);
    }

    public static Lookupable getLookupable(String lookupableName) {
        return getService(lookupableName);
    }

    public static org.kuali.kfs.krad.service.PersistenceService getPersistenceServiceOjb() {
        return getService(PERSISTENCE_SERVICE_OJB);
    }

    public static KualiModuleService getKualiModuleService() {
        return getService(KUALI_MODULE_SERVICE);
    }

    public static KualiRuleService getKualiRuleService() {
        return getService(KUALI_RULE_SERVICE);
    }

    public static DocumentService getDocumentService() {
        return getService(DOCUMENT_SERVICE);
    }

    public static DocumentSerializerService getDocumentSerializerService() {
        return (DocumentSerializerService) getService(DOCUMENT_SERIALIZER_SERVICE);
    }

    public static LookupService getLookupService() {
        return (LookupService) getService(LOOKUP_SERVICE);
    }

    public static DictionaryValidationService getDictionaryValidationService() {
        return (DictionaryValidationService) getService(DICTIONARY_VALIDATION_SERVICE);
    }

    public static InactivationBlockingDetectionService getInactivationBlockingDetectionService(String serviceName) {
        return (InactivationBlockingDetectionService) getService(serviceName);
    }

    public static DataObjectMetaDataService getDataObjectMetaDataService() {
        return (DataObjectMetaDataService) getService(DATA_OBJECT_METADATA_SERVICE);
    }

    public static ExpressionEvaluatorService getExpressionEvaluatorService() {
        return (ExpressionEvaluatorService) getService(EXPRESSION_EVALUATOR_SERVICE);
    }

    public static ViewService getViewService() {
        return (ViewService) getService(VIEW_SERVICE);
    }

    public static ViewDictionaryService getViewDictionaryService() {
        return (ViewDictionaryService) getService(VIEW_DICTIONARY_SERVICE);
    }

    public static ViewValidationService getViewValidationService() {
        return (ViewValidationService) getService(VIEW_VALIDATION_SERVICE);
    }

    public static AttributeQueryService getAttributeQueryService() {
        return (AttributeQueryService) getService(ATTRIBUTE_QUERY_SERVICE);
    }

    public static DataDictionaryRemoteFieldService getDataDictionaryRemoteFieldService() {
        return (DataDictionaryRemoteFieldService) getService(DATA_DICTIONARY_REMOTE_FIELD_SERVICE);
    }

    public static final MaintainableXMLConversionService getMaintainableXMLConversionService() {
        return getService(MAINTAINABLE_XML_CONVERSION_SERVICE);
    }

    public static CsrfService getCsrfService() {
        return getService(CSRF_SERVICE);
    }
}
