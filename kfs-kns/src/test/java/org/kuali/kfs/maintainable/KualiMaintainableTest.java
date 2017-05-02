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
package org.kuali.kfs.maintainable;

import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.kns.maintenance.KualiMaintainableImpl;
import org.kuali.kfs.kns.maintenance.Maintainable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test methods for default Kuali maintainable implementation.
 */
public class KualiMaintainableTest {
    Maintainable maintainable = null;


    @Before
    public void setUp() throws Exception {
        maintainable = new KualiMaintainableImpl();
    }

    /**
     * Tests the retrieval of the inactive record display setting when it has not been set (default). Default
     * should be false according to specification.
     */
    @Test
    public void testGetShowInactiveRecords_Default() throws Exception {
        boolean displayInactive = maintainable.getShowInactiveRecords("fooCollection");
        assertTrue("display setting returned true for unset collection", displayInactive);
    }

    /**
     * Tests method throws an exception when given name is null.
     */
    @Test
    public void testGetShowInactiveRecords_NullParam() throws Exception {
        boolean failedAsExpected = false;
        try {
            maintainable.getShowInactiveRecords(null);
        } catch (IllegalArgumentException expected) {
            failedAsExpected = true;
        }

        assertTrue("exception not thrown for null collection name", failedAsExpected);
    }

    /**
     * Tests setting to display inactive records for a collection.
     */
    @Test
    public void testSetShowInactiveRecords_DisplayCollectionInActive() throws Exception {
        maintainable.setShowInactiveRecords("collection1", true);
        assertTrue("state failure on set inactive display to true", maintainable.getShowInactiveRecords("collection1"));
    }

    /**
     * Tests setting to not display inactive records for a collection.
     */
    @Test
    public void testSetShowInactiveRecords_NoDisplayCollectionInActive() throws Exception {
        maintainable.setShowInactiveRecords("collection1", false);
        assertFalse("state failure on set inactive display to false", maintainable.getShowInactiveRecords("collection1"));
    }

    /**
     * Tests setting to display inactive records for a sub-collection.
     */
    @Test
    public void testSetShowInactiveRecords_DisplaySubCollectionInActive() throws Exception {
        maintainable.setShowInactiveRecords("collection1.subCollection", true);
        assertTrue("state failure on set inactive display to true", maintainable.getShowInactiveRecords("collection1.subCollection"));
    }

    /**
     * Tests setting to not display inactive records for a sub-collection.
     */
    @Test
    public void testSetShowInactiveRecords_NoDisplaySubCollectionInActive() throws Exception {
        maintainable.setShowInactiveRecords("collection1.subCollection", false);
        assertFalse("state failure on set inactive display to false", maintainable.getShowInactiveRecords("collection1.subCollection"));
    }

    /**
     * Tests method throws an exception when given name is null.
     */
    @Test
    public void testSetShowInactiveRecords_NullParam() throws Exception {
        boolean failedAsExpected = false;
        try {
            maintainable.setShowInactiveRecords(null, true);
        } catch (IllegalArgumentException expected) {
            failedAsExpected = true;
        }

        assertTrue("exception not thrown for null collection name", failedAsExpected);
    }
}
