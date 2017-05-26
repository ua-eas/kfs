package edu.arizona.kfs.module.purap.document.web.struts;

/**
 * Overridden to hold whether the form has been saved yet or not
 */
public class ElectronicInvoiceRejectForm extends org.kuali.kfs.module.purap.document.web.struts.ElectronicInvoiceRejectForm {
    private boolean saved = false;

    /**
     * @return true if the form has been saved to the database during this session; false otherwise
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Mark this form as having been saved
     */
    public void markAsSaved() {
        this.saved = true;
    }

}
