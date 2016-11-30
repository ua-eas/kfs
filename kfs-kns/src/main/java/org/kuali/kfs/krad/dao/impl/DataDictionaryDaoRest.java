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

package org.kuali.kfs.krad.dao.impl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.kuali.kfs.krad.dao.DataDictionaryDao;
import org.kuali.kfs.krad.datadictionary.ApiNamesGenerator;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class DataDictionaryDaoRest implements DataDictionaryDao {

    private String configurationBaseUrl;
    private String authorizationToken;

    @Override
    public Map<String, Object> retrieveBusinessObjectEntry(String className) {

        Client client = Client.create(new DefaultClientConfig());
        ApiNamesGenerator apiNamesGenerator = new ApiNamesGenerator();
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String moduleName = apiNamesGenerator.generateUrlModuleNameForClass(clazz);
        String businessObjectReferenceName = apiNamesGenerator.convertBusinessObjectClassNameToUrlBoName(clazz.getSimpleName());

        WebResource resource = client.resource(getConfigurationBaseUrl());

        return resource.path("api/v1/metadata/business-objects/" + moduleName +"/"+ businessObjectReferenceName)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .header("Authorization", "bearer "+authorizationToken)
                .get(HashMap.class);


    }

    @Override
    public Map<String, Object> retrieveDocumentEntryByClassname(String className) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> retrieveDocumentEntryByType(String type) {
        Client client = Client.create(new DefaultClientConfig());

        WebResource resource = client.resource(getConfigurationBaseUrl());

        return resource.path("api/v1/metadata/documents/document-type/"+ type)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .header("Authorization", "bearer "+authorizationToken)
                .get(HashMap.class);

    }

    @Override
    public void saveBusinessObjectEntry(Map<String, Object> businessObjectEntry) {
        Client client = Client.create(new DefaultClientConfig());

        String moduleName = (String)businessObjectEntry.get("module");
        String businessObjectReferenceName = (String)businessObjectEntry.get("businessObjectReferenceName");

        WebResource resource = client.resource(getConfigurationBaseUrl());

        ClientResponse response = resource.path("api/v1/metadata/business_objects/" + moduleName +"/"+ businessObjectReferenceName)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .header("Authorization", "bearer "+authorizationToken)
                .entity(businessObjectEntry, MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);

    }

    @Override
    public void saveDocumentEntry(Map<String, Object> documentEntry) {
        Client client = Client.create(new DefaultClientConfig());

        String moduleName = (String)documentEntry.get("module");
        String documentReferenceName = (String)documentEntry.get("documentReferenceName");

        WebResource resource = client.resource(getConfigurationBaseUrl());

        ClientResponse response = resource.path("api/v1/metadata/documents/" + moduleName +"/"+ documentReferenceName)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .header("Authorization", "bearer "+authorizationToken)
                .entity(documentEntry, MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);
    }

    public String getConfigurationBaseUrl() {
        return configurationBaseUrl;
    }

    public void setConfigurationBaseUrl(String configurationBaseUrl) {
        this.configurationBaseUrl = configurationBaseUrl;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }
}
