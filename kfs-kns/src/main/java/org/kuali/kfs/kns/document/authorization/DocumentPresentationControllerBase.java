/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.kns.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.KRADConstants;

/**
 * KNS version of the DocumentPresentationControllerBase - adds #getDocumentActions via {@link DocumentPresentationController}
 */
public class DocumentPresentationControllerBase extends org.kuali.kfs.krad.document.DocumentPresentationControllerBase implements DocumentPresentationController {
    /**
     * @see DocumentPresentationController#getDocumentActions(Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document){
    	Set<String> documentActions = new HashSet<String>();
    	if (canEdit(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT);
    	}
    	
    	if(canAnnotate(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
    	}
    	 
    	if(canClose(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_CLOSE);
    	}
    	 
    	if(canSave(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_SAVE);
    	}
    	if(canRoute(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_ROUTE);
    	}
    	 
    	if(canCancel(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_CANCEL);
    	}

        if(canRecall(document)){
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_RECALL);
        }
    	 
    	if(canReload(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_RELOAD);
    	}
    	if(canCopy(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_COPY);
    	}
    	if(canPerformRouteReport(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);
    	}
    	
    	if(canAddAdhocRequests(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
    	}

        // KULRICE-8762: Approve & Blanket Approve should be disabled for a person who is doing COMPLETE action
        boolean canComplete = this.canComplete(document);
        if(!canComplete && canBlanketApprove(document)){
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }
        if (!canComplete && canApprove(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_APPROVE);
        }

    	if (canDisapprove(document)) {
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
    	}
    	if (canSendAdhocRequests(document)) {
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
    	}
    	if(canSendNoteFyi(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
    	}
    	if(this.canEditDocumentOverview(document)){
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
}
