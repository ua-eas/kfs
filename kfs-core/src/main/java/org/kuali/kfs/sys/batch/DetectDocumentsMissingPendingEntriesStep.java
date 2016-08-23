/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
