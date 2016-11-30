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

package org.kuali.kfs.krad.datadictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributeDefinitionSerializer {
    public List<Map<String, Object>> serializeAttributeDefinitionsForEntry(DataDictionaryEntryBase entry) {
        final List<Map<String,Object>> serializedAttributes =
                entry.getAttributes().stream()
                .map(attributeDefinition -> serializeAttributeDefinition(attributeDefinition))
                .collect(Collectors.toList());

        return serializedAttributes;
    }

    protected Map<String, Object> serializeAttributeDefinition(AttributeDefinition attributeDefinition) {
        Map<String, Object> serializedAttributeDefinition = new HashMap<>();

        serializedAttributeDefinition.put("name", attributeDefinition.getName());
        serializedAttributeDefinition.put("label", attributeDefinition.getLabel());
        serializedAttributeDefinition.put("shortLabel", attributeDefinition.getShortLabel());
        serializedAttributeDefinition.put("required", attributeDefinition.isRequired());

        return serializedAttributeDefinition;
    }
}
