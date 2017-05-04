package edu.arizona.kfs.module.cam.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.arizona.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.lookup.LookupUtils;

public class GeneralLedgerEntryLookupableHelperServiceImpl extends org.kuali.kfs.module.cam.businessobject.lookup.GeneralLedgerEntryLookupableHelperServiceImpl {
	
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // update status code from user input value to DB value.
        updateStatusCodeCriteria(fieldValues);

        List<? extends BusinessObject> searchResults = super.getSearchResults(fieldValues);
        if (searchResults == null || searchResults.isEmpty()) {
            return searchResults;
        }
        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(GeneralLedgerEntry.class);
        Long matchingResultsCount = null;
        List<GeneralLedgerEntry> newList = new ArrayList<GeneralLedgerEntry>();
        for (BusinessObject businessObject : searchResults) {
            GeneralLedgerEntry entry = (GeneralLedgerEntry) businessObject;
            if (!CamsConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
            	if (!CamsConstants.PRNC.equals(entry.getFinancialDocumentTypeCode())) {
	                if (!CamsConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
	                    newList.add(entry);
	                }
	                else if (CamsConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
	                    Map<String, String> cmKeys = new HashMap<String, String>();
	                    cmKeys.put(CamsPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, entry.getDocumentNumber());
	                    // check if CAB PO document exists, if not included
	                    Collection<PurchasingAccountsPayableDocument> matchingCreditMemos = getBusinessObjectService().findMatching(PurchasingAccountsPayableDocument.class, cmKeys);
	                    if (matchingCreditMemos == null || matchingCreditMemos.isEmpty()) {
	                        newList.add(entry);
	                    }
	                }
	            }
            }
        }
        matchingResultsCount = Long.valueOf(newList.size());
        if (matchingResultsCount.intValue() <= searchResultsLimit.intValue()) {
            matchingResultsCount = new Long(0);
        }
        return new CollectionIncomplete(newList, matchingResultsCount);
    }
}
