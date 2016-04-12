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

import org.kuali.kfs.krad.bo.AdHocRouteWorkgroup;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.AddAdHocRouteWorkgroupRule;
import org.kuali.kfs.krad.rules.rule.BusinessRule;

/**
 * This class represents the add AdHocRouteWorkgroup event that is part of an eDoc in Kuali. This is triggered when a user presses
 * the add button for a given adHocRouteWorkgroup.
 *
 *
 */
public final class AddAdHocRouteWorkgroupEvent extends KualiDocumentEventBase {
    private AdHocRouteWorkgroup adHocRouteWorkgroup;

    /**
     * Constructs an AddAdHocRouteWorkgroupEvent with the specified errorPathPrefix, document, and adHocRouteWorkgroup
     *
     * @param document
     * @param adHocRouteWorkgroup
     * @param errorPathPrefix
     */
    public AddAdHocRouteWorkgroupEvent(String errorPathPrefix, Document document, AdHocRouteWorkgroup adHocRouteWorkgroup) {
        super("creating add ad hoc route workgroup event for document " + getDocumentId(document), errorPathPrefix, document);
        this.adHocRouteWorkgroup = adHocRouteWorkgroup;
    }

    /**
     * Constructs an AddAdHocRouteWorkgroupEvent with the given document
     *
     * @param document
     * @param adHocRouteWorkgroup
     */
    public AddAdHocRouteWorkgroupEvent(Document document, AdHocRouteWorkgroup adHocRouteWorkgroup) {
        this("", document, adHocRouteWorkgroup);
    }

    /**
     * This method retrieves the document adHocRouteWorkgroup associated with this event.
     *
     * @return AdHocRouteWorkgroup
     */
    public AdHocRouteWorkgroup getAdHocRouteWorkgroup() {
        return adHocRouteWorkgroup;
    }

    /**
     * @see KualiDocumentEvent#validate()
     */
    @Override
    public void validate() {
        super.validate();
        if (this.adHocRouteWorkgroup == null) {
            throw new IllegalArgumentException("invalid (null) document adHocRouteWorkgroup");
        }
    }

    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return AddAdHocRouteWorkgroupRule.class;
    }

    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddAdHocRouteWorkgroupRule) rule).processAddAdHocRouteWorkgroup(getDocument(), this.adHocRouteWorkgroup);
    }
}
