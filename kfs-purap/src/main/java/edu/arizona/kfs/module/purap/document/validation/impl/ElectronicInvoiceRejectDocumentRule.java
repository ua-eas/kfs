package edu.arizona.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class ElectronicInvoiceRejectDocumentRule extends org.kuali.kfs.module.purap.document.validation.impl.ElectronicInvoiceRejectDocumentRule {

    @Override
    protected boolean processBusinessRules(Document document) {
        boolean isValid = true;

        ElectronicInvoiceRejectDocument eirDocument = (ElectronicInvoiceRejectDocument) document;

        // check to see if the document is being researched
        if (eirDocument.isInvoiceResearchIndicator()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapConstants.REJECT_DOCUMENT_RESEARCH_INCOMPETE);
            isValid = false;
        }

        if (!eirDocument.isDocumentCreationInProgress()) {
            isValid = isValid && SpringContext.getBean(ElectronicInvoiceHelperService.class).doMatchingProcess(eirDocument);
            if (isValid) {
                SpringContext.getBean(ElectronicInvoiceHelperService.class).createPaymentRequest(eirDocument);
            }
        }

        return isValid;
    }
}
