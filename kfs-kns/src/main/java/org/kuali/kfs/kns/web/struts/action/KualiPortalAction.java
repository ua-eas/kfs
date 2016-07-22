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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.kfs.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the action for the portal.
 *
 * 
 */
public class KualiPortalAction extends KualiSimpleAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String gotoUrl = null;
        String selectedTab = null;

        if (request.getQueryString() != null && request.getQueryString().indexOf("channelUrl") >= 0) {
            gotoUrl = request.getQueryString().substring(request.getQueryString().indexOf("channelUrl") + 11, request.getQueryString().length());
        } else if (request.getParameter("channelUrl") != null && request.getParameter("channelUrl").length() > 0) {
            gotoUrl = request.getParameter("channelUrl");
        }

        if (gotoUrl != null) {
            // encode some characters for security purposes if present in url
            gotoUrl = gotoUrl.replace(">", "%3E");
            gotoUrl = gotoUrl.replace("<", "%3C");
            gotoUrl = gotoUrl.replace("\"", "%22");

            // check url allowed to display in portal
            Pattern pattern = Pattern.compile(ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.PORTAL_ALLOWED_REGEX));
            Matcher matcher = pattern.matcher(gotoUrl);
            if(!matcher.matches()) {
                throw new Exception("The requested channel URL is not authorized for display in portal.");
            }
        }

        if (request.getParameter("selectedTab") != null && request.getParameter("selectedTab").length() > 0) {
            request.getSession().setAttribute("selectedTab", request.getParameter("selectedTab"));
        }

        request.setAttribute("gotoUrl", gotoUrl);

        return super.execute(mapping, form, request, response);
    }
}
