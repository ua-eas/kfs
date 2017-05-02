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
