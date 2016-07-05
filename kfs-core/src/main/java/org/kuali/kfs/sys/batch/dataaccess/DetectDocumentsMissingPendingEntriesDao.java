package org.kuali.kfs.sys.batch.dataaccess;

import org.kuali.kfs.sys.businessobject.DocumentHeaderData;

import java.util.List;

/**
 * Interface for data access objects which will find all of the general ledger documents without pending entries
 */
public interface DetectDocumentsMissingPendingEntriesDao {
    /**
     * ind all of the general ledger documents without pending entries which have gone to final since the given start time
     * @param startTime the start time to start searching through documents which may not have pending ledger entries
     * @param documentTypesToSearch the list of document types to search within
     * @return a List of FinancialSystemDocumentHeaders for documents missing pending ledger entries or an empty List if nothing is found
     */
    public List<DocumentHeaderData> discoverLedgerDocumentsWithoutPendingEntries(java.util.Date startTime, List<String> documentTypesToSearch);
}
