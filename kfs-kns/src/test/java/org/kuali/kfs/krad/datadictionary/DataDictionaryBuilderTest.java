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
package org.kuali.kfs.krad.datadictionary;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class is used to test the DataDictionaryBuilder.
 */

public class DataDictionaryBuilderTest {
    protected final Logger LOG = Logger.getLogger(getClass());

    static final String PACKAGE_CORE_BO = "org/kuali/rice/krad/bo/datadictionary/";

    static final String PACKAGE_CORE_DOCUMENT = "org/kuali/rice/krad/document/datadictionary/";

    static final String PACKAGE_KFS = "org/kuali/kfs/datadictionary/";

    static final String PACKAGE_CHART = "org/kuali/module/chart/datadictionary/";

    static final String PACKAGE_CG = "org/kuali/module/cg/datadictionary/";

    static final String PACKAGE_KRA_BUDGET = "org/kuali/module/kra/budget/datadictionary/";

    static final String PACKAGE_KRA_ROUTINGFORM = "org/kuali/module/kra/routingform/datadictionary/";

    static final String TESTPACKAGE_INVALID = "org/kuali/rice/krad/datadictionary/test/invalid/";

    DataDictionary dd = null;

    @Before
    public void setUp() throws Exception {

        dd = new DataDictionary();
    }

    @After
    public void tearDown() throws Exception {
        dd = null;
    }

    @Test
    public final void testDataDictionaryBuilder_source_invalid() throws Exception {
        boolean failedAsExpected = false;

        try {
            dd.addConfigFileLocation(null);
        } catch (DataDictionaryException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public final void testDataDictionaryBuilder_source_unknownFile() throws Exception {
        String INPUT_FILE = TESTPACKAGE_INVALID + "foo.xml";

        boolean failedAsExpected = false;

        try {
            dd.addConfigFileLocation(INPUT_FILE);
        } catch (DataDictionaryException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public final void testDataDictionaryBuilder_source_unknownPackage() throws Exception {
        String UNKNOWN_PACKAGE = TESTPACKAGE_INVALID + "foo/";

        boolean failedAsExpected = false;

        try {
            dd.addConfigFileLocation(UNKNOWN_PACKAGE);
        } catch (DataDictionaryException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public final void testDataDictionaryBuilder_invalidXml() throws Exception {
        String INPUT_FILE = TESTPACKAGE_INVALID + "InvalidXml.xml";

        boolean failedAsExpected = false;

        try {
            dd.addConfigFileLocation(INPUT_FILE);
            dd.parseDataDictionaryConfigurationFiles(false);
        } catch (DataDictionaryException e) {
            failedAsExpected = true;
        } catch (Exception e) {
            LOG.error("Error loading DD files", e);
            fail("Data Dictionary file load failed but with wrong exception type '" + e.getClass().getName() + "'");
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public final void testDataDictionaryBuilder_getInvalidDictionary() throws Exception {
        String INPUT_FILE = TESTPACKAGE_INVALID + "InvalidXml.xml";

        boolean failedAsExpected = false;

        try {
            dd.addConfigFileLocation(INPUT_FILE);
            dd.parseDataDictionaryConfigurationFiles(false);
        } catch (DataDictionaryException e) {
            failedAsExpected = true;
        } catch (Exception e) {
            LOG.error("Error loading DD files", e);
            fail("Data Dictionary file load failed but with wrong exception type '" + e.getClass().getName() + "'");
        }

        assertTrue(failedAsExpected);
    }

}
