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
package org.kuali.kfs.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.kfs.krad.util.GlobalVariables;

import java.util.Map;

/**
 * Defines functions that can be used in el expressions within
 * the UIF dictionary files
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExpressionFunctions {

    /**
     * Checks whether the given class parameter is assignable from the given object class
     * parameter
     *
     * @param assignableClass - class to use for assignable to
     * @param objectClass - class to use for assignable from
     * @return boolean true if the object class is of type assignable class, false if not
     */
    public static boolean isAssignableFrom(Class<?> assignableClass, Class<?> objectClass) {
        return assignableClass.isAssignableFrom(objectClass);
    }

    /**
     * Checks whether the given value is null or blank string
     *
     * @param value - property value to check
     * @return boolean true if value is null or blank, false if not
     */
    public static boolean empty(Object value) {
        return (value == null) || (StringUtils.isBlank(value.toString()));
    }

    /**
     * Returns the name for the given class
     *
     * @param clazz - class object to return name for
     * @return String class name or empty string if class is null
     */
    public static String getName(Class<?> clazz) {
        if (clazz == null) {
            return "";
        } else {
            return clazz.getName();
        }
    }

    /**
     * Retrieves the value of the parameter identified with the given namespace, component, and name
     *
     * @param namespaceCode - namespace code for the parameter to retrieve
     * @param componentCode - component code for the parameter to retrieve
     * @param parameterName - name of the parameter to retrieve
     * @return String value of parameter as a string or null if parameter does not exist
     */
    public static String getParm(String namespaceCode, String componentCode, String parameterName) {
        return CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(namespaceCode, componentCode,
                parameterName);
    }

    /**
     * Retrieves the value of the parameter identified with the given namespace, component, and name and converts
     * to a Boolean
     *
     * @param namespaceCode - namespace code for the parameter to retrieve
     * @param componentCode - component code for the parameter to retrieve
     * @param parameterName - name of the parameter to retrieve
     * @return Boolean value of parameter as a boolean or null if parameter does not exist
     */
    public static Boolean getParmInd(String namespaceCode, String componentCode, String parameterName) {
        return CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(namespaceCode,
                componentCode, parameterName);
    }

    /**
     * Indicates whether the current user has the permission identified by the given namespace and permission name
     *
     * @param namespaceCode - namespace code for the permission to check
     * @param permissionName - name of the permission to check
     * @return boolean true if the current user has the permission, false if not or the permission does not exist
     */
    public static boolean hasPerm(String namespaceCode, String permissionName) {
        Person user = GlobalVariables.getUserSession().getPerson();

        return KimApiServiceLocator.getPermissionService().hasPermission(user.getPrincipalId(), namespaceCode,
                permissionName);
    }

    /**
     * Indicates whether the current user has the permission identified by the given namespace and permission name
     * and with the given details and role qualification
     *
     * @param namespaceCode - namespace code for the permission to check
     * @param permissionName - name of the permission to check
     * @param permissionDetails - details for the permission check
     * @param roleQualifiers - qualification for assigned roles
     * @return boolean true if the current user has the permission, false if not or the permission does not exist
     */
    public static boolean hasPermDtls(String namespaceCode, String permissionName, Map<String, String> permissionDetails,
            Map<String, String> roleQualifiers) {
        Person user = GlobalVariables.getUserSession().getPerson();

        return KimApiServiceLocator.getPermissionService().isAuthorized(user.getPrincipalId(), namespaceCode,
                permissionName, roleQualifiers);
    }

    /**
     * Indicates whether the current user has the permission identified by the given namespace and template name
     * and with the given details and role qualification
     *
     * @param namespaceCode - namespace code for the permission to check
     * @param templateName - name of the permission template to find permissions for
     * @param permissionDetails - details for the permission check
     * @param roleQualifiers - qualification for assigned roles
     * @return boolean true if the current user has a permission with the given template, false if not or
     * the permission does not exist
     */
    public static boolean hasPermTmpl(String namespaceCode, String templateName, Map<String, String> permissionDetails,
            Map<String, String> roleQualifiers) {
        Person user = GlobalVariables.getUserSession().getPerson();

        return KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(user.getPrincipalId(), namespaceCode,
                templateName, permissionDetails, roleQualifiers);
    }
}
