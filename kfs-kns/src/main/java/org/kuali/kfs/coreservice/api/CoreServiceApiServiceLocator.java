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
package org.kuali.kfs.coreservice.api;

import org.kuali.kfs.coreservice.api.component.ComponentService;
import org.kuali.kfs.coreservice.api.namespace.NamespaceService;
import org.kuali.kfs.coreservice.api.parameter.ParameterRepositoryService;
import org.kuali.kfs.coreservice.api.style.StyleRepositoryService;
import org.kuali.kfs.coreservice.api.style.StyleService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

public class CoreServiceApiServiceLocator {

    public static final String NAMESPACE_SERVICE = "cf.namespaceService";
    public static final String PARAMETER_REPOSITORY_SERVICE = "cf.parameterRepositoryService";
    public static final String COMPONENT_SERVICE = "componentService";
    public static final String STYLE_REPOSITORY_SERVICE = "cf.styleRepositoryService";

    public static final String STYLE_SERVICE = "cf.styleService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static NamespaceService getNamespaceService() {
        return getService(NAMESPACE_SERVICE);
    }

    public static ParameterRepositoryService getParameterRepositoryService() {
        return getService(PARAMETER_REPOSITORY_SERVICE);
    }

    public static ComponentService getComponentService() {
        return getService(COMPONENT_SERVICE);
    }

    public static StyleService getStyleService() {
        return getService(STYLE_SERVICE);
    }

    public static StyleRepositoryService getStyleRepositoryService() {
        return getService(STYLE_REPOSITORY_SERVICE);
    }

}
