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
package org.kuali.kfs.apitest.test.rest.businessobjectapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.CollectorUtilities;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

public class CollectorFlatFileTest {
    @Test
    public void testCollectorUnauthorized() throws IOException {
        HttpResponse response = CollectorUtilities.collectorRequest(Constants.DAY_TOKEN,"text/plain",null);

        Assert.assertEquals(HttpStatus.SC_UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testCollectorInvalid() throws IOException {
        String badData = "bad data bad data bad data bad data bad data bad data bad data bad data bad data \n";
        HttpResponse response = CollectorUtilities.collectorRequest(Constants.BUTT_TOKEN,"text/plain",badData);

        HttpEntity body = response.getEntity();
        StringWriter writer = new StringWriter();
        IOUtils.copy(body.getContent(), writer, "UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode errorsNode = mapper.readTree(writer.toString());
        Assert.assertTrue(errorsNode.get("errors") != null);
        JsonNode errorListNode = errorsNode.get("errors");
        Assert.assertTrue(errorListNode.isArray());
        Assert.assertEquals("Collector Header (HD) record missing at line 1.",errorListNode.get(0).asText());
        Assert.assertEquals("Fiscal year 'bad ' contains an invalid value.",errorListNode.get(1).asText());
        Assert.assertEquals("Transaction Sequence Number 'ta ba' contains an invalid value.",errorListNode.get(2).asText());
        Assert.assertEquals("Transaction Amount cannot be blank.",errorListNode.get(3).asText());
        Assert.assertEquals("Collector Trailer (TL) record missing at line 1.",errorListNode.get(4).asText());

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testCollectorValid() throws IOException {
        // Unique doc number
        Date d = new Date();
        String docNumber = Long.toString(d.getTime()) + " ";

        String goodData = "2017BL0000     2017-03-07HD0TEST@TEST.COM                           KHUNTLEY                      OFFICE OF THE CONTROLLER      1234 Main Street              BL5173533121\n" +
                          "2017BL4631640-----2400---ACEX10SB  EM" + docNumber + "00003TEST OFFSET OFFSET OFFSET OFFS          +00000000000000050.00D2013-04-23          ----------                                                                            \n" +
                          "2017BL4631640-----2400---ACEX10SB  EM" + docNumber + "00005TEST OFFSET OFFSET OFFSET OFFS          +00000000000000100.00D2013-04-17          ----------                                                                              \n" +
                          "2017BL4631648-----2400---ACEX10SB  EM" + docNumber + "00001TEST OFFSET OFFSET OFFSET OFFS          +00000000000000015.00D2013-04-08          ----------                                                                              \n" +
                          "                         TL                   00003                                         00000000000000165.00                 \n";
        HttpResponse response = CollectorUtilities.collectorRequest(Constants.BUTT_TOKEN,"text/plain",goodData);

        HttpEntity body = response.getEntity();
        StringWriter writer = new StringWriter();
        IOUtils.copy(body.getContent(), writer, "UTF-8");
        String responseBody = writer.toString();

        Assert.assertEquals("",responseBody);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }
}
