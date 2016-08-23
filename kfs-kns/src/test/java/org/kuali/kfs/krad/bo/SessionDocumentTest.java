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
package org.kuali.kfs.krad.bo;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * This is a description of what this class does - chang don't forget to fill this in.
 */
public class SessionDocumentTest {

    SessionDocument dummySessionDocument;

    @Before
    public void setUp() throws Exception {
        dummySessionDocument = new SessionDocument();
    }

    /**
     * This method ...
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        dummySessionDocument = null;
    }

    @Test
    public void testSerializedDocumentForm() {

        byte[] dummyByte = "dummy".getBytes();
        dummySessionDocument.setSerializedDocumentForm(dummyByte);
        assertEquals("Testing SerializedDocumentForm in SessionDocumentService", "dummy", new String(dummySessionDocument.getSerializedDocumentForm()));
    }

    @Test
    public void testSessionId() {
        dummySessionDocument.setSessionId("dummySeesionID");
        assertEquals("Testing SessionId in SessionDocumentService", "dummySeesionID", dummySessionDocument.getSessionId());
    }


    @Test
    public void testLastUpdatedDate() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        Timestamp currentTimestamp = new Timestamp(now.getTime());
        dummySessionDocument.setLastUpdatedDate(currentTimestamp);
        assertEquals("Testing LastUpdatedDate in SessionDocumentService", currentTimestamp, dummySessionDocument.getLastUpdatedDate());
    }

    @Test
    public void testDocumentNumber() {
        dummySessionDocument.setDocumentNumber("dummyDocumentNumber");
        assertEquals("Testing DocumentNumber in SessionDocumentService", "dummyDocumentNumber", dummySessionDocument.getDocumentNumber());
    }


    @Test
    public void testPrincipalId() {
        dummySessionDocument.setPrincipalId("dummyPrincipalId");
        assertEquals("Testing PrincipalId in SessionDocumentService", "dummyPrincipalId", dummySessionDocument.getPrincipalId());
    }

    @Test
    public void testIpAddress() {
        dummySessionDocument.setIpAddress("dummyIpAddress");
        assertEquals("Testing IpAddress in SessionDocumentService", "dummyIpAddress", dummySessionDocument.getIpAddress());
    }

    @Test
    public void testEncrypted() {
        dummySessionDocument.setEncrypted(true);
        assertEquals("Testing Encrypted in SessionDocumentService", true, dummySessionDocument.isEncrypted());
    }
}
