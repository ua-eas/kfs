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
package org.kuali.kfs.coreservice.web.parameter;

import org.kuali.kfs.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.kfs.coreservice.api.parameter.Parameter;
import org.kuali.kfs.coreservice.api.parameter.ParameterKey;
import org.kuali.kfs.coreservice.api.parameter.ParameterRepositoryService;
import org.kuali.kfs.coreservice.api.parameter.ParameterType;
import org.kuali.kfs.coreservice.impl.parameter.ParameterBo;
import org.kuali.kfs.kns.maintenance.KualiMaintainableImpl;

public class ParameterMaintainableImpl extends KualiMaintainableImpl {
    private static final long serialVersionUID = 4914145799502207182L;

    @Override
    public void saveDataObject() {
        if (super.getDataObject() instanceof ParameterBo) {
            ParameterBo object  = (ParameterBo)getDataObject();
            Parameter param = null;

            // construct a ParameterType.Builder for when we need to set the type
            // (since object may not have a ParameterType set)
            ParameterType.Builder parameterTypeBuilder =
                    ParameterType.Builder.create(object.getParameterTypeCode());

            ParameterRepositoryService parameterRepository = CoreServiceApiServiceLocator.getParameterRepositoryService();

            param = parameterRepository.getParameter(
                    ParameterKey.create(object.getApplicationId(),
                            object.getNamespaceCode(),
                            object.getComponentCode(),
                            object.getName()));

            // Note that the parameter repository service will try to get back a parameter with the Rice application
            // ID if it can't find it with the give application ID.  If the given application ID is not Rice, and a
            // parameter for Rice comes back, we have to be careful and not update it as that would effectively
            // erase the Rice parameter.

            if(param == null || !object.getApplicationId().equals(param.getApplicationId())) { // create new
                param = parameterRepository.createParameter(Parameter.Builder.create(object.getApplicationId(),
                        object.getNamespaceCode(), object.getComponentCode(), object.getName(), parameterTypeBuilder
                    /* set the type using our builder from above */).build());

            }

            Parameter.Builder b = Parameter.Builder.create(param);
            b.setValue(object.getValue());
            b.setDescription(object.getDescription());
            b.setEvaluationOperator(object.getEvaluationOperator());
            b.setParameterType(parameterTypeBuilder); // set the type using our builder from above

            parameterRepository.updateParameter(b.build());

        } else {
            throw new RuntimeException(
                    "Cannot update object of type: " + this.getDataObjectClass() + " with Parameter repository service");
        }
    }

}
