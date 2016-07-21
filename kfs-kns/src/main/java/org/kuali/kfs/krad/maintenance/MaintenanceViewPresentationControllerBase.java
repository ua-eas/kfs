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

import org.kuali.kfs.krad.uif.view.MaintenanceView;
import org.kuali.kfs.krad.uif.view.ViewPresentationController;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.DocumentViewPresentationControllerBase;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

/**
 * Implementation of {@link ViewPresentationController} for
 * {@link MaintenanceView} instances
 *
 * 
 */
public class MaintenanceViewPresentationControllerBase extends DocumentViewPresentationControllerBase {

    public boolean canCreate(Class<?> dataObjectClass) {
        return KRADServiceLocatorWeb.getDocumentDictionaryService().getAllowsNewOrCopy(
                KRADServiceLocatorWeb.getDocumentDictionaryService().getMaintenanceDocumentTypeName(dataObjectClass))
                .booleanValue();
    }

    @Override
    public boolean canSave(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return (!workflowDocument.isEnroute() && super.canSave(document));
    }

    @Override
    public boolean canBlanketApprove(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return (!workflowDocument.isEnroute() && super.canBlanketApprove(document));
    }
}
