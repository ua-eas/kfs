package org.kuali.kfs.module.purap.service.impl.purapgeneralledgerserviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class GenerateEntriesVoidPurchaseOrderTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;

    private PurchaseOrderDocument po;
    private PurapAccountingService purapAccountingService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;
    
    private List<PurApItem> poItems;
    private List<PurApItem> poActiveItems;
    private List<GeneralLedgerPendingEntry> glpes;
    private List<SourceAccountingLine> sourceAccountingLines;
    private List<Object> dynamicMocks;
    
    @Before
    public void setUp() {
        purapGeneralLedgerService = new PurapGeneralLedgerServiceImpl();

        purapAccountingService = EasyMock.createMock(PurapAccountingService.class);
        purapGeneralLedgerService.setPurapAccountingService(purapAccountingService);
        generalLedgerPendingEntryService = EasyMock.createMock(GeneralLedgerPendingEntryService.class);
        purapGeneralLedgerService.setGeneralLedgerPendingEntryService(generalLedgerPendingEntryService);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        purapGeneralLedgerService.setBusinessObjectService(businessObjectService);

        po = EasyMock.createMock(PurchaseOrderDocument.class);
        
        poItems = new ArrayList<>();
        poActiveItems = new ArrayList<>();
        glpes = new ArrayList<>();
        sourceAccountingLines = new ArrayList<>();
        dynamicMocks = new ArrayList<>();
    }
    
    private void execute() {
        EasyMock.replay(po, purapAccountingService, generalLedgerPendingEntryService, businessObjectService);
        for (Object mock : dynamicMocks) {
            EasyMock.replay(mock);
        }
        
        purapGeneralLedgerService.generateEntriesVoidPurchaseOrder(po);
        EasyMock.verify(po, purapAccountingService, generalLedgerPendingEntryService, businessObjectService);
        for (Object mock : dynamicMocks) {
            EasyMock.verify(mock);
        }
    }
    
    private void preparePO() {
        EasyMock.expect(po.getItems()).andReturn(poItems).anyTimes();
        EasyMock.expect(po.getItemsActiveOnly()).andReturn(poActiveItems).anyTimes();
        po.setGlOnlySourceAccountingLines(sourceAccountingLines);
        EasyMock.expectLastCall();
        EasyMock.expect(po.getGeneralLedgerPendingEntries()).andReturn(glpes).anyTimes();
    }
    
    private void prepareItem(int lineNumber, double outstandingAmount, List<PurApAccountingLine> accountingLines, boolean active) {
        PurchaseOrderItem item = EasyMock.createMock(PurchaseOrderItem.class);
        
        EasyMock.expect(item.getItemLineNumber()).andReturn(lineNumber).anyTimes();
        EasyMock.expect(item.isItemActiveIndicator()).andReturn(active).anyTimes();
        
        if (active) {
            EasyMock.expect(item.getItemOutstandingEncumberedAmount())
                .andReturn(new KualiDecimal(outstandingAmount)).anyTimes();
            EasyMock.expect(item.getSourceAccountingLines()).andReturn(accountingLines).anyTimes();
            poActiveItems.add(item);
        }
        
        poItems.add(item);        
        dynamicMocks.add(item);
    }
    
    private PurchaseOrderAccount createPoAccountingLine(double percent, double newOutstandingAmount, Double altAmount) {       
        PurchaseOrderAccount account = EasyMock.mock(PurchaseOrderAccount.class);
        EasyMock.expect(account.isEmpty()).andReturn(false).anyTimes();
        EasyMock.expect(account.getAccountLinePercent()).andReturn(new BigDecimal(percent)).anyTimes();
        EasyMock.expect(account.compareTo(EasyMock.isA(PurchaseOrderAccount.class))).andReturn(0).anyTimes();
        
        account.setAlternateAmountForGLEntryCreation(new KualiDecimal(newOutstandingAmount));
        EasyMock.expectLastCall();
        
        if (altAmount != null) {
            EasyMock.expect(account.getAlternateAmountForGLEntryCreation()).andReturn(new KualiDecimal(newOutstandingAmount));
            account.setAlternateAmountForGLEntryCreation(new KualiDecimal(altAmount));
            EasyMock.expectLastCall();
        }
        
        sourceAccountingLines.add(account);
        dynamicMocks.add(account);
        return account;
    }
    
    private void prepareItemsNoAccountingLines() {
        // Active
        prepareItem(1, 10.0, new ArrayList<>(), true);
        // Not active
        prepareItem(2, 15.0, new ArrayList<>(), false);
    }
    
    private void prepareItemsWithAccountingLines() {
        List<PurApAccountingLine> accountingLines;
        
        // One accounting line
        accountingLines = new ArrayList<>();
        accountingLines.add(createPoAccountingLine(100, 10.0, 10.0));
        prepareItem(1, 10.0, accountingLines, true);
        
        // 50/50
        accountingLines = new ArrayList<>();
        accountingLines.add(createPoAccountingLine(50, 5.0, null));
        accountingLines.add(createPoAccountingLine(50, 5.0, 5.0));
        prepareItem(2, 10.0, accountingLines, true);
        
        // Rounding error to fix
        accountingLines = new ArrayList<>();
        accountingLines.add(createPoAccountingLine(66.66, 6666.0, null));
        accountingLines.add(createPoAccountingLine(33.33, 3333.0, 3334.0));
        prepareItem(3, 10000.0, accountingLines, true);
    }
    
    private void baseExpectations() {
        preparePO();
        EasyMock.expect(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(poActiveItems))
            .andReturn(sourceAccountingLines);
        EasyMock.expect(generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po)).andReturn(true);
        EasyMock.expect(businessObjectService.save(glpes)).andReturn(null);
    }
    
    @Test
    public void testBaseCase() {
        baseExpectations();
        execute();
    }

    @Test
    public void testPoWithItems() {
        baseExpectations();
        prepareItemsNoAccountingLines();
        execute();
    }
    
    @Test
    public void testPoWithAccountingLines() {
        baseExpectations();
        prepareItemsWithAccountingLines();
        execute();
    }
}
