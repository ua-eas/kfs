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
import java.util.List;
import java.util.Map;

public class SerializedCollectionsTest {
    private static String VENDOR_HEADER_API = "/vnd/api/v1/reference/pven/830E61147605C23CE0404F8189D82CFD";

    @Test
    public void unmaskedVendorData() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(VENDOR_HEADER_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> bo = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertTrue("Vendor should have collection of addresses", bo.containsKey("vendorAddresses"));
        Assert.assertEquals("Specifically, vendor should have a single address", 1, ((List<Map<String,Object>>)bo.get("vendorAddresses")).size());
        Assert.assertEquals("The first line of that address should be \"341 Pine Tree Rd\"", "341 Pine Tree Rd", ((List<Map<String,Object>>)bo.get("vendorAddresses")).get(0).get("vendorLine1Address"));
        Assert.assertTrue("The vendor address should link out to a vendor address type", ((List<Map<String,Object>>)bo.get("vendorAddresses")).get(0).containsKey("vendorAddressType"));
    }
}
