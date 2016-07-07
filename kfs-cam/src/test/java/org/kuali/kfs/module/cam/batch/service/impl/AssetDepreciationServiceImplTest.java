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