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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;

import java.io.IOException;

public class InvalidModuleNameTest {
    private static String BAD_API = "/coa/api/v1/reference/xxx/sacc/0395AAE3A27C9478E043814FD8819478";
    private static String GOOD_API = "/coa/api/v1/reference/sacc/0395AAE3A27C9478E043814FD8819478";
    private static String BAD_SEARCH_API = "/coa/api/v1/reference/xxx/sacc";
    private static String GOOD_SEARCH_API = "/coa/api/v1/reference/sacc";

    @Test
    public void invalidModuleName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
    }

    @Test
    public void validModuleName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidSearchModuleName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
    }

    @Test
    public void validSearchModuleName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }
}
