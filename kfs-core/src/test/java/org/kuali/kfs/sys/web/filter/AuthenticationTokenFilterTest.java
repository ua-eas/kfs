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

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.businessobject.JwtData;
import org.kuali.kfs.sys.service.CoreApiKeyAuthenticationService;
import org.kuali.kfs.sys.service.JwtService;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationTokenFilterTest {
    private AuthenticationTokenFilter authenticationTokenFilter;
    private ConfigurationService configurationService;
    private JwtService jwtService;
    private CoreApiKeyAuthenticationService coreApiKeyAuthenticationService;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private FilterChain filterChain;

    @Before
    public void setUp() {
        authenticationTokenFilter = new AuthenticationTokenFilter();
        coreApiKeyAuthenticationService = EasyMock.createMock(CoreApiKeyAuthenticationService.class);
        configurationService = EasyMock.createMock(ConfigurationService.class);
        jwtService = EasyMock.createMock(JwtService.class);
        httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
        httpServletResponse = EasyMock.createMock(HttpServletResponse.class);
        filterChain = EasyMock.createMock(FilterChain.class);

        authenticationTokenFilter.setJwtService(jwtService);
        authenticationTokenFilter.setConfigurationService(configurationService);
        authenticationTokenFilter.setCoreApiKeyAuthenticationService(coreApiKeyAuthenticationService);
    }

    private void replayAll() {
        EasyMock.replay(configurationService, jwtService, httpServletRequest, httpServletResponse, filterChain, coreApiKeyAuthenticationService);
    }

    private void verifyAll() {
        EasyMock.verify(configurationService, jwtService, httpServletRequest, httpServletResponse, filterChain, coreApiKeyAuthenticationService);
    }

    @Test
    public void testCoreNoCoreCooke() throws IOException, ServletException {
        Cookie[] cookies = new Cookie[0];
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies).times(2);

        replayAll();
        try {
            authenticationTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
            Assert.fail();
        } catch (RuntimeException e) {
            // Expected
        }
        verifyAll();
    }

    @Test
    public void testCoreFinancialsCookieExists() throws IOException, ServletException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(AuthenticationTokenFilter.FIN_AUTH_TOKEN_COOKIE_NAME, "token");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies);
        filterChain.doFilter(httpServletRequest, httpServletResponse);

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testNonCoreFinancialsCookieExists() throws IOException, ServletException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(AuthenticationTokenFilter.FIN_AUTH_TOKEN_COOKIE_NAME, "token");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        EasyMock.expect(jwtService.decodeJwt("token")).andReturn(null);

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testNonCoreInvalidFinancialsCookieExists() throws IOException, ServletException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(AuthenticationTokenFilter.FIN_AUTH_TOKEN_COOKIE_NAME, "token");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        EasyMock.expect(jwtService.decodeJwt("token")).andThrow(new RuntimeException());
        EasyMock.expect(httpServletRequest.getRemoteUser()).andReturn("khuntley");
        EasyMock.expect(configurationService.getPropertyValueAsString(AuthenticationTokenFilter.JWT_EXPIRATION_SECONDS)).andReturn("100");
        EasyMock.expect(jwtService.generateJwt(EasyMock.anyObject(JwtData.class))).andReturn("token");
        EasyMock.expect(httpServletRequest.isSecure()).andReturn(true);
        httpServletResponse.addCookie(EasyMock.anyObject(Cookie.class));

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testNoFinancialsCookieCoreCookieExists() throws IOException, ServletException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(AuthenticationTokenFilter.AUTH_TOKEN_COOKIE_NAME, "token");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies).times(2);
        EasyMock.expect(httpServletRequest.isSecure()).andReturn(true);
        httpServletResponse.addCookie(EasyMock.anyObject(Cookie.class));
        filterChain.doFilter(httpServletRequest, httpServletResponse);

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testNoFinancialsCookieNoCoreCookie() throws IOException, ServletException {
        Cookie[] cookies = new Cookie[0];
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies);
        EasyMock.expect(httpServletRequest.getRemoteUser()).andReturn("khuntley");
        EasyMock.expect(configurationService.getPropertyValueAsString(AuthenticationTokenFilter.JWT_EXPIRATION_SECONDS)).andReturn("100");
        EasyMock.expect(jwtService.generateJwt(EasyMock.anyObject(JwtData.class))).andReturn("token");
        EasyMock.expect(httpServletRequest.isSecure()).andReturn(true);
        httpServletResponse.addCookie(EasyMock.anyObject(Cookie.class));
        filterChain.doFilter(httpServletRequest, httpServletResponse);

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        verifyAll();
    }

    @Test
    public void testMissingJwtExpirationSeconds() throws IOException, ServletException {
        Cookie[] cookies = new Cookie[0];
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies);
        EasyMock.expect(httpServletRequest.getRemoteUser()).andReturn("khuntley");
        EasyMock.expect(configurationService.getPropertyValueAsString(AuthenticationTokenFilter.JWT_EXPIRATION_SECONDS)).andReturn(null);

        replayAll();
        try {
            authenticationTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
            Assert.fail();
        } catch (RuntimeException e) {
            // Expected
        }
        verifyAll();
    }

    @Test
    public void testInvalidJwtExpirationSeconds() throws IOException, ServletException {
        Cookie[] cookies = new Cookie[0];
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies);
        EasyMock.expect(httpServletRequest.getRemoteUser()).andReturn("khuntley");
        EasyMock.expect(configurationService.getPropertyValueAsString(AuthenticationTokenFilter.JWT_EXPIRATION_SECONDS)).andReturn("Not A Number");

        replayAll();
        try {
            authenticationTokenFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
            Assert.fail();
        } catch (RuntimeException e) {
            // Expected
        }
        verifyAll();
    }
}
