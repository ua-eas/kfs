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
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;
import org.kuali.kfs.apitest.utils.SearchResult;

import java.io.IOException;
import java.util.Calendar;

public class UnspecifiedSortSearchTest {
    private static String BANK_SEARCH_API = "/sys/api/v1/reference/bank";
    private static String SUB_OBJECT_CODE_CURRENTS_SEARCH_API = "/coa/api/v1/reference/sobj?limit=20&universityFiscalYear=";

    @Test
    public void testBusinessObjectWithPrimaryKey() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BANK_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        RestUtilities.assertStatusOk(response);

        SearchResult searchResult = RestUtilities.parseSearchResult(response);
        Assert.assertEquals("We should have 13 banks", 13, searchResult.getTotalCount());
        Assert.assertEquals("We should have all 13 banks on hand", 13, searchResult.getResultsCount());
        Assert.assertEquals("The first bank should be 0001", "0001", searchResult.getFirstResult().get("bankCode"));
        Assert.assertEquals("The last bank should be TST6", "TST6", searchResult.getLastResult().get("bankCode"));
    }

    @Test
    public void testBusinessObjectWithNoObjectIdButPrimaryKey() throws IOException {
        Calendar cal = Calendar.getInstance();
        HttpResponse response = RestUtilities.makeRequest(SUB_OBJECT_CODE_CURRENTS_SEARCH_API+cal.get(Calendar.YEAR), Constants.KHUNTLEY_TOKEN);

        RestUtilities.assertStatusOk(response);

        SearchResult searchResult = RestUtilities.parseSearchResult(response);
        Assert.assertEquals("We should have 275 sub object code currents", 275, searchResult.getTotalCount());
        Assert.assertEquals("We should only have 10 sub object code currents on hand", 20, searchResult.getResultsCount());
        Assert.assertEquals("The first sub-object code should have a chart of BA", "BA", searchResult.getFirstResult().get("chartOfAccountsCode"));
        Assert.assertEquals("The first sub-object code should have an account number of 6044900", "6044900", searchResult.getFirstResult().get("accountNumber"));
        Assert.assertEquals("The first sub-object code should have an object code of 1466", "1466", searchResult.getFirstResult().get("financialObjectCode"));
        Assert.assertEquals("The first sub-object code should have a sub-object code of 001", "001", searchResult.getFirstResult().get("financialSubObjectCode"));
        Assert.assertEquals("The last sub-object code should have a chart of BA", "BA", searchResult.getLastResult().get("chartOfAccountsCode"));
        Assert.assertEquals("The last sub-object code should have an account number of 6044900", "6044900", searchResult.getLastResult().get("accountNumber"));
        Assert.assertEquals("The last sub-object code should have an object code of 6100", "6100", searchResult.getLastResult().get("financialObjectCode"));
        Assert.assertEquals("The last sub-object code should have a sub-object code of BTE", "BTE", searchResult.getLastResult().get("financialSubObjectCode"));
    }
}
