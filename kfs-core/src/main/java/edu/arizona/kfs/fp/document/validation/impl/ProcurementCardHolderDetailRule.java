package edu.arizona.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

import com.rsmart.kuali.kfs.fp.businessobject.ProcurementCardHolderDetail;

/**
 * This class represents business rules for the procurement cardholder maintenance document
 */
public class ProcurementCardHolderDetailRule extends MaintenanceDocumentRuleBase {
    
    protected static final String WARNING_CARDHOLDER_LAST_ACTIVE_MEMBER = "warning.document.procurementcardholderdetail.cardholder.last.active";

    private ProcurementCardHolderDetail newProcurementCardHolderDetail;

    /**
     * Returns value from processCustomRouteDocumentBusinessRules(document)
     * 
     * @param document maintenance document
     * @return value from processCustomRouteDocumentBusinessRules(document)
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        return processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * Returns true procurement cardholder maintenance document is routed successfully
     * 
     * @param document submitted procurement cardholder maintenance document
     * @return true if chart/account/organization is valid
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean continueRouting = super.processCustomRouteDocumentBusinessRules(document);
        newProcurementCardHolderDetail = (ProcurementCardHolderDetail)document.getNewMaintainableObject().getBusinessObject();
        
        // check chart/account/organization is valid
        continueRouting &= checkAccountValidity();
        
        // KITT-909
        // check membership of reconciler group against cardholder ID
        continueRouting &= checkGroupMembership();
        // KITT-909 end

        return continueRouting;
    }

    /**
     * Returns true if chart/account/organization is valid
     * 
     * @return true if chart/account/organization is valid
     */
    
    protected boolean checkAccountValidity() {
        boolean result = false;
      
        // check that an org has been entered
        if (StringUtils.isNotBlank(newProcurementCardHolderDetail.getOrganizationCode())) {           
           
            AccountService acctService = SpringContext.getBean(AccountService.class);
            if ( StringUtils.isNotBlank(newProcurementCardHolderDetail.getChartOfAccountsCode()) ) {
                Account defAccount = acctService.getByPrimaryId(newProcurementCardHolderDetail.getChartOfAccountsCode(), newProcurementCardHolderDetail.getAccountNumber());
                // if the object doesn't exist, then we can't continue, so exit
                if (ObjectUtils.isNull(defAccount)) {
                    return result;
                }                
                if (newProcurementCardHolderDetail.getOrganizationCode().equals(defAccount.getOrganizationCode())) {
                    result = true;
                }
                if (!result) {
                    putFieldError("organizationCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ORG, new String[] {newProcurementCardHolderDetail.getAccountNumber(), newProcurementCardHolderDetail.getOrganizationCode()});              
                }
            }               
        }
                
        return result;
    }
    
    // KITT-909
    /**
     * Returns true if cardholder is not only member of reconciler group
     * 
     * @return true if cardholder is not only member of reconciler group
     */
    
    protected boolean checkGroupMembership() {
        boolean result = true;
      
        // check that a reconciler group id and cardholder id have been entered
        if (StringUtils.isNotBlank(newProcurementCardHolderDetail.getCardGroupId()) && StringUtils.isNotBlank(newProcurementCardHolderDetail.getCardHolderSystemId())) {           
           
            List<String> groupMembers = new ArrayList<String>();
            groupMembers = SpringContext.getBean(GroupService.class).getMemberPrincipalIds(newProcurementCardHolderDetail.getCardGroupId());
            for (String groupMember : groupMembers) {
                if (groupMembers.size() < 2 && groupMember.equals(newProcurementCardHolderDetail.getCardHolderSystemId())) {
                    //card holder is only remaining member of reconciler group 
                    result = false;
                }                
            }
            if (!result) { 
                putGlobalError(WARNING_CARDHOLDER_LAST_ACTIVE_MEMBER);                                        
            }
        }
        
        return result;      
    }
    // KITT-909 end
}

