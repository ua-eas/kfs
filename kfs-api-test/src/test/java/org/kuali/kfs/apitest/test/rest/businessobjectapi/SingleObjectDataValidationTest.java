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
package org.kuali.kfs.apitest.test.rest.businessobjectapi;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;

import java.io.IOException;
import java.util.Map;

public class SingleObjectDataValidationTest {
    private static String API = "/ar/api/v1/reference/arsi/5A691AF93B5A424DE0404F8189D80D73";

    @Test
    public void validModuleName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> bo = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66255",(String)bo.get("lockboxNumber"));
        Assert.assertNull(bo.get("newCollectionRecord"));
        Assert.assertEquals("5A691AF93B5A424DE0404F8189D80D73",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/coa/api/v1/reference/coat/014F3DAF748BA448E043814FD28EA448"));
    }
}
