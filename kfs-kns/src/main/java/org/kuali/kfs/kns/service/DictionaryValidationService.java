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

import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.document.Document;


@Deprecated
public interface DictionaryValidationService extends org.kuali.kfs.krad.service.DictionaryValidationService {

    /**
     * Validates the contents of a document (i.e. attributes within a document) against the data dictionary.
     * Recursively
     * checks
     * business objects of the document.
     *
     * @param document - document to validate
     * @param depth - Specify how deep the recrusion should go (0 based). If a negative number is supplied, it's
     * infinite.
     * @deprecated Use {@link #validateDocumentAndUpdatableReferencesRecursively(Document,
     *             int, boolean)}
     */
    @Deprecated
    public void validateDocumentRecursively(Document document, int depth);

    @Deprecated
    public void validateBusinessObjectOnMaintenanceDocument(BusinessObject businessObject, String docTypeName);

    /**
     * Validates the business object against the dictionary, uses reflection to get any child business objects, and
     * recursively
     * calls back. Adds errors to the map as they are encountered.
     *
     * @param businessObject - business object to validate
     * @param depth - Specify how deep the recrusion should go (0 based). If a negative number is supplied, it's
     * infinite.
     * @deprecated since 1.1
     */
    @Deprecated
    public void validateBusinessObjectsRecursively(BusinessObject businessObject, int depth);

    /**
     * Validates an attribute of a given class for proper min, max length, syntax, and required.
     *
     * @param entryName - name of the dd entry
     * @param attributeName - name of attribute in the bo class
     * @param attributeValue - current value to validate
     * @param errorKey - key to place the errors under
     * @deprecated since 1.1
     */
    @Deprecated
    public void validateAttributeFormat(String entryName, String attributeName, String attributeValue, String errorKey);

    /**
     * Validates an attribute of a given class for proper min, max length, syntax, and required. The attribute will be
     * validated
     * according to the specified data type.
     *
     * @param entryName - name of the dd entry
     * @param attributeName - name of attribute in the bo class
     * @param attributeValue - current value to validate
     * @param attributeDataType - data type that this attribute should be treated as for validation purposes
     * @param errorKey - key to place the errors under
     * @deprecated since 1.1
     */
    @Deprecated
    public void validateAttributeFormat(String entryName, String attributeName, String attributeValue,
            String attributeDataType, String errorKey);

    /**
     * Validates an attribute of a given class for required check.
     *
     * @param entryName - name of the dd entry
     * @param attributeName - name of attribute in the bo class
     * @param attributeValue - current value to validate
     * @param errorKey - key to place to errors under
     * @deprecated since 1.1
     */
    @Deprecated
    public void validateAttributeRequired(String entryName, String attributeName, Object attributeValue,
            Boolean forMaintenance, String errorKey);
}
