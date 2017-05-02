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
package org.kuali.kfs.sys.service.impl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.kuali.kfs.sys.businessobject.CoreAuthUser;
import org.kuali.kfs.sys.service.CoreApiKeyAuthenticationService;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.util.Optional;

public class CoreApiKeyAuthenticationServiceImpl implements CoreApiKeyAuthenticationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CoreApiKeyAuthenticationServiceImpl.class);

    private static final String AUTHORIZATION_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String CONTENT_TYPE = "application/json";

    protected ConfigurationService configurationService;

    protected Optional<String> coreAuthBaseUrl;

    /**
     * Get the principal ID from an API Key.
     *
     * @param apiKey API Key passed from client
     * @return principal ID if valid API Key
     */
    @Override
    public Optional<String> getPrincipalIdFromApiKey(final String apiKey) {
        initializeUrlOrThrow();

        return getUserFromCore(apiKey).map(user -> user.getUsername());
    }

    /**
     * Determine if system is configured to use core
     *
     * @return true if core is configured
     */
    @Override
    public boolean useCore() {
        initializeUrl();

        return coreAuthBaseUrl.isPresent();
    }

    protected Optional<CoreAuthUser> getUserFromCore(final String apiKey) {
        ClientResponse response = invokeWebResource(apiKey);

        if (response.getStatus() != 200) {
            LOG.debug("getUserFromCore() non-OK response from core: " + response.getStatus());
            return Optional.empty();
        }

        try {
            return Optional.of(response.getEntity(CoreAuthUser.class));
        } catch (ClientHandlerException e) {
            // if we receive this exception it means that we got a response back that couldn't be converted into an
            // AuthUser. This could be an html page or similar response, let's log it so we know what happened, and then
            // return an empty Optional to redirect user back to login
            LOG.error("Invalid response from auth API, failed to parse response. "
                + "Content-Type was: " + response.getHeaders().getFirst("Content-Type") + ". "
                + "Content was: " + response.getEntity(String.class), e);
            return Optional.empty();
        }
    }

    protected WebResource getWebResource() {
        String currentGetUserUrl = getCoreAuthBaseUrl() + "/api/v1/users/current";

        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJaxbJsonProvider.class);
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        Client client = Client.create(config);

        return client.resource(currentGetUserUrl);
    }

    protected ClientResponse invokeWebResource(String authTokenValue) {
        return getWebResource().header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_PREFIX + authTokenValue)
            .header("Content-Type", CONTENT_TYPE)
            .get(ClientResponse.class);
    }

    protected String getCoreAuthBaseUrl() {
        initializeUrlOrThrow();

        return coreAuthBaseUrl.get();
    }

    protected void initializeUrlOrThrow() {
        initializeUrl();

        if (!coreAuthBaseUrl.isPresent()) {
            throw new RuntimeException("Core is not enabled");
        }
    }

    protected void initializeUrl() {
        if (coreAuthBaseUrl == null) {
            coreAuthBaseUrl = Optional.ofNullable(configurationService.getPropertyValueAsString("core.authentication.filter.authBaseUrl"));
        }
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
