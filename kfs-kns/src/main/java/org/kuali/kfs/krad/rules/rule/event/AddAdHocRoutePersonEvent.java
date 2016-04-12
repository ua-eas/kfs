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

import org.kuali.kfs.krad.bo.AdHocRoutePerson;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.AddAdHocRoutePersonRule;
import org.kuali.kfs.krad.rules.rule.BusinessRule;

/**
 * This class represents the add AdHocRoutePerson event that is part of an eDoc in Kuali. This is triggered when a user presses the
 * add button for a given adHocRoutePerson.
 * 
 * 
 */
public final class AddAdHocRoutePersonEvent extends KualiDocumentEventBase {
    private AdHocRoutePerson adHocRoutePerson;

    /**
     * Constructs an AddAdHocRoutePersonEvent with the specified errorPathPrefix, document, and adHocRoutePerson
     * 
     * @param document
     * @param adHocRoutePerson
     * @param errorPathPrefix
     */
    public AddAdHocRoutePersonEvent(String errorPathPrefix, Document document, AdHocRoutePerson adHocRoutePerson) {
        super("creating add ad hoc route person event for document " + getDocumentId(document), errorPathPrefix, document);
        this.adHocRoutePerson = adHocRoutePerson;
    }

    /**
     * Constructs an AddAdHocRoutePersonEvent with the given document
     * 
     * @param document
     * @param adHocRoutePerson
     */
    public AddAdHocRoutePersonEvent(Document document, AdHocRoutePerson adHocRoutePerson) {
        this("", document, adHocRoutePerson);
    }

    /**
     * This method retrieves the document adHocRoutePerson associated with this event.
     * 
     * @return AdHocRoutePerson
     */
    public AdHocRoutePerson getAdHocRoutePerson() {
        return adHocRoutePerson;
    }

    /**
     * @see KualiDocumentEvent#validate()
     */
    @Override
    public void validate() {
        super.validate();
        if (this.adHocRoutePerson == null) {
            throw new IllegalArgumentException("invalid (null) document adHocRoutePerson");
        }
    }

    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return AddAdHocRoutePersonRule.class;
    }

    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddAdHocRoutePersonRule) rule).processAddAdHocRoutePerson(getDocument(), this.adHocRoutePerson);
    }
}
