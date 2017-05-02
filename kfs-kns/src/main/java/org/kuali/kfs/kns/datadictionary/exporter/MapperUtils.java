/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

import org.kuali.kfs.kns.datadictionary.FieldDefinition;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

/**
 * Defines utility methods associated with mapping Entries et al
 */
@Deprecated
public final class MapperUtils {

    private MapperUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    /**
     * @param attributeName
     * @return ExportMap containing the standard entries associated with an attribute name
     */
    public static ExportMap buildAttributeMap(String attributeName) {
        ExportMap attributeMap = new ExportMap(attributeName);

        attributeMap.set("attributeName", attributeName);

        return attributeMap;
    }

    /**
     * @param fieldDefinition
     * @return ExportMap containing the standard entries associated with a FieldDefinition
     */
    public static ExportMap buildFieldMap(FieldDefinition field) {
        return buildAttributeMap(field.getAttributeName());
    }

}
