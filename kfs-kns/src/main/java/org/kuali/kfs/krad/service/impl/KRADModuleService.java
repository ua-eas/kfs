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

import org.kuali.kfs.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

import java.util.List;
import java.util.Map;

/**
 * Module service implementation for the Rice KRAD module
 */
public class KRADModuleService extends ModuleServiceBase {
    protected List<String> businessObjects;

    @Override
    public boolean isResponsibleFor(Class businessObjectClass) {
        if (businessObjects != null) {
            if (businessObjects.contains(businessObjectClass.getName())) {
                return true;
            }
        }

        if (ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass)) {
            Class externalizableBusinessObjectInterface =
                ExternalizableBusinessObjectUtils.determineExternalizableBusinessObjectSubInterface(
                    businessObjectClass);
            if (externalizableBusinessObjectInterface != null) {
                Map<Class, Class> validEBOs = getModuleConfiguration().getExternalizableBusinessObjectImplementations();
                if (validEBOs != null) {
                    if (validEBOs.get(externalizableBusinessObjectInterface) != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<String> getBusinessObjects() {
        return this.businessObjects;
    }

    public void setBusinessObjects(List<String> businessObjects) {
        this.businessObjects = businessObjects;
    }
}
