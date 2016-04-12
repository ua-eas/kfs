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
package org.kuali.kfs.krad.rules.rule.event;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.ApproveDocumentRule;
import org.kuali.kfs.krad.rules.rule.BusinessRule;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the approve event that is part of an eDoc in Kuali. This could be triggered when a user presses the approve
 * button for a given document enroute or it could happen when another piece of code calls the approve method in the document
 * service.
 * 
 * 
 */
public class ApproveDocumentEvent extends KualiDocumentEventBase {
    /**
     * Constructs an ApproveDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param errorPathPrefix
     * @param document
     */
    public ApproveDocumentEvent(String errorPathPrefix, Document document) {
        this("approve", errorPathPrefix, document);
    }

    /**
     * Constructs an ApproveDocumentEvent with the given document
     * 
     * @param document
     */
    public ApproveDocumentEvent(Document document) {
        this("approve", "", document);
    }

    /**
     * Constructs a ApproveDocumentEvent, allowing the eventType to be passed in so that subclasses can specify a more accurate
     * message.
     * 
     * @param eventType
     * @param errorPathPrefix
     * @param document
     */
    protected ApproveDocumentEvent(String eventType, String errorPathPrefix, Document document) {
        super("creating " + eventType + " event for document " + getDocumentId(document), errorPathPrefix, document);
    }


    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return ApproveDocumentRule.class;
    }

    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ApproveDocumentRule) rule).processApproveDocument(this);
    }

    /**
     * @see KualiDocumentEvent#generateEvents()
     */
    @Override
    public List<KualiDocumentEvent> generateEvents() {
        List<KualiDocumentEvent> events = new ArrayList<KualiDocumentEvent>();
        events.add(new RouteDocumentEvent(getDocument()));
        return events;
    }
}
