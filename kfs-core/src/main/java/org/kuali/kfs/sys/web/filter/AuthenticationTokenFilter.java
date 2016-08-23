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
package org.kuali.kfs.sys.web.filter;

import org.kuali.kfs.sys.businessobject.JwtData;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.CoreApiKeyAuthenticationService;
import org.kuali.kfs.sys.service.JwtService;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * This filter will either generate a session token or take the core token (if running under core authentication) and
 * put that token in a cookie for the financials application javascript to use
 */
public class AuthenticationTokenFilter implements Filter {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AuthenticationTokenFilter.class);

    public static final String AUTH_TOKEN_COOKIE_NAME = "authToken";
    public static final String FIN_AUTH_TOKEN_COOKIE_NAME = "financialsAuthToken";
    public static final String JWT_EXPIRATION_SECONDS = "jwt.expiration.seconds";

    private ConfigurationService configurationService;
    private JwtService jwtService;
    private CoreApiKeyAuthenticationService coreApiKeyAuthenticationService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (getCoreApiKeyAuthenticationService().useCore()) {
            coreDoFilter(request, response, filterChain);
        } else {
            nonCoreDoFilter(request, response, filterChain);
        }
    }

    protected void coreDoFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Optional<String> financialsAuthToken = getFinancialsAuthToken(request);
        if (!financialsAuthToken.isPresent()) {
            Optional<String> coreToken = getCoreAuthToken(request);
            if (!coreToken.isPresent()) {
                throw new RuntimeException("Unable to access core token");
            }

            Cookie financialsAuthCookie = new Cookie(FIN_AUTH_TOKEN_COOKIE_NAME, coreToken.get());
            financialsAuthCookie.setSecure(request.isSecure());
            response.addCookie(financialsAuthCookie);
        }

        filterChain.doFilter(request, response);
    }

    protected void nonCoreDoFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = "";

        Optional<String> financialsAuthToken = getFinancialsAuthToken(request);
        if (financialsAuthToken.isPresent()) {
            try {
                // Decode it to make sure it hasn't expired and is our jwt
                jwtService.decodeJwt(financialsAuthToken.get());

                // It's valid so nothing needs to be done
                filterChain.doFilter(request, response);
                return;
            } catch (RuntimeException e) {
                JwtData jwtData = new JwtData(request.getRemoteUser(), this.getExpirationSeconds());
                token = getJwtService().generateJwt(jwtData);
            }
        } else {
            JwtData jwtData = new JwtData(request.getRemoteUser(), this.getExpirationSeconds());
            token = getJwtService().generateJwt(jwtData);
        }

        Cookie financialsAuthCookie = new Cookie(FIN_AUTH_TOKEN_COOKIE_NAME, token);
        financialsAuthCookie.setSecure(request.isSecure());
        response.addCookie(financialsAuthCookie);

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    protected int getExpirationSeconds() {
        String value = getConfigurationService().getPropertyValueAsString(JWT_EXPIRATION_SECONDS);
        if (value == null) {
            LOG.error("getExpirationSeconds() Missing configuration property: " + JWT_EXPIRATION_SECONDS);
            throw new RuntimeException("Missing configuration property: " + JWT_EXPIRATION_SECONDS);
        }

        try {
            Integer i = new Integer(value);
            return i.intValue();
        } catch (NumberFormatException e) {
            LOG.error("getExpirationSeconds() Invalid configuration property - must be number: " + JWT_EXPIRATION_SECONDS, e);
            throw new RuntimeException("Invalid configuration property: " + JWT_EXPIRATION_SECONDS);
        }
    }

    protected Optional<String> getFinancialsAuthToken(HttpServletRequest httpRequest) {
        return getCookie(httpRequest, FIN_AUTH_TOKEN_COOKIE_NAME);
    }

    protected Optional<String> getCoreAuthToken(HttpServletRequest httpRequest) {
        return getCookie(httpRequest, AUTH_TOKEN_COOKIE_NAME);
    }

    protected Optional<String> getCookie(HttpServletRequest httpRequest, String name) {
        Cookie[] cookies = httpRequest.getCookies();
        return cookies != null ? Arrays.asList(cookies).stream()
            .filter(cookie -> cookie.getName().equals(name))
            .findFirst()
            .map(cookie -> cookie.getValue()) : Optional.empty();
    }

    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

    protected JwtService getJwtService() {
        if (jwtService == null) {
            jwtService = SpringContext.getBean(JwtService.class);
        }
        return jwtService;
    }

    protected CoreApiKeyAuthenticationService getCoreApiKeyAuthenticationService() {
        if (coreApiKeyAuthenticationService == null) {
            coreApiKeyAuthenticationService = SpringContext.getBean(CoreApiKeyAuthenticationService.class);
        }
        return coreApiKeyAuthenticationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void setCoreApiKeyAuthenticationService(CoreApiKeyAuthenticationService coreApiKeyAuthenticationService) {
        this.coreApiKeyAuthenticationService = coreApiKeyAuthenticationService;
    }
}
