package edu.arizona.kfs.module.purap.document.validation.impl;

import org.kuali.rice.krad.document.Document;

public class ElectronicInvoiceRejectDocumentRule extends org.kuali.kfs.module.purap.document.validation.impl.ElectronicInvoiceRejectDocumentRule {

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        processBusinessRules(document);
        return true;
    }

}
