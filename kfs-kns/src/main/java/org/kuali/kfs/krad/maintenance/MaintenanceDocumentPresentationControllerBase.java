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
package org.kuali.kfs.krad.maintenance;

import org.kuali.kfs.krad.document.DocumentPresentationControllerBase;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceDocumentPresentationControllerBase extends DocumentPresentationControllerBase
        implements MaintenanceDocumentPresentationController {
    private static final long serialVersionUID = 2849921477944820474L;

    private transient DocumentDictionaryService documentDictionaryService;

    public boolean canCreate(Class boClass) {
        return getDocumentDictionaryService().getAllowsNewOrCopy(
                getDocumentDictionaryService().getMaintenanceDocumentTypeName(boClass));
    }

    public boolean canMaintain(Object dataObject) {
        return true;
    }

    protected DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
