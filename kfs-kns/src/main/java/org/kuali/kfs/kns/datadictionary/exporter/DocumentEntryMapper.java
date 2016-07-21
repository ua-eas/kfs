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
package org.kuali.kfs.kns.datadictionary.exporter;

import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.kfs.kns.datadictionary.KNSDocumentEntry;
import org.kuali.kfs.kns.service.DocumentHelperService;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

/**
 * DocumentEntryMapper
 * 
 * 
 */
@Deprecated
public abstract class DocumentEntryMapper {
    
    protected DocumentType getDocumentType(String documentTypeName) {
        return KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeName);
    }

    /**
     * @param entry
     * @return Map containing entries for properties common to all DocumentEntry subclasses
     */
    @SuppressWarnings("unchecked")
	protected ExportMap mapEntry(KNSDocumentEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("invalid (null) entry");
        }

        // simple properties
        ExportMap entryMap = new ExportMap(entry.getJstlKey());

        Class businessRulesClass = entry.getBusinessRulesClass();
        if (businessRulesClass != null) {
            entryMap.set("businessRulesClass", businessRulesClass.getName());
        }

        entryMap.set("documentTypeName", entry.getDocumentTypeName());

        DocumentType docType = getDocumentType(entry.getDocumentTypeName());
        entryMap.set("label", docType.getLabel());

        if (docType.getDescription() != null) {
            entryMap.set("description", docType.getDescription());
        }

        DocumentHelperService documentHelperService = KNSServiceLocator.getDocumentHelperService();
        entryMap.set("documentAuthorizerClass", documentHelperService.getDocumentAuthorizer(entry.getDocumentTypeName()).getClass().getName());
        entryMap.set("documentPresentationControllerClass", documentHelperService.getDocumentPresentationController(entry.getDocumentTypeName()).getClass().getName());

        entryMap.set("allowsNoteAttachments", Boolean.toString(entry.getAllowsNoteAttachments()));

        entryMap.set("allowsNoteFYI", Boolean.toString(entry.getAllowsNoteFYI()));
        
        if (entry.getAttachmentTypesValuesFinderClass() != null) {
            entryMap.set("attachmentTypesValuesFinderClass", entry.getAttachmentTypesValuesFinderClass().getName());
        }

        entryMap.set("displayTopicFieldInNotes", Boolean.toString(entry.getDisplayTopicFieldInNotes()));
        entryMap.set("sessionDocument", Boolean.toString(entry.isSessionDocument()));
        
        entryMap.set(new AttributesMapBuilder().buildAttributesMap((DataDictionaryEntryBase) entry));
        entryMap.set(new CollectionsMapBuilder().buildCollectionsMap((DataDictionaryEntryBase) entry));

        return entryMap;
    }

}
