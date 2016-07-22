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
import org.kuali.kfs.krad.rules.rule.DocumentAuditRule;
import org.kuali.kfs.krad.rules.rule.BusinessRule;

/**
 * Event class for document audit  
 * 
 * 
 *
 */
public class DocumentAuditEvent extends KualiDocumentEventBase {
    
    /**
     * Constructs a RunAuditEvent with the given errorPathPrefix and document.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public DocumentAuditEvent(String errorPathPrefix, Document document) {
        super("Running audit on " + KualiDocumentEventBase.getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a RunAuditEvent with the given document.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public DocumentAuditEvent(Document document) {
        this("", document);
    }

    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return DocumentAuditRule.class;
    }

    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((DocumentAuditRule) rule).processRunAuditBusinessRules(getDocument());
    }
}

