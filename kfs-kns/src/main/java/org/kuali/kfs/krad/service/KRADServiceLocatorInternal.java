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

import org.kuali.kfs.coreservice.api.namespace.NamespaceService;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.krad.dao.BusinessObjectDao;
import org.kuali.kfs.krad.dao.DocumentDao;
import org.kuali.kfs.krad.util.OjbCollectionHelper;
import org.kuali.kfs.krad.workflow.service.WorkflowAttributePropertyResolutionService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class KRADServiceLocatorInternal {

    public static final String VALIDATION_COMPLETION_UTILS = "validationCompletionUtils";

    public static <T extends Object> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }


    public static final String MAIL_SERVICE = "cf.mailService";

    public static final MailService getMailService() {
        return (MailService) getService(MAIL_SERVICE);
    }

    public static final String POST_PROCESSOR_SERVICE = "cf.postProcessorService";

    public static PostProcessorService getPostProcessorService() {
        return (PostProcessorService) getService(POST_PROCESSOR_SERVICE);
    }

    public static final String NAMESPACE_SERVICE = "namespaceService";

    public static NamespaceService getNamespaceService() {
        return (NamespaceService) getService(NAMESPACE_SERVICE);
    }

    public static final String OJB_COLLECTION_HELPER = "cf.ojbCollectionHelper";

    public static OjbCollectionHelper getOjbCollectionHelper() {
        return (OjbCollectionHelper) getService(OJB_COLLECTION_HELPER);
    }

    public static final String TRANSACTION_MANAGER = "transactionManager";

    public static PlatformTransactionManager getTransactionManager() {
        return (PlatformTransactionManager) getService(TRANSACTION_MANAGER);
    }

    public static final String TRANSACTION_TEMPLATE = "transactionTemplate";

    public static TransactionTemplate getTransactionTemplate() {
        return (TransactionTemplate) getService(TRANSACTION_TEMPLATE);
    }

    public static final String INACTIVATION_BLOCKING_DISPLAY_SERVICE = "inactivationBlockingDisplayService";

    public static InactivationBlockingDisplayService getInactivationBlockingDisplayService() {
        return (InactivationBlockingDisplayService) getService(INACTIVATION_BLOCKING_DISPLAY_SERVICE);
    }

    public static final String DATA_DICTIONARY_COMPONENT_PUBLISHER_SERVICE = "cf.dataDictionaryComponentPublisherService";

    public static DataDictionaryComponentPublisherService getDataDictionaryComponentPublisherService() {
        return getService(DATA_DICTIONARY_COMPONENT_PUBLISHER_SERVICE);
    }


    public static final String DOCUMENT_DAO = "cf.documentDao";

    public static DocumentDao getDocumentDao() {
        return (DocumentDao) getService(DOCUMENT_DAO);
    }

    public static final String BUSINESS_OBJECT_DAO = "cf.businessObjectDao";

    public static BusinessObjectDao getBusinessObjectDao() {
        return (BusinessObjectDao) getService(BUSINESS_OBJECT_DAO);
    }


    public static final String DB_PLATFORM = "dbPlatform";

    public static DatabasePlatform getDatabasePlatform() {
        return (DatabasePlatform) getService(DB_PLATFORM);
    }

    public static final String MAINTENANCE_DOCUMENT_AUTHORIZATION_SERVICE = "maintenanceDocumentAuthorizationService";

    public static BusinessObjectAuthorizationService getMaintenanceDocumentAuthorizationService() {
        return (BusinessObjectAuthorizationService) getService(MAINTENANCE_DOCUMENT_AUTHORIZATION_SERVICE);
    }

    public static final String WORKFLOW_ATTRIBUTE_PROPERTY_RESOLUTION_SERVICE = "cf.workflowAttributePropertyResolutionService";

    public static WorkflowAttributePropertyResolutionService getWorkflowAttributePropertyResolutionService() {
        return (WorkflowAttributePropertyResolutionService) getService(WORKFLOW_ATTRIBUTE_PROPERTY_RESOLUTION_SERVICE);
    }

    public static final String INACTIVATEABLE_FROM_TO_SERVICE = "inactivateableFromToService";

    public static InactivateableFromToService getInactivateableFromToService() {
        return (InactivateableFromToService) getService(INACTIVATEABLE_FROM_TO_SERVICE);
    }

}
