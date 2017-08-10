package edu.arizona.kfs.module.purap.service;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

public interface PurapAccountingHelperService {

	/**
	 * 
	 * convenience method that generates a list of source accounts while
	 * excluding items with $0 amounts and use tax and any tax items
	 * 
	 * @param items the items to generate source accounts from
	 * @return a list of source accounts "rolled up" from the purap accounts
	 */
	public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsNoUseTaxNoWithholding(List<PurApItem> items);

	/**
	 * 
	 * convenience method that generates a list of source accounts while
	 * excluding items with $0 amounts and use tax using alternate amount
	 * 
	 * @param items the items to generate source accounts from
	 * @return a list of source accounts "rolled up" from the purap accounts
	 */
	public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsNoUseTaxUsingAlternateAmount(List<PurApItem> items);

}
