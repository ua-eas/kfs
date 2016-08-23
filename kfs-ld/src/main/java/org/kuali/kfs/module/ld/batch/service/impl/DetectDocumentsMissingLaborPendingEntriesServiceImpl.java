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
package org.kuali.kfs.module.ld.batch.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.module.ld.batch.service.DetectDocumentsMissingLaborPendingEntriesService;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.dataaccess.DetectDocumentsMissingPendingEntriesDao;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Transactional
public class DetectDocumentsMissingLaborPendingEntriesServiceImpl implements DetectDocumentsMissingLaborPendingEntriesService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetectDocumentsMissingLaborPendingEntriesServiceImpl.class);

    protected DetectDocumentsMissingPendingEntriesDao detectDocumentsMissingPendingEntriesDao;
    protected ParameterService parameterService;

    @Override
    public List<DocumentHeaderData> discoverLaborLedgerDocumentsWithoutPendingEntries(Date earliestProcessingDate) {
        LOG.debug("Running discoverLaborLedgerDocumentsWithoutPendingEntries");
        return detectDocumentsMissingPendingEntriesDao.discoverLedgerDocumentsWithoutPendingEntries(earliestProcessingDate, getSearchByDocumentTypes());
    }

    protected List<String> getSearchByDocumentTypes() {
        List<String> searchByDocumentTypes = new ArrayList<>();
        final Collection<String> parameterDocumentTypesValues = parameterService.getParameterValuesAsString(KfsParameterConstants.LABOR_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.LEDGER_ENTRY_GENERATING_DOCUMENT_TYPES);
        if (!CollectionUtils.isEmpty(parameterDocumentTypesValues)) {
            searchByDocumentTypes.addAll(parameterDocumentTypesValues);
        }
        return searchByDocumentTypes;
    }

    public DetectDocumentsMissingPendingEntriesDao getDetectDocumentsMissingPendingEntriesDao() {
        return detectDocumentsMissingPendingEntriesDao;
    }

    public void setDetectDocumentsMissingPendingEntriesDao(DetectDocumentsMissingPendingEntriesDao detectDocumentsMissingPendingEntriesDao) {
        this.detectDocumentsMissingPendingEntriesDao = detectDocumentsMissingPendingEntriesDao;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
