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
package org.kuali.module.financial.rules;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.financial.document.CreditCardReceiptDocument;

/**
 * Business rules applicable to Credit Card Receipt documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CreditCardReceiptDocumentRule extends CashReceiptDocumentRule {
    /**
     * For Credit Card Receipt documents, the document is balanced if the sum total of credit card receipts
     * equals the sum total of the accounting lines.
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) transactionalDocument;

        boolean isValid = super.isDocumentBalanceValidConsideringDebitsAndCredits(ccr);
        
        // make sure that the credit card total is greater than zero
        if(isValid) {
            isValid = ccr.getSumTotalAmount().compareTo(Constants.ZERO) > 0;
            if (!isValid) {
                GlobalVariables.getErrorMap().put(PropertyConstants.NEW_CREDIT_CARD_RECEIPT, 
                        KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_TOTAL_INVALID);
            }
        }

        if (isValid) {
            // make sure the document is in balance
            isValid = ccr.getSourceTotal().compareTo(ccr.getSumTotalAmount()) == 0;

            if (!isValid) {
                GlobalVariables.getErrorMap().put(PropertyConstants.NEW_CREDIT_CARD_RECEIPT,
                        KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_OUT_OF_BALANCE);
            }
        }

        return isValid;
    }
    
    /**
     * Overrides to call super and then make sure the minimum number of credit card receipt 
     * lines exist on this document.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        
        if(isValid) {
            isValid = isMinimumNumberOfCreditCardReceiptsMet(document);
        }
        
        return isValid;
    }

    /**
     * This method is a helper that checks to make sure that at least one credit card receipt 
     * line exists for the document.
     * 
     * @param document
     * @return boolean
     */
    private boolean isMinimumNumberOfCreditCardReceiptsMet(Document document) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) document;
        
        if(ccr.getCreditCardReceipts().size() == 0) {
            GlobalVariables.getErrorMap().put(DOCUMENT_ERROR_PREFIX,
                    KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_REQ_NUMBER_RECEIPTS_NOT_MET);
            return false;
        }
        return true;
    }
    
    /**
     * Overrides to call super and then to validate all of the credit card receipts associated with this document.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        
        if(isValid) {
            isValid = validateCreditCardReceipts((CreditCardReceiptDocument) document);
        }
        
        return isValid;
    }
    
    /**
     * Validates all the CreditCardReceipts in the given Document.
     *
     * @param creditCardReceiptDocument
     * @return boolean
     */
    private boolean validateCreditCardReceipts(CreditCardReceiptDocument creditCardReceiptDocument) {
        GlobalVariables.getErrorMap().addToErrorPath(Constants.DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;
        for (int i = 0; i < creditCardReceiptDocument.getCreditCardReceipts().size(); i++) {
            String propertyName = PropertyConstants.CREDIT_CARD_RECEIPT + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            isValid &= CreditCardReceiptDocumentRuleUtil.validateCreditCardReceipt(creditCardReceiptDocument.getCreditCardReceipt(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(Constants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }
}
