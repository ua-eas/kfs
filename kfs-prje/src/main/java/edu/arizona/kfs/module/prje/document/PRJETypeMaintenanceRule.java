/*
 * Copyright 2009 The Kuali Foundation.
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
package edu.arizona.kfs.module.prje.document;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.module.prje.PRJEConstants.Containment;
import edu.arizona.kfs.module.prje.PRJEConstants.ProrateCreditType;
import edu.arizona.kfs.module.prje.PRJEConstants.ProrateDebitType;
import edu.arizona.kfs.module.prje.PRJEConstants.ProrateOptions;
import edu.arizona.kfs.module.prje.PRJEKeyConstants;
import edu.arizona.kfs.module.prje.businessobject.PRJEAccountLine;
import edu.arizona.kfs.module.prje.businessobject.PRJEBaseAccount;
import edu.arizona.kfs.module.prje.businessobject.PRJEBaseObject;
import edu.arizona.kfs.module.prje.businessobject.PRJEType;

public class PRJETypeMaintenanceRule extends MaintenanceDocumentRuleBase {
    private static Logger LOG = Logger.getLogger(PRJETypeMaintenanceRule.class);
    
    @Override
    public boolean processSaveDocument(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument)document;
        PRJEType type = (PRJEType)maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        return isTypeValid(type) && super.processSaveDocument(document);
    }
        
    public boolean isTypeValid(PRJEType type) {
        String prorateOptions = type.getProrateOptions();
        List<PRJEBaseObject> baseObjects = type.getBaseObjects();
        List<PRJEBaseAccount> baseAccounts = type.getBaseAccounts();           
        List<PRJEAccountLine> accountLines = type.getAccountLines();            

        boolean valid = true;
        
        if ( baseAccounts.size() == 0 ) {
            putFieldError("baseAccounts", "error.prje.noBaseAccounts");            
            valid = false;
        }
        
        if ( accountLines.size() == 0 ) {
            putFieldError("accountLines", "error.prje.noAccountLines");            
            valid = false;
        }
        
        if ( baseAccounts.size() > 1  
                && ( ProrateOptions.SINGLE_TO_MULTIPLE.getKey().equals(prorateOptions) 
                || ProrateOptions.SINGLE_TO_SINGLE.equals(prorateOptions)) ) {
            putFieldError("baseAccounts", "error.prje.manyBaseAccounts");              
            valid = false;
        }
        
        if ( accountLines.size() > 1  
                && ( ProrateOptions.MULTIPLE_TO_SINGLE.equals(prorateOptions) 
                || ProrateOptions.SINGLE_TO_SINGLE.getKey().equals(prorateOptions)) ) {
            putFieldError("accountLines", "error.prje.manyAccountLines");              
            valid = false;
        }
        
        valid &= checkTypeIncludes(type);
        valid &= checkBaseAccountAmounts(type);
        valid &= checkOverrideOnSingleAccount(type);

        return valid;
    }

    /**
     * Checks that the PRJE type validated by this maintenance document rule has at least one "Includes" section
     * @param type the type to check
     * @return true if the PRJE type includes at least one "includes" section, false otherwise (with error set)
     */
    protected boolean checkTypeIncludes(PRJEType type) {
       boolean result = true;
       
       if (!hasAtLeastOneInclude(type)) {
           result = false;
           putFieldError("add.baseObjects.include", PRJEKeyConstants.PRJE_TYPE_NO_INCLUDE_BASE_OBJECTS);
       }
       
       return result;
    }
    
    /**
     * Determines if the given PRJEType has at least one include base object code range associated with it
     * @param type the PRJE type to test
     * @return true if at least one base object code range type on the given PRJE type
     */
    protected boolean hasAtLeastOneInclude(PRJEType type) {
        int count = 0;
        while (count < type.getBaseObjects().size()) {
            final PRJEBaseObject baseObject = type.getBaseObjects().get(count);
            if (baseObject.getInclude().equals(Containment.INCLUDE.getKey())) {
                return true;
            }
            count += 1;
        }
        
        return false;
    }
    
    /**
     * Checks to make sure that all base accounts on the PRJEType has either an amount or percentage set
     * @param type
     * @return
     */
    protected boolean checkBaseAccountAmounts(PRJEType type) {
        boolean result = true;
        
        for (int i = 0; i < type.getBaseAccounts().size(); i++) {
            PRJEBaseAccount account = type.getBaseAccounts().get(i);
            if (StringUtils.equals(account.getProrateType(), ProrateDebitType.AMOUNT.getKey())) {
                if (account.getProrateAmount() == null || account.getProrateAmount().equals(new KualiDecimal(0.0))) {
                    result = false;
                    putFieldError("baseAccounts[" + i + "].prorateAmount", PRJEKeyConstants.PRJE_TYPE_NO_BASE_ACCOUNT_AMOUNT);
                }
            } else if (StringUtils.equals(account.getProrateType(), ProrateDebitType.PERCENTAGE.getKey())) {
                if (account.getProratePercent() == null || account.getProratePercent().equals(new KualiDecimal(0.0))) {
                    result = false;
                    putFieldError("baseAccounts[" + i + "].proratePercent", PRJEKeyConstants.PRJE_TYPE_NO_BASE_ACCOUNT_AMOUNT);
                }
            }
        }
        return result;
    }
    
    /**
     * Checks to make sure that when the PRJE type is set to a single account(Many -> Single or Single -> Single)
     * that the To Account Line does not have an override
     * @param type
     * @return
     */
    protected boolean checkOverrideOnSingleAccount(PRJEType type) {
        boolean result = true;
        if (StringUtils.equals(type.getProrateOptions(), ProrateOptions.MULTIPLE_TO_SINGLE.getKey())
                || StringUtils.equals(type.getProrateOptions(), ProrateOptions.SINGLE_TO_SINGLE.getKey())) {
            PRJEAccountLine account = type.getAccountLines().get(0);
            if (account != null) {
                if (!StringUtils.equals(account.getOverrideProrateType(), ProrateCreditType.NO_OVERRIDE.getKey())) {
                    result = false;
                    putFieldError("accountLines[0].overrideProrateType", PRJEKeyConstants.PRJE_TYPE_SINGLE_ACCOUNT_LINE_OVERRIDE);
                }
            }
        }
        return result;
    }

}
