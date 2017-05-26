package edu.arizona.kfs.module.purap.document.validation.impl;

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import edu.arizona.kfs.sys.KFSKeyConstants;

public class ElectronicInvoiceRejectDocumentRule extends org.kuali.kfs.module.purap.document.validation.impl.ElectronicInvoiceRejectDocumentRule {

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if (GlobalVariables.getMessageMap().hasErrors()) {
            MessageMap errorMap = GlobalVariables.getMessageMap();
            if (errorMap.containsMessageKey(KFSKeyConstants.ERROR_MAX_LENGTH)) {
                return false;
            }
        }
        processBusinessRules(document);
        return true;
    }

}
