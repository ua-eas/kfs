/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.PostalZipCode;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;

/**
 * 
 * PreRules checks for the Account that needs to occur while still in the 
 * Struts processing.  This includes defaults, confirmations, etc.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 * 
 */
public class AccountPreRules extends PreRulesContinuationBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountPreRules.class);
    
    private static final String CHART_MAINTENANCE_EDOC = "ChartMaintenanceEDoc";
    private static final String DEFAULT_STATE_CODE = "Account.Defaults.StateCode";
    private static final String DEFAULT_ACCOUNT_TYPE_CODE = "Account.Defaults.AccountType";
    
    private KualiConfigurationService configService;
    private AccountService accountService;
    private Account newAccount;
    private Account copyAccount;
    
    private static final String CONTRACTS_GRANTS_CD = "CG";
    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";
    
    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    private static final String RESTRICTED_CD_NOT_APPLICABLE = "N";
    
    
    public AccountPreRules() {
        accountService=SpringServiceLocator.getAccountService();
        configService = SpringServiceLocator.getKualiConfigurationService();
    }

    /**
     * This processes certain rules that need to occur at the UI level or actually need to modify the Account object
     * before being passed on down the rules chain
     * @see org.kuali.core.rule.PreRulesCheck#processPreRuleChecks(org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, org.kuali.core.rule.event.PreRulesCheckEvent)
     */
    public boolean doRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        newAccountDefaults(document);
        setStateFromZip(document);
        
        return true;
    }
    
    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");
        
        if (newAccount.getReportsToAccountNumber()!=null){
            Account account=checkForContinuationAccount("Fringe Benefit Account", newAccount.getReportsToChartOfAccountsCode(),newAccount.getReportsToAccountNumber(), "");
            if (account!=null) { //override old user inputs
                if (LOG.isDebugEnabled()) {
                    LOG.debug("overriding original account with continuation account: "+account);
                }
                newAccount.setReportsToAccountNumber(account.getAccountNumber());
                newAccount.setReportsToChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
        
        if (newAccount.getEndowmentIncomeAccountNumber()!=null){
            Account account=checkForContinuationAccount("Endowment Account", newAccount.getEndowmentIncomeAcctFinCoaCd(),newAccount.getEndowmentIncomeAccountNumber(), "");
        	if (account!=null) { //override old user inputs
        	    if (LOG.isDebugEnabled()) {
        	        LOG.debug("overriding original account with continuation account: "+account);
        	    }
        	    newAccount.setEndowmentIncomeAccountNumber(account.getAccountNumber());
        	    newAccount.setEndowmentIncomeAcctFinCoaCd(account.getChartOfAccountsCode());
        	}
        }
        
        if (newAccount.getIncomeStreamAccountNumber()!=null){
            Account account=checkForContinuationAccount("Income Stream Account", newAccount.getIncomeStreamFinancialCoaCode(),newAccount.getIncomeStreamAccountNumber(), "");
            if (account!=null) { //override old user inputs
                if (LOG.isDebugEnabled()) {
                    LOG.debug("overriding original account with continuation account: "+account);
                }
                newAccount.setIncomeStreamAccountNumber(account.getAccountNumber());
                newAccount.setIncomeStreamFinancialCoaCode(account.getChartOfAccountsCode());
            }
        }
        
        if (newAccount.getContractControlAccountNumber()!=null){
            Account account=checkForContinuationAccount("Contract Control Account", newAccount.getContractControlFinCoaCode(),newAccount.getContractControlAccountNumber(), "");
            if (account!=null) { //override old user inputs
                if (LOG.isDebugEnabled()) {
                    LOG.debug("overriding original account with continuation account: "+account);
                }
                newAccount.setContractControlFinCoaCode(account.getAccountNumber());
                newAccount.setContractControlAccountNumber(account.getChartOfAccountsCode());
            }
        }
        
        if (newAccount.getIndirectCostRecoveryAcctNbr()!=null){
            Account account=checkForContinuationAccount("Indirect Cost Recovery Account", newAccount.getIndirectCostRcvyFinCoaCode(),newAccount.getIndirectCostRecoveryAcctNbr(), "");
            if (account!=null) { //override old user inputs
                if (LOG.isDebugEnabled()) {
                    LOG.debug("overriding original account with continuation account: "+account);
                }
                newAccount.setIndirectCostRcvyFinCoaCode(account.getAccountNumber());
                newAccount.setIndirectCostRecoveryAcctNbr(account.getChartOfAccountsCode());
            }
        }
        
        
        
        
    }

    
    private Account checkForContinuationAccount(String accName, String chart, String accountNumber, String accountName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("entering checkForContinuationAccounts("+accountNumber+")");
        }
        if (accountNumber==null || accountNumber.length()==0) return null;
        
        Account account=accountService.getByPrimaryId(chart,accountNumber);
        
        if (account!=null && !account.isExpired()) { //no need for a continuation account
            return null;
        }
        
        boolean useContinuationAccount=true;
        while (account!=null && account.isExpired() && useContinuationAccount) {
            LOG.debug("Expired account: "+accountNumber);
            String continuationAccountNumber=account.getContinuationAccountNumber();
            
            useContinuationAccount=askOrAnalyzeYesNoQuestion("ContinuationAccount"+accName+accountNumber,
                    buildBudgetConfirmationQuestion(accName, accountNumber,continuationAccountNumber));
            if (useContinuationAccount) {
                accountNumber=continuationAccountNumber;
                chart=account.getContinuationFinChrtOfAcctCd();
                account=accountService.getByPrimaryId(chart,accountNumber);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Selected continuation account: "+account);
                }
            }
        }
        return account;
        
    }
    
    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you
     * have short and easy handles to the new and old objects contained in the 
     * maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load 
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {
        
        //	setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        copyAccount = (Account) ObjectUtils.deepCopy(newAccount);
        copyAccount.refresh();
    }
    
    /**
     * 
     * This method sets the default values for RestrictedStatusCode, based on the 
     * FundGroups.
     * 
     * @param document - the MaintenanceDocument being evaluated
     * 
     */
    private void setRestrictedCodeDefaults(MaintenanceDocument document) {
        
        //  NOTE that this method is no longer used.  It was found to be confusing 
        // to the users when the field was silently changed with no explanation.
        
        String fundGroupCode = "";
        
        //	if subFundGroupCode was not entered, then we have nothing 
        // to do here, so exit
        if (ObjectUtils.isNull(copyAccount.getSubFundGroup()) || 
                StringUtils.isBlank(copyAccount.getSubFundGroupCode())) {
            return;
        }
        fundGroupCode = copyAccount.getSubFundGroup().getFundGroupCode().trim();
       
        if (!StringUtils.isBlank(fundGroupCode)) {
            
	        //	on the account screen, if the fund group of the account is CG (contracts & grants) or 
	        // RF (restricted funds), the restricted status code is set to 'R'.
	        if (fundGroupCode.equalsIgnoreCase(CONTRACTS_GRANTS_CD) || fundGroupCode.equalsIgnoreCase(RESTRICTED_FUND_CD)) {
	            newAccount.setAccountRestrictedStatusCode(RESTRICTED_CD_RESTRICTED);
	        }
	
	        //	If the fund group is EN (endowment) or PF (plant fund) the value is not set by the system and 
	        // must be set by the user 
	        else if (fundGroupCode.equalsIgnoreCase(ENDOWMENT_FUND_CD) || fundGroupCode.equalsIgnoreCase(PLANT_FUND_CD)) {
	            // do nothing, must be set by user
	        }
	        
	        //	for all other fund groups the value is set to 'U'. R being restricted,U being unrestricted.
	        else {
	            newAccount.setAccountRestrictedStatusCode(RESTRICTED_CD_UNRESTRICTED);
	        }
        }
    }
    
    /**
     * 
     * This method sets up some defaults for new Account
     * @param maintenanceDocument
     */
    private void newAccountDefaults(MaintenanceDocument maintenanceDocument) {
        
        //On new Accounts acct_effect_date is defaulted to the doc creation date
        if (copyAccount.getAccountEffectiveDate() == null) {
            Timestamp ts = maintenanceDocument.getDocumentHeader().getWorkflowDocument().getCreateDate();
            if (ts != null) {
                newAccount.setAccountEffectiveDate(ts);
            }
        }
        
        //On new Accounts acct_state_cd is defaulted to the value of "IN"
        if (StringUtils.isBlank(copyAccount.getAccountStateCode())) {
            String defaultStateCode = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, 
    				DEFAULT_STATE_CODE);
    		if (StringUtils.isBlank(defaultStateCode)) {
    			throw new RuntimeException("Expected ConfigurationService.ApplicationParameterValue was not found " + 
    										"for ScriptName = '" + CHART_MAINTENANCE_EDOC + "' and " + 
    										"Parameter = '" + DEFAULT_STATE_CODE + "'");
    		}
            newAccount.setAccountStateCode(defaultStateCode);
        }
        
        //if the account type code is left blank it will default to NA.
        if (StringUtils.isBlank(copyAccount.getAccountTypeCode())) {
            String defaultAccountTypeCode = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, 
    				DEFAULT_ACCOUNT_TYPE_CODE);
    		if (StringUtils.isBlank(defaultAccountTypeCode)) {
    			throw new RuntimeException("Expected ConfigurationService.ApplicationParameterValue was not found " + 
    										"for ScriptName = '" + CHART_MAINTENANCE_EDOC + "' and " + 
    										"Parameter = '" + DEFAULT_ACCOUNT_TYPE_CODE + "'");
    		}
            newAccount.setAccountTypeCode(defaultAccountTypeCode);
        }
    }
    
    //	lookup state and city from populated zip, set the values on the form
    private void setStateFromZip(MaintenanceDocument maintenanceDocument) {
        
        //	acct_zip_cd, acct_state_cd, acct_city_nm all are populated by looking up 
        // the zip code and getting the state and city from that
        if (!StringUtils.isBlank(copyAccount.getAccountZipCode())) {

            HashMap primaryKeys = new HashMap();
            primaryKeys.put("postalZipCode", copyAccount.getAccountZipCode());
            PostalZipCode zip = (PostalZipCode) SpringServiceLocator.getBusinessObjectService()
            									.findByPrimaryKey(PostalZipCode.class, primaryKeys);
            
            //TODO: finish this
            
            //	set the state field
            
            //	set the city field
            
        }
    }

    protected String buildBudgetConfirmationQuestion(String accName, String expiredAccount, String continuationAccount)  {
        String result=configService.getPropertyString(KeyConstants.QUESTION_CONTINUATION_ACCOUNT_SELECTION);
        result=StringUtils.replace(result, "{0}", accName);
        result=StringUtils.replace(result, "{1}", expiredAccount);
        result=StringUtils.replace(result, "{2}", continuationAccount);
        return result;
     }

    
}

