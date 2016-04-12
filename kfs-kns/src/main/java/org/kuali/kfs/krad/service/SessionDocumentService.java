/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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

import java.sql.Timestamp;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.web.form.DocumentFormBase;

/**
 * Service API for persisting <code>Document</code> form content and
 * retrieving back
 *
 * <p>
 * Used as an extension to session support. If a session times out, the doucment contents
 * can be retrieved from the persistence storage and work resumed
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface SessionDocumentService {

    /**
     * Retrieves a document from the user session for the given document id
     */
    public WorkflowDocument getDocumentFromSession(UserSession userSession, String docId);

    /**
     * This method places a document into the user session.
     */
    public void addDocumentToUserSession(UserSession userSession, WorkflowDocument document);
	
     /**
     * Delete DocumentFormBase from session and database.
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @throws
     */
    public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress);
    
	/**
     * Delete KualiDocumentFormBase from session and database.
     *
     * @param documentNumber
     * @throws
     */
    public void purgeAllSessionDocuments(Timestamp expirationDate);

    /**
     * This method stores a UifFormBase into session and database
     *
     * @param form
     * @param userSession
     * @param ipAddress
     */
    public void setDocumentForm(DocumentFormBase form, UserSession userSession, String ipAddress);

    /**
     * Returns DocumentFormBase object from the db
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @param ipAddress
     * @return
     */
    public DocumentFormBase getDocumentForm(String documentNumber, String docFormKey, UserSession userSession,
            String ipAddress);
}
