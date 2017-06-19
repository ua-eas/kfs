package edu.arizona.kfs.module.purap.document.web.struts;

public class ElectronicInvoiceRejectForm extends org.kuali.kfs.module.purap.document.web.struts.ElectronicInvoiceRejectForm {

    private static final long serialVersionUID = -4060784389124079993L;
    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    public void markAsSaved() {
        this.saved = true;
    }

}
