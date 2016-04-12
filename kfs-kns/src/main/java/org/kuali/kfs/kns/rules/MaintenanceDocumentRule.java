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
package org.kuali.kfs.kns.rules;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.krad.rules.rule.ApproveDocumentRule;
import org.kuali.kfs.krad.rules.rule.RouteDocumentRule;
import org.kuali.kfs.krad.rules.rule.SaveDocumentRule;
import org.kuali.kfs.krad.rules.rule.event.ApproveDocumentEvent;

/**
 * Rule event interface for implementing business rules against a <code>MaintenanceDocument</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MaintenanceDocumentRule {

    /**
     * Runs all business rules needed prior to saving. This includes both common rules for all maintenance documents,
     * plus class-specific business rules.
     *
     * Will only return false if it fails the isValidForSave() test. Otherwise, it will always return positive
     * regardless of the outcome of the business rules. However, any error messages resulting from the business rules
     * will still be populated, for display to the consumer of this service.
     *
     * @see SaveDocumentRule#processSaveDocument(Document)
     */
    public abstract boolean processSaveDocument(Document document);

    /**
     * Runs all business rules needed prior to routing. This includes both common rules for all maintenance documents,
     * plus class-specific business rules.
     *
     * Will return false if any business rule fails, or if the document is in an invalid state, and not routable (see
     * isDocumentValidForRouting()).
     *
     * @see RouteDocumentRule#processRouteDocument(Document)
     */
    public abstract boolean processRouteDocument(Document document);

    /**
     * Runs all business rules needed prior to approving. This includes both common rules for all maintenance documents,
     * plus class-specific business rules.
     *
     * Will return false if any business rule fails, or if the document is in an invalid state, and not approvable (see
     * isDocumentValidForApproving()).
     *
     * @see ApproveDocumentRule#processApproveDocument(ApproveDocumentEvent)
     */
    public abstract boolean processApproveDocument(ApproveDocumentEvent approveEvent);

    /**
     * Sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     *
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their
     * primary keys, if available.
     *
     * @param document - the maintenanceDocument being evaluated
     */
    public void setupBaseConvenienceObjects(MaintenanceDocument document);

    /**
     * Should always be overriden if a subclass is created.
     *
     * The goal for this is to cast the oldBo and newBo into the correct types of the subclass.
     */
    public void setupConvenienceObjects();
}
