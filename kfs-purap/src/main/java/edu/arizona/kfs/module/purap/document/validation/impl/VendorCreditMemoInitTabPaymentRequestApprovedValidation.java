package edu.arizona.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.krad.util.GlobalVariables;

import edu.arizona.kfs.module.purap.PurapKeyConstants;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;

public class VendorCreditMemoInitTabPaymentRequestApprovedValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        if (event.getDocument() instanceof VendorCreditMemoDocument) {
            VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) event.getDocument();
            PaymentRequestDocument preq = (PaymentRequestDocument) cmDocument.getPaymentRequestDocument();
            if (preq == null) {
                // the Payment Request # option on the Credit Memo initiation screen wasn't used.
                return true;
            }
            boolean isApproved = preq.getDocumentHeader().getWorkflowDocument().isApproved();
            if (!isApproved) {
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(PurapPropertyConstants.PAYMENT_REQUEST_ID, PurapKeyConstants.ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID_DOCUMENT_STATUS, cmDocument.getPaymentRequestIdentifier().toString());
                return false;
            }
        }
        // if for some reason this isn't a CM, return true automatically.
        return true;
    }
}
