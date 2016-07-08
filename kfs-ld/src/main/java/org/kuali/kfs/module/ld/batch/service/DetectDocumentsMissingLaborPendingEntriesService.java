package org.kuali.kfs.module.ld.batch.service;


import org.kuali.kfs.sys.businessobject.DocumentHeaderData;

import java.util.List;

public interface DetectDocumentsMissingLaborPendingEntriesService {
    /**
     * @param earliestProcessingDate documents searched for should have been processed on or after this moment
     * @return a List of document header data about documents which posted and are expected to have pending entries but do not
     */
    public List<DocumentHeaderData> discoverLaborLedgerDocumentsWithoutPendingEntries(java.util.Date earliestProcessingDate);
}
