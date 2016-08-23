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
package org.kuali.kfs.coreservice.web.component;

import org.kuali.kfs.coreservice.impl.component.ComponentBo;
import org.kuali.kfs.coreservice.impl.component.DerivedComponentBo;
import org.kuali.kfs.kns.inquiry.KualiInquirableImpl;
import org.kuali.kfs.krad.inquiry.Inquirable;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.service.KRADServiceLocator;

import java.util.HashMap;
import java.util.Map;

/**
 * Since ParameterDetailType can be either DataDictionary or DB based, we need a custom {@link Inquirable} to
 * check both.
 *
 */
public class ComponentInquirableImpl extends KualiInquirableImpl {

	private static final String COMPONENT_CODE = "code";
	private static final String NAMESPACE_CODE = "namespaceCode";

	@Override
	public Object retrieveDataObject(Map fieldValues){
		BusinessObject result = (BusinessObject)super.retrieveDataObject(fieldValues);
		if (result == null) {
            result = loadDerivedComponent(fieldValues);
        }
		return result;
    }

	@Override
	public BusinessObject getBusinessObject(Map fieldValues) {
		BusinessObject result = super.getBusinessObject(fieldValues);
		if (result == null) {
			result = loadDerivedComponent(fieldValues);
		}
		return result;
	}

    protected ComponentBo loadDerivedComponent(Map fieldValues) {
        String componentCode = (String)fieldValues.get(COMPONENT_CODE);
	    String namespaceCode = (String)fieldValues.get(NAMESPACE_CODE);
        if (componentCode == null) {
            throw new RuntimeException(COMPONENT_CODE + " is a required key for this inquiry");
        }
	    if (namespaceCode == null) {
            throw new RuntimeException(NAMESPACE_CODE + " is a required key for this inquiry");
        }
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(COMPONENT_CODE, componentCode);
        primaryKeys.put(NAMESPACE_CODE, namespaceCode);

        DerivedComponentBo derivedComponentBo = KRADServiceLocator.getBusinessObjectService().findByPrimaryKey(
                DerivedComponentBo.class, primaryKeys);
        if (derivedComponentBo != null) {
            return DerivedComponentBo.toComponentBo(derivedComponentBo);
        }
        return null;
    }

}
