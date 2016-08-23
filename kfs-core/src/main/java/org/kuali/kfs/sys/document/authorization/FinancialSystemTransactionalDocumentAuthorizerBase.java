/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.document.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.PermissionTemplate;
import org.kuali.rice.kim.api.identity.Person;

import java.util.Set;

public class FinancialSystemTransactionalDocumentAuthorizerBase extends TransactionalDocumentAuthorizerBase {
    private static final Log LOG = LogFactory.getLog(FinancialSystemTransactionalDocumentAuthorizerBase.class);

    /**
     * Overridden to check if document error correction can be allowed here.
     *
     * @see org.kuali.kfs.krad.document.authorization.DocumentAuthorizerBase#getDocumentActions(org.kuali.kfs.krad.document.Document,
     * org.kuali.rice.kim.api.identity.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);

        if (documentActionsToReturn.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)
            && !(documentActionsToReturn.contains(KRADConstants.KUALI_ACTION_CAN_COPY)
            && canErrorCorrect(document, user))) {
            documentActionsToReturn.remove(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
        }

        if (documentActionsToReturn.contains(KFSConstants.KFS_ACTION_CAN_EDIT_BANK)
            && !canEditBankCode(document, user)) {
            documentActionsToReturn.remove(KFSConstants.KFS_ACTION_CAN_EDIT_BANK);
        }

        if (documentActionsToReturn.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && documentActionsToReturn.contains(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION)) {
            // check KIM permission for view, approvers always have permission to view
            if (!canViewAccountingDropDown(document, user)) {
                documentActionsToReturn.remove(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);
            }
            // check KIM permission for edit
            else if (canEditAccountingDropDown(document, user)) {
                documentActionsToReturn.add(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_EDIT_DOCUMENT_ACTION);
            }
        }
        return documentActionsToReturn;
    }

    /**
     * Determines if the KIM permission is available to error correct the given document
     *
     * @param document the document to correct
     * @param user     the user to check error correction for
     * @return true if the user can error correct, false otherwise
     */
    public boolean canErrorCorrect(Document document, Person user) {
        return isAuthorizedByTemplate(document, KFSConstants.CoreModuleNamespaces.KFS, PermissionTemplate.ERROR_CORRECT_DOCUMENT.name, user.getPrincipalId());
    }

    /**
     * Determines if the KIM permission is available to error correct the given document
     *
     * @param document the document to correct
     * @param user     the user to check error correction for
     * @return true if the user can error correct, false otherwise
     */
    public boolean canEditBankCode(Document document, Person user) {
        return isAuthorizedByTemplate(document, KFSConstants.CoreModuleNamespaces.KFS, PermissionTemplate.EDIT_BANK_CODE.name, user.getPrincipalId());
    }

    /**
     * Determines if given user can view the accounting period select control
     *
     * @param document the document to verify against
     * @param user     the user to check if has the ability to view accounting period select
     * @return true if user can view accounting period, false otherwise
     */
    protected boolean canViewAccountingDropDown(Document document, Person user) {
        return document.getDocumentHeader().getWorkflowDocument().isApprovalRequested() || super.isAuthorized(document, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_PERMISSION, user.getPrincipalId());
    }

    /**
     * Determines if given usre can edit the accounting period select control
     *
     * @param document the document to verify against
     * @param user     the user to check if has the ability to edit accounting period select
     * @return true if user can edit accounting period, false otherwise
     */
    protected boolean canEditAccountingDropDown(Document document, Person user) {
        return isAuthorized(document, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.YEAR_END_ACCOUNTING_PERIOD_EDIT_PERMISSION, user.getPrincipalId());
    }

}
