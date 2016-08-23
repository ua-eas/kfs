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
package org.kuali.kfs.core.framework.persistence.ojb.conversion;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;

import java.security.GeneralSecurityException;


/**
 * This class calls core service to hash values going to the database.
 *
 *
 */

public class OjbKualiHashFieldConversion implements FieldConversion {

    /**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        Object converted = source;
        if (converted != null) {
            // don't convert if already a hashed value
            if (converted.toString().endsWith(EncryptionService.HASH_POST_PREFIX)) {
                converted = StringUtils.stripEnd(converted.toString(), EncryptionService.HASH_POST_PREFIX);
            } else {
                try {
                    converted = CoreApiServiceLocator.getEncryptionService().hash(converted);
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException("Unable to hash value to db: " + e.getMessage());
                }
            }
        }

        return converted;
    }

    /**
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        if (source == null) {
            return "";
        }
        return source.toString() + EncryptionService.HASH_POST_PREFIX;
    }
}
