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
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.RouteDocumentRule;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the route event that is part of an eDoc in Kuali. This could be triggered when a user presses the route
 * button for a given document or it could happen when another piece of code calls the route method in the document service.
 */
public final class RouteDocumentEvent extends KualiDocumentEventBase {
    /**
     * Constructs a RouteDocumentEvent with the specified errorPathPrefix and document
     *
     * @param errorPathPrefix
     * @param document
     */
    public RouteDocumentEvent(String errorPathPrefix, Document document) {
        super("creating route event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a RouteDocumentEvent with the given document
     *
     * @param document
     */
    public RouteDocumentEvent(Document document) {
        this("", document);
    }

    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return RouteDocumentRule.class;
    }

    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((RouteDocumentRule) rule).processRouteDocument(document);
    }

    /**
     * @see KualiDocumentEvent#generateEvents()
     */
    @Override
    public List<KualiDocumentEvent> generateEvents() {
        List<KualiDocumentEvent> events = new ArrayList<KualiDocumentEvent>();
        events.add(new SaveDocumentEvent(getDocument()));
        return events;
    }
}
