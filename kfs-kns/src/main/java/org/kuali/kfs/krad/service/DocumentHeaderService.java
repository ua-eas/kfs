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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.bo.DocumentHeader;

/**
 * This is an interface to allow for Rice client applications to override the
 * DocumentHeader class being used. Originally written to facilitate the Kuali
 * Financial System custom document header which included a 'total amount'
 * field.
 */
public interface DocumentHeaderService {

    /**
     * This method returns the class to use to instantiate document header objects
     *
     * @return the class to be used for new document header objects
     */
    public Class<? extends DocumentHeader> getDocumentHeaderBaseClass();

    /**
     * This method retrieves a document header using the given documentHeaderId
     *
     * @param documentHeaderId - the id of the document to retrieve the document header for
     * @return the document header associated with the given document header id
     */
    public DocumentHeader getDocumentHeaderById(String documentHeaderId);


    /**
     * This method saves a document header object
     *
     * @param documentHeader - the document header object to save
     */
    public void saveDocumentHeader(DocumentHeader documentHeader);

    /**
     * This method deletes a document header object
     *
     * @param documentHeader - the document header to be removed
     */
    public void deleteDocumentHeader(DocumentHeader documentHeader);

}
