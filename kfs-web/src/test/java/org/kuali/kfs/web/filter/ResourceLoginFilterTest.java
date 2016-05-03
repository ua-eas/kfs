package org.kuali.kfs.web.filter;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.service.ApiKeyService;
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
    private ApiKeyService apiKeyService;
    private FilterChain filterChain;
    private AuthenticationService authenticationService;
    private boolean userSessionEstablished;

    @Before
    public void setUp() throws Exception {
        filter = new ResourceLoginFilter() {
            @Override
            protected ApiKeyService getApiKeyService() {
                return apiKeyService;
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
        apiKeyService = EasyMock.createMock(ApiKeyService.class);
        session = EasyMock.createMock(HttpSession.class);
        filterChain = EasyMock.createMock(FilterChain.class);
        authenticationService = EasyMock.createMock(AuthenticationService.class);
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
    public void testBadHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer BAD");
        EasyMock.expect(apiKeyService.getPrincipalIdFromApiKey("BAD")).andReturn(Optional.empty());
        expectErrorResponse();

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    @Test
    public void testGoodHeader() throws Exception {
        EasyMock.expect(request.getHeader("Authorization")).andReturn("Bearer GOOD");
        EasyMock.expect(apiKeyService.getPrincipalIdFromApiKey("GOOD")).andReturn(Optional.of("user"));
        EasyMock.expect(request.getSession()).andReturn(session);
        filterChain.doFilter(request,response);

        replayAll();

        filter.doFilter(request,response,filterChain);
    }

    private void replayAll() {
        EasyMock.replay(request);
        EasyMock.replay(response);
        EasyMock.replay(writer);
        EasyMock.replay(apiKeyService);
        EasyMock.replay(session);
        EasyMock.replay(filterChain);
        EasyMock.replay(authenticationService);
    }

    private void expectErrorResponse() throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        EasyMock.expect(response.getWriter()).andReturn(writer);
        writer.println(ResourceLoginFilter.UNAUTHORIZED_JSON);
    }
}