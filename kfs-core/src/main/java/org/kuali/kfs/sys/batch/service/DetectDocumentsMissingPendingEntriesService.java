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
package org.kuali.kfs.sys.batch.service;

import org.kuali.kfs.sys.businessobject.DocumentHeaderData;

import java.util.List;

/**
 * Business logic which helps find documents which posted recently and are expected to have pending entries but do not
 */
public interface DetectDocumentsMissingPendingEntriesService {
    /**
     * @param earliestProcessingDate documents searched for should have been processed on or after this moment
     * @return a List of document header data about documents which posted and are expected to have pending entries but do not
     */
    public List<DocumentHeaderData> discoverGeneralLedgerDocumentsWithoutPendingEntries(java.util.Date earliestProcessingDate);

    /**
     * Reports (either through e-mail or logging) about documents which are missing pending ledger entries
     * @param documentHeaders the documents which are missing expected pending ledger entries
     */
    public void reportDocumentsWithoutPendingEntries(List<DocumentHeaderData> documentHeaders);
}
