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
import org.kuali.kfs.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.kfs.coreservice.api.component.Component;
import org.kuali.kfs.coreservice.impl.parameter.ParameterBo;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a description of what this class does - kellerj don't forget to fill
 * this in.
 */
public class ParameterRule extends MaintenanceDocumentRuleBase {

    /**
     * This overridden method ...
     *
     * @see MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        result &= checkAllowsMaintenanceEdit(document.getDocumentHeader().getWorkflowDocument()
            .getInitiatorPrincipalId(), (ParameterBo) document.getNewMaintainableObject().getDataObject());

        result &= checkComponent((ParameterBo) document.getNewMaintainableObject().getDataObject());


        return result;
    }

    protected boolean checkAllowsMaintenanceEdit(String initiatorPrincipalId, ParameterBo newBO) {

        boolean allowsEdit = false;
        ParameterBo parm = newBO;

        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, parm.getNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, parm.getComponentCode());
        permissionDetails.put(KimConstants.AttributeConstants.PARAMETER_NAME, parm.getName());
        allowsEdit = KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(
            GlobalVariables.getUserSession().getPerson().getPrincipalId(), KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.MAINTAIN_SYSTEM_PARAMETER, permissionDetails,
            Collections.<String, String>emptyMap());
        if (!allowsEdit) {
            putGlobalError(RiceKeyConstants.AUTHORIZATION_ERROR_PARAMETER);
        }
        return allowsEdit;
    }

    public boolean checkComponent(ParameterBo param) {
        String componentCode = param.getComponentCode();
        String namespace = param.getNamespaceCode();
        boolean result = false;
        if (StringUtils.isNotBlank(componentCode) && StringUtils.isNotBlank(namespace)) {
            Component component = CoreServiceApiServiceLocator.getComponentService().getComponentByCode(namespace, componentCode);
            if (component != null) {
                result = true;
            }
            if (!result) {
                putFieldError("componentCode", "error.document.parameter.detailType.invalid", componentCode);
            }
        }
        return result;
    }

}
