package edu.arizona.kfs.module.tax.businessobject.inquiry;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.inquiry.KualiInquirableImpl;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.BusinessObject;

import edu.arizona.kfs.module.tax.TaxPropertyConstants;

@SuppressWarnings("deprecation")
// TODO UA dev: org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl is gone. Replaced parent class by KualiInquirableImpl 
public class PaymentInquirable extends KualiInquirableImpl {
    private static final long serialVersionUID = 7996535011188449247L;

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (TaxPropertyConstants.PaymentFields.DOCUMENT_NUMBER.equals(attributeName)) {
            String documentNumber = (String) ObjectUtils.getPropertyValue(businessObject, attributeName);
            if (StringUtils.isNotBlank(documentNumber)) {
                boolean exists = SpringContext.getBean(DocumentService.class).documentExists(documentNumber);
                if (!exists) {
                    return new AnchorHtmlData();
                }
            }
            AnchorHtmlData hRef = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);
            hRef.setHref(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.WORKFLOW_URL_KEY) + KRADConstants.DOCHANDLER_DO_URL + documentNumber + KRADConstants.DOCHANDLER_URL_CHUNK);
            return hRef;
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}
