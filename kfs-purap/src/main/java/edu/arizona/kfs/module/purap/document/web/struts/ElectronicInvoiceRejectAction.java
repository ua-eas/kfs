package edu.arizona.kfs.module.purap.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.module.purap.PurapKeyConstants;

public class ElectronicInvoiceRejectAction extends org.kuali.kfs.module.purap.document.web.struts.ElectronicInvoiceRejectAction {

    /**
     * Deprecation: super.approve is marked as deprecated.
     */
    @SuppressWarnings("deprecation")
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ElectronicInvoiceRejectForm eirForm = (ElectronicInvoiceRejectForm) form;
        if (!eirForm.isSaved()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_EIR_APPROVAL_REQUIRES_SAVE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        return super.approve(mapping, form, request, response);
    }

    /**
     * Deprecation: super.save is marked as deprecated.
     */
    @SuppressWarnings("deprecation")
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward forward = super.save(mapping, form, request, response);
        ElectronicInvoiceRejectForm eirForm = (ElectronicInvoiceRejectForm) form;
        eirForm.markAsSaved();
        return forward;
    }

}
