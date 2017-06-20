package edu.arizona.kfs.fp.batch.service;

import edu.arizona.kfs.fp.businessobject.BankTransaction;
import org.kuali.rice.krad.document.Document;

/**
 * Created by nataliac on 5/3/17.
 */
public interface BankDocumentService {

    /**
     * Creates and returns the right type of Document - CC, AD, DI to be created for the given bankTransaction
     * Used in the Document Creation from Bank Transaction File batch job
     * @param bankTransaction
     * @return Document
     */
    public Document getDocument(BankTransaction bankTransaction);


    /**
     * Creates document Notes, saves the doc and blanketApproves it.
     * @param Document
     * @return
     */
    public void blanketApproveBankDocument(Document bankDocument);
}
