package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PaymentRequestEditMode;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.document.authorization.IncomeTypeAuthorizationHelper;

public class VendorCreditMemoDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.VendorCreditMemoDocumentPresentationController {
    private static final long serialVersionUID = 8615870275897942790L;

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        editModes.add(KFSConstants.VIEW_DOCUWARE);

        IncomeTypeAuthorizationHelper.addIncomeTypeEditModes(document, editModes);
        
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PaymentRequestEditMode.CLEAR_ALL_TAXES);
        }

        return editModes;
    }
}
