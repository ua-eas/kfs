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
package org.kuali.kfs.krad.service;

import java.util.List;

public interface KualiModuleService {

    /**
     * get the list of all installed module services
     *
     * @return
     */
    List<ModuleService> getInstalledModuleServices();

    /**
     * Returns the module service with the given ID or null if the module ID is not found.
     *
     * @param moduleId
     * @return
     */
    ModuleService getModuleService(String moduleId);

    /**
     * Returns the module service with the given moduleCode or null if the moduleCode is not found.
     *
     * @param namespaceCode
     * @return
     */
    ModuleService getModuleServiceByNamespaceCode(String namespaceCode);

    boolean isModuleServiceInstalled(String namespaceCode);

    /**
     * Given a class, this method will return the module service which is responsible for authorizing access to it. It returns null if no
     * module is found.
     *
     * @param boClass
     * @return ModuleService representing the service responsible for the passed in Class
     * @throws ModuleServiceNotFoundException if boClass is an ExternalizableBusinessObject that no ModuleService is responsible for.
     */
    ModuleService getResponsibleModuleService(Class boClass);

    /**
     * Given a job name, this method will return the module service which is responsible for handling it. It returns null if no
     * module is found.
     *
     * @param jobName
     * @return
     */
    ModuleService getResponsibleModuleServiceForJob(String jobName);

    public void setInstalledModuleServices(List<ModuleService> moduleServices);

    public List<String> getDataDictionaryPackages();

    /**
     * This method gets namespace name for the given namespace code
     *
     * @param namespaceCode
     * @return
     */
    public String getNamespaceName(String namespaceCode);

    /**
     * Determines whether a business object is an externally implemented EBO.
     *
     * @param boClassName
     * @return
     */
    public boolean isBusinessObjectExternal(String boClassName);

    String getNamespaceCode(Class<?> documentOrStepClass);

    String getComponentCode(Class<?> documentOrStepClass);

}

