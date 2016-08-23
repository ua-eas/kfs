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
package org.kuali.kfs.kns.rule.event;

import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.rule.AddCollectionLineRule;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEventBase;

@Deprecated
public class KualiAddLineEvent extends KualiDocumentEventBase {

    private PersistableBusinessObject bo;
    private String collectionName;

    public KualiAddLineEvent(Document document, String collectionName, PersistableBusinessObject addLine) {
        super("adding bo to document collection " + KualiDocumentEventBase.getDocumentId(document), "", document);

        this.bo = addLine;//(BusinessObject)ObjectUtils.deepCopy( addLine );
        this.collectionName = collectionName;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddCollectionLineRule) rule).processAddCollectionLineBusinessRules((MaintenanceDocument) getDocument(), collectionName, bo);
    }

    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return AddCollectionLineRule.class;
    }
}
