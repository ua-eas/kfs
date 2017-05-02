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
package org.kuali.kfs.krad.keyvalues;

import org.apache.commons.lang.WordUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * ValuesFinder that derives values directly from a Java enum.
 * KeyValues are provided in enum definition order, enum name
 * is the key, capitalized lowercase enum name is the label.
 */
public class EnumValuesFinder extends KeyValuesBase {
    private Class<? extends Enum> enumeration;

    public EnumValuesFinder(Class<? extends Enum> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        for (Enum enumval : enumeration.getEnumConstants()) {
            labels.add(new ConcreteKeyValue(getEnumKey(enumval), getEnumLabel(enumval)));
        }
        return labels;
    }

    /**
     * Derives a key value from an enum
     */
    protected String getEnumKey(Enum enm) {
        return enm.name();
    }

    /**
     * Derives a label value from an enum
     */
    protected String getEnumLabel(Enum enm) {
        return WordUtils.capitalize(enm.name().toLowerCase());
    }
}
