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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.businessobject.JwtData;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class JwtServiceImplTest {
    private JwtServiceImpl jwtService;
    private ConfigurationService configurationService;

    private final static String KEY = "deaRQYUun0P9sPyPLfdz+w==";
    private final static String JWT = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJraHVudGxleSIsImlhdCI6MTQ2OTU2MTQxNH0.-xUKQ9z7qmLABUzLBNGDqZlEgLhC5oQXueUfMh9r9zQOCUlNE-5OA9fMA_D86Ff4pIL9v2BANJNDm39w-9E2Qg";

    @Before
    public void setUp() {
        jwtService = new JwtServiceImpl();
        configurationService = EasyMock.createMock(ConfigurationService.class);
        jwtService.setConfigurationService(configurationService);
    }

    @Test
    public void testGenerateJwtWithKey() {
        JwtData data = new JwtData("khuntley", 100);
        String jwt = jwtService.generateJwt(data, KEY);

        // Test it by decoding
        Key key = decodeKey(KEY);

        Claims claims = Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(jwt).getBody();

        Assert.assertEquals("khuntley", claims.getSubject());
    }

    @Test
    public void testGenerateJwt() {
        EasyMock.expect(configurationService.getPropertyValueAsString("jwt.signing.key")).andReturn(KEY);
        EasyMock.replay(configurationService);

        JwtData data = new JwtData("khuntley", 100);
        String jwt = jwtService.generateJwt(data, KEY);

        // Test it by decoding
        Key key = decodeKey(KEY);

        Claims claims = Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(jwt).getBody();

        Assert.assertEquals("khuntley", claims.getSubject());
    }

    @Test
    public void testDecodeJwtWithKey() {
        JwtData data = jwtService.decodeJwt(JWT, KEY);

        Assert.assertEquals("khuntley", data.getPrincipalName());
        Assert.assertNull(data.getExpired());
        Assert.assertNotNull(data.getIssuedAt());
    }

    @Test
    public void testDecideJwt() {
        EasyMock.expect(configurationService.getPropertyValueAsString("jwt.signing.key")).andReturn(KEY);
        EasyMock.replay(configurationService);

        JwtData data = jwtService.decodeJwt(JWT);

        Assert.assertEquals("khuntley", data.getPrincipalName());
        Assert.assertNull(data.getExpired());
        Assert.assertNotNull(data.getIssuedAt());

    }

    private Key decodeKey(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
