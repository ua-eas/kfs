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

import org.kuali.kfs.krad.uif.view.DocumentView;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.view.ViewModel;
import org.kuali.kfs.krad.uif.view.ViewPresentationController;
import org.kuali.kfs.krad.uif.view.ViewPresentationControllerBase;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.web.form.DocumentFormBase;
import org.kuali.kfs.krad.web.form.UifFormBase;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link ViewPresentationController} for
 * {@link DocumentView} instances
 * <p>
 * <p>
 * Adds flags for various document actions like save, route, cancel
 * </p>
 * <p>
 * <p>
 * By default delegates to the {@link DocumentPresentationController} configured for the document in the data dictionary
 * </p>
 */
public class DocumentViewPresentationControllerBase extends ViewPresentationControllerBase implements DocumentPresentationController {
    private static final long serialVersionUID = 1461173145806477758L;

    private DocumentPresentationController documentPresentationController;

    @Override
    public Set<String> getActionFlags(View view, UifFormBase model) {
        Set<String> documentActions = new HashSet<String>();

        Document document = ((DocumentFormBase) model).getDocument();

        if (canEdit(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        if (canAnnotate(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
        }

        if (canClose(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_CLOSE);
        }

        if (canSave(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }

        if (canRoute(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_ROUTE);
        }

        if (canCancel(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_CANCEL);
        }

        if (canReload(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_RELOAD);
        }

        if (canCopy(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_COPY);
        }

        if (canPerformRouteReport(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);
        }

        if (canAddAdhocRequests(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
        }

        if (canBlanketApprove(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }

        if (canApprove(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_APPROVE);
        }

        if (canDisapprove(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
        }

        if (canSendAdhocRequests(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
        }

        if (canSendNoteFyi(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (this.canEditDocumentOverview(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW);
        }

        if (canFyi(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_FYI);
        }

        if (canAcknowledge(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
        }

        if (canComplete(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_COMPLETE);
        }

        return documentActions;
    }

    public boolean canInitiate(String documentTypeName) {
        return getDocumentPresentationController().canInitiate(documentTypeName);
    }

    public boolean canEdit(Document document) {
        return getDocumentPresentationController().canEdit(document);
    }

    /**
     * Verify the document can be edited in addition to the view
     */
    @Override
    public boolean canEditView(View view, ViewModel model) {
        DocumentFormBase documentForm = (DocumentFormBase) model;

        return super.canEditView(view, model) && canEdit(documentForm.getDocument());
    }

    public boolean canAnnotate(Document document) {
        return getDocumentPresentationController().canAnnotate(document);
    }

    public boolean canReload(Document document) {
        return getDocumentPresentationController().canReload(document);
    }

    public boolean canClose(Document document) {
        return getDocumentPresentationController().canClose(document);
    }

    public boolean canSave(Document document) {
        return getDocumentPresentationController().canSave(document);
    }

    public boolean canRoute(Document document) {
        return getDocumentPresentationController().canRoute(document);
    }

    public boolean canCancel(Document document) {
        return getDocumentPresentationController().canEdit(document);
    }

    public boolean canRecall(Document document) {
        return getDocumentPresentationController().canRecall(document);
    }

    public boolean canCopy(Document document) {
        return getDocumentPresentationController().canCopy(document);
    }

    public boolean canPerformRouteReport(Document document) {
        return getDocumentPresentationController().canPerformRouteReport(document);
    }

    public boolean canAddAdhocRequests(Document document) {
        return getDocumentPresentationController().canAddAdhocRequests(document);
    }

    public boolean canBlanketApprove(Document document) {
        return getDocumentPresentationController().canBlanketApprove(document);
    }

    public boolean canApprove(Document document) {
        return getDocumentPresentationController().canApprove(document);
    }

    public boolean canDisapprove(Document document) {
        return getDocumentPresentationController().canDisapprove(document);
    }

    public boolean canSendAdhocRequests(Document document) {
        return getDocumentPresentationController().canSendAdhocRequests(document);
    }

    public boolean canSendNoteFyi(Document document) {
        return getDocumentPresentationController().canSendNoteFyi(document);
    }

    public boolean canEditDocumentOverview(Document document) {
        return getDocumentPresentationController().canEditDocumentOverview(document);
    }

    public boolean canFyi(Document document) {
        return getDocumentPresentationController().canFyi(document);
    }

    public boolean canAcknowledge(Document document) {
        return getDocumentPresentationController().canAcknowledge(document);
    }

    public boolean canComplete(Document document) {
        return getDocumentPresentationController().canComplete(document);
    }

    public DocumentPresentationController getDocumentPresentationController() {
        return documentPresentationController;
    }

    public void setDocumentPresentationController(DocumentPresentationController documentPresentationController) {
        this.documentPresentationController = documentPresentationController;
    }

    public void setDocumentPresentationControllerClass(
        Class<? extends DocumentPresentationController> documentPresentationControllerClass) {
        this.documentPresentationController = ObjectUtils.newInstance(documentPresentationControllerClass);
    }
}
