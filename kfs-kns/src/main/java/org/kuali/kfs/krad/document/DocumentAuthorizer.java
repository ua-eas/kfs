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

import org.kuali.kfs.krad.bo.DataObjectAuthorizer;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Authorizer class for {@link Document} instances
 * <p>
 * <p>
 * Authorizer provides user based authorization
 * </p>
 * <p>
 * <p>
 * The document authorizer is associated with a document type through its data dictionary
 * {@link DocumentEntry}. This is then used by the framework to authorize certain
 * actions and in addition used for view presentation logic
 * </p>
 */
public interface DocumentAuthorizer extends DataObjectAuthorizer {

    public boolean canInitiate(String documentTypeName, Person user);

    public boolean canOpen(Document document, Person user);

    public boolean canEdit(Document document, Person user);

    public boolean canAnnotate(Document document, Person user);

    public boolean canReload(Document document, Person user);

    public boolean canClose(Document document, Person user);

    public boolean canSave(Document document, Person user);

    public boolean canRoute(Document document, Person user);

    public boolean canCancel(Document document, Person user);

    public boolean canCopy(Document document, Person user);

    public boolean canPerformRouteReport(Document document, Person user);

    public boolean canBlanketApprove(Document document, Person user);

    public boolean canApprove(Document document, Person user);

    public boolean canDisapprove(Document document, Person user);

    public boolean canSendNoteFyi(Document document, Person user);

    public boolean canEditDocumentOverview(Document document, Person user);

    public boolean canFyi(Document document, Person user);

    public boolean canAcknowledge(Document document, Person user);

    public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode);

    public boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user);

    public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode,
                                           String authorUniversalIdentifier, Person user);

    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, String authorUniversalIdentifier,
                                         Person user);

    public boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user);

    public boolean canSendAnyTypeAdHocRequests(Document document, Person user);

    public boolean canTakeRequestedAction(Document document, String actionRequestCode, Person user);

    /**
     * @since 2.1
     */
    public boolean canRecall(Document document, Person user);
}
