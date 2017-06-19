package edu.arizona.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import edu.arizona.kfs.sys.KFSKeyConstants;

public class ElectronicInvoiceRejectDocumentRule extends org.kuali.kfs.module.purap.document.validation.impl.ElectronicInvoiceRejectDocumentRule {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceRejectDocumentRule.class);

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if (GlobalVariables.getMessageMap().hasErrors()) {
            MessageMap errorMap = GlobalVariables.getMessageMap();
            if (errorMap.containsMessageKey(KFSKeyConstants.ERROR_MAX_LENGTH)) {
                return false;
            }
        }
        boolean isValid = processBusinessRules(document);
        return true; // we always want to return true to allow a save unless there is something that would prevent the save.
    }

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
        }

        return isValid;
    }

}
