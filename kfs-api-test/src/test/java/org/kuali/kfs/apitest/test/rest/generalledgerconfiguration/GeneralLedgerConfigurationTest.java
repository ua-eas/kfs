/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 The Kuali Foundation
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

package org.kuali.kfs.apitest.test.rest.generalledgerconfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GeneralLedgerConfigurationTest {
    private static final String GL_CONFIGURATION_URL = "/gl/api/v1/general-ledger-configuration";

    @Test
    public void testGeneralLedgerConfiguration() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GL_CONFIGURATION_URL, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        final Map<String,Object> configurationMap = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        assertConfigurationKey(configurationMap, "accountsCanCrossCharts", Boolean.class);
        assertConfigurationKey(configurationMap, "continuationAccountBypassBalanceTypeCodes", List.class);
        assertConfigurationKey(configurationMap, "continuationAccountBypassOriginationCodes", List.class);
        assertConfigurationKey(configurationMap, "continuationAccountBypassDocumentTypeCodes", List.class);
        assertConfigurationKey(configurationMap, "contractsAndGrantsDenotingValues", List.class);
        assertConfigurationKey(configurationMap, "fundGroupDenotesContractsandGrants", Boolean.class);
        assertConfigurationKey(configurationMap, "annualClosingDocumentType", String.class);
        assertConfigurationKey(configurationMap, "scrubberValidationDaysOffset", String.class);
        assertConfigurationKey(configurationMap, "objectTypeBypassOriginations", List.class);
    }

    private void assertConfigurationKey(Map<String, Object> configuration, String keyName, Class<?> expectedType) {
        Assert.assertTrue("General Ledger Congfiguration should have key: \""+keyName+"\"", configuration.containsKey(keyName));
        Assert.assertNotNull("General Ledger Configuration should actually hold value for key: \"" + keyName + "\"", configuration.get(keyName));
        Assert.assertTrue("General Ledger Congfiguration key: \""+keyName+"\" should have a value assignable from "+expectedType.getName(), expectedType.isAssignableFrom(configuration.get(keyName).getClass()));
    }
}
