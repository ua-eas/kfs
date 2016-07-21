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
package org.kuali.kfs.kns.web.struts.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class handles the logout. After logout it will do an external redirect to an url
 * specified by a Parameter (LOGOUT_REDIRECT_URL) or a config property (rice.portal.logout.redirectUrl).
 * 
 * 
 *
 */
public class KualiLogoutAction extends Action {

    /**
     * Invalidates the users session and redirects to a configurable url after logout.
     * 
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String redirectString = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.LOGOFF_REDIRECT_URL_PARAMETER);

        if(redirectString == null) {
            redirectString = ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.LOGOFF_REDIRECT_URL_PROPERTY);
        }
        
        request.getSession().invalidate();
        
        return new ActionForward(redirectString, true);
    }

}
