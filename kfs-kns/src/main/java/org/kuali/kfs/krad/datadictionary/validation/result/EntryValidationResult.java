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
package org.kuali.kfs.krad.datadictionary.validation.result;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 *
 */
public class EntryValidationResult {

    private String entryName;
    private Map<String, AttributeValidationResult> attributeValidationResultMap;

    public EntryValidationResult(String entryName) {
        this.entryName = entryName;
        this.attributeValidationResultMap = new LinkedHashMap<String, AttributeValidationResult>();
    }

    public Iterator<AttributeValidationResult> iterator() {
        return attributeValidationResultMap.values().iterator();
    }

    protected AttributeValidationResult getAttributeValidationResult(String attributeName) {
        AttributeValidationResult attributeValidationResult = attributeValidationResultMap.get(attributeName);
        if (attributeValidationResult == null) {
            attributeValidationResult = new AttributeValidationResult(attributeName);
            attributeValidationResultMap.put(attributeName, attributeValidationResult);
        }
        return attributeValidationResult;
    }

}
