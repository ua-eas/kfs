package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.purap.document.authorization.PurapAccountingLineAuthorizer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;

import edu.arizona.kfs.module.purap.PurapConstants;

public class PurchaseOrderAmendAccountingLineAuthorizer extends PurapAccountingLineAuthorizer {
    
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        WorkflowDocument workflowDoc = accountingDocument.getFinancialSystemDocumentHeader().getWorkflowDocument();
        Set<String> currentRouteNodeName = workflowDoc.getCurrentNodeNames();

        // if its in the NEW_UNORDERED_ITEMS node, then allow the new line to be drawn
        if (CollectionUtils.isNotEmpty(currentRouteNodeName) && PurapConstants.PurchaseOrderStatuses.NODE_AWAIT_NEW_UNORDERED_ITEM_REVIEW.equals(currentRouteNodeName.toString())) {
            return true;
        }
        
        if (CollectionUtils.isNotEmpty(currentRouteNodeName) && KFSConstants.RouteLevelNames.ACCOUNT.equals(currentRouteNodeName.toString())) {
            return true;
        }

        return super.renderNewLine(accountingDocument, accountingGroupProperty);
    }

    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
        // the fields in a new line should be always editable
        if (accountingLine.getSequenceNumber() == null) {
            return true;
        }

        return super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable);
    }
    
    @Override
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set<String> unviewableBlocks = super.getUnviewableBlocks(accountingDocument, accountingLine, newLine, currentUser);
        unviewableBlocks.remove(KFSPropertyConstants.PERCENT);
        unviewableBlocks.remove(KFSPropertyConstants.AMOUNT);

        return unviewableBlocks;
    }
}
