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

import java.util.List;

/**
 * Parent interface of all document-related events, which are used to drive the business rules evaluation process.
 */
public interface KualiDocumentEvent {
    /**
     * @return Document The document associated with this event
     */
    public Document getDocument();

    /**
     * The name of the event.
     *
     * @return String
     */
    public String getName();

    /**
     * A description of the event.
     *
     * @return String
     */
    public String getDescription();


    /**
     * @return errorPathPrefix for this event
     */
    public String getErrorPathPrefix();

    /**
     * Returns the interface that classes must implement to receive this event.
     *
     * @return
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass();

    /**
     * Validates the event has all the necessary properties.
     */
    public void validate();

    /**
     * Invokes the event handling method on the rule object.
     *
     * @param rule
     * @return
     */
    public boolean invokeRuleMethod(BusinessRule rule);

    /**
     * This will return a list of events that are spawned from this event.
     *
     * @return
     */
    public List<KualiDocumentEvent> generateEvents();
}
