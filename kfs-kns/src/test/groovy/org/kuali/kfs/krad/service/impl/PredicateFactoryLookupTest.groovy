/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.service.impl

import org.junit.Test
import org.kuali.rice.core.api.criteria.AndPredicate
import org.kuali.rice.core.api.criteria.EqualIgnoreCasePredicate
import org.kuali.rice.core.api.criteria.EqualPredicate
import org.kuali.rice.core.api.criteria.NullPredicate
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.kuali.kfs.krad.service.impl.PredicateFactoryLookup.createPredicate
import static org.kuali.kfs.krad.service.impl.PredicateFactoryLookup.getFlagsStr

class PredicateFactoryLookupTest {

    @Test void test_getFlagsStr() {
        //valid
        assertEquals "(?i)", getFlagsStr("(?i)")
        assertEquals "(?im)", getFlagsStr("(?im)")
        assertEquals "(?im)", getFlagsStr("(?im)foo")
        assertEquals "(?f)", getFlagsStr("(?f)(?i)")

        //invalid
        assertEquals "", getFlagsStr("(?)")
        assertEquals "", getFlagsStr("foo(?i)")
    }

    @Test void test_createPredicate_null_value() {
        assertTrue createPredicate(Object.class, "foo", null) instanceof NullPredicate
    }

    @Test void test_createPredicate_string_simple() {
        assertTrue createPredicate(Object.class, "foo", "bar") instanceof EqualPredicate
    }

    @Test void test_createPredicate_string_simple_ignore_case() {
        assertTrue createPredicate(Object.class, "foo", "(?i)bar") instanceof EqualIgnoreCasePredicate
        assertTrue createPredicate(Object.class, "foo", "(?mi)bar") instanceof EqualIgnoreCasePredicate
    }

    @Test void test_createPredicate_collection_string_simple_single() {
        //optimization - will not return an "and" for single item collections
        assertTrue createPredicate(Object.class, "foo", ["bar"]) instanceof EqualPredicate
    }

    @Test void test_createPredicate_collection_string_simple_multi() {
        //this will create a query that will yield zero results but is still a literal translation
        //of what is requested
        def p = createPredicate(Object.class, "foo", ["bar", "baz"])
        assertTrue p instanceof AndPredicate
        def i = p.getPredicates().asList();
        assertEquals i.size(), 2
        assertTrue i[0] instanceof EqualPredicate
        assertTrue i[1] instanceof EqualPredicate
    }


}
