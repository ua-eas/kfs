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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.JwtData;

public interface JwtService {
    /**
     * Generate a new Jwt signing key
     *
     * @return
     */
    String generateNewKey();

    /**
     * Generate a JWT with a specific signing key
     *
     * @param data
     * @param key
     * @return
     */
    String generateJwt(JwtData data, String key);

    /**
     * Generate a JWT with the application configured signing key
     *
     * @param data
     * @return
     */
    String generateJwt(JwtData data);

    /**
     * Decode a JWT with a specific signing key
     *
     * @param jwt
     * @param key
     * @return
     */
    JwtData decodeJwt(String jwt,String key);

    /**
     * Decode a JWT with the application configured signing key
     *
     * @param jwt
     * @return
     */
    JwtData decodeJwt(String jwt);
}
