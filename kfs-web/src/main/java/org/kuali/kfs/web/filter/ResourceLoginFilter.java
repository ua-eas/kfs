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
package org.kuali.kfs.web.filter;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.exception.AuthenticationException;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.web.filter.LoginFilterBase;
import org.kuali.kfs.sys.businessobject.JwtData;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.CoreApiKeyAuthenticationService;
import org.kuali.kfs.sys.service.JwtService;
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
import java.util.Optional;

public class ResourceLoginFilter extends LoginFilterBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResourceLoginFilter.class);

    public static final String UNAUTHORIZED_JSON = "[ \"Unauthorized\" ]";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.debug("doFilter() started");

        try {
            String authorizationHeader = request.getHeader("Authorization");
            Optional<String> user = getPrincipalNameFromHeader(request, response, authorizationHeader);
            if (!user.isPresent()) {
                sendError(response);
                removeFromMDC();
                return;
            }

            setUserSession(request, user.get());
            establishUserSession(request, response);

            chain.doFilter(request, response);
        } catch (AuthenticationException | IllegalArgumentException e) {
            LOG.error("doFilter() AuthenticationException", e);
            sendError(response);
        } finally {
            removeFromMDC();
        }
    }

    protected void establishUserSession(HttpServletRequest request, HttpServletResponse response) {
        UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        if (userSession != null) {
            GlobalVariables.setUserSession(userSession);
        }

        establishSessionCookie(request, response);
        establishBackdoorUser(request);

        addToMDC(request);
    }

    private Optional<String> getPrincipalNameFromHeader(HttpServletRequest request, HttpServletResponse response, String authorizationHeader) throws IOException {
        if (authorizationHeader == null) {
            return Optional.empty();
        }
        Optional<String> oKey = getApiKey(authorizationHeader);
        if (oKey.isPresent()) {
            if (getCoreApiKeyAuthenticationService().useCore()) {
                return getCoreApiKeyAuthenticationService().getPrincipalIdFromApiKey(oKey.get());

            } else {
                try {
                    JwtData data = getJwtService().decodeJwt(oKey.get());
                    return Optional.of(data.getPrincipalName());
                } catch (RuntimeException e) {
                    LOG.debug("getPrincipalNameFromHeader() invalid financials token", e);
                }
            }
        }

        return Optional.empty();
    }

    private void sendError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(UNAUTHORIZED_JSON);
    }

    protected void setUserSession(HttpServletRequest request, String principalName) {
        UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        if (userSession == null || userSession.getActualPerson() == null || !StringUtils.equals(userSession.getActualPerson().getPrincipalName(), principalName)) {
            final UserSession newUserSession = new UserSession(principalName);
            request.getSession().setAttribute(KRADConstants.USER_SESSION_KEY, newUserSession);
        }
    }

    private Optional<String> getApiKey(String authorizationHeader) {
        if (!authorizationHeader.toLowerCase().startsWith("bearer")) {
            LOG.error("getApiKey() authorization header missing Bearer prefix");
            return Optional.empty();
        }

        String split[] = authorizationHeader.split("\\s+");
        if (split.length != 2) {
            LOG.error("doFilter() authorization header should be two parts");
            return Optional.empty();
        }

        return Optional.of(split[1]);
    }

    protected CoreApiKeyAuthenticationService getCoreApiKeyAuthenticationService() {
        return SpringContext.getBean(CoreApiKeyAuthenticationService.class);
    }

    protected JwtService getJwtService() {
        return SpringContext.getBean(JwtService.class);
    }

    protected AuthenticationService getAuthenticationService() {
        return (AuthenticationService) GlobalResourceLoader.getResourceLoader().getService(new QName("kimAuthenticationService"));
    }
}
