/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.businessobject.JwtData;
import org.kuali.kfs.sys.service.CoreApiKeyAuthenticationService;
import org.kuali.kfs.sys.service.JwtService;
import org.kuali.rice.kim.api.identity.AuthenticationService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Optional;

public class ResourceLoginFilterTest {
    private ResourceLoginFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private PrintWriter writer;
    private FilterChain filterChain;
    private AuthenticationService authenticationService;
    private CoreApiKeyAuthenticationService coreApiKeyAuthenticationService;
    private JwtService jwtService;
    private boolean userSessionEstablished;

    @Before
    public void setUp() throws Exception {
        filter = new ResourceLoginFilter() {
            @Override
            protected JwtService getJwtService() {
                return jwtService;
            }

            @Override
            protected CoreApiKeyAuthenticationService getCoreApiKeyAuthenticationService() {
                return coreApiKeyAuthenticationService;
            }

            @Override
            protected boolean isUserSessionEstablished(HttpServletRequest request) {
                return userSessionEstablished;
            }

            @Override
            protected void establishUserSession(HttpServletRequest request, HttpServletResponse response) {
            }

            @Override
            protected void setUserSession(HttpServletRequest request, String principalName) {
            }

            @Override
            protected AuthenticationService getAuthenticationService() {
                return authenticationService;
            }
        };

        userSessionEstablished = false;
        request = EasyMock.createMock(HttpServletRequest.class);
        response = EasyMock.createMock(HttpServletResponse.class);
        writer = EasyMock.createMock(PrintWriter.class);
        session = EasyMock.createMock(HttpSession.class);
        filterChain = EasyMock.createMock(FilterChain.class);
        authenticationService = EasyMock.createMock(AuthenticationService.class);
        coreApiKeyAuthenticationService = EasyMock.createMock(CoreApiKeyAuthenticationService.class);
        jwtService = EasyMock.createMock(JwtService.class);
    }

    @Test
    public void testNotInSession() throws Exception {
        userSessionEstablished = false;
        EasyMock.expect(request.getHeader("Authorization")).andReturn(null);
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testNotInSessionOrRequest() throws Exception {
        userSessionEstablished = false;
        EasyMock.expect(request.getHeader("Authorization")).andReturn(null);
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }


    @Test
    public void testInSession() throws Exception {
        userSessionEstablished = true;
        EasyMock.expect(request.getHeader("Authorization")).andReturn(null);
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testBlankAuthHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("");
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testNoBearerHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("XXX");
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testOnlyBearerHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer");
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testBlankKeyHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer ");
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testNonCoreBadHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer BAD");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(jwtService.decodeJwt("BAD")).andThrow(new RuntimeException("Error"));
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testNonCoreGoodHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer GOOD");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        JwtData data = new JwtData("user", 1000);
        EasyMock.expect(jwtService.decodeJwt("GOOD")).andReturn(data);
        filterChain.doFilter(request, response);

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testCoreBadHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer BAD");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("BAD")).andReturn(Optional.empty());
        expectErrorResponse();

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    @Test
    public void testCoreGoodHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer GOOD");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("GOOD")).andReturn(Optional.of("user"));
        filterChain.doFilter(request, response);

        replayAll();

        filter.doFilter(request, response, filterChain);

        verifyAll();
    }

    private void replayAll() {
        EasyMock.replay(request, response, writer, session, filterChain, authenticationService, jwtService, coreApiKeyAuthenticationService);
    }

    private void verifyAll() {
        EasyMock.verify(request, response, writer, session, filterChain, authenticationService, jwtService, coreApiKeyAuthenticationService);
    }

    private void expectErrorResponse() throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        EasyMock.expect(response.getWriter()).andReturn(writer);
        writer.println(ResourceLoginFilter.UNAUTHORIZED_JSON);
    }
}
