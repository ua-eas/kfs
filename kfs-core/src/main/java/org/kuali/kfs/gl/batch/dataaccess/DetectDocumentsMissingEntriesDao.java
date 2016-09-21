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
package org.kuali.kfs.gl.batch.dataaccess;

import java.util.List;
import java.util.Optional;

import org.kuali.kfs.sys.businessobject.DocumentHeaderData;

/**
 * Interface for data access objects which will find all of the general ledger
 * documents without pending entries
 */
public interface DetectDocumentsMissingEntriesDao {
    /**
     * Find all of the general ledger documents without ledger entries which
     * have gone to final since the given start time
     *
     * @param startTime
     *            the start time to start searching through documents which may
     *            not have ledger entries
     * @param documentTypesToSearch
     *            the list of document types to search within
     * @return a List of FinancialSystemDocumentHeaders for documents missing
     *         ledger entries or an empty List if nothing is found
     */
    List<DocumentHeaderData> discoverLedgerDocumentsWithoutEntries(java.util.Date startTime,
            List<String> documentTypesToSearch);

    /**
     * Get the payment medium code from a cash control document
     *
     * @param documentNumber
     *            document number
     * @return optional payment medium code
     */
    Optional<String> getCustomerPaymentMediumCodeFromCashControlDocument(String documentNumber);
}
