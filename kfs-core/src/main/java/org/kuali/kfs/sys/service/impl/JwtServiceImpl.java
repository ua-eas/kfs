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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.kuali.kfs.sys.businessobject.JwtData;
import org.kuali.kfs.sys.service.JwtService;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class JwtServiceImpl implements JwtService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JwtServiceImpl.class);

    private static final String JWT_SIGNING_KEY = "jwt.signing.key";

    protected ConfigurationService configurationService;

    @Override
    public String generateNewKey() {
        LOG.debug("generateNewKey() started");

        try {
            Key key = KeyGenerator.getInstance("AES").generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate key",e);
        }
    }

    @Override
    public String generateJwt(JwtData data) {
        LOG.debug("generateJwt() started");

        String key = configurationService.getPropertyValueAsString(JWT_SIGNING_KEY);
        if ( key == null ) {
            throw new RuntimeException("Missing configuration property: " + JWT_SIGNING_KEY);
        }

        return generateJwt(data,key);
    }

    @Override
    public String generateJwt(JwtData data, String stringKey) {
        LOG.debug("generateJwt() started");

        Key key = decodeKey(stringKey);

        return Jwts.builder()
                .setSubject(data.getPrincipalName())
                .setIssuedAt(data.getIssuedAt())
                .setExpiration(data.getExpired())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    @Override
    public JwtData decodeJwt(String jwt) {
        LOG.debug("decodeJwt() started");

        String key = configurationService.getPropertyValueAsString(JWT_SIGNING_KEY);
        if ( key == null ) {
            throw new RuntimeException("Missing configuration property: " + JWT_SIGNING_KEY);
        }

        return decodeJwt(jwt,key);
    }

    @Override
    public JwtData decodeJwt(String jwt,String stringKey) {
        LOG.debug("decodeJwt() started");

        Key key = decodeKey(stringKey);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(jwt).getBody();

            JwtData data = new JwtData();
            data.setPrincipalName(claims.getSubject());
            data.setIssuedAt(claims.getIssuedAt());
            data.setExpired(claims.getExpiration());
            return data;
        } catch (ExpiredJwtException|UnsupportedJwtException|MalformedJwtException| io.jsonwebtoken.SignatureException|IllegalArgumentException e) {
            // Not a system problem, so not logged at error level
            LOG.debug("decodeJwt() Invalid JWT",e);
            throw new RuntimeException("Invalid JWT");
        }
    }

    private Key decodeKey(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
