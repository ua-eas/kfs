package edu.arizona.kfs.module.prje.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.service.ReportWriterService;

import edu.arizona.kfs.module.prje.PRJEConstants;
import edu.arizona.kfs.module.prje.businessobject.PRJEAccountLine;
import edu.arizona.kfs.module.prje.businessobject.PRJEAuditItem;
import edu.arizona.kfs.module.prje.businessobject.PRJEBaseAccount;
import edu.arizona.kfs.module.prje.businessobject.PRJEBaseObject;
import edu.arizona.kfs.module.prje.businessobject.PRJEType;
import edu.arizona.kfs.module.prje.businessobject.PRJETransferRecord;
import edu.arizona.kfs.module.prje.service.PRJEReportsService;
import edu.arizona.kfs.module.prje.service.PRJEServiceBaseImpl;
import edu.arizona.kfs.sys.KFSConstants;

public class PRJEReportsServiceImpl extends PRJEServiceBaseImpl implements PRJEReportsService {
    private static Logger LOG = Logger.getLogger(PRJEReportsServiceImpl.class);
        
    private ReportWriterService ledgerReportWriterService;
    private ReportWriterService auditReportWriterService;
    
    public void writeReports(List<PRJETransferRecord> transferRecords) {
        writePostingLedger(transferRecords);
        writeAuditReport(transferRecords);
    }
    
    public void writePostingLedger(List<PRJETransferRecord> transferRecords) {
        LOG.info("Writing Posting Ledger Report");
        try {
            ReportWriterService reportWriterService = getLedgerReportWriterService();
            
            if ( reportWriterService instanceof WrappingBatchService ) {
                ((WrappingBatchService)reportWriterService).initialize();
            }
            
            // Use the Ledger Summary Report class to produce the report
            
            LedgerSummaryReport ledgerReport = new LedgerSummaryReport();
            
            for ( PRJETransferRecord transferRecord : transferRecords ) {
                ledgerReport.summarizeEntry(transferRecord.getDebitEntry());
                ledgerReport.summarizeEntry(transferRecord.getCreditEntry());
            }
            
            ledgerReport.writeReport(reportWriterService);
            
            if ( reportWriterService instanceof WrappingBatchService ) {
                ((WrappingBatchService)reportWriterService).destroy();
            }
        }
        // need to prevent the exception from being swallowed
        catch ( RuntimeException e ) {
            LOG.error("Error running writePostingLedger", e);
            throw e;
        }
    }
    
    public void writeAuditReport(List<PRJETransferRecord> transferRecords) {
        LOG.info("Writing Audit Report");
        try {
            ReportWriterService reportWriterService = getAuditReportWriterService();
            if ( reportWriterService instanceof WrappingBatchService ) {
                ((WrappingBatchService)reportWriterService).initialize();
            }

            PRJEType lastType = null;
            List<PRJETransferRecord> group = new ArrayList<PRJETransferRecord>();
            
            for ( PRJETransferRecord transferRecord : transferRecords ) {
                PRJEType type = transferRecord.getBaseAccount().getType();
                if (lastType != null && type.getTypeId() == lastType.getTypeId() && group.size() > 0 ) {
                    writeAuditReportGroup(group);
                    group.clear();
                }
                
                group.add(transferRecord);
                lastType = type;
            }
            
            if ( group.size() > 0 ) {
                writeAuditReportGroup(group);
            }
            
            if ( reportWriterService instanceof WrappingBatchService ) {
                ((WrappingBatchService)reportWriterService).destroy();
            }
        }
        // need to prevent the exception from being swallowed
        catch ( RuntimeException e ) {
            LOG.error("Error running writeAuditReport", e);
            throw e;
        }
    }
    
    private void writeAuditReportGroup(List<PRJETransferRecord> transferRecords) {
        ReportWriterService reportWriterService = getAuditReportWriterService();
        PRJEType type = transferRecords.get(0).getBaseAccount().getType();
        
        // Write the Audit Entries
        List<PRJEAuditItem> auditItems = generateAuditItems(transferRecords);
        reportWriterService.writeTable(auditItems, true, false);
        
        reportWriterService.writeNewLines(3);
    }
    
    private void writeAuditReportHeader(PRJEType type) {
        ReportWriterService reportWriterService = getAuditReportWriterService();

        reportWriterService.writeFormattedMessageLine("%35s: %-62s %35s: %-62s", 
                "Prorate Type Name", type.getEntryName(), 
                "Prorate Cardinality", getProrateOptionsString(type.getProrateOptions()));

        for ( PRJEBaseAccount baseAccount : type.getBaseAccounts() ) {
            writeAuditReportBaseAccount(baseAccount);
        }
        
        reportWriterService.writeFormattedMessageLine("%35s: %-165s", "Base Object Code Ranges", getObjectCodeRanges(type));
        
        reportWriterService.writeNewLines(1);
                
        reportWriterService.writeFormattedMessageLine("Generated Transactions for PRJE Type %s (* Indicates Override)", type.getEntryName());
        
        reportWriterService.writeNewLines(1);
    }
    
    private void writeAuditReportBaseAccount(PRJEBaseAccount baseAccount) {
        ReportWriterService reportWriterService = getAuditReportWriterService();

        reportWriterService.writeFormattedMessageLine("%35s: %-62s %35s: %-62s",
                "Prorate Percent or Amount", getRateOrAmount(baseAccount),
                "Prorate Source Frequency", getFrequencyString(baseAccount.getFrequency()));

        reportWriterService.writeFormattedMessageLine("%35s: %-62s %35s: %-62s",
                "Base Account Nbr", formatAccount(baseAccount.getBaseAccount()),
                "From Account Number", formatAccount(baseAccount.getFromAccount()));

        reportWriterService.writeFormattedMessageLine("%35s: %-62s %35s: %-62s",
                "Base Sub-Account Nbr", formatSubAccount(baseAccount.getBaseSubAccount()),
                "From Sub-Account Number", formatSubAccount(baseAccount.getFromSubAccount()));
    }
    
    private String formatAccount(String account) {
        if ( account != null ) {
            return account;
        }
        else {
            return KFSConstants.BLANK_ACCOUNT;
        }
    }
    
    private String formatSubAccount(String subAccount) {
        if ( subAccount != null ) {
            return subAccount;
        }
        else {
            return KFSConstants.BLANK_SUBACCOUNT;
        }
    }
    
    private String getRateOrAmount(PRJEBaseAccount baseAccount) {
        if ( PRJEConstants.ProrateDebitType.AMOUNT.getKey().equals(baseAccount.getProrateType()) ) {
            return baseAccount.getProrateAmount().toString();
        }
        else {
            return baseAccount.getProratePercent() + KFSConstants.PERCENTAGE_SIGN;
        }
    }
    
    private String getProrateOptionsString(String prorateOption) {
        PRJEConstants.ProrateOptions[] options = PRJEConstants.ProrateOptions.values();
        for ( int i = 0; i < options.length; i++ ) {
            PRJEConstants.ProrateOptions option = options[i];
            if ( option.getKey().equals(prorateOption) ) {
                return option.getLabel();
            }
        }
        return "<Unknown Prorate Option>";
    }
    
    private String getFrequencyString(String frequency) {
        PRJEConstants.Frequency[] freqs = PRJEConstants.Frequency.values();
        for ( int i = 0; i < freqs.length; i++ ) {
            PRJEConstants.Frequency freq = freqs[i];
            if ( freq.getKey().equals(frequency) ) {
                return freq.getLabel();
            }
        }
        return "<Unknown Frequency>";
    }
    
    private String getObjectCodeRanges(PRJEType type) {
        StringBuffer sb = new StringBuffer();
        
        List<PRJEBaseObject> baseObjects = type.getBaseObjects();
        for ( PRJEBaseObject baseObject : baseObjects ) {
            if ( sb.length() > 0 ) {
                sb.append(", ");
            }
            
            sb.append(baseObject.getObjectCodeRangeName());
        }
        
        return sb.toString();
    }
    
    private String generateDescription(PRJETransferRecord transferRecord) {
        PRJEBaseAccount baseAccount = transferRecord.getBaseAccount();
        PRJEAccountLine accountLine = transferRecord.getAccountLine();
        PRJEType type = baseAccount.getType();
        
        StringBuffer description = new StringBuffer();
        description.append(type.getEntryName());

        if ( PRJEConstants.ProrateDebitType.PERCENTAGE.getKey().equals(accountLine.getOverrideProrateType()) ) {
            description.append(" ");
            description.append(accountLine.getOverridePercent());
            description.append("pct of ");
            description.append(transferRecord.getBalance());
        }            
        else if ( PRJEConstants.ProrateDebitType.PERCENTAGE.getKey().equals(baseAccount.getProrateType()) ) {
            description.append(" ");
            description.append(baseAccount.getProratePercent());
            description.append("pct of ");
            description.append(transferRecord.getBalance());
        }
        
        return description.toString();
    }
    
    private List<PRJEAuditItem> generateAuditItems(List<PRJETransferRecord> transferRecords) {
        List<PRJEAuditItem> items = new ArrayList<PRJEAuditItem>();
        for ( PRJETransferRecord transferRecord : transferRecords ) {
            PRJEAuditItem auditItem = new PRJEAuditItem(transferRecord);
            auditItem.setDescription(generateDescription(transferRecord));
            items.add(auditItem);
        }
        return items;
    }
    
    public ReportWriterService getLedgerReportWriterService() {
        return ledgerReportWriterService;
    }

    public void setLedgerReportWriterService(ReportWriterService ledgerReportWriterService) {
        this.ledgerReportWriterService = ledgerReportWriterService;
    }

    public ReportWriterService getAuditReportWriterService() {
        return auditReportWriterService;
    }

    public void setAuditReportWriterService(ReportWriterService auditReportWriterService) {
        this.auditReportWriterService = auditReportWriterService;
    }
}
