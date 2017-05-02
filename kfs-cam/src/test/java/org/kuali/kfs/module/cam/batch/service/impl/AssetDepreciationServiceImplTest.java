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
package org.kuali.kfs.module.cam.batch.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;

public class AssetDepreciationServiceImplTest {
    private AssetDepreciationServiceImpl assetDepreciationService;
    private DepreciationBatchDao depreciationBatchDao;

    @Before
    public void setUp() {
        assetDepreciationService = new AssetDepreciationServiceImpl();
        depreciationBatchDao = EasyMock.mock(DepreciationBatchDao.class);
        assetDepreciationService.setDepreciationBatchDao(depreciationBatchDao);
    }

    @Test
    public void testSuccess() throws Exception {
        depreciationBatchDao.resetPeriodValuesWhenFirstFiscalPeriod(1);

        EasyMock.replay(depreciationBatchDao);

        Assert.assertTrue(assetDepreciationService.resetPeriodValuesWhenFirstFiscalPeriod());
    }

    @Test
    public void testFailure() throws Exception {
        depreciationBatchDao.resetPeriodValuesWhenFirstFiscalPeriod(1);
        EasyMock.expectLastCall().andThrow(new RuntimeException());

        EasyMock.replay(depreciationBatchDao);

        Assert.assertFalse(assetDepreciationService.resetPeriodValuesWhenFirstFiscalPeriod());
    }
}
