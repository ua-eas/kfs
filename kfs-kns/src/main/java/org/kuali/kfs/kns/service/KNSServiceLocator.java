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
package org.kuali.kfs.kns.service;

import org.kuali.kfs.kns.inquiry.Inquirable;
import org.kuali.kfs.kns.lookup.LookupResultsService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.kfs.kns.lookup.Lookupable;
import org.kuali.kfs.kns.question.Question;

/**
 * Service locator for the KRAD Web module
 *
 * 
 */
public class KNSServiceLocator {

    public static final String BUSINESS_OBJECT_AUTHORIZATION_SERVICE = "cf.businessObjectAuthorizationService";
    public static final String BUSINESS_OBJECT_METADATA_SERVICE = "cf.businessObjectMetaDataService";
    public static final String BUSINESS_OBJECT_DICTIONARY_SERVICE = "cf.businessObjectDictionaryService";
    public static final String CF_AUTHENTICATION_SERVICE = "cfAuthenticationService";
    public static final String DATA_DICTIONARY_SERVICE = "cf.dataDictionaryService";
    public static final String DICTIONARY_VALIDATION_SERVICE = "cf.dictionaryValidationService";
    public static final String DOCUMENT_HELPER_SERVICE = "cf.documentHelperService";
    public static final String LOOKUP_RESULTS_SERVICE = "cf.lookupResultsService";
    public static final String KUALI_INQUIRABLE = "cf.kualiInquirable";
    public static final String KUALI_LOOKUPABLE = "cf.kualiLookupable";
    public static final String MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE = "cf.maintenanceDocumentDictionaryService";
    public static final String SESSION_DOCUMENT_SERVICE = "cf.knsSessionDocumentService";
    public static final String TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE = "cf.transactionalDocumentDictionaryService";

    public static <T extends Object> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static CfAuthenticationService getCfAuthenticationService() {
        return getService(CF_AUTHENTICATION_SERVICE);
    }

    public static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        return getService(BUSINESS_OBJECT_AUTHORIZATION_SERVICE);
    }

    public static BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return getService(BUSINESS_OBJECT_METADATA_SERVICE);
    }

    public static DictionaryValidationService getKNSDictionaryValidationService() {
	return (DictionaryValidationService) getService(DICTIONARY_VALIDATION_SERVICE);
    }

    public static LookupResultsService getLookupResultsService() {
        return (LookupResultsService) getService(LOOKUP_RESULTS_SERVICE);
    }

    public static Inquirable getKualiInquirable() {
        return getService(KUALI_INQUIRABLE);
    }

    public static Lookupable getKualiLookupable() {
        return getService(KUALI_LOOKUPABLE);
    }

    public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        return getService(MAINTENANCE_DOCUMENT_DICTIONARY_SERVICE);
    }

    public static TransactionalDocumentDictionaryService getTransactionalDocumentDictionaryService() {
        return (TransactionalDocumentDictionaryService) getService(TRANSACTIONAL_DOCUMENT_DICTIONARY_SERVICE);
    }

    public static SessionDocumentService getSessionDocumentService() {
        return  getService(SESSION_DOCUMENT_SERVICE);
    }

    public static Lookupable getLookupable(String lookupableName) {
        return getService(lookupableName);
    }

    public static DataDictionaryService getDataDictionaryService() {
        return getService(DATA_DICTIONARY_SERVICE);
    }

    public static BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return getService(BUSINESS_OBJECT_DICTIONARY_SERVICE);
    }

    public static DocumentHelperService getDocumentHelperService() {
        return getService(DOCUMENT_HELPER_SERVICE);
    }

    public static Question getQuestion(String questionName) {
        return (Question) getService(questionName);
    }
}
