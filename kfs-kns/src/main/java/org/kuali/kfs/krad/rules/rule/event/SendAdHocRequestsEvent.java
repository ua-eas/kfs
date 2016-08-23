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
import org.kuali.kfs.krad.rules.rule.SendAdHocRequestsRule;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiRuleService;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a description of what this class does - wliang don't forget to fill this in.
 *
 *
 *
 */
public class SendAdHocRequestsEvent extends KualiDocumentEventBase {

    public SendAdHocRequestsEvent(String errorPathPrefix, Document document) {
        this("creating send adhoc requests event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a SaveDocumentEvent with the given document
     *
     * @param document
     */
    public SendAdHocRequestsEvent(Document document) {
        this("", document);
    }

    public SendAdHocRequestsEvent(String description, String errorPathPrefix, Document document) {
    	super(description, errorPathPrefix, document);
    }

	/**
	 * This overridden method ...
	 *
	 * @see KualiDocumentEvent#getRuleInterfaceClass()
	 */
	public Class<? extends BusinessRule> getRuleInterfaceClass() {
		return SendAdHocRequestsRule.class;
	}

	/**
	 * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
	 */
	public boolean invokeRuleMethod(BusinessRule rule) {
		return ((SendAdHocRequestsRule) rule).processSendAdHocRequests(document);
	}

	/**
	 * @see KualiDocumentEventBase#generateEvents()
	 */
	@Override
	public List<KualiDocumentEvent> generateEvents() {
		KualiRuleService ruleService = KRADServiceLocatorWeb.getKualiRuleService();

		List<KualiDocumentEvent> events = new ArrayList<KualiDocumentEvent>();
        events.addAll(ruleService.generateAdHocRoutePersonEvents(getDocument()));
        events.addAll(ruleService.generateAdHocRouteWorkgroupEvents(getDocument()));
        return events;
	}
}
