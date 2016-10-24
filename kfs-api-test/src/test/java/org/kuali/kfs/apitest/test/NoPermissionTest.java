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
package org.kuali.kfs.apitest.test;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;

import java.io.IOException;

public class NoPermissionTest {
    private static String API = "/api/v1/business-object/sys/banks/684207EF61A57565E0404F8189D81EBF";
    private static String SEARCH_API = "/api/v1/business-object/sys/banks";

    @Test
    public void noPermission() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(API, Constants.EAGLE_TOKEN);

        Assert.assertEquals(HttpStatus.SC_FORBIDDEN,response.getStatusLine().getStatusCode());
    }

    @Test
    public void hasPermission() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(API,Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }

    @Test
    public void noPermissionSearch() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API, Constants.EAGLE_TOKEN);

        Assert.assertEquals(HttpStatus.SC_FORBIDDEN,response.getStatusLine().getStatusCode());
    }

    @Test
    public void hasPermissionSearch() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API,Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }
}
