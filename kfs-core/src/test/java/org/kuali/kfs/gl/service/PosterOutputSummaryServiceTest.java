package org.kuali.kfs.gl.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.coa.service.impl.ObjectTypeServiceImpl;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryTotal;

import java.util.ArrayList;
import java.util.List;

public class PosterOutputSummaryServiceTest {
    private ObjectTypeService objectTypeService;

    @Before
    public void setup() {
        objectTypeService = new MockObjectTypeService();
    }

    @After
    public void tearDown() {
        objectTypeService = null;
    }

    @Test
    public void testPosterOutputSummaryDoesNotMolestCurrentYearExpenseObjectTypes() {
        final ObjectTypeService closureObjectTypeService = this.objectTypeService;
        assertExpenseObjectTypesNoAsset();
        new PosterOutputSummaryTotal() {
            @Override
            protected ObjectTypeService getObjectTypeService() {
                return closureObjectTypeService;
            }
        };
        assertExpenseObjectTypesNoAsset();
    }

    protected void assertExpenseObjectTypesNoAsset() {
        Assert.assertEquals("Expense Object Types should have length of 4", 4, objectTypeService.getCurrentYearExpenseObjectTypes().size());
        Assert.assertTrue("ExpenseObjectTypes should contain ES", objectTypeService.getCurrentYearExpenseObjectTypes().contains("ES"));
        Assert.assertTrue("ExpenseObjectTypes should contain EX", objectTypeService.getCurrentYearExpenseObjectTypes().contains("EX"));
        Assert.assertTrue("ExpenseObjectTypes should contain EE", objectTypeService.getCurrentYearExpenseObjectTypes().contains("EE"));
        Assert.assertTrue("ExpenseObjectTypes should contain TE", objectTypeService.getCurrentYearExpenseObjectTypes().contains("TE"));
        Assert.assertFalse("ExpenseObjectTypes should NOT contain AS", objectTypeService.getCurrentYearExpenseObjectTypes().contains("AS"));
    }

    class MockObjectTypeService extends ObjectTypeServiceImpl {
        private List<String> currentYearExpenseObjecTtypes = new ArrayList<>(); // have a single list, which simulates a cache

        public MockObjectTypeService() {
            currentYearExpenseObjecTtypes.add("EX");
            currentYearExpenseObjecTtypes.add("ES");
            currentYearExpenseObjecTtypes.add("TE");
            currentYearExpenseObjecTtypes.add("EE");
        }

        @Override
        public List<String> getCurrentYearExpenseObjectTypes() {
            return currentYearExpenseObjecTtypes;
        }

        @Override
        public String getCurrentYearAssetObjectType() {
            return "AS";
        }
    }
}
