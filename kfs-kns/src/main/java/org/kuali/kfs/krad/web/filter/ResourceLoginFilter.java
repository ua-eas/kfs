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
package org.kuali.kfs.krad.web.filter;

import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.exception.AuthenticationException;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.AuthenticationService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;

public class ResourceLoginFilter extends LoginFilterBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResourceLoginFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            if (!isUserSessionEstablished(request)) {
                String principalName = ((AuthenticationService) GlobalResourceLoader.getResourceLoader().getService(new QName("kimAuthenticationService"))).getPrincipalName(request);

                if (principalName == null) {
                    LOG.error("doFilter() null principalName");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    removeFromMDC();
                    return;
                }

                final UserSession userSession = new UserSession(principalName);
                request.getSession().setAttribute(KRADConstants.USER_SESSION_KEY, userSession);
            }

            UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
            if ( userSession != null ) {
                GlobalVariables.setUserSession(userSession);
            }

            establishSessionCookie(request, response);
            establishBackdoorUser(request);

            addToMDC(request);

            chain.doFilter(request, response);
        } catch (AuthenticationException|IllegalArgumentException e) {
            LOG.error("doFilter() AuthenticationException",e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } finally {
            removeFromMDC();
        }
    }
}
