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
            Assert.assertTrue(step.execute("resetAssetDepreciationForFiscalYearJob",new Date()));
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
            Assert.assertFalse(step.execute("resetAssetDepreciationForFiscalYearJob",new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail("Exception thrown");
        }

    }
}