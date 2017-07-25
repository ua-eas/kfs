package edu.arizona.kfs.module.cam.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.coreservice.framework.parameter.ParameterConstants.NAMESPACE;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cam.CamsConstants;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;


@NAMESPACE(namespace = KfsParameterConstants.PURCHASING_NAMESPACE)
public class PurchasingAccountsPayableDocument extends org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableDocument {
	
    @Override
    public String getStatusDescription() {

        if (StringUtils.isNotBlank(this.statusDescription)) {
            return this.statusDescription;
        }
        else {
            Map<String, Integer> objectKeys = new HashMap<String, Integer>();
            objectKeys.put(CamsPropertyConstants.PurchasingAccountsPayableDocument.PURAP_DOCUMENT_IDENTIFIER, this.getPurapDocumentIdentifier());

            if (CamsConstants.PREQ.equals(getDocumentTypeCode()) || CamsConstants.PRNC.equals(getDocumentTypeCode())) {

                PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PaymentRequestDocument.class, objectKeys);
                if (ObjectUtils.isNotNull(paymentRequestDocument)) {
                    statusDescription = paymentRequestDocument.getApplicationDocumentStatus();
                }
            }
            else {
                VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(VendorCreditMemoDocument.class, objectKeys);
                if (ObjectUtils.isNotNull(vendorCreditMemoDocument)) {
                    statusDescription = vendorCreditMemoDocument.getApplicationDocumentStatus();
                }
            }
        }

        return statusDescription;
    }
}
