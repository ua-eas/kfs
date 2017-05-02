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
package org.kuali.kfs.krad.maintenance;

import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.rice.kim.api.identity.Person;


public interface MaintenanceDocumentAuthorizer extends DocumentAuthorizer {

    public boolean canCreate(Class boClass, Person user);

    public boolean canMaintain(Object dataObject, Person user);

    public boolean canCreateOrMaintain(MaintenanceDocument maintenanceDocument, Person user);

}
