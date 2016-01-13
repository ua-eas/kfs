package org.kuali.kfs.module.external.kc.businessobject.lookup;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsInvoiceLookupableHelperServiceImpl;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Properties;

public class ContractsGrantsAwardLookupableHelperServiceImpl extends ContractsGrantsInvoiceLookupableHelperServiceImpl {

    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        HtmlData.AnchorHtmlData inquiryHref = new HtmlData.AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);

        if (KFSPropertyConstants.PROPOSAL_NUMBER.equals(propertyName)) {
            Properties parameters = new Properties();
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, ContractsAndGrantsAward.class.getName());
            parameters.put(KFSPropertyConstants.PROPOSAL_NUMBER, ((Award)bo).getAwardId().toString());
            inquiryHref.setHref(UrlFactory.parameterizeUrl(KFSConstants.RICE_PATH_PREFIX + KRADConstants.INQUIRY_ACTION, parameters));
        } else {
            inquiryHref = (HtmlData.AnchorHtmlData)super.getInquiryUrl(bo, propertyName);
        }

        return inquiryHref;
    }
}
