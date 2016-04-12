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

import java.util.Collection;

import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.kfs.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.TransactionalDocumentEntry;
import org.kuali.kfs.krad.document.TransactionalDocument;
import org.kuali.kfs.krad.service.DataDictionaryService;

/**
 * This class is the service implementation for the TransactionalDocumentDictionary structure. Defines the API for the interacting
 * with Document-related entries in the data dictionary. This is the default implementation that gets delivered with Kuali.
 */
@Deprecated
public class TransactionalDocumentDictionaryServiceImpl implements TransactionalDocumentDictionaryService {
    private DataDictionaryService dataDictionaryService;

    /**
     * @see TransactionalDocumentDictionaryService#getAllowsCopy(org.kuali.bo.TransactionalDocument)
     */
    public Boolean getAllowsCopy(TransactionalDocument document) {
        Boolean allowsCopy = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntry(document);
        if (entry != null) {
            allowsCopy = Boolean.valueOf(entry.getAllowsCopy());
        }

        return allowsCopy;
    }

    /**
     * @see TransactionalDocumentDictionaryService#getDocumentClassByName(java.lang.String)
     */
    public Class getDocumentClassByName(String documentTypeName) {
        Class documentClass = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntryBydocumentTypeName(documentTypeName);
        if (entry != null) {
            documentClass = entry.getDocumentClass();
        }

        return documentClass;
    }

    /**
     * @see TransactionalDocumentDictionaryService#getDescription(org.kuali.bo.TransactionalDocument)
     */
    public String getDescription(String transactionalDocumentTypeName) {
        String description = null;

        DocumentType docType = getDocumentType(transactionalDocumentTypeName);
        if (docType != null) {
            description = docType.getDescription();
        }

        return description;
    }

    /**
     * @see TransactionalDocumentDictionaryService#getDescription(org.kuali.bo.TransactionalDocument)
     */
    public String getLabel(String transactionalDocumentTypeName) {
        String label = null;

        DocumentType docType = getDocumentType(transactionalDocumentTypeName);
        if (docType != null) {
            label = docType.getLabel();
        }

        return label;
    }


    /**
     * Sets the data dictionary instance.
     * 
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Retrieves the data dictionary instance.
     * 
     * @return
     */
    public DataDictionary getDataDictionary() {
        return this.dataDictionaryService.getDataDictionary();
    }

    /**
     * This method gets the workflow document type for the given documentTypeName
     * 
     * @param documentTypeName
     * @return
     */
    protected DocumentType getDocumentType(String documentTypeName) {
        return KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeName);
    }

    /**
     * Retrieves the document entry by transactional document class instance.
     * 
     * @param document
     * @return TransactionalDocumentEntry
     */
    private TransactionalDocumentEntry getTransactionalDocumentEntry(TransactionalDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }

        TransactionalDocumentEntry entry = (TransactionalDocumentEntry)getDataDictionary().getDocumentEntry(document.getClass().getName());

        return entry;
    }

    /**
     * Retrieves the document entry by transactional document type name.
     * 
     * @param documentTypeName
     * @return
     */
    private TransactionalDocumentEntry getTransactionalDocumentEntryBydocumentTypeName(String documentTypeName) {
        if (documentTypeName == null) {
            throw new IllegalArgumentException("invalid (null) document type name");
        }

        TransactionalDocumentEntry entry = (TransactionalDocumentEntry) getDataDictionary().getDocumentEntry(documentTypeName);

        return entry;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see TransactionalDocumentDictionaryService#getDefaultExistenceChecks(java.lang.String)
	 */
	public Collection getDefaultExistenceChecks(String docTypeName) {
        Collection defaultExistenceChecks = null;

        TransactionalDocumentEntry entry = getTransactionalDocumentEntryBydocumentTypeName(docTypeName);
        if (entry != null) {
            defaultExistenceChecks = entry.getDefaultExistenceChecks();
        }

        return defaultExistenceChecks;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see TransactionalDocumentDictionaryService#getDefaultExistenceChecks(TransactionalDocument)
	 */
	public Collection getDefaultExistenceChecks(TransactionalDocument document) {
		return getDefaultExistenceChecks(getTransactionalDocumentEntry(document).getDocumentTypeName());
	}
}
