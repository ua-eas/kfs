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
package org.kuali.kfs.krad.util;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * A class with utilities related to externalizable business objects
 */
public final class ExternalizableBusinessObjectUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExternalizableBusinessObjectUtils.class);

    private ExternalizableBusinessObjectUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    /**
     * Given a class, this method determines which of the interfaces that the class implements extends {@link ExternalizableBusinessObject}
     *
     * @param businessObjectClass
     * @return
     */
    public static Class determineExternalizableBusinessObjectSubInterface(Class businessObjectClass) {
        if (businessObjectClass == null) {
            return null;
        }
        if (businessObjectClass.isInterface() && !ExternalizableBusinessObject.class.equals(businessObjectClass) && ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass)) {
            return businessObjectClass;
        }
        if (ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass)) {
            Class tempClass = businessObjectClass;
            while (tempClass != null && !Object.class.equals(tempClass)) {
                for (Class tempClassInterface : tempClass.getInterfaces()) {
                    if (!ExternalizableBusinessObject.class.equals(tempClassInterface) && ExternalizableBusinessObject.class.isAssignableFrom(tempClassInterface)) {
                        return tempClassInterface;
                    }
                }
                tempClass = tempClass.getSuperclass();
            }
        }
        return null;
    }

    public static boolean isExternalizableBusinessObjectInterface(Class businessObjectClass) {
        return businessObjectClass != null && businessObjectClass.isInterface() && ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass);
    }

    public static boolean isExternalizableBusinessObject(Class businessObjectClass) {
        return businessObjectClass != null && ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass);
    }


    public static boolean isExternalizableBusinessObjectInterface(String businessObjectClassName) {
        try {
            Class businessObjectClass = Class.forName(businessObjectClassName);
            return isExternalizableBusinessObjectInterface(businessObjectClass);
        } catch (Exception ex) {
            LOG.debug("Unable to get class object for class name: " + businessObjectClassName, ex);
            return false;
        }
    }
}
