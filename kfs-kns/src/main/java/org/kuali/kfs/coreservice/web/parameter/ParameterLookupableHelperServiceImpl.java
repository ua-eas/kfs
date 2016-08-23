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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.coreservice.impl.component.DerivedComponentBo;
import org.kuali.kfs.coreservice.impl.parameter.ParameterBo;
import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 */
public class ParameterLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final long serialVersionUID = 4381873774407301041L;

    private static final Log LOG = LogFactory.getLog(ParameterLookupableHelperServiceImpl.class);
    private static final String COMPONENT_NAME = "component.name";
    private static final String COMPONENT_CODE = "componentCode";

    @Override
    protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {

        ParameterBo parm = (ParameterBo) businessObject;

        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, parm.getNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, parm.getComponentCode());
        permissionDetails.put(KimConstants.AttributeConstants.PARAMETER_NAME, parm.getName());
        return KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(
            GlobalVariables.getUserSession().getPerson().getPrincipalId(), KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.MAINTAIN_SYSTEM_PARAMETER, permissionDetails,
            Collections.<String, String>emptyMap());
    }

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {

        if (fieldValues.containsKey(COMPONENT_NAME) && fieldValues.containsKey(COMPONENT_CODE)) {
            //remove hidden derived component code if component name exists.
            fieldValues.remove(COMPONENT_CODE);
            fieldValues.put(COMPONENT_CODE, "");
        }

        String componentNameFieldValue = fieldValues.get(COMPONENT_NAME);
        fieldValues.remove(COMPONENT_NAME);

        List<ParameterBo> results = (List<ParameterBo>) super.getSearchResultsUnbounded(fieldValues);
        normalizeParameterComponents(results);

        if (!StringUtils.isBlank(componentNameFieldValue)) {
            componentNameFieldValue = componentNameFieldValue.trim();
            componentNameFieldValue = componentNameFieldValue.replace("*", ".*");
            componentNameFieldValue = ".*" + componentNameFieldValue + ".*";

            Pattern pattern = Pattern.compile(componentNameFieldValue, Pattern.CASE_INSENSITIVE);

            Iterator<ParameterBo> resultsIter = results.iterator();
            while (resultsIter.hasNext()) {
                ParameterBo result = resultsIter.next();
                if ((result.getComponent() == null) ||
                    (!pattern.matcher(result.getComponent().getName()).matches())) {
                    resultsIter.remove();
                }
            }
        }

        return results;
    }

    private void normalizeParameterComponents(List<ParameterBo> parameters) {
        // attach the derived components where needed
        for (ParameterBo parameterBo : parameters) {
            if (parameterBo.getComponent() == null) {
                parameterBo.setComponent(DerivedComponentBo.toComponentBo(parameterBo.getDerivedComponent()));
            }
        }
    }

}

