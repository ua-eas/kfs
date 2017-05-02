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
package org.kuali.kfs.sys.document.validation.event;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.event.SaveOnlyDocumentEvent;

/**
 * This class...
 */
public class DocumentSystemSaveEvent extends SaveOnlyDocumentEvent {

    /**
     * Constructs a DocumentSystemSaveEvent with the specified errorPathPrefix and document. Event will
     *
     * @param document
     * @param errorPathPrefix
     */
    public DocumentSystemSaveEvent(String errorPathPrefix, Document document) {
        super("creating unvalidated save event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * METHOD ALWAYS RETURNS TRUE
     *
     * @see org.kuali.kfs.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.kfs.krad.rule.BusinessRule)
     */
    @Override
    public boolean invokeRuleMethod(BusinessRule arg0) {
        return true;
    }

}
