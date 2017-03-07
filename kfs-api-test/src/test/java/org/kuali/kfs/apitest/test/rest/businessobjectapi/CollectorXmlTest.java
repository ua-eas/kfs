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

public class CollectorXmlTest {
    @Test
    public void testCollectorUnauthorized() throws IOException {
        HttpResponse response = CollectorUtilities.collectorRequest(Constants.DAY_TOKEN,"text/xml",null);

        Assert.assertEquals(HttpStatus.SC_UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testCollectorInvalid() throws IOException {
        String badData = "bad data bad data bad data bad data bad data bad data bad data bad data bad data \n";
        HttpResponse response = CollectorUtilities.collectorRequest(Constants.BUTT_TOKEN,"text/xml",badData);

        HttpEntity body = response.getEntity();
        StringWriter writer = new StringWriter();
        IOUtils.copy(body.getContent(), writer, "UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode errorsNode = mapper.readTree(writer.toString());
        Assert.assertTrue(errorsNode.get("errors") != null);
        JsonNode errorListNode = errorsNode.get("errors");
        Assert.assertTrue(errorListNode.isArray());
        Assert.assertEquals("Errors were encountered while parsing file: fatal error Parsing error was encountered on line 1, column 1: Content is not allowed in prolog.",errorListNode.get(0).asText());

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testCollectorValid() throws IOException {
        // Unique doc number
        Date d = new Date();
        String docNumber = Long.toString(d.getTime());

        String goodData = "<batch xsi:schemaLocation=\"http://www.kuali.org/kfs/gl/collector collector.xsd\" xmlns=\"http://www.kuali.org/kfs/gl/collector\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "\t<header>\n" +
            "\t\t<chartOfAccountsCode>BL</chartOfAccountsCode>\n" +
            "\t\t<organizationCode>CHEM</organizationCode>\n" +
            "\t\t<transmissionDate>2017-03-07</transmissionDate>\n" +
            "\t\t<batchSequenceNumber>0</batchSequenceNumber>\n" +
            "\t\t<personUserId>khuntley</personUserId>\n" +
            "\t\t<campusCode>BL</campusCode>\n" +
            "\t\t<phoneNumber>555-555-5555</phoneNumber>\n" +
            "\t\t<mailingAddress>1234 Main St.xxxxxxxxxxxxxxxxx</mailingAddress>\n" +
            "\t\t<departmentName>Thirty Char Limit to Dept Name</departmentName>\n" +
            "\t\t<emailAddress>test@test.com</emailAddress>\n" +
            "\t</header>\n" +
            "\t<glEntry>\n" +
            "\t\t<universityFiscalYear>2017</universityFiscalYear>\n" +
            "\t\t<chartOfAccountsCode>BL</chartOfAccountsCode>\n" +
            "\t\t<accountNumber>1031400</accountNumber>\n" +
            "\t\t<subAccountNumber>-----</subAccountNumber>\n" +
            "\t\t<objectCode>2400</objectCode>\n" +
            "\t\t<subObjectCode>---</subObjectCode>\n" +
            "\t\t<balanceTypeCode>AC</balanceTypeCode>\n" +
            "\t\t<objectTypeCode>EX</objectTypeCode>\n" +
            "\t\t<universityFiscalAccountingPeriod>08</universityFiscalAccountingPeriod>\n" +
            "\t\t<documentTypeCode>SB</documentTypeCode>\n" +
            "\t\t<originationCode>01</originationCode>\n" +
            "\t\t<documentNumber>" + docNumber + "</documentNumber>\n" +
            "\t\t<transactionEntrySequenceId>1</transactionEntrySequenceId>\n" +
            "\t\t<transactionLedgerEntryDescription>Description</transactionLedgerEntryDescription>\n" +
            "\t\t<transactionLedgerEntryAmount>189.00</transactionLedgerEntryAmount>\n" +
            "\t\t<debitOrCreditCode>C</debitOrCreditCode>\n" +
            "\t\t<transactionDate>2017-03-03</transactionDate>\n" +
            "\t\t<organizationDocumentNumber>ODN</organizationDocumentNumber>\n" +
            "\t\t<projectCode>----------</projectCode>\n" +
            "\t\t<organizationReferenceId>ORI</organizationReferenceId>\n" +
            "\t\t<referenceDocumentTypeCode>XXXX</referenceDocumentTypeCode>\n" +
            "\t\t<referenceOriginationCode>TF</referenceOriginationCode>\n" +
            "\t\t<referenceDocumentNumber>1000000</referenceDocumentNumber>\n" +
            "\t\t<documentReversalDate>2017-04-01</documentReversalDate>\n" +
            "\t\t<encumbranceUpdateCode>D</encumbranceUpdateCode>\n" +
            "\t</glEntry>\n" +
            "\t<trailer>\n" +
            "\t\t<totalRecords>1</totalRecords>\n" +
            "\t\t<totalAmount>189.0</totalAmount>\n" +
            "\t</trailer>\n" +
            "</batch>\n";
        System.out.println(goodData);
        HttpResponse response = CollectorUtilities.collectorRequest(Constants.KHUNTLEY_TOKEN,"text/xml",goodData);

        HttpEntity body = response.getEntity();
        StringWriter writer = new StringWriter();
        IOUtils.copy(body.getContent(), writer, "UTF-8");
        String responseBody = writer.toString();

        Assert.assertEquals("",responseBody);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }
}
