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
package org.kuali.kfs.sys.rest.helper;

import org.kuali.kfs.sys.rest.service.SerializationService;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionSerializationHelper {
    private String collectionName;
    private Class<? extends BusinessObject> collectionItemClass;
    private List<String> fields;
    private Map<String, Object> translatedFields;
    private SerializationService serializationService;

    public CollectionSerializationHelper(String collectionName, Class<? extends BusinessObject> collectionItemClass, SerializationService serializationService) {
        this.collectionName = collectionName;
        this.collectionItemClass = collectionItemClass;
        this.fields = new ArrayList<>();
        this.serializationService = serializationService;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public Class<? extends BusinessObject> getCollectionItemClass() {
        return collectionItemClass;
    }

    public List<String> getFields() {
        return fields;
    }

    public void addField(String fieldName) {
        fields.add(fieldName);
    }

    public Map<String, Object> getTranslatedFields() {
        if (translatedFields == null) {
            translatedFields = serializationService.businessObjectFieldsToMap(fields);
        }
        return translatedFields;
    }
}
