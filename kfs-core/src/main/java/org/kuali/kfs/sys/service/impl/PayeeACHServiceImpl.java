package org.kuali.kfs.sys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.PayeeACHService;

public class PayeeACHServiceImpl implements PayeeACHService {
	
	private BusinessObjectService businessObjectService;
	
	@Override
	public boolean isPayeeSignedUpForACH(String payeeTypeCode, String payeeIdNumber) {

		Map<String, Object> keys = new HashMap<String, Object>();
	    keys.put(PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, payeeTypeCode);
	    keys.put(PdpPropertyConstants.PAYEE_ID_NUMBER, payeeIdNumber);
	    keys.put(KFSPropertyConstants.ACCOUNT_ACTIVE_INDICATOR, Boolean.TRUE);
	    List<PayeeACHAccount> payeeACHAccountList = (List<PayeeACHAccount>)businessObjectService.findMatching(PayeeACHAccount.class, keys);
	    
	    if(ObjectUtils.isNull(payeeACHAccountList) || payeeACHAccountList.isEmpty())
	    	return false;
	    
	    return true;
    }
	
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
