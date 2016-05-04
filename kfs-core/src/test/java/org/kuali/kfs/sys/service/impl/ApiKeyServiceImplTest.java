package org.kuali.kfs.sys.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.service.ApiKeyAuthenticationService;
import org.kuali.kfs.sys.service.CoreApiKeyAuthenticationService;

import java.util.Optional;

public class ApiKeyServiceImplTest {
    protected CoreApiKeyAuthenticationService coreApiKeyAuthenticationService;
    protected ApiKeyAuthenticationService localApiKeyAuthenticationService;
    protected ApiKeyServiceImpl apiKeyService;
    @Before
    public void setUp() throws Exception {
        coreApiKeyAuthenticationService = EasyMock.createMock(CoreApiKeyAuthenticationService.class);
        localApiKeyAuthenticationService = EasyMock.createMock(ApiKeyAuthenticationService.class);

        apiKeyService = new ApiKeyServiceImpl();
        apiKeyService.setCoreApiKeyAuthenticationService(coreApiKeyAuthenticationService);
        apiKeyService.setLocalApiKeyAuthenticationService(localApiKeyAuthenticationService);
    }

    @Test
    public void testCoreAuthenticated() {
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("KEY")).andReturn(Optional.of("username"));

        replayAll();

        Assert.assertEquals("username",apiKeyService.getPrincipalIdFromApiKey("KEY").get());
    }

    @Test
    public void testCoreNotAuthenticated() {
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(true);
        EasyMock.expect(coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("KEY")).andReturn(Optional.empty());

        replayAll();

        Assert.assertFalse(apiKeyService.getPrincipalIdFromApiKey("KEY").isPresent());
    }

    @Test
    public void testLocalAuthenticated() {
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(localApiKeyAuthenticationService.getPrincipalIdFromApiKey("KEY")).andReturn(Optional.of("username"));

        replayAll();

        Assert.assertEquals("username",apiKeyService.getPrincipalIdFromApiKey("KEY").get());
    }

    @Test
    public void testLocalNotAuthenticated() {
        EasyMock.expect(coreApiKeyAuthenticationService.useCore()).andReturn(false);
        EasyMock.expect(localApiKeyAuthenticationService.getPrincipalIdFromApiKey("KEY")).andReturn(Optional.empty());

        replayAll();

        Assert.assertFalse(apiKeyService.getPrincipalIdFromApiKey("KEY").isPresent());
    }

    private void replayAll() {
        EasyMock.replay(coreApiKeyAuthenticationService);
        EasyMock.replay(localApiKeyAuthenticationService);
    }
}