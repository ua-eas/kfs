package edu.arizona.kfs.module.purap.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.B2BShoppingCartItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class B2BShoppingServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.B2BShoppingServiceImpl {

    @Override
    protected RequisitionItem createRequisitionItem(B2BShoppingCartItem item, Integer itemLine,
    		String defaultCommodityCode) {
    	RequisitionItem reqsItem = super.createRequisitionItem(item, itemLine, defaultCommodityCode);
    	// rescan item description and trim any characters that will fail Rice anyCharacterValidation
    	if (StringUtils.isNotBlank(reqsItem.getItemDescription())) {
    		reqsItem.setItemDescription(reqsItem.getItemDescription().replaceAll("[^\\p{Graph}\\p{Space}]",""));
    	}
    	
    	return reqsItem;
    }
}
