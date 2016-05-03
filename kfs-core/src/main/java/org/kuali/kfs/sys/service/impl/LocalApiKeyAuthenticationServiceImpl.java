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

import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.sys.businessobject.ApiKey;
import org.kuali.kfs.sys.service.ApiKeyAuthenticationService;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LocalApiKeyAuthenticationServiceImpl implements ApiKeyAuthenticationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LocalApiKeyAuthenticationServiceImpl.class);

    protected static final int KEY_LENGTH = 128;

    private BusinessObjectService businessObjectService;
    protected int iterations;

    @Override
    public Optional<String> getPrincipalIdFromApiKey(String apiKey) {
        LOG.debug("getPrincipalIdFromApiKey() started");

        Map<String,Object> where = new HashMap<>();
        where.put("key",apiKey);
        where.put("active",true);
        Collection<ApiKey> keys = businessObjectService.findMatching(ApiKey.class, where);

        return keys.stream().findFirst().map(ak -> ak.getPrincipalId());
    }

    protected String generateKey(final int keyLen) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keyLen);
            SecretKey secretKey = keyGen.generateKey();
            byte[] encoded = secretKey.getEncoded();
            return DatatypeConverter.printHexBinary(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        LocalApiKeyAuthenticationServiceImpl apiKeyService = new LocalApiKeyAuthenticationServiceImpl();
        apiKeyService.setIterations(10);

        System.out.println("APIKey: " + apiKeyService.generateKey(KEY_LENGTH));
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
