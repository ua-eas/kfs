package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.RequisitionEditMode;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.module.purap.PurapAuthorizationConstants;
import edu.arizona.kfs.module.purap.PurapConstants;

public class RequisitionDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.RequisitionDocumentPresentationController {
    private static final String LOCK_TO_RECEIVING_ADDRESS = "lockToReceivingAddress";

    @Override
    public boolean canEdit(Document document) {
        RequisitionDocument reqDocument = (RequisitionDocument) document;
        if (RequisitionStatuses.APPDOC_AWAIT_FISCAL_REVIEW.equals(reqDocument.getApplicationDocumentStatus())
            || RequisitionStatuses.APPDOC_AWAIT_CHART_REVIEW.equals(reqDocument.getApplicationDocumentStatus())
            || RequisitionStatuses.APPDOC_AWAIT_SUB_ACCT_REVIEW.equals(reqDocument.getApplicationDocumentStatus())
            || RequisitionStatuses.APPDOC_AWAIT_OBJECT_SUB_TYPE_CODE_REVIEW.equals(reqDocument.getApplicationDocumentStatus())) {
            return true;
        }
        return super.canEdit(document);
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        
        RequisitionDocument reqDocument = (RequisitionDocument)document;
        if (reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_ORG_REVIEW) || reqDocument.isDocumentStoppedInRouteNode(RequisitionStatuses.NODE_OBJECT_SUB_TYPE_CODE)) {
            editModes.add(PurapAuthorizationConstants.RequisitionEditMode.RESTRICT_FISCAL_ENTRY);
        }
    
        if (!canEditReceivingAddress((RequisitionDocument) document)) {
            editModes.add(LOCK_TO_RECEIVING_ADDRESS);
            // setting the receving address lock-down here as well, since
            // the point of locking this down is that it also needs to be
            // set to true and switched to the default receiving address
            ((RequisitionDocument) document).loadReceivingAddress();
            ((RequisitionDocument) document).setAddressToVendorIndicator(true);
        }
     // allow capital asset tab to be editable based on requisition status
        String reqDocumentStatus = ((RequisitionDocument) document).getApplicationDocumentStatus();
        if (RequisitionStatuses.APPDOC_AWAIT_FISCAL_REVIEW.equals(reqDocumentStatus)
        	    || RequisitionStatuses.APPDOC_AWAIT_OBJECT_SUB_TYPE_CODE_REVIEW.equals(reqDocumentStatus)
        	    || RequisitionStatuses.APPDOC_AWAIT_HAS_ACCOUNTING_LINES.equals(reqDocumentStatus)
                || RequisitionStatuses.APPDOC_IN_PROCESS.equals(reqDocumentStatus))
        {
            editModes.add(RequisitionEditMode.ALLOW_CAPITAL_ASSET_EDITS);
        }
        return editModes;
    }

    /**
     * Receiving address will be locked down if a B2B order to a campus matching
     * a system parameter is used.
     */
    protected boolean canEditReceivingAddress(RequisitionDocument reqn) {
        // only need to check if B2B
        if (StringUtils.equals(reqn.getRequisitionSourceCode(), PurapConstants.RequisitionSources.B2B)) {
            // check the route code on the delivery address to the parameter
            // if it matches, then it is a route Code which is allowed to set their
            // delivery address
            
            if(StringUtils.isBlank(reqn.getRouteCode())) {
                return true;
            }
            
            ParameterEvaluator param = SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(PurchaseOrderDocument.class, PurapConstants.B2B_DIRECT_SHIP_ROUTE_CODES_PARM, reqn.getRouteCode());
            
            return param.evaluationSucceeds();
        }
        return true;
    }
}
