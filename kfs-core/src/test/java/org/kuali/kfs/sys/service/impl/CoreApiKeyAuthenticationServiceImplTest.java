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
package org.kuali.kfs.sys.service.impl;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.businessobject.CoreAuthUser;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CoreApiKeyAuthenticationServiceImplTest {
    private CoreApiKeyAuthenticationServiceImpl coreApiKeyAuthenticationService;
    private Optional<String> coreAuthBaseUrl;
    private Optional<CoreAuthUser> user;

    private ClientResponse clientResponse = null;

    @Before
    public void setUp() throws Exception {
        coreAuthBaseUrl = Optional.of("url");
        user = Optional.empty();

        clientResponse = EasyMock.createMock(ClientResponse.class);

        coreApiKeyAuthenticationService = new CoreApiKeyAuthenticationServiceImpl() {
            @Override
            protected void initializeUrl() {
                this.coreAuthBaseUrl = CoreApiKeyAuthenticationServiceImplTest.this.coreAuthBaseUrl;
            }

            @Override
            protected ClientResponse invokeWebResource(String authTokenValue) {
                return clientResponse;
            }
        };
    }

    public void replayAll() {
        EasyMock.replay(clientResponse);
    }

    @Test
    public void testNotUseCore() {
        coreAuthBaseUrl = Optional.empty();

        assertFalse(coreApiKeyAuthenticationService.useCore());
    }

    @Test
    public void testUseCore() {
        assertTrue(coreApiKeyAuthenticationService.useCore());
    }

    @Test
    public void testBadKey() {
        CoreAuthUser coreUser = new CoreAuthUser();
        coreUser.setUsername("username");
        user = Optional.of(coreUser);

        EasyMock.expect(clientResponse.getStatus()).andReturn(500).times(2);
        EasyMock.expect(clientResponse.getEntity(CoreAuthUser.class)).andReturn(user.get());

        replayAll();

        Optional<String> principalId = coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("BAD");

        assertFalse(principalId.isPresent());
    }

    @Test
    public void testGoodKey() {
        CoreAuthUser coreUser = new CoreAuthUser();
        coreUser.setUsername("username");
        user = Optional.of(coreUser);

        EasyMock.expect(clientResponse.getStatus()).andReturn(200);
        EasyMock.expect(clientResponse.getEntity(CoreAuthUser.class)).andReturn(user.get());

        replayAll();

        Optional<String> principalId = coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("GOOD");

        assertEquals("username",principalId.get());
    }

    @Test
    public void testGetAuthUserFromToken_200Response() {
        String token = "mytokenvalue";
        CoreAuthUser coreUser = new CoreAuthUser();
        coreUser.setAuthToken(token);
        coreUser.setUsername("joeuser");
        user = Optional.of(coreUser);

        EasyMock.expect(clientResponse.getStatus()).andReturn(200);
        EasyMock.expect(clientResponse.getEntity(CoreAuthUser.class)).andReturn(user.get());

        replayAll();

        Optional<CoreAuthUser> optionalAuthUser = coreApiKeyAuthenticationService.getUserFromCore(token);
        assertTrue(optionalAuthUser.isPresent());
        assertEquals(user.get(), optionalAuthUser.get());
    }

    @Test
    public void testGetAuthUserFromToken_500Response() {
        String token = "mytokenvalue";

        EasyMock.expect(clientResponse.getStatus()).andReturn(500).times(2);

        replayAll();

        Optional<CoreAuthUser> optionalAuthUser = coreApiKeyAuthenticationService.getUserFromCore(token);
        assertFalse(optionalAuthUser.isPresent());
        assertEquals(Optional.empty(), optionalAuthUser);
    }

    @Test
    public void testGetAuthUserFromToken_ClientHandlerException() {
        String token = "mytokenvalue";

        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        headers.putSingle("Content-Type", "text/html");

        EasyMock.expect(clientResponse.getStatus()).andReturn(200);
        EasyMock.expect(clientResponse.getEntity(CoreAuthUser.class)).andThrow(new ClientHandlerException());
        EasyMock.expect(clientResponse.getHeaders()).andReturn(headers);
        EasyMock.expect(clientResponse.getEntity(String.class)).andReturn("<html><head><title>Bad Things</title></head><body>Bad things have happened!</body></html>");

        replayAll();

        Optional<CoreAuthUser> optionalAuthUser = null;
        try {
            optionalAuthUser = coreApiKeyAuthenticationService.getUserFromCore(token);
        } catch (ClientHandlerException e) {
            fail("ClientHandlerException should have been caught");
        }

        assertFalse(optionalAuthUser.isPresent());
        assertEquals(Optional.empty(), optionalAuthUser);
    }
}
