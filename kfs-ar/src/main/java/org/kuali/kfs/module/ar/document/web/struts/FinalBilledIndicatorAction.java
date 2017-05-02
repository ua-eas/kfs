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
package org.kuali.kfs.module.ar.document.web.struts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.kfs.module.ar.businessobject.FinalBilledIndicatorEntry;
import org.kuali.kfs.module.ar.document.FinalBilledIndicatorDocument;
import org.kuali.kfs.module.ar.document.validation.impl.FinalBilledIndicatorValidation;
import org.kuali.kfs.sys.KFSConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action class for Final Billed Indicator Document.
 */
public class FinalBilledIndicatorAction extends KualiTransactionalDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinalBilledIndicatorAction.class);

    /**
     * This method adds an entry to the list of entries.
     *
     * @param mapping
     * @param actionForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addInvoiceEntry(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FinalBilledIndicatorForm form = (FinalBilledIndicatorForm) actionForm;
        FinalBilledIndicatorDocument document = form.getFinalBilledIndicatorDocument();
        FinalBilledIndicatorEntry newUnfinalizeInvoiceEntry = form.getInvoiceEntry();
        if (FinalBilledIndicatorValidation.validateEntry(newUnfinalizeInvoiceEntry)) {
            document.addInvoiceEntry(newUnfinalizeInvoiceEntry);
            form.setInvoiceEntry(new FinalBilledIndicatorEntry());
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method removes an entry to the list of entries.
     *
     * @param mapping
     * @param actionForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteInvoiceEntry(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FinalBilledIndicatorForm form = (FinalBilledIndicatorForm) actionForm;
        FinalBilledIndicatorDocument document = form.getFinalBilledIndicatorDocument();
        int deleteIndex = getLineToDelete(request);
        document.removeInvoiceEntry(deleteIndex);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#doProcessingAfterPost(org.kuali.rice.kns.web.struts.form.KualiForm,
     * javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void doProcessingAfterPost(KualiForm actionForm, HttpServletRequest request) {
        FinalBilledIndicatorForm form = (FinalBilledIndicatorForm) actionForm;
        FinalBilledIndicatorDocument document = form.getFinalBilledIndicatorDocument();
        FinalBilledIndicatorValidation.validateDocument(document);
        super.doProcessingAfterPost(actionForm, request);
    }

}
