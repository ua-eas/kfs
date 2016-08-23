/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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

import org.kuali.kfs.krad.document.TransactionalDocument;

import java.util.Collection;

/**
 * This interface defines methods that a TransactionalDocumentDictionary Service must provide. Defines the API for the interacting
 * with TransactionalDocument-related entries in the data dictionary.
 */
@Deprecated
public interface TransactionalDocumentDictionaryService {
    /**
     * Returns whether or not this document's data dictionary file has flagged it to allow document copies.
     *
     * @param document
     * @return True if copies are allowed, false otherwise.
     */
    public Boolean getAllowsCopy(TransactionalDocument document);

    /**
     * Retrieves a document instance by it's class name.
     *
     * @param documentTypeName
     * @return A document instance.
     */
    public Class getDocumentClassByName(String documentTypeName);

    /**
     * Retrieves the full description of the transactional document as described in its data dictionary entry.
     *
     * @param transactionalDocumentTypeName
     * @return The transactional document's full description.
     */
    public String getDescription(String transactionalDocumentTypeName);

    /**
     * Retrieves the label for the transactional document as described in its data dictionary entry.
     *
     * @param transactionalDocumentTypeName
     * @return The transactional document's label.
     */
    public String getLabel(String transactionalDocumentTypeName);


    /**
     * The collection of ReferenceDefinition objects defined as DefaultExistenceChecks for the MaintenanceDocument.
     *
     * @param document
     * @return A Collection of ReferenceDefinitions
     */
    public Collection getDefaultExistenceChecks(TransactionalDocument document);

    /**
     * The collection of ReferenceDefinition objects defined as DefaultExistenceChecks for the MaintenanceDocument.
     *
     * @param docTypeName
     * @return A Collection of ReferenceDefinitions
     */
    public Collection getDefaultExistenceChecks(String docTypeName);
}
