/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.gl.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.gl.batch.service.DetectDocumentsMissingEntriesService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;

public class DetectDocumentsMissingEntriesStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(DetectDocumentsMissingEntriesStep.class);

    protected DetectDocumentsMissingEntriesService detectDocumentsMissingEntriesService;

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("Starting to execute DetectDocumentsMissingEntriesStep");
        List<DocumentHeaderData> allDocumentHeadersToReport = new ArrayList<>();

        allDocumentHeadersToReport
                .addAll(detectDocumentsMissingEntriesService.discoverGeneralLedgerDocumentsWithoutEntries());
        if (!CollectionUtils.isEmpty(allDocumentHeadersToReport)) {
            detectDocumentsMissingEntriesService.reportDocumentsWithoutEntries(allDocumentHeadersToReport);
        }
        return true;
    }

    public DetectDocumentsMissingEntriesService getDetectDocumentsMissingEntriesService() {
        return detectDocumentsMissingEntriesService;
    }

    public void setDetectDocumentsMissingEntriesService(
            DetectDocumentsMissingEntriesService detectDocumentsMissingEntriesService) {
        this.detectDocumentsMissingEntriesService = detectDocumentsMissingEntriesService;
    }

}
