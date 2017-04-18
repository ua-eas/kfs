/*
 * Copyright 2014 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kfs.module.prje.document.validation.impl;

import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.module.prje.PRJEConstants;
import edu.arizona.kfs.module.prje.PRJEKeyConstants;
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
                putFieldError("prorateAmount", PRJEKeyConstants.PRJE_AMOUNT_TYPE_NO_VALUE);
                return false;
            }
            else if(account.getProratePercent() != null){
                putFieldError("proratePercent", PRJEKeyConstants.PRJE_PERCENT_TYPE_UNNECESSARY);
                return false;
            }
        }
        else {
         
            if(account.getProratePercent() == null || !account.getProratePercent().isPositive()){
                putFieldError("proratePercent", PRJEKeyConstants.PRJE_PERCENT_TYPE_NO_VALUE);
                return false;
            }
            else if(account.getProrateAmount() != null){
                putFieldError("prorateAmount", PRJEKeyConstants.PRJE_AMOUNT_TYPE_UNNECESSARY);
                return false;
            }
        }
        return true;
        
    }
}
