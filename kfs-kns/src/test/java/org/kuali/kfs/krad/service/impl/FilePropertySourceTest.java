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
package org.kuali.kfs.krad.service.impl;

import org.junit.Test;
import org.kuali.kfs.krad.exception.PropertiesException;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the FilePropertySource methods.
 */
public class FilePropertySourceTest {

    @Test
    public void testLoadProperties_defaultFileName() {
        ConfigurationServiceImpl.FilePropertySource fps = new ConfigurationServiceImpl.FilePropertySource();

        boolean failedAsExpected = false;
        try {
            fps.loadProperties();
        } catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testLoadProperties_invalidFileName() {
        ConfigurationServiceImpl.FilePropertySource fps = new ConfigurationServiceImpl.FilePropertySource();
        fps.setFileName("      ");

        boolean failedAsExpected = false;
        try {
            fps.loadProperties();
        } catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testLoadProperties_unknownFileName() {
        ConfigurationServiceImpl.FilePropertySource fps = new ConfigurationServiceImpl.FilePropertySource();
        fps.setFileName("unknown");

        boolean failedAsExpected = false;
        try {
            fps.loadProperties();
        } catch (PropertiesException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testLoadProperties_knownFileName_noSuffix() {
        ConfigurationServiceImpl.FilePropertySource fps = new ConfigurationServiceImpl.FilePropertySource();
        fps.setFileName("configuration");

        boolean failedAsExpected = false;
        try {
            fps.loadProperties();
        } catch (PropertiesException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }
}
