package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PaymentRequestEditMode;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.module.purap.PurapAuthorizationConstants;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.document.authorization.IncomeTypeAuthorizationHelper;

public class PaymentRequestDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.PaymentRequestDocumentPresentationController {

    private static final long serialVersionUID = 4322870730864097087L;

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        editModes.add(KFSConstants.VIEW_DOCUWARE);

        // Remove ability for AP Specialist to EDIT PREQ vendor address.
        editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_VENDOR_ADDR_EDIT_MODE);

        // Add edit modes for the 1099 Information Tab
        IncomeTypeAuthorizationHelper.addIncomeTypeEditModes(document, editModes);
        
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PaymentRequestEditMode.CLEAR_ALL_TAXES);
        }

        return editModes;
    }
}
