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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.kfs.krad.rules.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;

import java.util.List;


/**
 * Defines the interface to the business-rule evaluation service, used to evauluate document-type-specific business rules using
 * document-related events to drive the process.
 */
public interface KualiRuleService {

    /**
     * Retrieves and instantiates the businessRulesClass associated with the event's document type (if any), and calls the
     * appropriate process* method of that businessRule for handling the given event type. This is a helper method that takes in the
     * generic KualiDocumentEvent class and determines which event call to make.
     *
     * @param event
     * @return true if no rule is applied, or all rules are applied successfully, false otherwise
     */
    public boolean applyRules(KualiDocumentEvent event);

    /**
     * Builds a list containing ad hoc route person events appropriate for the context.
     *
     * @param document
     * @return List
     */
    public List<AddAdHocRoutePersonEvent> generateAdHocRoutePersonEvents(Document document);

    /**
     * Builds a list containing ad hoc route workgroup events appropriate for the context.
     *
     * @param document
     * @return List
     */
    public List<AddAdHocRouteWorkgroupEvent> generateAdHocRouteWorkgroupEvents(Document document);

    /**
     * Allows code in actions or business objects to directly access rule methods in the class.
     *
     * @param document
     * @param ruleInterface
     * @return BusinessRule
     */
    public BusinessRule getBusinessRulesInstance(Document document, Class<? extends BusinessRule> ruleInterface);
}
