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
package org.kuali.kfs.krad.document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.krad.uif.view.DocumentView;
import org.kuali.kfs.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.view.ViewAuthorizerBase;
import org.kuali.kfs.krad.uif.view.ViewModel;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.web.form.DocumentFormBase;

import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link ViewAuthorizer} for
 * {@link DocumentView} instances
 *
 * <p>
 * Performs KIM permission checks for the various document actions such as save, approve, cancel
 * </p>
 *
 * <p>
 * By default delegates to the {@link DocumentAuthorizer} configured for the document in the data dictionary
 * </p>
 *
 *
 */
public class DocumentViewAuthorizerBase extends ViewAuthorizerBase implements DocumentAuthorizer {
    private static final long serialVersionUID = 3800780934223224565L;

    protected static Log LOG = LogFactory.getLog(DocumentViewAuthorizerBase.class);

    public static final String PRE_ROUTING_ROUTE_NAME = "PreRoute";

    private DocumentAuthorizer documentAuthorizer;

    /**
     * @see ViewAuthorizer#getActionFlags(View,
     *      ViewModel, org.kuali.rice.kim.api.identity.Person,
     *      java.util.Set<java.lang.String>)
     */
    @Override
    public Set<String> getActionFlags(View view, ViewModel model, Person user, Set<String> actions) {
        Document document = ((DocumentFormBase) model).getDocument();

        if (LOG.isDebugEnabled()) {
            LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '"
                    + document.getDocumentNumber()
                    + "'. user '"
                    + user.getPrincipalName()
                    + "'");
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && !canEdit(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_COPY) && !canCopy(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_COPY);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_CLOSE) && !canClose(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_CLOSE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_RELOAD) && !canReload(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_RELOAD);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE) && !canBlanketApprove(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_CANCEL) && !canCancel(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_CANCEL);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_RECALL) && !canRecall(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_RECALL);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_SAVE) && !canSave(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ROUTE) && !canRoute(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ROUTE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE) && !canAcknowledge(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_FYI) && !canFyi(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_APPROVE) && !canApprove(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_APPROVE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE) && !canDisapprove(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
        }

        if (!canSendAnyTypeAdHocRequests(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI) && !canSendNoteFyi(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ANNOTATE) && !canAnnotate(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW) && !canEditDocumentOverview(
                document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT) && !canPerformRouteReport(document,
                user)) {
            actions.remove(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);
        }

        return actions;
    }

    public final boolean canInitiate(String documentTypeName, Person user) {
        return getDocumentAuthorizer().canInitiate(documentTypeName, user);
    }

    public final boolean canOpen(Document document, Person user) {
        return getDocumentAuthorizer().canOpen(document, user);
    }

    @Override
    public boolean canOpenView(View view, ViewModel model, Person user) {
        DocumentFormBase documentForm = (DocumentFormBase) model;

        return super.canOpenView(view, model, user) && canOpen(documentForm.getDocument(), user);
    }

    public boolean canEdit(Document document, Person user) {
        return getDocumentAuthorizer().canEdit(document, user);
    }

    @Override
    public boolean canEditView(View view, ViewModel model, Person user) {
        DocumentFormBase documentForm = (DocumentFormBase) model;

        return super.canEditView(view, model, user) && canEdit(documentForm.getDocument(), user);
    }

    public boolean canAnnotate(Document document, Person user) {
        return getDocumentAuthorizer().canAnnotate(document, user);
    }

    public boolean canReload(Document document, Person user) {
        return getDocumentAuthorizer().canReload(document, user);
    }

    public boolean canClose(Document document, Person user) {
        return getDocumentAuthorizer().canClose(document, user);
    }

    public boolean canSave(Document document, Person user) {
        return getDocumentAuthorizer().canSave(document, user);
    }

    public boolean canRoute(Document document, Person user) {
        return getDocumentAuthorizer().canRoute(document, user);
    }

    public boolean canCancel(Document document, Person user) {
        return getDocumentAuthorizer().canCancel(document, user);
    }

    public boolean canRecall(Document document, Person user) {
        return getDocumentAuthorizer().canRecall(document, user);
    }

    public boolean canCopy(Document document, Person user) {
        return getDocumentAuthorizer().canCopy(document, user);
    }

    public boolean canPerformRouteReport(Document document, Person user) {
        return getDocumentAuthorizer().canPerformRouteReport(document, user);
    }

    public boolean canBlanketApprove(Document document, Person user) {
        return getDocumentAuthorizer().canBlanketApprove(document, user);
    }

    public boolean canApprove(Document document, Person user) {
        return getDocumentAuthorizer().canApprove(document, user);
    }

    public boolean canDisapprove(Document document, Person user) {
        return getDocumentAuthorizer().canDisapprove(document, user);
    }

    public boolean canSendNoteFyi(Document document, Person user) {
        return getDocumentAuthorizer().canSendNoteFyi(document, user);
    }

    public boolean canFyi(Document document, Person user) {
        return getDocumentAuthorizer().canFyi(document, user);
    }

    public boolean canAcknowledge(Document document, Person user) {
        return getDocumentAuthorizer().canAcknowledge(document, user);
    }

    public final boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode) {
        return getDocumentAuthorizer().canReceiveAdHoc(document, user, actionRequestCode);
    }

    public final boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        return getDocumentAuthorizer().canAddNoteAttachment(document, attachmentTypeCode, user);
    }

    public final boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode,
            String authorUniversalIdentifier, Person user) {
        return getDocumentAuthorizer().canDeleteNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier,
                user);
    }

    public final boolean canViewNoteAttachment(Document document, String attachmentTypeCode,
            String authorUniversalIdentifier, Person user) {
        return getDocumentAuthorizer().canViewNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier,
                user);
    }

    public final boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user) {
        return getDocumentAuthorizer().canSendAdHocRequests(document, actionRequestCd, user);
    }

    public boolean canEditDocumentOverview(Document document, Person user) {
        return getDocumentAuthorizer().canEditDocumentOverview(document, user);
    }

    public boolean canSendAnyTypeAdHocRequests(Document document, Person user) {
        return getDocumentAuthorizer().canSendAnyTypeAdHocRequests(document, user);
    }

    public boolean canTakeRequestedAction(Document document, String actionRequestCode, Person user) {
        return getDocumentAuthorizer().canTakeRequestedAction(document, actionRequestCode, user);
    }

    @Override
    protected void addPermissionDetails(Object dataObject, Map<String, String> attributes) {
        super.addPermissionDetails(dataObject, attributes);

        if (dataObject instanceof Document) {
            addStandardAttributes((Document) dataObject, attributes);
        }
    }

    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        super.addRoleQualification(dataObject, attributes);

        if (dataObject instanceof Document) {
            addStandardAttributes((Document) dataObject, attributes);
        }
    }

    protected void addStandardAttributes(Document document, Map<String, String> attributes) {
        WorkflowDocument wd = document.getDocumentHeader().getWorkflowDocument();
        attributes.put(KimConstants.AttributeConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        attributes.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, wd.getDocumentTypeName());

        if (wd.isInitiated() || wd.isSaved()) {
            attributes.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME, PRE_ROUTING_ROUTE_NAME);
        } else {
            attributes.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME,
                    KRADServiceLocatorWeb.getWorkflowDocumentService().getCurrentRouteNodeNames(wd));
        }

        attributes.put(KimConstants.AttributeConstants.ROUTE_STATUS_CODE, wd.getStatus().getCode());
    }

    protected boolean isDocumentInitiator(Document document, Person user) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return workflowDocument.getInitiatorPrincipalId().equalsIgnoreCase(user.getPrincipalId());
    }

    public DocumentAuthorizer getDocumentAuthorizer() {
        return documentAuthorizer;
    }

    public void setDocumentAuthorizer(DocumentAuthorizer documentAuthorizer) {
        this.documentAuthorizer = documentAuthorizer;
    }

    public void setDocumentAuthorizerClass(Class<? extends DocumentAuthorizer> documentAuthorizerClass) {
        this.documentAuthorizer = ObjectUtils.newInstance(documentAuthorizerClass);
    }
}
