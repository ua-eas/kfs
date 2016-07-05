package org.kuali.kfs.sys.batch;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.sys.batch.service.DetectDocumentsMissingPendingEntriesService;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DetectDocumentsMissingPendingEntriesStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetectDocumentsMissingPendingEntriesStep.class);

    protected ConfigurationService configurationService;
    protected DetectDocumentsMissingPendingEntriesService detectDocumentsMissingPendingEntriesService;
    protected int lookbackHours;

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("Starting to execute DetectDocumentsMissingPendingEntriesStep");
        List<DocumentHeaderData> allDocumentHeadersToReport = new ArrayList<>();

        allDocumentHeadersToReport.addAll(detectDocumentsMissingPendingEntriesService.discoverGeneralLedgerDocumentsWithoutPendingEntries(calculateEarliestProcessingDate(jobRunDate)));
        if (configurationService.getPropertyValueAsBoolean("module.labor.distribution.enabled")) {
            allDocumentHeadersToReport.addAll(SpringContext.getBean(LaborModuleService.class).discoverLaborLedgerDocumentsWithoutPendingEntries(calculateEarliestProcessingDate(jobRunDate)));
        }
        if (!CollectionUtils.isEmpty(allDocumentHeadersToReport)) {
            detectDocumentsMissingPendingEntriesService.reportDocumentsWithoutPendingEntries(allDocumentHeadersToReport);
        }
        return true;
    }

    protected java.util.Date calculateEarliestProcessingDate(java.util.Date runDate) {
        java.util.Calendar currentMoment = Calendar.getInstance();
        currentMoment.setTimeInMillis(runDate.getTime());
        currentMoment.add(Calendar.HOUR, lookbackHours*-1);
        return currentMoment.getTime();
    }

    public DetectDocumentsMissingPendingEntriesService getDetectDocumentsMissingPendingEntriesService() {
        return detectDocumentsMissingPendingEntriesService;
    }

    public void setDetectDocumentsMissingPendingEntriesService(DetectDocumentsMissingPendingEntriesService detectDocumentsMissingPendingEntriesService) {
        this.detectDocumentsMissingPendingEntriesService = detectDocumentsMissingPendingEntriesService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public int getLookbackHours() {
        return lookbackHours;
    }

    public void setLookbackHours(int lookbackHours) {
        this.lookbackHours = lookbackHours;
    }
}
