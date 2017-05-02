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

package org.kuali.kfs.apitest.test.rest.financialdocumenttype;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;

import java.io.IOException;
import java.util.Map;

public class FinancialDocumentTypeTest {
    private static final String FINANCIAL_DOCUMENT_TYPE_URL = "/sys/api/v1/financial-document-types/";

    private static final String ACCOUNTING_FINANCIAL_SYSTEM_DOCUMENT_TYPE = "IB";
    private static final String LOWER_CASE_ACCOUNTING_FINANCIAL_SYSTEM_DOCUMENT_TYPE = "ib"; // parent: FP, label: Internal Billing
    private static final String LEDGER_ONLY_FINANCIAL_SYSTEM_DOCUMENT_TYPE = "ACLO"; // parent: FSLO, label: Annual Closing
    private static final String LOWER_CASE_LEDGER_ONLY_FINANCIAL_SYSTEM_DOCUMENT_TYPE = "aclo"; // parent: FSLO, label: Annual Closin
    private static final String NON_ACCOUNTING_FINANCIAL_SYSTEM_DOCUMENT_TYPE = "ACCT"; // parent; COA, label: Account
    private static final String LOWER_CASE_NON_ACCOUNTING_FINANCIAL_SYSTEM_DOCUMENT_TYPE = "acct"; // parent; COA, label: Account
    private static final String ACCESS_SECURITY_DOCUMENT_TYPE = "SecurityModelMaintenanceDocument"; // parent: AccessSecuritySimpleMaintenanceDocument, label: Security Model
    private static final String RICE_DOCUMENT_TYPE = "IdentityManagementPersonDocument"; // parent: IdentityManagementDocument, label: Person
    private static final String BAD_DOCUMENT_TYPE_NAME = "ZZZZ";

    @Test
    public void testGetFinancialSystemDocumentType_AccountingFinancialDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+ACCOUNTING_FINANCIAL_SYSTEM_DOCUMENT_TYPE, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> financialDocumentTypeMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        assertInternalBillingDocumentType(financialDocumentTypeMap);
    }

    @Test
    public void testGetFinancialSystemDocumentType_LowerCaseAccountingFinancialDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+LOWER_CASE_ACCOUNTING_FINANCIAL_SYSTEM_DOCUMENT_TYPE, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> financialDocumentTypeMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        assertInternalBillingDocumentType(financialDocumentTypeMap);
    }

    private void assertInternalBillingDocumentType(Map<String, Object> financialDocumentTypeMap) {
        assertBasicFinancialDocumentTypeInformation(financialDocumentTypeMap, "IB");
        Assert.assertTrue("IB should be an accounting document", (Boolean)financialDocumentTypeMap.get("currentActiveAccountingDocumentType"));
        Assert.assertTrue("IB should be a financial document", (Boolean)financialDocumentTypeMap.get("financialSystemDocumentType"));
        assertExtraFinancialDocumentInformation(financialDocumentTypeMap, "Internal Billing", "FP");
    }

    @Test
    public void testGetFinancialSystemDocumentType_LedgerOnlyDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+LEDGER_ONLY_FINANCIAL_SYSTEM_DOCUMENT_TYPE, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> financialDocumentTypeMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        assertAnnualClosingDocumentType(financialDocumentTypeMap);
    }

    @Test
    public void testGetFinancialSystemDocumentType_LowerCaseLedgerOnlyDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+LOWER_CASE_LEDGER_ONLY_FINANCIAL_SYSTEM_DOCUMENT_TYPE, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> financialDocumentTypeMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        assertAnnualClosingDocumentType(financialDocumentTypeMap);
    }

    private void assertAnnualClosingDocumentType(Map<String, Object> financialDocumentTypeMap) {
        assertBasicFinancialDocumentTypeInformation(financialDocumentTypeMap, "ACLO");
        Assert.assertTrue("ACLO should be an accounting document", (Boolean)financialDocumentTypeMap.get("currentActiveAccountingDocumentType"));
        Assert.assertFalse("ACLO should not be a financial document", (Boolean)financialDocumentTypeMap.get("financialSystemDocumentType"));
        assertExtraFinancialDocumentInformation(financialDocumentTypeMap, "Annual Closing", "FSLO");
    }

    @Test
    public void testGetFinancialSystemDocumentType_NonAccountingDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+NON_ACCOUNTING_FINANCIAL_SYSTEM_DOCUMENT_TYPE, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> financialDocumentTypeMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        assertAccountDocumentType(financialDocumentTypeMap);

    }

    @Test
    public void testGetFinancialSystemDocumentType_LowerCaseNonAccountingDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+LOWER_CASE_NON_ACCOUNTING_FINANCIAL_SYSTEM_DOCUMENT_TYPE, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> financialDocumentTypeMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        assertAccountDocumentType(financialDocumentTypeMap);
    }

    private void assertAccountDocumentType(Map<String, Object> financialDocumentTypeMap) {
        assertBasicFinancialDocumentTypeInformation(financialDocumentTypeMap, "ACCT");
        Assert.assertFalse("ACCT should not be an accounting document", (Boolean)financialDocumentTypeMap.get("currentActiveAccountingDocumentType"));
        Assert.assertTrue("ACCT should be a financial document", (Boolean)financialDocumentTypeMap.get("financialSystemDocumentType"));
        assertExtraFinancialDocumentInformation(financialDocumentTypeMap, "Account", "COA");
    }

    @Test
    public void testGetFinancialSystemDocumentType_AccessSecurityDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+ACCESS_SECURITY_DOCUMENT_TYPE, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> financialDocumentTypeMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        assertBasicFinancialDocumentTypeInformation(financialDocumentTypeMap, ACCESS_SECURITY_DOCUMENT_TYPE);
        Assert.assertFalse(ACCESS_SECURITY_DOCUMENT_TYPE+" should be an accounting document", (Boolean)financialDocumentTypeMap.get("currentActiveAccountingDocumentType"));
        Assert.assertTrue(ACCESS_SECURITY_DOCUMENT_TYPE+" should not be a financial document", (Boolean)financialDocumentTypeMap.get("financialSystemDocumentType"));
        assertExtraFinancialDocumentInformation(financialDocumentTypeMap, "Security Model", "AccessSecuritySimpleMaintenanceDocument");
    }

    @Test
    public void testGetFinancialSystemDocumentType_RiceDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+RICE_DOCUMENT_TYPE, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> financialDocumentTypeMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        assertBasicFinancialDocumentTypeInformation(financialDocumentTypeMap, RICE_DOCUMENT_TYPE);
        Assert.assertFalse(RICE_DOCUMENT_TYPE+" should not be an accounting document", (Boolean)financialDocumentTypeMap.get("currentActiveAccountingDocumentType"));
        Assert.assertFalse(RICE_DOCUMENT_TYPE+" should not be a financial document", (Boolean)financialDocumentTypeMap.get("financialSystemDocumentType"));
        assertExtraFinancialDocumentInformation(financialDocumentTypeMap, "Person", "IdentityManagementDocument");
    }

    @Test
    public void testGetFinancialSystemDocumentType_NonExitingDocumentType() throws IOException {
        final HttpResponse response = RestUtilities.makeRequest(FINANCIAL_DOCUMENT_TYPE_URL+BAD_DOCUMENT_TYPE_NAME, Constants.KHUNTLEY_TOKEN);
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
        Assert.assertEquals("Response entity should have a content length of 0", 0, response.getEntity().getContentLength());
    }

    private void assertBasicFinancialDocumentTypeInformation(Map<String, Object> financialDocumentTypeMap, String documentTypeName) {
        Assert.assertNotNull("We should have gotten back a document type", financialDocumentTypeMap);
        Assert.assertEquals("The size of the map should be 5", 5, financialDocumentTypeMap.size());
        Assert.assertEquals("We sent it "+documentTypeName+".  We should get \"+documentTypeName+\" back as the document type name", documentTypeName, (String)financialDocumentTypeMap.get("documentTypeName"));
    }

    private void assertExtraFinancialDocumentInformation(Map<String, Object> financialDocumentTypeMap, String documentTypeLabel, String parentDocumentType) {
        Assert.assertEquals("The label should be \""+documentTypeLabel+"\"", documentTypeLabel, (String)financialDocumentTypeMap.get("documentTypeLabel"));
        Assert.assertEquals("The parent document type should be \""+parentDocumentType+"\"", parentDocumentType, (String)financialDocumentTypeMap.get("parentDocumentTypeName"));
    }
}
