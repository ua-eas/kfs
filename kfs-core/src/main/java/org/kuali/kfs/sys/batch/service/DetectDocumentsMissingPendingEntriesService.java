package org.kuali.kfs.sys.batch.service;

import org.kuali.kfs.sys.businessobject.DocumentHeaderData;

import java.util.List;

/**
 * Business logic which helps find documents which posted recently and are expected to have pending entries but do not
 */
public interface DetectDocumentsMissingPendingEntriesService {
    /**
     * @param earliestProcessingDate documents searched for should have been processed on or after this moment
     * @return a List of document header data about documents which posted and are expected to have pending entries but do not
     */
    public List<DocumentHeaderData> discoverGeneralLedgerDocumentsWithoutPendingEntries(java.util.Date earliestProcessingDate);

    /**
     * Reports (either through e-mail or logging) about documents which are missing pending ledger entries
     * @param documentHeaders the documents which are missing expected pending ledger entries
     */
    public void reportDocumentsWithoutPendingEntries(List<DocumentHeaderData> documentHeaders);
}
