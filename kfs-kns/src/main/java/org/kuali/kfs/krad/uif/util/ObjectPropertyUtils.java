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
package org.kuali.kfs.krad.uif.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * Utility methods to get/set property values and working with objects
 *
 * @see org.springframework.beans.BeanWrapper
 */
public class ObjectPropertyUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectPropertyUtils.class);

    public static void copyPropertiesToObject(Map<String, String> properties, Object object) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            setPropertyValue(object, property.getKey(), property.getValue());
        }
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Object object) {
        return wrapObject(object).getPropertyDescriptors();
    }

    public static Class<?> getPropertyType(Class<?> object, String propertyPath) {
        return new BeanWrapperImpl(object).getPropertyType(propertyPath);
    }

    public static Class<?> getPropertyType(Object object, String propertyPath) {
        return wrapObject(object).getPropertyType(propertyPath);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> T getPropertyValue(Object object, String propertyPath) {
        T result = null;
        try {
            result = (T) wrapObject(object).getPropertyValue(propertyPath);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error getting property '" + propertyPath + "' from " + object, e);
        }
        return result;
    }

    public static void initializeProperty(Object object, String propertyPath) {
        Class<?> propertyType = getPropertyType(object, propertyPath);
        try {
            setPropertyValue(object, propertyPath, propertyType.newInstance());
        } catch (InstantiationException e) {
            // just set the value to null
            setPropertyValue(object, propertyPath, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to set new instance for property: " + propertyPath, e);
        }
    }

    public static void setPropertyValue(Object object, String propertyPath, Object propertyValue) {
        wrapObject(object).setPropertyValue(propertyPath, propertyValue);
    }

    public static void setPropertyValue(Object object, String propertyPath, Object propertyValue, boolean ignoreUnknown) {
        try {
            wrapObject(object).setPropertyValue(propertyPath, propertyValue);
        } catch (BeansException e) {
            // only throw exception if they have indicated to not ignore unknown
            if (!ignoreUnknown) {
                throw new RuntimeException(e);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Ignoring exception thrown during setting of property '" + propertyPath + "': "
                    + e.getLocalizedMessage());
            }
        }
    }

    public static boolean isReadableProperty(Object object, String propertyPath) {
        return wrapObject(object).isReadableProperty(propertyPath);
    }

    public static boolean isWritableProperty(Object object, String propertyPath) {
        return wrapObject(object).isWritableProperty(propertyPath);
    }

    public static BeanWrapper wrapObject(Object object) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(object);
        beanWrapper.setAutoGrowNestedPaths(true);

        return beanWrapper;
    }

}
