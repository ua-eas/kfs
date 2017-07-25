package edu.arizona.kfs.module.cam.batch.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Entry;

import edu.arizona.kfs.module.cam.CamsConstants;
import edu.arizona.kfs.module.cam.CamsPropertyConstants;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;


import org.kuali.kfs.module.cam.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;


public class BatchExtractServiceImpl extends org.kuali.kfs.module.cam.batch.service.impl.BatchExtractServiceImpl {
	
	/** 			
	* Retrieves a PRNC document for a specific document number
	*
	* @param entry GL Line
	* @return PaymentRequestDocument
	*/ 			
	protected PaymentRequestDocument findPrncDocument(Entry entry) {
		PaymentRequestDocument PRNCDocument = null;
		Map<String, String> keys = new LinkedHashMap<String, String>();
		keys.put(CamsPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
		Collection<PaymentRequestDocument> matchingPreqs = businessObjectService.findMatching(PaymentRequestDocument.class, keys);
		if (matchingPreqs != null && matchingPreqs.size() == 1) {
			PRNCDocument = matchingPreqs.iterator().next();
		}
		return PRNCDocument;
	}
	
    protected VendorCreditMemoDocument findCmncDocument(Entry entry) {
        VendorCreditMemoDocument CMNCDocument = null;
        Map<String, String> keys = new LinkedHashMap<String, String>();
        keys.put(CamsPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
        Collection<VendorCreditMemoDocument> matchingCms = businessObjectService.findMatching(VendorCreditMemoDocument.class, keys);
        if (matchingCms != null && matchingCms.size() == 1) {
            CMNCDocument = matchingCms.iterator().next();
        }
        return CMNCDocument;
    }

    @Override
    protected PurchasingAccountsPayableDocument createPurchasingAccountsPayableDocument(Entry entry) {
        AccountsPayableDocumentBase apDoc = null;
        PurchasingAccountsPayableDocument camPurapDoc = null;
        // If document is not in CAB, create a new document to save in CAB
        if (CamsConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
            // find PREQ
            apDoc = findPaymentRequestDocument(entry);
        }
        else if (CamsConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
            // find CM
            apDoc = findCreditMemoDocument(entry);
        }
    	else if (CamsConstants.PRNC.equals(entry.getFinancialDocumentTypeCode())) {
	    	// find PRNC
	    	apDoc = findPrncDocument(entry);
    	}
    	else if (CamsConstants.CMNC.equals(entry.getFinancialDocumentTypeCode())) {
    		//find CMNC doc
    		apDoc = findCmncDocument(entry);
    	}
    	
        if (apDoc == null) {
            LOG.error("A valid Purchasing Document (PREQ or CM) could not be found for this document number " + entry.getDocumentNumber());
        }
        else {
            camPurapDoc = new PurchasingAccountsPayableDocument();
            camPurapDoc.setDocumentNumber(entry.getDocumentNumber());
            camPurapDoc.setPurapDocumentIdentifier(apDoc.getPurapDocumentIdentifier());
            camPurapDoc.setPurchaseOrderIdentifier(apDoc.getPurchaseOrderIdentifier());
            camPurapDoc.setDocumentTypeCode(entry.getFinancialDocumentTypeCode());
            camPurapDoc.setActivityStatusCode(CamsConstants.ActivityStatusCode.NEW);
            camPurapDoc.setVersionNumber(0L);
        }
        return camPurapDoc;
    }
    
    @Override
    public void separatePOLines(List<Entry> fpLines, List<Entry> purapLines, Collection<Entry> elgibleGLEntries) {
        for (Entry entry : elgibleGLEntries) {
            if (CamsConstants.PREQ.equals(entry.getFinancialDocumentTypeCode()) || CamsConstants.PRNC.equals(entry.getFinancialDocumentTypeCode())) {
                purapLines.add(entry);
            }

            else if (!CamsConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                fpLines.add(entry);
            }
            else if (CamsConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                Map<String, String> fieldValues = new HashMap<String, String>();
                fieldValues.put(CamsPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, entry.getDocumentNumber());
                // check if vendor credit memo, then include as FP line
                Collection<VendorCreditMemoDocument> matchingCreditMemos = businessObjectService.findMatching(VendorCreditMemoDocument.class, fieldValues);
                for (VendorCreditMemoDocument creditMemoDocument : matchingCreditMemos) {
                    if (creditMemoDocument.getPurchaseOrderIdentifier() == null) {
                        fpLines.add(entry);
                    }
                    else {
                        purapLines.add(entry);
                    }
                }
            }
        }
    }
    
    protected PurchasingAccountsPayableLineAssetAccount createPurchasingAccountsPayableLineAssetAccount(GeneralLedgerEntry generalLedgerEntry, PurchasingAccountsPayableDocument camPurapDoc, PurApAccountingLineBase purApAccountingLine, PurchasingAccountsPayableItemAsset itemAsset) {
        PurchasingAccountsPayableLineAssetAccount assetAccount = new PurchasingAccountsPayableLineAssetAccount();
        assetAccount.setDocumentNumber(camPurapDoc.getDocumentNumber());
        assetAccount.setAccountsPayableLineItemIdentifier(itemAsset.getAccountsPayableLineItemIdentifier());
        assetAccount.setCapitalAssetBuilderLineNumber(itemAsset.getCapitalAssetBuilderLineNumber());
        assetAccount.setGeneralLedgerAccountIdentifier(generalLedgerEntry.getGeneralLedgerAccountIdentifier());
        if (CamsConstants.CM.equals(generalLedgerEntry.getFinancialDocumentTypeCode()) || CamsConstants.CMNC.equals(generalLedgerEntry.getFinancialDocumentTypeCode())) {
            assetAccount.setItemAccountTotalAmount(purApAccountingLine.getAmount().negated());
        }
        else {
            assetAccount.setItemAccountTotalAmount(purApAccountingLine.getAmount());
        }
        assetAccount.setActivityStatusCode(CamsConstants.ActivityStatusCode.NEW);
        assetAccount.setVersionNumber(0L);
        return assetAccount;
    }
}
