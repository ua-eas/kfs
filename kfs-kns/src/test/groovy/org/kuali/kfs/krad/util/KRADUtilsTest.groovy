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
package org.kuali.kfs.krad.util

import org.junit.Test
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult

import static org.junit.Assert.assertTrue

public class KRADUtilsTest {

    @Test
    public void testHydrateAttributeValue() throws Exception {

        // KRADUtils.hydrateAttributeValue(Class, String) attempts to coerce the given string to the type of the class.
        // it has a few simple tricks it attempts to do this, otherwise it just returns null.

        Object result = KRADUtils.hydrateAttributeValue(Boolean.class, "Yes");
        assertTrue(Boolean.TRUE.equals(result));

        result = KRADUtils.hydrateAttributeValue(Boolean.TYPE, "Yes");
        assertTrue(Boolean.TRUE.equals(result));

        result = KRADUtils.hydrateAttributeValue(String.class, "Yes");
        assertTrue("Yes".equals(result));

        result = KRADUtils.hydrateAttributeValue(Double.class, "1.2");
        assertTrue(Double.valueOf("1.2").equals(result));

        // Can't possibly turn "Yes" into a DictionaryValidationResult
        result = KRADUtils.hydrateAttributeValue(DictionaryValidationResult.class, "Yes");
        assertTrue(result == null);

        // Can't possibly turn null String into anything but null
        result = KRADUtils.hydrateAttributeValue(String.class, null);
        assertTrue(result == null);

        // null class results in null coming back
        result = KRADUtils.hydrateAttributeValue(null, "Yes");
        assertTrue(result == null);
    }

}
