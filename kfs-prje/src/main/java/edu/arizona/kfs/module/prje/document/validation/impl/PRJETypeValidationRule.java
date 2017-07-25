package edu.arizona.kfs.module.prje.document.validation.impl;

import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.krad.document.Document;

import edu.arizona.kfs.module.prje.PRJEConstants;
import edu.arizona.kfs.module.prje.PRJEKeyConstants;
import edu.arizona.kfs.module.prje.PRJEPropertyConstants;
import edu.arizona.kfs.module.prje.businessobject.PRJEBaseAccount;

public class PRJETypeValidationRule extends MaintenanceDocumentRuleBase {

    @Override
    public boolean processSaveDocument(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument)document;
        PRJEBaseAccount account = (PRJEBaseAccount )maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        return isTypeValid(account) && super.processSaveDocument(document);
    }
    
    
    public boolean isTypeValid(PRJEBaseAccount  account){
        if(PRJEConstants.PRJE_AMOUNT.equals(account.getProrateType())){
           
            if(account.getProrateAmount() == null || !account.getProrateAmount().isPositive()){
                putFieldError(PRJEPropertyConstants.PRORATE_AMOUNT, PRJEKeyConstants.PRJE_AMOUNT_TYPE_NO_VALUE);
                return false;
            }
            else if(account.getProratePercent() != null){
                putFieldError(PRJEPropertyConstants.PRORATE_PCT, PRJEKeyConstants.PRJE_PERCENT_TYPE_UNNECESSARY);
                return false;
            }
        }
        else {
         
            if(account.getProratePercent() == null || !account.getProratePercent().isPositive()){
                putFieldError(PRJEPropertyConstants.PRORATE_PCT, PRJEKeyConstants.PRJE_PERCENT_TYPE_NO_VALUE);
                return false;
            }
            else if(account.getProrateAmount() != null){
                putFieldError(PRJEPropertyConstants.PRORATE_AMOUNT, PRJEKeyConstants.PRJE_AMOUNT_TYPE_UNNECESSARY);
                return false;
            }
        }
        return true;
        
    }
}
