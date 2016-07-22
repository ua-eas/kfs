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
package org.kuali.kfs.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.datadictionary.KNSDocumentEntry;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.kfs.kns.document.authorization.DocumentAuthorizer;
import org.kuali.kfs.kns.document.authorization.DocumentAuthorizerBase;
import org.kuali.kfs.kns.document.authorization.DocumentPresentationController;
import org.kuali.kfs.kns.document.authorization.DocumentPresentationControllerBase;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.kfs.kns.document.authorization.TransactionalDocumentPresentationControllerBase;
import org.kuali.kfs.kns.service.DocumentHelperService;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

/**
 * This class is a utility service intended to help retrieve objects related to particular documents.
 * 
 * 
 *
 */
public class DocumentHelperServiceImpl implements DocumentHelperService {
    
    private DataDictionaryService dataDictionaryService;

    /**
     * @see DocumentHelperService#getDocumentAuthorizer(java.lang.String)
     * // TODO: in krad documents could have multiple views and multiple authorizer classes
     */
    public DocumentAuthorizer getDocumentAuthorizer(String documentType) {
        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();

        if (StringUtils.isBlank(documentType)) {
            throw new IllegalArgumentException("invalid (blank) documentType");
        }

        KNSDocumentEntry documentEntry = (KNSDocumentEntry) dataDictionary.getDocumentEntry(documentType);
        if (documentEntry == null) {
            throw new IllegalArgumentException("unknown documentType '" + documentType + "'");
        }

        Class<? extends DocumentAuthorizer> documentAuthorizerClass = documentEntry.getDocumentAuthorizerClass();

        DocumentAuthorizer documentAuthorizer = null;
        try {
            if (documentAuthorizerClass != null) {
                documentAuthorizer = documentAuthorizerClass.newInstance();
            }
            else if (documentEntry instanceof MaintenanceDocumentEntry) {
                documentAuthorizer = new MaintenanceDocumentAuthorizerBase();
            }
            else if (documentEntry instanceof TransactionalDocumentEntry) {
                documentAuthorizer = new TransactionalDocumentAuthorizerBase();
            }
            else {
                documentAuthorizer = new DocumentAuthorizerBase();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("unable to instantiate documentAuthorizer '" + documentAuthorizerClass.getName() + "' for doctype '" + documentType + "'", e);
        }

        return documentAuthorizer;
    }

    /**
     * @see DocumentHelperService#getDocumentAuthorizer(Document)
     */
    public DocumentAuthorizer getDocumentAuthorizer(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        } else if (document.getDocumentHeader() == null) {
            throw new IllegalArgumentException(
                    "invalid (null) document.documentHeader");
        } else if (!document.getDocumentHeader().hasWorkflowDocument()) {
            throw new IllegalArgumentException(
                    "invalid (null) document.documentHeader.workflowDocument");
        }

        String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        DocumentAuthorizer documentAuthorizer = getDocumentAuthorizer(documentType);
        return documentAuthorizer;
    }

    /**
     * @see DocumentHelperService#getDocumentPresentationController(java.lang.String)
     * // TODO: in krad documents could have multiple views and multiple presentation controller
     */
    public DocumentPresentationController getDocumentPresentationController(String documentType) {
        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
        DocumentPresentationController documentPresentationController = null;
        
        if (StringUtils.isBlank(documentType)) {
            throw new IllegalArgumentException("invalid (blank) documentType");
        }

        KNSDocumentEntry documentEntry = (KNSDocumentEntry) dataDictionary.getDocumentEntry(documentType);
        if (documentEntry == null) {
            throw new IllegalArgumentException("unknown documentType '" + documentType + "'");
        }
        Class<? extends DocumentPresentationController> documentPresentationControllerClass = null;
        try{
            documentPresentationControllerClass = documentEntry.getDocumentPresentationControllerClass();
            if(documentPresentationControllerClass != null){
                documentPresentationController = documentPresentationControllerClass.newInstance();
            } else {
                KNSDocumentEntry doc = (KNSDocumentEntry) dataDictionary.getDocumentEntry(documentType);
                if ( doc instanceof TransactionalDocumentEntry) {
                    documentPresentationController = new TransactionalDocumentPresentationControllerBase();
                } else if(doc instanceof MaintenanceDocumentEntry) {
                    documentPresentationController = new MaintenanceDocumentPresentationControllerBase();
                } else {
                    documentPresentationController = new DocumentPresentationControllerBase();
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("unable to instantiate documentPresentationController '" + documentPresentationControllerClass.getName() + "' for doctype '" + documentType + "'", e);
        }

        return documentPresentationController;
    }

    /**
     * @see DocumentHelperService#getDocumentPresentationController(Document)
     */
    public DocumentPresentationController getDocumentPresentationController(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        else if (document.getDocumentHeader() == null) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader");
        }
        else if (!document.getDocumentHeader().hasWorkflowDocument()) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader.workflowDocument");
        }

        String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        DocumentPresentationController documentPresentationController = getDocumentPresentationController(documentType);
        return documentPresentationController;
    }

    /**
     * @return the dataDictionaryService
     */
    public DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return this.dataDictionaryService;
    }

    /**
     * @param dataDictionaryService the dataDictionaryService to set
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
