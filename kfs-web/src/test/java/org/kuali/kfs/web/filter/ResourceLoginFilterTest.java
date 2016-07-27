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
        EasyMock.expect(authenticationService.getPrincipalName(request)).andReturn("username");
        filterChain.doFilter(request,response);

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testNotInSessionOrRequest() throws Exception {
        userSessionEstablished = false;
        EasyMock.expect(request.getHeader("Authorization")).andReturn(null);
        EasyMock.expect(authenticationService.getPrincipalName(request)).andReturn(null);
        expectErrorResponse();

        replayAll();

        filter.doFilter(request,response,filterChain);
    }


    @Test
    public void testInSession() throws Exception {
        userSessionEstablished = true;
        EasyMock.expect(request.getHeader("Authorization")).andReturn(null);
        filterChain.doFilter(request,response);

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testBlankAuthHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("");
        expectErrorResponse();

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testNoBearerHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("XXX");
        expectErrorResponse();

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testOnlyBearerHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer");
        expectErrorResponse();

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testBlankKeyHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer ");
        expectErrorResponse();

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testNonCoreBadHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer BAD");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(jwtService.decodeJwt("BAD")).andThrow(new RuntimeException("Error"));
        expectErrorResponse();

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testNonCoreGoodHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer GOOD");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        JwtData data = new JwtData("user",1000);
        EasyMock.expect(jwtService.decodeJwt("GOOD")).andReturn(data);
        EasyMock.expect(request.getSession()).andReturn(session);
        filterChain.doFilter(request,response);

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testCoreBadHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer BAD");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("BAD")).andReturn(Optional.empty());
        expectErrorResponse();

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testCoreGoodHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer GOOD");
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("GOOD")).andReturn(Optional.of("user"));
        EasyMock.expect(request.getSession()).andReturn(session);
        filterChain.doFilter(request,response);

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    private void replayAll() {
        EasyMock.replay(request);
        EasyMock.replay(response);
        EasyMock.replay(writer);
        EasyMock.replay(session);
        EasyMock.replay(filterChain);
        EasyMock.replay(authenticationService);
        EasyMock.replay(jwtService);
        EasyMock.replay(coreApiKeyAuthenticationService);
    }

    private void expectErrorResponse() throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        EasyMock.expect(response.getWriter()).andReturn(writer);
        writer.println(ResourceLoginFilter.UNAUTHORIZED_JSON);
    }
}