package org.kuali.kfs.sys.batch.dataaccess.impl;

public class DetectDocumentsMissingGeneralLedgerPendingEntriesDaoJdbc extends DetectDocumentsMissingPendingEntriesDaoJdbc {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetectDocumentsMissingGeneralLedgerPendingEntriesDaoJdbc.class);

    protected String getPendingEntryTableName() {
        return "gl_pending_entry_t";
    }
}
