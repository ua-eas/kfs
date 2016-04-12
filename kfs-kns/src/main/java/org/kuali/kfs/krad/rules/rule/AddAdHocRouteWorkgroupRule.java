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
package org.kuali.kfs.krad.rules.rule;

import org.kuali.kfs.krad.bo.AdHocRouteWorkgroup;
import org.kuali.kfs.krad.document.Document;

/**
 * Defines a rule which gets invoked immediately before a document has an adHocRouteWorkgroup added to it.
 * 
 * 
 */
public interface AddAdHocRouteWorkgroupRule extends BusinessRule {
    /**
     * This method is responsible for housing business rules that need to be checked before a document adHocRouteWorkgroup is added
     * to a document.
     * 
     * @param document
     * @param adHocRouteWorkgroup
     * @return false if the rule fails
     */
    public boolean processAddAdHocRouteWorkgroup(Document document, AdHocRouteWorkgroup adHocRouteWorkgroup);
}
