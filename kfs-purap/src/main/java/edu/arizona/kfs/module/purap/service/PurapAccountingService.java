package edu.arizona.kfs.module.purap.service;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface PurapAccountingService extends org.kuali.kfs.module.purap.service.PurapAccountingService {

    /**
    *
    * This method has different list of parameters as the one on org's. The new parameter is list of purap items.   It is used to get the first item's accounts as 
    * distribution accounts for proration.
    *
    * @param accounts the incoming source accounts from generateSummary
    * @param totalAmount the total amount of the document
    * @param percentScale the scale to round to
    * @param clazz the class of the Purchasing Accounts Payable Account
	* @param purApItemslist of purap items
    * @return a list of new Purchasing Accounts Payable Accounts
    */

    public List<PurApAccountingLine> generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz, List<PurApItem> purApItems);

    /**
     * Generates an account summary, that is it creates a list of source accounts by rounding up the purap accounts off of the purap
     * items.
     *
     * @param items the items to determ
     * @param itemTypeCodes the item types to determine whether to look at an item in combination with itemTypeCodesAreIncluded
     * @param itemTypeCodesAreIncluded value to tell whether the itemTypeCodes parameter lists inclusion or exclusion variables
     * @param useZeroTotals whether to include items with a zero dollar total
     * @param useAlternateAmount an alternate amount used in certain cases for GL entry
     * @return a list of source accounts
     */
    public List<SourceAccountingLine> generateAccountSummary(List<PurApItem> items, Set<String> itemTypeCodes, Boolean itemTypeCodesAreIncluded, Boolean useZeroTotals, Boolean useAlternateAmount, Boolean useTaxIncluded, Boolean taxableOnly);

}
