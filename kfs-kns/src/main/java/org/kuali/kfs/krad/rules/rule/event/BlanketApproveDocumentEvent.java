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
package org.kuali.kfs.krad.rules.rule.event;

import org.kuali.kfs.krad.document.Document;

/**
 * This class represents the blanketApprove event that is part of an eDoc in Kuali. This could be triggered when a user presses the
 * blanketApprove button for a given document enroute or it could happen when another piece of code calls the blanketApprove method
 * in the document service.
 */
public final class BlanketApproveDocumentEvent extends ApproveDocumentEvent {
    /**
     * Constructs an BlanketApproveDocumentEvent with the specified errorPathPrefix and document
     *
     * @param errorPathPrefix
     * @param document
     */
    public BlanketApproveDocumentEvent(String errorPathPrefix, Document document) {
        super("blanketApprove", errorPathPrefix, document);
    }

    /**
     * Constructs a BlanketApproveDocumentEvent with the given document
     *
     * @param document
     */
    public BlanketApproveDocumentEvent(Document document) {
        super("blanketApprove", "", document);
    }
}
