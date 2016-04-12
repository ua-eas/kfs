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
package org.kuali.kfs.krad.datadictionary;

import org.kuali.kfs.krad.datadictionary.exception.CompletionException;

import java.util.List;


/**
 * Defines methods common to all DataDictionaryDefinition types.
 * 
 *     DD: The highest level objects in the data dictionary are of
        the following types:
        * BusinessObjectEntry
        * MaintenanceDocumentEntry
        * TransactionalDocumentEntry

    JSTL: The data dictionary is exposed as a Map which is accessed
    by referring to the "DataDictionary" global constant.  This Map contains
    the following kinds of entries keyed as indicated:
        * Business Object Entries -
            Key = dataObjectClass name
            Value = Map created by BusinessObjectEntryMapper
        * Maintenance Document entries -
            Key = DocumentType name
            Value = Map created by MaintenanceObjectEntryMapper
        * Transactional Document entries -
            Key = DocumentType name
            Value = Map created by TransactionalDocumentEntryMapper

    All elements are exposed to JSTL as Maps (where the element has a
    unique key by which they can be retrieved), or Strings.  For collections
    of elements having no unique key, the entry's position in the list
    (0, 1, etc.) is used as its index.

    All Maps (except the top-level DataDictionary one) are guaranteed to
    present their entries with an iteration order identical to the order
    in which the elements were defined in XML.

 */
public interface DataDictionaryEntry {
    /**
     * @return String used as a globally-unique key for this entry's jstl-exported version
     */
    public String getJstlKey();

    /**
     * Kicks off complete entry-wide validation which couldn't be done earlier.
     * 
     * @throws CompletionException if a problem arises during validation-completion
     */
    public void completeValidation();

    /**
     * @param attributeName
     * @return AttributeDefinition with the given name, or null if none with that name exists
     */
    public AttributeDefinition getAttributeDefinition(String attributeName);

    /**
     * Returns the full class name of the underlying object.
     */
    public String getFullClassName();
    
    /**
     * @return a Map containing all RelationshipDefinitions associated with this BusinessObjectEntry, indexed by relationshipName
     */
    public List<RelationshipDefinition> getRelationships();
}
