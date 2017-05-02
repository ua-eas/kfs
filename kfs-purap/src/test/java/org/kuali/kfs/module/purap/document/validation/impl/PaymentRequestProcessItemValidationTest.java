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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class PaymentRequestProcessItemValidationTest {

    private PaymentRequestItem paymentRequestItemMock;
    private PaymentRequestProcessItemValidation cut;

    @Before
    public void setUp() {
        this.paymentRequestItemMock = EasyMock.createMock(PaymentRequestItem.class);
        this.cut = new PaymentRequestProcessItemValidation();
    }

    @Test
    public void testValidateItemWithoutAccounts_ValidExtendedCost_AccountListNotEmpty_ReturnsTrue () {
        EasyMock.expect(this.paymentRequestItemMock.getExtendedPrice()).andReturn(new KualiDecimal(70)).times(2);
        EasyMock.expect(this.paymentRequestItemMock.isAccountListEmpty()).andReturn(false);
        EasyMock.replay(this.paymentRequestItemMock);

        Assert.assertTrue(this.cut.validateItemWithoutAccounts(this.paymentRequestItemMock, "foo"));
    }

    @Test
    public void testValidateItemWithoutAccounts_ValidExtendedCost_NoAccount_ReturnsFalse () {
        EasyMock.expect(this.paymentRequestItemMock.getExtendedPrice()).andReturn(new KualiDecimal(70)).times(2);
        EasyMock.expect(this.paymentRequestItemMock.isAccountListEmpty()).andReturn(true);
        EasyMock.replay(this.paymentRequestItemMock);

        Assert.assertFalse(this.cut.validateItemWithoutAccounts(this.paymentRequestItemMock, "foo"));
    }

    @Test
    public void testValidateItemWithoutAccounts_NullExtendedCost_ReturnsTrue () {
        EasyMock.expect(this.paymentRequestItemMock.getExtendedPrice()).andReturn(null);
        EasyMock.replay(this.paymentRequestItemMock);

        Assert.assertTrue(this.cut.validateItemWithoutAccounts(this.paymentRequestItemMock, "foo"));
    }
}
