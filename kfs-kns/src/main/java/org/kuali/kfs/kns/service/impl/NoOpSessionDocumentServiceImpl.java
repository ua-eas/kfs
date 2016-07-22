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
package org.kuali.kfs.kns.service.impl;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.kfs.kns.service.SessionDocumentService;
import org.kuali.kfs.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.krad.UserSession;

import java.sql.Timestamp;

/**
 * A {@link SessionDocumentService} implementation which does nothing.
 *
 * 
 */
public class NoOpSessionDocumentServiceImpl implements SessionDocumentService{

    @Override
    public WorkflowDocument getDocumentFromSession(UserSession userSession, String docId) {
        return null;
    }

    @Override
    public void addDocumentToUserSession(UserSession userSession, WorkflowDocument document) {

    }

    @Override
    public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress) {

    }

    @Override
    public void purgeAllSessionDocuments(Timestamp expirationDate) {

    }

    @Override
    public KualiDocumentFormBase getDocumentForm(String documentNumber, String docFormKey, UserSession userSession,
            String ipAddress) {
        return null;
    }

    @Override
    public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession, String ipAddress) {

    }
}
