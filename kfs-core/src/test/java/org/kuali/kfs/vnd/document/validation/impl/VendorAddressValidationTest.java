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

package org.kuali.kfs.vnd.document.validation.impl;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.springframework.util.AutoPopulatingList;

public class VendorAddressValidationTest {
    @Test
    public void testCheckInactiveAllowed() {
        VendorRule vendorRule = new VendorRule();
        VendorAddress vendorAddress = new VendorAddress();
        vendorAddress.setActive(true);
        vendorAddress.setVendorDefaultAddressIndicator(true);
        Assert.assertTrue("Expected inactive to be allowed if vendoraddress is active and is default", vendorRule.checkInactiveAllowed(vendorAddress));

        vendorAddress.setActive(false);
        vendorAddress.setVendorDefaultAddressIndicator(true);
        Assert.assertFalse("Expected inactive to not be allowed if vendoraddress is inactive and is default", vendorRule.checkInactiveAllowed(vendorAddress));

        vendorAddress.setActive(true);
        vendorAddress.setVendorDefaultAddressIndicator(false);
        Assert.assertTrue("Expected inactive to be allowed if vendoraddress is active and is not default", vendorRule.checkInactiveAllowed(vendorAddress));

        vendorAddress.setActive(false);
        vendorAddress.setVendorDefaultAddressIndicator(false);
        Assert.assertTrue("Expected inactive to be allowed if vendoraddress is inactive and is not default", vendorRule.checkInactiveAllowed(vendorAddress));

        MessageMap errors = GlobalVariables.getMessageMap();
        Assert.assertTrue(errors.getErrorMessages().containsKey("active"));
        AutoPopulatingList<ErrorMessage> errorMessages = errors.getErrorMessages().get("active");
        Assert.assertNotNull("Expected error message for key active with vendoraddress is inactive and is not default", errorMessages);
        Assert.assertEquals("Expected 1 error message with vendoraddress is inactive and is not default", 1, errorMessages.size());
        Assert.assertEquals("Unexpected error message with vendoraddress is inactive and is not default", VendorKeyConstants.ERROR_ADDRESS_DEFAULT_ADDRESS_MUST_BE_ACTIVE, errorMessages.get(0).getErrorKey());
    }
}
