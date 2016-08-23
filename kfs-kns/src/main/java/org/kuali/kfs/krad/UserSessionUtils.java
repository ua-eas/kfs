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
package org.kuali.kfs.krad;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility class for working with the UserSession.
 */
public final class UserSessionUtils {

    private UserSessionUtils() {
        throw new IllegalStateException("this class should not be instantiated");
    }

    /**
     * Adds the given {@link org.kuali.rice.kew.api.WorkflowDocument} to the {@link org.kuali.rice.krad.UserSession}.
     *
     * @param userSession      the session to add the workflow document to
     * @param workflowDocument the workflow doc to add to the session
     */
    public static void addWorkflowDocument(UserSession userSession, WorkflowDocument workflowDocument) {
        Map<String, WorkflowDocument> workflowDocMap = getWorkflowDocumentMap(userSession);

        workflowDocMap.put(workflowDocument.getDocumentId(), workflowDocument);
    }

    /**
     * Returns the {@link org.kuali.rice.kew.api.WorkflowDocument} with the given ID from the
     * {@link org.kuali.rice.krad.UserSession}.  If there is not one cached in the session with
     * that ID, then null is returned.
     *
     * @param userSession        the user session from which to retrieve the workflow document
     * @param workflowDocumentId the ID of the workflow document to get
     * @return the cached workflow document, or null if a document with that ID is not cached in the user session
     */
    public static WorkflowDocument getWorkflowDocument(UserSession userSession, String workflowDocumentId) {
        Map<String, WorkflowDocument> workflowDocMap = getWorkflowDocumentMap(userSession);

        return workflowDocMap.get(workflowDocumentId);
    }

    /**
     * Returns the map of workflow document IDs to {@link org.kuali.rice.kew.api.WorkflowDocument}, making sure to
     * initialize in a thread-safe way if the map does not exist.
     * <p>
     * <p>
     * We assume the {@link org.kuali.rice.krad.UserSession} is not null here.
     * </p>
     *
     * @param userSession the user session from which to retrieve the workflow document
     * @return the map of workflow document IDs to workflow documents
     */
    @SuppressWarnings("unchecked")
    private static Map<String, WorkflowDocument> getWorkflowDocumentMap(UserSession userSession) {
        userSession.addObjectIfAbsent(
            KewApiConstants.WORKFLOW_DOCUMENT_MAP_ATTR_NAME, new ConcurrentHashMap<String, WorkflowDocument>());

        return (ConcurrentMap<String, WorkflowDocument>) userSession.retrieveObject(
            KewApiConstants.WORKFLOW_DOCUMENT_MAP_ATTR_NAME);
    }
}
