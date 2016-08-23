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


/**
 * Determines what actions are applicable to the given document, irrespective of user
 * or other state.  These initial actions are used as inputs for further filtering depending
 * on context.
 * @see DocumentAuthorizer
 *
 */
public interface DocumentPresentationController {

    public boolean canInitiate(String documentTypeName);

    public boolean canEdit(Document document);

    public boolean canAnnotate(Document document);

    public boolean canReload(Document document);

    public boolean canClose(Document document);

    public boolean canSave(Document document);

    public boolean canRoute(Document document);

    public boolean canCancel(Document document);

    public boolean canCopy(Document document);

    public boolean canPerformRouteReport(Document document);

    public boolean canAddAdhocRequests(Document document);

    public boolean canBlanketApprove(Document document);

    public boolean canApprove(Document document);

    public boolean canDisapprove(Document document);

    public boolean canSendAdhocRequests(Document document);

    public boolean canSendNoteFyi(Document document);

    public boolean canEditDocumentOverview(Document document);

    public boolean canFyi(Document document);

    public boolean canAcknowledge(Document document);

    public boolean canComplete(Document document);

    /**
     * @since 2.1
     */
    public boolean canRecall(Document document);
}
