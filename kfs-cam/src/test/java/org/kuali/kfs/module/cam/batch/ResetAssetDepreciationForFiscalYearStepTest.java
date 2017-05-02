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
package org.kuali.kfs.module.cam.batch;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.cam.batch.service.AssetDepreciationService;

import java.util.Date;

public class ResetAssetDepreciationForFiscalYearStepTest {
    private ResetAssetDepreciationForFiscalYearStep step;
    private AssetDepreciationService assetDepreciationService;

    @Before
    public void setUp() {
        step = new ResetAssetDepreciationForFiscalYearStep();

        assetDepreciationService = EasyMock.mock(AssetDepreciationService.class);
        step.setAssetDepreciationService(assetDepreciationService);
    }

    @Test
    public void testExecuteSuccess() {
        EasyMock.expect(assetDepreciationService.resetPeriodValuesWhenFirstFiscalPeriod()).andReturn(true);
        EasyMock.replay(assetDepreciationService);

        try {
            Assert.assertTrue(step.execute("resetAssetDepreciationForFiscalYearJob", new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail("Exception thrown");
        }
    }

    @Test
    public void testExecuteFailure() {
        EasyMock.expect(assetDepreciationService.resetPeriodValuesWhenFirstFiscalPeriod()).andReturn(false);
        EasyMock.replay(assetDepreciationService);

        try {
            Assert.assertFalse(step.execute("resetAssetDepreciationForFiscalYearJob", new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail("Exception thrown");
        }

    }
}
