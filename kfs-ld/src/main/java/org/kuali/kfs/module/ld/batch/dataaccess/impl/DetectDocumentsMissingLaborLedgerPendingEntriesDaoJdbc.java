package org.kuali.kfs.module.ld.batch.dataaccess.impl;

import org.kuali.kfs.sys.batch.dataaccess.impl.DetectDocumentsMissingPendingEntriesDaoJdbc;

public class DetectDocumentsMissingLaborLedgerPendingEntriesDaoJdbc extends DetectDocumentsMissingPendingEntriesDaoJdbc {
    @Override
    protected String getPendingEntryTableName() {
        return "LD_PND_LDGR_ENTR_T";
    }
}
