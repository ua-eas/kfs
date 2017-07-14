package edu.arizona.kfs.gl.businessobject.lookup;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.Constant;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.LookupService;

public class GecEntryLookupableHelperServiceImpl extends org.kuali.kfs.gl.businessobject.lookup.EntryLookupableHelperServiceImpl {

    private LookupService lookupService;


    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {

        @SuppressWarnings("unchecked")//super.getFieldsForLookup() not generified
        Map fieldsForLookup = new HashMap(lookupForm.getFieldsForLookup());
        String debitCreditOption = getDebitCreditOption(fieldsForLookup);

        // Reason for overriding:
        lookupForm.getFieldsForLookup().remove(Constant.DEBIT_CREDIT_OPTION);

        Collection displayList = super.performLookup(lookupForm, resultTable, bounded);
        updateByDebitCreditOption(resultTable, debitCreditOption);

        return displayList;
    }


    /**
     * This method will not truly be unbounded, but determined by the GecEntry and YeGecEntry
     * search limits, found in the respective DD files. Letting the framework open the flood
     * gates results in potential timeouts.
     *
     * @param fieldValues the constraints ultimately used in an SQL search
     * @return any matching Entry objects, sans those identified in the specs
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return this.getSearchResults(fieldValues);
    }


    protected LookupService getLookupService() {
        return lookupService;
    }


    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

}
