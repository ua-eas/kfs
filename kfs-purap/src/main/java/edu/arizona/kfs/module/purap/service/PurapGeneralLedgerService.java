package edu.arizona.kfs.module.purap.service;

import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;

public interface PurapGeneralLedgerService extends org.kuali.kfs.module.purap.service.PurapGeneralLedgerService {

	/**
	 * Generates general ledger pending entries for the modification of a
	 * Payment Request. No entries will be created if the calculated change is
	 * zero (meaning no change was made). Also, no encumbrance entries will be
	 * created.
	 * 
	 * @param preq PaymentRequestDocument which holds the accounts to create the entries
	 */
	public void generateEntriesModifyPaymentRequest(PaymentRequestDocument paymentRequestDocument, String currentNode);
}
