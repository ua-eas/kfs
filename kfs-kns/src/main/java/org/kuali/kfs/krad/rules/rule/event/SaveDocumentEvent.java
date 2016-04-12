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
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.SaveDocumentRule;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiRuleService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the save event that is part of an eDoc in Kuali. This could be triggered when a user presses the save
 * button for a given document or it could happen when another piece of code calls the save method in the document service.
 * 
 * 
 */
public class SaveDocumentEvent extends KualiDocumentEventBase implements SaveEvent {
    /**
     * Constructs a SaveDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param document
     * @param errorPathPrefix
     */
    public SaveDocumentEvent(String errorPathPrefix, Document document) {
        this("creating save event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a SaveDocumentEvent with the given document
     * 
     * @param document
     */
    public SaveDocumentEvent(Document document) {
        this("", document);
    }
    
    /**
     * @see KualiDocumentEventBase#KualiDocumentEventBase(java.lang.String, java.lang.String, Document)
     */
    public SaveDocumentEvent(String description, String errorPathPrefix, Document document) {
	super(description, errorPathPrefix, document);
    }
    
    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return SaveDocumentRule.class;
    }

    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((SaveDocumentRule) rule).processSaveDocument(document);
    }

    /**
     * @see KualiDocumentEvent#generateEvents()
     */
    @Override
    public List<KualiDocumentEvent> generateEvents() {
        KualiRuleService ruleService = KRADServiceLocatorWeb.getKualiRuleService();

        List<KualiDocumentEvent> events = new ArrayList<KualiDocumentEvent>();
        events.addAll(ruleService.generateAdHocRoutePersonEvents(getDocument()));
        events.addAll(ruleService.generateAdHocRouteWorkgroupEvents(getDocument()));
        
        events.addAll(getDocument().generateSaveEvents());

        /*
        if (getDocument() instanceof CashReceiptDocument) {
            events.addAll(ruleService.generateCheckEvents((CashReceiptDocument) getDocument()));
        }

        if (getDocument() instanceof AccountingDocument) {
            events.addAll(ruleService.generateAccountingLineEvents((AccountingDocument) getDocument()));
        }
        */
        return events;
    }
}
