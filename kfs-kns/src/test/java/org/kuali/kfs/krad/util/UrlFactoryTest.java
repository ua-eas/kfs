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
package org.kuali.kfs.krad.util;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the UrlFactory methods.
 */
public class UrlFactoryTest {

    /**
     * Test that what is returned from url factory matches the url we expect.
     */
    @Test
    public void testFactoryMatch() throws Exception {
        String basePath = "http://localhost:8080/";
        String actionPath = "kr/lookup.do";
        String testUrl = basePath + actionPath + "?" + KRADConstants.DISPATCH_REQUEST_PARAMETER + "=start" + "&" + KRADConstants.DOC_FORM_KEY + "=903" + KRADConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME + "=accountLookupableImpl" + KRADConstants.RETURN_LOCATION_PARAMETER + "=" + basePath + "ib.do";
        testUrl = UrlFactory.encode(testUrl);

        // construct lookup url
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(KRADConstants.DOC_FORM_KEY, "903");
        parameters.put(KRADConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, "accountLookupableImpl");
        parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, basePath + "ib.do");

        String returnedUrl = UrlFactory.parameterizeUrl(basePath + actionPath, parameters);

        assertTrue("Returned url is empty", StringUtils.isNotBlank(returnedUrl));
        assertTrue("Returned url has incorrect base", returnedUrl.startsWith(basePath + actionPath + "?"));
        assertTrue("Returned url does not have correct # of &", StringUtils.countMatches(returnedUrl, "&") == 3);
        assertTrue("Returned url missing parameter 1", StringUtils.contains(returnedUrl, KRADConstants.DISPATCH_REQUEST_PARAMETER + "=start"));
        assertTrue("Returned url missing parameter 2", StringUtils.contains(returnedUrl, KRADConstants.DOC_FORM_KEY + "=903"));
        assertTrue("Returned url missing parameter 3", StringUtils.contains(returnedUrl, KRADConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME + "=accountLookupableImpl"));
        // assertTrue("Returned url missing parameter 4",StringUtils.contains(returnedUrl,
        // UrlFactory.encode(KRADConstants.RETURN_LOCATION_PARAMETER + "=" + basePath + "ib.do")));
    }
}
