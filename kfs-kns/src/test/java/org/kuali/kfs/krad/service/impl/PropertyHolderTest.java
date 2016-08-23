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
package org.kuali.kfs.krad.service.impl;

import org.junit.Test;
import org.kuali.kfs.krad.exception.DuplicateKeyException;
import org.kuali.kfs.krad.exception.PropertiesException;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the PropertyHolder methods.
 */
public class PropertyHolderTest {
    private static final String KNOWN_KEY1 = "key1";
    private static final String KNOWN_VALUE1 = "value1";

    private static final String KNOWN_KEY2 = "key 2";
    private static final String KNOWN_VALUE2 = "value 2";

    private static final String KNOWN_KEY3 = "";
    private static final String KNOWN_VALUE3 = "";

    @Test
    public void testIsEmpty_emptyHolder() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        assertTrue(propertyHolder.isEmpty());
    }

    @Test
    public void testIsEmpty_notEmptyHolder() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        assertTrue(!propertyHolder.isEmpty());
    }

    @Test
    public void testContainsKey_invalidKey() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.containsKey(null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testContainsKey_emptyHolder() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        assertFalse(propertyHolder.containsKey(KNOWN_KEY1));
        assertFalse(propertyHolder.containsKey(KNOWN_KEY2));
        assertFalse(propertyHolder.containsKey(KNOWN_KEY3));
    }

    @Test
    public void testContainsKey_notContains() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        assertFalse(propertyHolder.containsKey(KNOWN_KEY1 + "foo"));
        assertFalse(propertyHolder.containsKey(KNOWN_KEY2 + "foo"));
        assertFalse(propertyHolder.containsKey(KNOWN_KEY3 + "foo"));
    }

    @Test
    public void testContainsKey_contains() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        assertTrue(propertyHolder.containsKey(KNOWN_KEY2));
        assertTrue(propertyHolder.containsKey(KNOWN_KEY3));
    }

    @Test
    public void testGetProperty_invalidKey() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.getProperty(null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testGetProperty_emptyHolder() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        assertNull(propertyHolder.getProperty(KNOWN_KEY1));
        assertNull(propertyHolder.getProperty(KNOWN_KEY2));
        assertNull(propertyHolder.getProperty(KNOWN_KEY3));
    }

    @Test
    public void testGetProperty_notContains() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        assertNull(propertyHolder.getProperty(KNOWN_KEY1 + "foo"));
        assertNull(propertyHolder.getProperty(KNOWN_KEY2 + "foo"));
        assertNull(propertyHolder.getProperty(KNOWN_KEY3 + "foo"));
    }

    @Test
    public void testGetProperty_contains() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        String value = propertyHolder.getProperty(KNOWN_KEY1);
        assertEquals(KNOWN_VALUE1, value);
        value = propertyHolder.getProperty(KNOWN_KEY2);
        assertEquals(KNOWN_VALUE2, value);
        value = propertyHolder.getProperty(KNOWN_KEY3);
        assertEquals(KNOWN_VALUE3, value);
    }

    @Test
    public void testSetProperty_invalidKey() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.setProperty(null, KNOWN_VALUE1);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testSetProperty_invalidValue() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.setProperty(KNOWN_KEY1, null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testSetProperty_uniqueKey() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        propertyHolder.setProperty(KNOWN_KEY1, KNOWN_VALUE1);
        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        assertEquals(KNOWN_VALUE1, propertyHolder.getProperty(KNOWN_KEY1));
    }

    @Test
    public void testSetProperty_duplicateKey() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        boolean failedAsExpected = false;
        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        try {
            propertyHolder.setProperty(KNOWN_KEY1, KNOWN_VALUE1);
        } catch (DuplicateKeyException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testClearProperty_invalidKey() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        boolean failedAsExpected = false;
        try {
            propertyHolder.clearProperty(null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testClearProperty_unknownKey() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        propertyHolder.clearProperty(KNOWN_KEY1 + "foo");
        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
    }

    @Test
    public void testClearProperty_knownKey() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        assertTrue(propertyHolder.containsKey(KNOWN_KEY1));
        assertTrue(propertyHolder.containsKey(KNOWN_KEY2));
        propertyHolder.clearProperty(KNOWN_KEY1);
        assertFalse(propertyHolder.containsKey(KNOWN_KEY1));
        assertTrue(propertyHolder.containsKey(KNOWN_KEY2));
    }

    @Test
    public void testClearProperties_empty() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        assertTrue(propertyHolder.isEmpty());
        propertyHolder.clearProperties();
        assertTrue(propertyHolder.isEmpty());
    }

    @Test
    public void testClearProperties_nonEmpty() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        assertFalse(propertyHolder.isEmpty());
        propertyHolder.clearProperties();
        assertTrue(propertyHolder.isEmpty());
    }

    @Test
    public void testGetKeys_empty() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        assertTrue(propertyHolder.isEmpty());
        Iterator i = propertyHolder.getKeys();
        assertFalse(i.hasNext());
    }

    @Test
    public void testGetKeys_nonEmpty() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = buildNonEmpty();

        assertFalse(propertyHolder.isEmpty());
        Iterator i = propertyHolder.getKeys();
        assertTrue(i.hasNext());

        for (; i.hasNext(); ) {
            String key = (String) i.next();
            assertTrue(propertyHolder.containsKey(key));
        }
    }

    @Test
    public void testLoadProperties_nullPropertySource() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();

        boolean failedAsExpected = false;
        try {
            propertyHolder.loadProperties(null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testLoadProperties_invalidPropertySource() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();
        ConfigurationServiceImpl.FilePropertySource fps = new ConfigurationServiceImpl.FilePropertySource();

        boolean failedAsExpected = false;
        try {
            propertyHolder.loadProperties(fps);
        } catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testLoadProperties_unknownPropertySource() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();
        ConfigurationServiceImpl.FilePropertySource fps = new ConfigurationServiceImpl.FilePropertySource();
        fps.setFileName("foo");

        boolean failedAsExpected = false;
        try {
            propertyHolder.loadProperties(fps);
        } catch (PropertiesException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    private final ConfigurationServiceImpl.PropertyHolder buildNonEmpty() {
        ConfigurationServiceImpl.PropertyHolder propertyHolder = new ConfigurationServiceImpl.PropertyHolder();
        propertyHolder.setProperty(KNOWN_KEY1, KNOWN_VALUE1);
        propertyHolder.setProperty(KNOWN_KEY2, KNOWN_VALUE2);
        propertyHolder.setProperty(KNOWN_KEY3, KNOWN_VALUE3);

        return propertyHolder;
    }
}
