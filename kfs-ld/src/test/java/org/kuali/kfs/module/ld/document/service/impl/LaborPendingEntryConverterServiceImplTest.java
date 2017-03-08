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

package org.kuali.kfs.module.ld.document.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class LaborPendingEntryConverterServiceImplTest {
    protected LaborPendingEntryConverterServiceImpl laborPendingEntryConverterService;
    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;

    @Before
    public void setUp() {
        parameterService = EasyMock.createMock(ParameterService.class);
        dataDictionaryService = EasyMock.createMock(DataDictionaryService.class);

        laborPendingEntryConverterService = new LaborPendingEntryConverterServiceImpl();
        laborPendingEntryConverterService.setParameterService(parameterService);
    }

    @Test
    public void testCopySubObjectToBenefitEntriesEnabled() {
        EasyMock.expect(parameterService.getParameterValueAsBoolean(
                KfsParameterConstants.LABOR_DOCUMENT.class,
                LaborConstants.SalaryExpenseTransfer.COPY_SUB_OBJECT_TO_BENEFIT_ENTRIES_PARM_NM,
                false)
        ).andReturn(true).anyTimes();

        EasyMock.replay(parameterService);

        String subcode = "FOO";

        ExpenseTransferAccountingLine sourceLine = new ExpenseTransferSourceAccountingLine();
        sourceLine.setFinancialSubObjectCode(subcode);

        LaborLedgerPendingEntry pendingEntry = new LaborLedgerPendingEntry();
        Assert.assertNull(pendingEntry.getFinancialSubObjectCode());

        laborPendingEntryConverterService.setSubobjectCodeOnBenefitPendingEntry(sourceLine, pendingEntry);

        Assert.assertEquals(subcode, pendingEntry.getFinancialSubObjectCode());
    }

    @Test
    @PrepareForTest({SpringContext.class})
    public void testCopySubObjectToBenefitEntriesDisabled() {
        PowerMock.mockStatic(SpringContext.class);

        EasyMock.expect(SpringContext.getBean(DataDictionaryService.class)).andReturn(dataDictionaryService).anyTimes();
        EasyMock.expect(parameterService.getParameterValueAsBoolean(
                KfsParameterConstants.LABOR_DOCUMENT.class,
                LaborConstants.SalaryExpenseTransfer.COPY_SUB_OBJECT_TO_BENEFIT_ENTRIES_PARM_NM,
                false)
        ).andReturn(false).anyTimes();
        EasyMock.expect(dataDictionaryService.getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)).andReturn(10).anyTimes();

        PowerMock.replay(SpringContext.class);
        EasyMock.replay(parameterService, dataDictionaryService);

        ExpenseTransferAccountingLine sourceLine = new ExpenseTransferSourceAccountingLine();
        sourceLine.setFinancialSubObjectCode("FOO");

        LaborLedgerPendingEntry pendingEntry = new LaborLedgerPendingEntry();
        Assert.assertNull(pendingEntry.getFinancialSubObjectCode());

        laborPendingEntryConverterService.setSubobjectCodeOnBenefitPendingEntry(sourceLine, pendingEntry);

        Assert.assertEquals(KFSConstants.getDashFinancialSubObjectCode(), pendingEntry.getFinancialSubObjectCode());
    }
}
