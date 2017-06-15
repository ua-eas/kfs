package edu.arizona.kfs.module.prje.service;

import java.util.List;

import edu.arizona.kfs.module.prje.businessobject.PRJETransferRecord;

public interface PRJEReportsService {
    /**
     * Will generate all of the Reports made available by this module
     * @param transferRecords
     */
    public void writeReports(List<PRJETransferRecord> transferRecords);
    
    public void writePostingLedger(List<PRJETransferRecord> transferRecords);
    
    public void writeAuditReport(List<PRJETransferRecord> transferRecords);
}
