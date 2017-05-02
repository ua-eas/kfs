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
package org.kuali.kfs.gl.batch.service;

import java.util.List;

import org.kuali.kfs.sys.businessobject.DocumentHeaderData;

/**
 * Business logic which helps find documents which posted recently and are
 * expected to have GL entries but do not
 */
public interface DetectDocumentsMissingEntriesService {
    /**
     * @return a List of document header data about documents which posted and
     *         are expected to have GL entries but do not
     */
    public List<DocumentHeaderData> discoverGeneralLedgerDocumentsWithoutEntries();

    /**
     * Reports (either through e-mail or logging) about documents which are
     * missing GL entries
     *
     * @param documentHeaders
     *            the documents which are missing expected GL entries
     */
    public void reportDocumentsWithoutEntries(List<DocumentHeaderData> documentHeaders);
}
