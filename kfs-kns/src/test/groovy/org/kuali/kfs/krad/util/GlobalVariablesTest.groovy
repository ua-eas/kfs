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
package org.kuali.kfs.krad.util

import org.junit.Test
import org.junit.After
import org.kuali.kfs.krad.util.GlobalVariables
import org.springframework.test.AssertThrows
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals

class GlobalVariablesTest {
    @After
    void reset() {
        GlobalVariables.reset()
    }

    @Test
    void testInitialValue() {
        assertEmptyGlobalVariables()
    }

    @Test(expected=NoSuchElementException)
    void testUnderflow() {
        GlobalVariables.popGlobalVariables()
        GlobalVariables.popGlobalVariables()
    }

    @Test
    void testStack() {
        assertEmptyGlobalVariables()

        GlobalVariables.pushGlobalVariables()
        assertEmptyGlobalVariables()
        setGlobalVariables("a")
        assertGlobalVariables("a")

        GlobalVariables.pushGlobalVariables()
        assertEmptyGlobalVariables()
        setGlobalVariables("b")
        assertGlobalVariables("b")

        GlobalVariables.pushGlobalVariables()
        assertEmptyGlobalVariables()
        setGlobalVariables("c")
        assertGlobalVariables("c")

        GlobalVariables.popGlobalVariables()
        assertGlobalVariables("b")

        GlobalVariables.popGlobalVariables()
        assertGlobalVariables("a")

        new AssertThrows(NoSuchElementException.class) {
            public void test() {
                GlobalVariables.popGlobalVariables()
            }
        };
    }

    @Test(expected=RuntimeException)
    void testHideSessionFromTests() {
        GlobalVariables.hideSessionFromTestsMessage = "test"
        GlobalVariables.userSession
    }

    private static void assertEmptyGlobalVariables(seed = null) {
        assertNull(GlobalVariables.userSession)
        assertTrue(GlobalVariables.messageMap.hasNoMessages())
        assertNull(GlobalVariables.getRequestCache(seed))
    }

    private static void setGlobalVariables(String seed) {
        GlobalVariables.messageMap.putError(seed, seed)
        GlobalVariables.setRequestCache(seed, seed)
    }

    private static void assertGlobalVariables(String seed) {
        assertEquals(seed, GlobalVariables.messageMap.getErrorMessagesForProperty(seed).get(0).errorKey)
        assertEquals(seed, GlobalVariables.getRequestCache(seed))
    }
}
