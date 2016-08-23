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
package org.kuali.kfs.gl.batch.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.gl.batch.service.impl.RunDateServiceImpl;

/**
 * Tests the cutoff time functionality of RunDateService
 */
public class RunDateServiceTest {

    protected static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";


    /**
     * Initializes the RunDateService implementation to test
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Tests that the cutoff time is parsed correct and tests several times to see where they lie
     * against the cut off time
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    @Test
    public void testCalculateCutoff() throws Exception {
        final RunDateService runDateService =  new RunDateServiceImpl() {
           @Override
            protected String retrieveCutoffTimeValue() {
                return "10:00:00";
            }
        };


        Map<String, String> expectedCurrentToRunTimeMappings = new LinkedHashMap<String, String>();

        // assuming cutoff time of 10am in this code
        expectedCurrentToRunTimeMappings.put("6/1/2006 10:35:00", "6/1/2006 10:35:00");
        expectedCurrentToRunTimeMappings.put("3/1/2006 9:59:00", "2/28/2006 23:59:59");
        expectedCurrentToRunTimeMappings.put("3/1/2004 9:59:00", "2/29/2004 23:59:59");
        expectedCurrentToRunTimeMappings.put("4/1/2004 1:59:00", "3/31/2004 23:59:59");
        expectedCurrentToRunTimeMappings.put("9/21/2005 19:13:14", "9/21/2005 19:13:14");
        expectedCurrentToRunTimeMappings.put("1/1/2009 9:59:14", "12/31/2008 23:59:59");
        expectedCurrentToRunTimeMappings.put("5/12/2009 21:59:14", "5/12/2009 21:59:14");
        expectedCurrentToRunTimeMappings.put("5/12/2050 21:59:14", "5/12/2050 21:59:14");
        // 2100 is not a leap year
        expectedCurrentToRunTimeMappings.put("3/1/2100 9:59:00", "2/28/2100 23:59:59");
        expectedCurrentToRunTimeMappings.put("3/1/2104 9:59:00", "2/29/2104 23:59:59");


        DateFormat parser = new SimpleDateFormat(DATE_FORMAT);
        for (Entry<String, String> entry : expectedCurrentToRunTimeMappings.entrySet()) {
            Date calculatedRunTime = runDateService.calculateRunDate(parser.parse(entry.getKey()));
            Assert.assertTrue(entry.getKey() + " " + entry.getValue() + " " + calculatedRunTime, parser.parse(entry.getValue()).equals(calculatedRunTime));
        }
    }

    /**
     * Tests an edge case where the cutoff time is at midnight
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    @Test
    public void testCalculateCutoffDuringMidnightHour() throws Exception {
        final RunDateService runDateService =  new RunDateServiceImpl() {
            @Override
            protected String retrieveCutoffTimeValue() {
                return "0:05:00";
            }
        };        Map<String, String> expectedCurrentToRunTimeMappings = new LinkedHashMap<String, String>();

        expectedCurrentToRunTimeMappings.put("6/1/2006 0:05:00", "6/1/2006 0:05:00");
        expectedCurrentToRunTimeMappings.put("3/1/2006 0:02:33", "2/28/2006 23:59:59");

        DateFormat parser = new SimpleDateFormat(DATE_FORMAT);
        for (Entry<String, String> entry : expectedCurrentToRunTimeMappings.entrySet()) {
            Date calculatedRunTime = runDateService.calculateRunDate(parser.parse(entry.getKey()));
            Assert.assertTrue(entry.getKey() + " " + entry.getValue() + " " + calculatedRunTime, parser.parse(entry.getValue()).equals(calculatedRunTime));
        }
    }
}
