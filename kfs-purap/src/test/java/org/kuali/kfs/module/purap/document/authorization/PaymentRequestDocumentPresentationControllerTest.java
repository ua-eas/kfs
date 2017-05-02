/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.module.purap.document.authorization;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PaymentRequestEditMode;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.rice.core.api.uif.RemotableAttributeErrorContract;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.AdHocRevoke;
import org.kuali.rice.kew.api.action.AdHocToGroup;
import org.kuali.rice.kew.api.action.AdHocToPrincipal;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.action.RequestedActions;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(EasyMockRunner.class)
public class PaymentRequestDocumentPresentationControllerTest {

    @TestSubject
    PaymentRequestDocumentPresentationController preqDocPresentationController = new PaymentRequestDocumentPresentationController();

    @Mock
    private static PaymentRequestDocument preq;

    @Mock
    private static DocumentHeader docHeader;

    @BeforeClass
    public static void setUp() throws Exception {
        preq = EasyMock.createMock(PaymentRequestDocument.class);
        docHeader = EasyMock.createMock(DocumentHeader.class);
    }

    @Test
    public void testAddACHSignupInfoModeWhenPREQInFinalState() {
        createPREQMock(DocumentStatus.FINAL);

        Set<String> editModes = new HashSet<String>();
        preqDocPresentationController.addACHSignUpInfoMode(preq, editModes);

        // final state should not allow ach flag to be seen
        assertFalse(editModes.contains(PaymentRequestEditMode.ACH_ACCOUNT_INFO_DISPLAYED));
    }

    @Test
    public void testAddACHSignupInfoModeWhenPREQInProcessedState() throws Exception {
        createPREQMock(DocumentStatus.PROCESSED);

        Set<String> editModes = new HashSet<String>();
        preqDocPresentationController.addACHSignUpInfoMode(preq, editModes);

        // processed state should not allow ach flag to be seen
        assertFalse(editModes.contains(PaymentRequestEditMode.ACH_ACCOUNT_INFO_DISPLAYED));
    }

    @Test
    public void testAddACHSignupInfoModeWhenPREQInDisapprovedState() throws Exception {
        createPREQMock(DocumentStatus.DISAPPROVED);

        Set<String> editModes = new HashSet<String>();
        preqDocPresentationController.addACHSignUpInfoMode(preq, editModes);

        // disapproved state should not allow ach flag to be seen
        assertFalse(editModes.contains(PaymentRequestEditMode.ACH_ACCOUNT_INFO_DISPLAYED));
    }

    @Test
    public void testAddACHSignupInfoModeWhenPREQInEnrouteState() throws Exception {
        createPREQMock(DocumentStatus.ENROUTE);

        Set<String> editModes = new HashSet<String>();
        preqDocPresentationController.addACHSignUpInfoMode(preq, editModes);

        // enroute state should allow ach flag to be seen
        assertTrue(editModes.contains(PaymentRequestEditMode.ACH_ACCOUNT_INFO_DISPLAYED));

    }


    private void createPREQMock(DocumentStatus status) {
        StubWorkflowDocument workflowDocument = new StubWorkflowDocument();
        workflowDocument.setDocumentStatus(status);

        EasyMock.expect(docHeader.getWorkflowDocument()).andReturn(workflowDocument);
        EasyMock.replay(docHeader);

        EasyMock.expect(preq.getDocumentHeader()).andReturn(docHeader);
        EasyMock.replay(preq);
    }

    public final class StubWorkflowDocument implements WorkflowDocument {

        private DocumentStatus documentStatus;

        public void setDocumentStatus(DocumentStatus status) {
            this.documentStatus = status;
        }

        @Override
        public String getDocumentId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DocumentStatus getStatus() {
            return this.documentStatus;
        }

        @Override
        public DateTime getDateCreated() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DateTime getDateLastModified() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DateTime getDateApproved() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DateTime getDateFinalized() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getTitle() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getApplicationDocumentId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getInitiatorPrincipalId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getRoutedByPrincipalId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDocumentTypeName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDocumentTypeId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDocumentHandlerUrl() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getApplicationDocumentStatus() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DateTime getApplicationDocumentStatusDate() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map<String, String> getVariables() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getPrincipalId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void switchPrincipal(String principalId) {
            // TODO Auto-generated method stub

        }

        @Override
        public Document getDocument() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DocumentContent getDocumentContent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getApplicationContent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setTitle(String title) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setApplicationDocumentId(String applicationDocumentId) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setApplicationDocumentStatus(
            String applicationDocumentStatus) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setApplicationContent(String applicationContent) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setAttributeContent(String attributeContent) {
            // TODO Auto-generated method stub

        }

        @Override
        public void clearAttributeContent() {
            // TODO Auto-generated method stub

        }

        @Override
        public String getAttributeContent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void addAttributeDefinition(
            WorkflowAttributeDefinition attributeDefinition) {
            // TODO Auto-generated method stub

        }

        @Override
        public void removeAttributeDefinition(
            WorkflowAttributeDefinition attributeDefinition) {
            // TODO Auto-generated method stub

        }

        @Override
        public void clearAttributeDefinitions() {
            // TODO Auto-generated method stub

        }

        @Override
        public List<WorkflowAttributeDefinition> getAttributeDefinitions() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setSearchableContent(String searchableContent) {
            // TODO Auto-generated method stub

        }

        @Override
        public void addSearchableDefinition(
            WorkflowAttributeDefinition searchableDefinition) {
            // TODO Auto-generated method stub

        }

        @Override
        public void removeSearchableDefinition(
            WorkflowAttributeDefinition searchableDefinition) {
            // TODO Auto-generated method stub

        }

        @Override
        public void clearSearchableDefinitions() {
            // TODO Auto-generated method stub

        }

        @Override
        public void clearSearchableContent() {
            // TODO Auto-generated method stub

        }

        @Override
        public List<WorkflowAttributeDefinition> getSearchableDefinitions() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setVariable(String name, String value) {
            // TODO Auto-generated method stub

        }

        @Override
        public String getVariableValue(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setReceiveFutureRequests() {
            // TODO Auto-generated method stub

        }

        @Override
        public void setDoNotReceiveFutureRequests() {
            // TODO Auto-generated method stub

        }

        @Override
        public void setClearFutureRequests() {
            // TODO Auto-generated method stub

        }

        @Override
        public String getReceiveFutureRequestsValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDoNotReceiveFutureRequestsValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getClearFutureRequestsValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<? extends RemotableAttributeErrorContract> validateAttributeDefinition(
            WorkflowAttributeDefinition attributeDefinition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<ActionRequest> getRootActionRequests() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<ActionTaken> getActionsTaken() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ValidActions getValidActions() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public RequestedActions getRequestedActions() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void saveDocument(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void route(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void complete(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void disapprove(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void approve(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void cancel(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void recall(String annotation, boolean cancel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void blanketApprove(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void blanketApprove(String annotation, String... nodeNames) {
            // TODO Auto-generated method stub

        }

        @Override
        public void saveDocumentData() {
            // TODO Auto-generated method stub

        }

        @Override
        public void acknowledge(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void fyi(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void fyi() {
            // TODO Auto-generated method stub

        }

        @Override
        public void delete() {
            // TODO Auto-generated method stub

        }

        @Override
        public void refresh() {
            // TODO Auto-generated method stub

        }

        @Override
        public void adHocToPrincipal(ActionRequestType actionRequested,
                                     String annotation, String targetPrincipalId,
                                     String responsibilityDescription, boolean forceAction) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adHocToPrincipal(ActionRequestType actionRequested,
                                     String nodeName, String annotation, String targetPrincipalId,
                                     String responsibilityDescription, boolean forceAction) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adHocToPrincipal(ActionRequestType actionRequested,
                                     String nodeName, String annotation, String targetPrincipalId,
                                     String responsibilityDescription, boolean forceAction,
                                     String requestLabel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adHocToPrincipal(AdHocToPrincipal adHocToPrincipal,
                                     String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adHocToGroup(ActionRequestType actionRequested,
                                 String annotation, String targetGroupId,
                                 String responsibilityDescription, boolean forceAction) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adHocToGroup(ActionRequestType actionRequested,
                                 String nodeName, String annotation, String targetGroupId,
                                 String responsibilityDescription, boolean forceAction) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adHocToGroup(ActionRequestType actionRequested,
                                 String nodeName, String annotation, String targetGroupId,
                                 String responsibilityDescription, boolean forceAction,
                                 String requestLabel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void adHocToGroup(AdHocToGroup adHocToGroup, String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void revokeAdHocRequestById(String actionRequestId,
                                           String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void revokeAdHocRequests(AdHocRevoke revoke, String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void revokeAllAdHocRequests(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void returnToPreviousNode(String annotation, String nodeName) {
            // TODO Auto-generated method stub

        }

        @Override
        public void returnToPreviousNode(String annotation,
                                         ReturnPoint returnPoint) {
            // TODO Auto-generated method stub

        }

        @Override
        public void move(MovePoint movePoint, String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void takeGroupAuthority(String annotation, String groupId) {
            // TODO Auto-generated method stub

        }

        @Override
        public void releaseGroupAuthority(String annotation, String groupId) {
            // TODO Auto-generated method stub

        }

        @Override
        public void placeInExceptionRouting(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void superUserBlanketApprove(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void superUserNodeApprove(String nodeName, String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void superUserTakeRequestedAction(String actionRequestId,
                                                 String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void superUserDisapprove(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void superUserCancel(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void superUserReturnToPreviousNode(ReturnPoint returnPoint,
                                                  String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void logAnnotation(String annotation) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean isCompletionRequested() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isApprovalRequested() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isAcknowledgeRequested() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isFYIRequested() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isBlanketApproveCapable() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isRouteCapable() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isValidAction(ActionType actionType) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean checkStatus(DocumentStatus status) {
            return false;
        }

        @Override
        public boolean isInitiated() {
            return false;
        }

        @Override
        public boolean isSaved() {
            return false;
        }

        @Override
        public boolean isEnroute() {
            return this.documentStatus == DocumentStatus.ENROUTE;
        }

        @Override
        public boolean isException() {
            return false;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public boolean isRecalled() {
            return false;
        }

        @Override
        public boolean isDisapproved() {
            return this.documentStatus == DocumentStatus.DISAPPROVED;
        }

        @Override
        public boolean isApproved() {

            return isProcessed() || isFinal();
        }

        @Override
        public boolean isProcessed() {
            return this.documentStatus == DocumentStatus.PROCESSED;
        }

        @Override
        public boolean isFinal() {
            return this.documentStatus == DocumentStatus.FINAL;
        }

        @Override
        public Set<String> getNodeNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<String> getCurrentNodeNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<RouteNodeInstance> getActiveRouteNodeInstances() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<RouteNodeInstance> getCurrentRouteNodeInstances() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<RouteNodeInstance> getRouteNodeInstances() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<String> getPreviousNodeNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DocumentDetail getDocumentDetail() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void updateDocumentContent(
            DocumentContentUpdate documentContentUpdate) {
            // TODO Auto-generated method stub

        }

    }

}
