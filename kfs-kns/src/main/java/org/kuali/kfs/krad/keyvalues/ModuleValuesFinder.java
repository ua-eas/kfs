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
package org.kuali.kfs.krad.keyvalues;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.ModuleService;

import java.util.ArrayList;
import java.util.List;

public class ModuleValuesFinder extends org.kuali.kfs.krad.keyvalues.KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
	public List<KeyValue> getKeyValues() {
    	List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("", ""));
        //keyValues.add(getKeyValue(RiceConstants.CROSS_MODULE_CODE, RiceConstants.CROSS_MODULE_NAME));
        for (ModuleService moduleService : KRADServiceLocatorWeb.getKualiModuleService().getInstalledModuleServices()) {
            keyValues.add(getKeyValue(moduleService.getModuleConfiguration().getNamespaceCode(), 
            		KRADServiceLocatorWeb.getKualiModuleService().getNamespaceName(moduleService.getModuleConfiguration().getNamespaceCode())));
        }
        return keyValues;
    }
    
    private KeyValue getKeyValue(String moduleCode, String moduleName) {
        return new ConcreteKeyValue(moduleCode, moduleCode + " - " + moduleName);
    }
}
