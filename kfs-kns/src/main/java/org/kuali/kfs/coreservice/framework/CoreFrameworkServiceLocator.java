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
package org.kuali.kfs.coreservice.framework;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.core.framework.impex.xml.XmlImpexRegistry;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

public class CoreFrameworkServiceLocator {

    public static final String PARAMETER_SERVICE = "cf.parameterService";
    public static final String XML_IMPEX_REGISTRY = "xmlImpexRegistry";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static ParameterService getParameterService() {
        return getService(PARAMETER_SERVICE);
    }
    
    public static XmlImpexRegistry getXmlImpexRegistry() {
    	return getService(XML_IMPEX_REGISTRY);
    }
}
