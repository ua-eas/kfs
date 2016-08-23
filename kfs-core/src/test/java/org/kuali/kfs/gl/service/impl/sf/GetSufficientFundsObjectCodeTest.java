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
package org.kuali.kfs.gl.service.impl.sf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.gl.service.impl.SufficientFundsServiceImpl;
import org.kuali.kfs.sys.KFSConstants;

public class GetSufficientFundsObjectCodeTest {
    private SufficientFundsServiceImpl sufficientFundsService;

    @Before
    public void setUp() {
        sufficientFundsService = new SufficientFundsServiceImpl();
    }

    @Test
    public void testNoChecking() {
        String objectCode = sufficientFundsService.getSufficientFundsObjectCode(SufficientFundsTestFixtures.getTestObjectCode(), KFSConstants.SF_TYPE_NO_CHECKING);

        Assert.assertEquals(KFSConstants.NOT_AVAILABLE_STRING, objectCode);
    }

    @Test
    public void testAccount() {
        String objectCode = sufficientFundsService.getSufficientFundsObjectCode(SufficientFundsTestFixtures.getTestObjectCode(), KFSConstants.SF_TYPE_ACCOUNT);

        Assert.assertEquals("    ", objectCode);
    }

    @Test
    public void testCashAtAccount() {
        String objectCode = sufficientFundsService.getSufficientFundsObjectCode(SufficientFundsTestFixtures.getTestObjectCode(), KFSConstants.SF_TYPE_CASH_AT_ACCOUNT);

        Assert.assertEquals("    ", objectCode);
    }

    @Test
    public void testObject() {
        String objectCode = sufficientFundsService.getSufficientFundsObjectCode(SufficientFundsTestFixtures.getTestObjectCode(), KFSConstants.SF_TYPE_OBJECT);

        Assert.assertEquals("1234", objectCode);
    }

    @Test
    public void testLevel() {
        String objectCode = sufficientFundsService.getSufficientFundsObjectCode(SufficientFundsTestFixtures.getTestObjectCode(), KFSConstants.SF_TYPE_LEVEL);

        Assert.assertEquals("2345", objectCode);
    }

    @Test
    public void testConsolidation() {
        String objectCode = sufficientFundsService.getSufficientFundsObjectCode(SufficientFundsTestFixtures.getTestObjectCode(), KFSConstants.SF_TYPE_CONSOLIDATION);

        Assert.assertEquals("3456", objectCode);
    }

    @Test
    public void testBad() {
        try {
            sufficientFundsService.getSufficientFundsObjectCode(SufficientFundsTestFixtures.getTestObjectCode(), null);
            Assert.fail("Method should have thrown exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
}
