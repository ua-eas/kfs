package org.kuali.kfs.sys.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.businessobject.CoreAuthUser;

import java.util.Optional;

public class CoreApiKeyAuthenticationServiceImplTest {
    private CoreApiKeyAuthenticationServiceImpl coreApiKeyAuthenticationService;
    private Optional<String> coreAuthBaseUrl;
    private Optional<CoreAuthUser> user;

    @Before
    public void setUp() throws Exception {
        coreAuthBaseUrl = Optional.of("url");
        user = Optional.empty();

        coreApiKeyAuthenticationService = new CoreApiKeyAuthenticationServiceImpl() {
            @Override
            protected void initializeUrl() {
                this.coreAuthBaseUrl = CoreApiKeyAuthenticationServiceImplTest.this.coreAuthBaseUrl;
            }

            @Override
            protected Optional<CoreAuthUser> getUserFromCore(String apiKey) {
                return user;
            }
        };
    }

    @Test
    public void testNotUseCore() {
        coreAuthBaseUrl = Optional.empty();

        Assert.assertFalse(coreApiKeyAuthenticationService.useCore());
    }

    @Test
    public void testUseCore() {
        Assert.assertTrue(coreApiKeyAuthenticationService.useCore());
    }

    @Test
    public void testBadKey() {
        Optional<String> principalId = coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("BAD");

        Assert.assertFalse(principalId.isPresent());
    }

    @Test
    public void testGoodKey() {
        CoreAuthUser coreUser = new CoreAuthUser();
        coreUser.setUsername("username");
        user = Optional.of(coreUser);

        Optional<String> principalId = coreApiKeyAuthenticationService.getPrincipalIdFromApiKey("GOOD");

        Assert.assertEquals("username",principalId.get());
    }
}