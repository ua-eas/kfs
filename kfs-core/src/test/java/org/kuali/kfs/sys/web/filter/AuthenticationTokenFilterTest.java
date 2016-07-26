package org.kuali.kfs.sys.web.filter;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.businessobject.JwtData;
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
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private FilterChain filterChain;

    @Before
    public void setUp() {
        authenticationTokenFilter = new AuthenticationTokenFilter();
        configurationService = EasyMock.createMock(ConfigurationService.class);
        jwtService = EasyMock.createMock(JwtService.class);
        httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
        httpServletResponse = EasyMock.createMock(HttpServletResponse.class);
        filterChain = EasyMock.createMock(FilterChain.class);

        authenticationTokenFilter.setJwtService(jwtService);
        authenticationTokenFilter.setConfigurationService(configurationService);
    }

    private void replayAll() {
        EasyMock.replay(configurationService,jwtService,httpServletRequest,httpServletResponse,filterChain);
    }

    @Test
    public void testFinancialsCookieExists() throws IOException,ServletException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(AuthenticationTokenFilter.FIN_AUTH_TOKEN_COOKIE_NAME,"token");
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies);
        filterChain.doFilter(httpServletRequest,httpServletResponse);
        EasyMock.expect(jwtService.decodeJwt("token")).andReturn(null);

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest,httpServletResponse,filterChain);
    }

    @Test
    public void testInvalidFinancialsCookieExists() throws IOException,ServletException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(AuthenticationTokenFilter.FIN_AUTH_TOKEN_COOKIE_NAME,"token");
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies);
        filterChain.doFilter(httpServletRequest,httpServletResponse);
        EasyMock.expect(jwtService.decodeJwt("token")).andThrow(new RuntimeException());
        EasyMock.expect(httpServletRequest.getRemoteUser()).andReturn("khuntley");
        EasyMock.expect(configurationService.getPropertyValueAsString(AuthenticationTokenFilter.JWT_EXPIRATION_SECONDS)).andReturn("100");
        EasyMock.expect(jwtService.generateJwt(EasyMock.anyObject(JwtData.class))).andReturn("token");
        EasyMock.expect(httpServletRequest.isSecure()).andReturn(true);
        httpServletResponse.addCookie(EasyMock.anyObject(Cookie.class));

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest,httpServletResponse,filterChain);
    }

    @Test
    public void testNoFinancialsCookieCoreCookieExists() throws IOException,ServletException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie(AuthenticationTokenFilter.AUTH_TOKEN_COOKIE_NAME,"token");
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies).times(2);
        EasyMock.expect(httpServletRequest.isSecure()).andReturn(true);
        httpServletResponse.addCookie(EasyMock.anyObject(Cookie.class));
        filterChain.doFilter(httpServletRequest,httpServletResponse);

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest,httpServletResponse,filterChain);
    }

    @Test
    public void testNoFinancialsCookieNoCoreCookie() throws IOException,ServletException {
        Cookie[] cookies = new Cookie[0];
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies).times(2);
        EasyMock.expect(httpServletRequest.getRemoteUser()).andReturn("khuntley");
        EasyMock.expect(configurationService.getPropertyValueAsString(AuthenticationTokenFilter.JWT_EXPIRATION_SECONDS)).andReturn("100");
        EasyMock.expect(jwtService.generateJwt(EasyMock.anyObject(JwtData.class))).andReturn("token");
        EasyMock.expect(httpServletRequest.isSecure()).andReturn(true);
        httpServletResponse.addCookie(EasyMock.anyObject(Cookie.class));
        filterChain.doFilter(httpServletRequest,httpServletResponse);

        replayAll();
        authenticationTokenFilter.doFilter(httpServletRequest,httpServletResponse,filterChain);
    }

    @Test
    public void testMissingJwtExpirationSeconds() throws IOException,ServletException {
        Cookie[] cookies = new Cookie[0];
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies).times(2);
        EasyMock.expect(httpServletRequest.getRemoteUser()).andReturn("khuntley");
        EasyMock.expect(configurationService.getPropertyValueAsString(AuthenticationTokenFilter.JWT_EXPIRATION_SECONDS)).andReturn(null);

        replayAll();
        try {
            authenticationTokenFilter.doFilter(httpServletRequest,httpServletResponse,filterChain);
            Assert.fail();
        } catch (RuntimeException e) {
            // Expected
        }
    }

    @Test
    public void testInvalidJwtExpirationSeconds() throws IOException,ServletException {
        Cookie[] cookies = new Cookie[0];
        EasyMock.expect(httpServletRequest.getCookies()).andReturn(cookies).times(2);
        EasyMock.expect(httpServletRequest.getRemoteUser()).andReturn("khuntley");
        EasyMock.expect(configurationService.getPropertyValueAsString(AuthenticationTokenFilter.JWT_EXPIRATION_SECONDS)).andReturn("Not A Number");

        replayAll();
        try {
            authenticationTokenFilter.doFilter(httpServletRequest,httpServletResponse,filterChain);
            Assert.fail();
        } catch (RuntimeException e) {
            // Expected
        }
    }
}