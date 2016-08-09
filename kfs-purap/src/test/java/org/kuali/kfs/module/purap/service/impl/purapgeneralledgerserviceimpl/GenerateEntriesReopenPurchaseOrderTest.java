package org.kuali.kfs.module.purap.service.impl.purapgeneralledgerserviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.businessobject.ItemType;
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

public class GenerateEntriesReopenPurchaseOrderTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;

    private PurchaseOrderDocument po;
    private PurchaseOrderItem item;
    private PurchaseOrderAccount accountingLine1;
    private PurchaseOrderAccount accountingLine2;
    private PurapAccountingService purapAccountingService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;

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
        item = EasyMock.createMock(PurchaseOrderItem.class);
        accountingLine1 = EasyMock.createMock(PurchaseOrderAccount.class);
        accountingLine2 = EasyMock.createMock(PurchaseOrderAccount.class);
    }
    
    private void execute() {
        EasyMock.replay(po, item, accountingLine1, accountingLine2, purapAccountingService, 
                generalLedgerPendingEntryService, businessObjectService);
        purapGeneralLedgerService.generateEntriesReopenPurchaseOrder(po);
        EasyMock.verify(po, item, accountingLine1, accountingLine2, purapAccountingService, 
                generalLedgerPendingEntryService, businessObjectService);
    }
    
    private void baseExpectations(List<PurApItem> items, List<PurApItem> activeItems, List<SourceAccountingLine> sourceAccountingLines) {
        EasyMock.expect(po.getItems()).andReturn(items).anyTimes();
        EasyMock.expect(po.getItemsActiveOnly()).andReturn(activeItems).anyTimes();
        EasyMock.expect(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(activeItems)).andReturn(sourceAccountingLines);
        po.setGlOnlySourceAccountingLines(sourceAccountingLines);
        EasyMock.expectLastCall();
        EasyMock.expect(po.getGlOnlySourceAccountingLines()).andReturn(sourceAccountingLines).anyTimes();        
    }
    
    private void mockQuantityBasedItem(PurchaseOrderItem item, int original, int invoiced, double unitPrice) {
        int remaining = original-invoiced;
        double outstandingAmount = unitPrice*remaining;
        
        ItemType itemType = new ItemType();
        itemType.setQuantityBasedGeneralLedgerIndicator(true);
        EasyMock.expect(item.getItemType()).andReturn(itemType).anyTimes();
        EasyMock.expect(item.getItemQuantity()).andReturn(new KualiDecimal(original)).anyTimes();
        EasyMock.expect(item.getItemInvoicedTotalQuantity()).andReturn(new KualiDecimal(invoiced)).anyTimes();        
        item.setItemOutstandingEncumberedQuantity(new KualiDecimal(remaining));
        EasyMock.expectLastCall();
        EasyMock.expect(item.getItemOutstandingEncumberedQuantity()).andReturn(new KualiDecimal(remaining)).anyTimes();
        EasyMock.expect(item.getItemUnitPrice()).andReturn(new BigDecimal(unitPrice)).anyTimes();        
        item.setItemOutstandingEncumberedAmount(new KualiDecimal(outstandingAmount));
        EasyMock.expectLastCall();
        EasyMock.expect(item.getItemOutstandingEncumberedAmount()).andReturn(new KualiDecimal(outstandingAmount)).anyTimes();
        EasyMock.expect(item.getItemLineNumber()).andReturn(1).anyTimes();
        EasyMock.expect(item.isItemActiveIndicator()).andReturn(true).anyTimes();
    }
    
    private void mockAmountBasedItem(PurchaseOrderItem item, double total, double invoiced) {
        ItemType itemType = new ItemType();
        itemType.setQuantityBasedGeneralLedgerIndicator(false);
        EasyMock.expect(item.getItemType()).andReturn(itemType).anyTimes();
        EasyMock.expect(item.getTotalAmount()).andReturn(new KualiDecimal(total)).anyTimes();
        EasyMock.expect(item.getItemInvoicedTotalAmount()).andReturn(new KualiDecimal(invoiced));
        item.setItemOutstandingEncumberedAmount(new KualiDecimal(total-invoiced));
        EasyMock.expectLastCall();
        EasyMock.expect(item.getItemOutstandingEncumberedAmount()).andReturn(new KualiDecimal(total-invoiced)).anyTimes();
        EasyMock.expect(item.getItemLineNumber()).andReturn(1).anyTimes();
        EasyMock.expect(item.isItemActiveIndicator()).andReturn(true).anyTimes();
    }
    
    private PurchaseOrderAccount mockAccountingLine(PurchaseOrderAccount accountingLine, double percent, double amount, Double altAmount) {
        EasyMock.expect(accountingLine.getAccountLinePercent()).andReturn(new BigDecimal(percent)).anyTimes();
        accountingLine.setItemAccountOutstandingEncumbranceAmount(new KualiDecimal(amount));
        EasyMock.expectLastCall();
        EasyMock.expect(accountingLine.isEmpty()).andReturn(false).anyTimes();
        accountingLine.setAlternateAmountForGLEntryCreation(new KualiDecimal(amount));
        EasyMock.expectLastCall();
        if (altAmount == null) {
            EasyMock.expect(accountingLine.getAlternateAmountForGLEntryCreation()).andReturn(new KualiDecimal(amount)).anyTimes();
        }
        else {
            EasyMock.expect(accountingLine.getAlternateAmountForGLEntryCreation()).andReturn(new KualiDecimal(amount));
            accountingLine.setAlternateAmountForGLEntryCreation(new KualiDecimal(altAmount));
            EasyMock.expectLastCall();
            EasyMock.expect(accountingLine.getAlternateAmountForGLEntryCreation()).andReturn(new KualiDecimal(altAmount)).anyTimes();
        }
        EasyMock.expect(accountingLine.compareTo(EasyMock.isA(PurchaseOrderAccount.class))).andReturn(0).anyTimes();
        
        return accountingLine;
    }
    
    @Test
    public void testNoItems() {
        List<PurApItem> items = new ArrayList<>();
        List<SourceAccountingLine> sourceAccountingLines = new ArrayList<>();
        baseExpectations(items, items, sourceAccountingLines);
        
        execute();       
    }
    
    @Test
    public void testQuantityBasedItem() {
        List<PurApItem> items = new ArrayList<>();
        mockQuantityBasedItem(item, 7, 4, 10.0);
        List<PurApAccountingLine> sourceAccountingLines = new ArrayList<>();        
        sourceAccountingLines.add(mockAccountingLine(accountingLine1, 100.0, 30.0, 30.0));
        EasyMock.expect(item.getSourceAccountingLines()).andReturn(sourceAccountingLines).anyTimes();
        items.add(item);
        
        List<SourceAccountingLine> glOnlySourceAccountingLines = new ArrayList<>();
        baseExpectations(items, items, glOnlySourceAccountingLines);
        
        execute();        
    }

    @Test
    public void testAmountBasedItem() {
        List<PurApItem> items = new ArrayList<>();
        mockAmountBasedItem(item, 70.0, 40.0);
        List<PurApAccountingLine> sourceAccountingLines = new ArrayList<>();        
        sourceAccountingLines.add(mockAccountingLine(accountingLine1, 100.0, 30.0, 30.0));
        EasyMock.expect(item.getSourceAccountingLines()).andReturn(sourceAccountingLines).anyTimes();
        items.add(item);
        
        List<SourceAccountingLine> glOnlySourceAccountingLines = new ArrayList<>();
        baseExpectations(items, items, glOnlySourceAccountingLines);
        
        execute();   
    }
    
    @Test
    public void testMultipleAccountingLines() {
        List<PurApItem> items = new ArrayList<>();
        mockAmountBasedItem(item, 70.0, 30.0);
        List<PurApAccountingLine> sourceAccountingLines = new ArrayList<>();        
        sourceAccountingLines.add(mockAccountingLine(accountingLine1, 66.66, 26.66, null));
        // This line has a rounding error to fix:
        sourceAccountingLines.add(mockAccountingLine(accountingLine2, 33.33, 13.33, 13.34));
        EasyMock.expect(item.getSourceAccountingLines()).andReturn(sourceAccountingLines).anyTimes();
        items.add(item);
        
        List<SourceAccountingLine> glOnlySourceAccountingLines = new ArrayList<>();
        baseExpectations(items, items, glOnlySourceAccountingLines);
        
        execute();   
    }
    
    @Test
    public void testShouldNotCreateGlPEs() {
        List<PurApItem> items = new ArrayList<>();
        
        List<SourceAccountingLine> sourceAccountingLines = new ArrayList<>();
        EasyMock.expect(accountingLine1.getAmount()).andReturn(KualiDecimal.ZERO);
        sourceAccountingLines.add(accountingLine1);
        
        baseExpectations(items, items, sourceAccountingLines);
        
        execute();    
    }
    
    @Test
    public void testShouldCreateGLPEs() {
        List<PurApItem> items = new ArrayList<>();
        
        List<SourceAccountingLine> sourceAccountingLines = new ArrayList<>();
        EasyMock.expect(accountingLine1.getAmount()).andReturn(new KualiDecimal(100.0));
        sourceAccountingLines.add(accountingLine1);
        
        baseExpectations(items, items, sourceAccountingLines);
        EasyMock.expect(generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po)).andReturn(true);
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        EasyMock.expect(po.getGeneralLedgerPendingEntries()).andReturn(glpes);
        EasyMock.expect(businessObjectService.save(glpes)).andReturn(null);
        
        execute();  
    }
}
