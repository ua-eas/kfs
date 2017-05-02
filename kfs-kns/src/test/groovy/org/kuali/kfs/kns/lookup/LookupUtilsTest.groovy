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
package org.kuali.kfs.kns.lookup

import org.junit.Test
import org.kuali.kfs.krad.util.KRADConstants
import org.kuali.rice.kew.api.KewApiConstants

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse

class LookupUtilsTest {
    @Test
    void testLookupUtilsPreprocessesStandardRangeFields() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("afield", "avalue");
        fields.put(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + "arangefield", "a");
        fields.put("arangefield", "b");
        Map<String, String> processFields = LookupUtils.preProcessRangeFields(fields);
        assertEquals("avalue", fields.get("afield"));
        assertEquals("a..b", fields.get("arangefield"));
    }

    // KULRICE-5630 base lookup preprocessing does NOT generate expressions for searchable attributes
    // we leave this to a second pass by the DocumentSearchCriteriaTranslator
    @Test
    void testLookupUtilsDoesNotPreprocessDocumentSearchAttributes() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("afield", "avalue");
        fields.put(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + "arangefield", "a");
        fields.put(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + "arangefield", "b");
        Map<String, String> processFields = LookupUtils.preProcessRangeFields(fields);
        assertEquals("avalue", fields.get("afield"));

        assertFalse("a..b".equals(fields.get(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + "arangefield")));
    }
}
