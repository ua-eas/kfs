package edu.arizona.kfs.module.purap.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

import edu.arizona.kfs.module.purap.service.PurapAccountingHelperService;
import edu.arizona.kfs.module.purap.service.PurapAccountingService;

public class PurapAccountingHelperServiceImpl implements PurapAccountingHelperService {
	private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingHelperServiceImpl.class);

	protected PurapAccountingService purapAccountingService;

	public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
		this.purapAccountingService = purapAccountingService;
	}

	// local constants
	protected static final Boolean ITEM_TYPES_INCLUDED_VALUE = Boolean.TRUE;;
	protected static final Boolean ITEM_TYPES_EXCLUDED_VALUE = Boolean.FALSE;
	protected static final Boolean ZERO_TOTALS_RETURNED_VALUE = Boolean.TRUE;
	protected static final Boolean ZERO_TOTALS_NOT_RETURNED_VALUE = Boolean.FALSE;
	protected static final Boolean ALTERNATE_AMOUNT_USED = Boolean.TRUE;
	protected static final Boolean ALTERNATE_AMOUNT_NOT_USED = Boolean.FALSE;
	protected static final Boolean USE_TAX_INCLUDED = Boolean.TRUE;
	protected static final Boolean USE_TAX_EXCLUDED = Boolean.FALSE;

	@Override
	public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsNoUseTaxNoWithholding(List<PurApItem> items) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("generateSummaryWithNoZeroTotalsNoUseTaxNoWithholding() started");
		}
		String[] taxItemTypeCodes = {
			PurapConstants.ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE, 
			PurapConstants.ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE };
		
		Set itemsToExclude = new HashSet(Arrays.asList(taxItemTypeCodes));

		List<SourceAccountingLine> returnList = purapAccountingService.generateAccountSummary(items, itemsToExclude, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_USED, USE_TAX_EXCLUDED, false);
		if (LOG.isDebugEnabled()) {
			LOG.debug("generateSummaryWithNoZeroTotalsNoUseTaxNoWithholding() ended");
		}

		return returnList;
	}

	@Override
	public List<SourceAccountingLine> generateSummaryWithNoZeroTotalsNoUseTaxUsingAlternateAmount(List<PurApItem> items) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("generateSummaryWithNoZeroTotalsNoUseTaxUsingAlternateAmount() started");
		}

		List<SourceAccountingLine> returnList = purapAccountingService.generateAccountSummary(items, null, ITEM_TYPES_EXCLUDED_VALUE, ZERO_TOTALS_NOT_RETURNED_VALUE, ALTERNATE_AMOUNT_USED, USE_TAX_EXCLUDED, false);
		if (LOG.isDebugEnabled()) {
			LOG.debug("generateSummaryWithNoZeroTotalsNoUseTaxUsingAlternateAmount() ended");
		}

		return returnList;
	}

}
