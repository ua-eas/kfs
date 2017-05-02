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
package org.kuali.kfs.krad.rules.rule.event;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.CompleteDocumentRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Complete document event
 */
public class CompleteDocumentEvent extends KualiDocumentEventBase {

    /**
     * Constructs a RouteDocumentEvent with the specified errorPathPrefix and document
     *
     * @param errorPathPrefix
     * @param document
     */
    public CompleteDocumentEvent(String errorPathPrefix, Document document) {
        super("creating complete event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a RouteDocumentEvent with the given document
     *
     * @param document
     */
    public CompleteDocumentEvent(Document document) {
        this("", document);
    }

    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return CompleteDocumentRule.class;
    }

    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((CompleteDocumentRule) rule).processCompleteDocument(document);
    }

    /**
     * @see KualiDocumentEvent#generateEvents()
     */
    public List generateEvents() {
        List events = new ArrayList();
        events.add(new RouteDocumentEvent(getDocument()));
        return events;
    }
}
