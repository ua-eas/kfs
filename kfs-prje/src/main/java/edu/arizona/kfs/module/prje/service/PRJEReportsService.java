package edu.arizona.kfs.module.prje.service;

import java.util.List;

import edu.arizona.kfs.module.prje.dataaccess.PRJETransferRecord;

public interface PRJEReportsService {
    /**
     * Will generate all of the Reports made available by this module
     * @param transferRecords
     */
    void writeReports(List<PRJETransferRecord> transferRecords);
    
    void writePostingLedger(List<PRJETransferRecord> transferRecords);
    
    void writeAuditReport(List<PRJETransferRecord> transferRecords);
}
