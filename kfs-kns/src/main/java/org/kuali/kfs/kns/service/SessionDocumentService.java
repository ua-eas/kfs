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
package org.kuali.kfs.kns.service;

import org.kuali.kfs.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.kfs.krad.UserSession;

import java.sql.Timestamp;


@Deprecated
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
     * Delete KualiDocumentFormBase from session and database.
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @throws
     */
    public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress);

    /**
     * Delete KualiDocumentFormBases from database.
     *
     * @param documentNumber
     * @throws
     */
    public void purgeAllSessionDocuments(Timestamp expirationDate);

    /**
     * Returns KualiDocumentFormBase object. It will check userSession first, if it failed then check database
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @return KualiDocumentFormBase
     * @throws
     */
    public KualiDocumentFormBase getDocumentForm(String documentNumber, String docFormKey, UserSession userSession,
            String ipAddress);

    /**
     * Store KualiDocumentFormBase into session and database.
     *
     * @param KualiDocumentFormBase
     * @param userSession
     * @throws
     */
    public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession, String ipAddress);
}
