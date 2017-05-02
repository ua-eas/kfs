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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.krad.exception.IntrospectionException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PersistenceServiceImplBase extends PersistenceServiceStructureImplBase {

    /**
     * @see org.kuali.rice.krad.service.PersistenceMetadataService#getPrimaryKeyFields(java.lang.Object)
     */
    public Map getPrimaryKeyFieldValues(Object persistableObject) {
        return getPrimaryKeyFieldValues(persistableObject, false);
    }

    /**
     * @see org.kuali.rice.krad.service.PersistenceMetadataService#getPrimaryKeyFields(java.lang.Object,
     * boolean)
     */
    public Map getPrimaryKeyFieldValues(Object persistableObject, boolean sortFieldNames) {
        if (persistableObject == null) {
            throw new IllegalArgumentException("invalid (null) persistableObject");
        }

        Map keyValueMap = null;
        if (sortFieldNames) {
            keyValueMap = new TreeMap();
        } else {
            keyValueMap = new HashMap();
        }

        String className = null;
        String fieldName = null;
        try {
            List fields = listPrimaryKeyFieldNames(persistableObject.getClass());
            for (Iterator i = fields.iterator(); i.hasNext(); ) {
                fieldName = (String) i.next();
                className = persistableObject.getClass().getName();
                Object fieldValue = PropertyUtils.getSimpleProperty(persistableObject, fieldName);

                keyValueMap.put(fieldName, fieldValue);
            }
        } catch (IllegalAccessException e) {
            throw new IntrospectionException("problem accessing property '" + className + "." + fieldName + "'", e);
        } catch (NoSuchMethodException e) {
            throw new IntrospectionException("unable to invoke getter for property '" + className + "." + fieldName + "'", e);
        } catch (InvocationTargetException e) {
            throw new IntrospectionException("problem invoking getter for property '" + className + "." + fieldName + "'", e);
        }

        return keyValueMap;
    }

}
