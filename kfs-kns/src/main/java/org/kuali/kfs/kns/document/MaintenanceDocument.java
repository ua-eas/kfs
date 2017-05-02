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
package org.kuali.kfs.kns.document;

import org.kuali.kfs.kns.maintenance.Maintainable;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;

/**
 * Common interface for all maintenance documents.
 */
public interface MaintenanceDocument extends org.kuali.kfs.krad.maintenance.MaintenanceDocument {

    /**
     * @return Maintainable which holds the new maintenance record
     */
    public Maintainable getNewMaintainableObject();

    /**
     * @return Maintainable which holds the old maintenance record
     */
    public Maintainable getOldMaintainableObject();

    /**
     * Returns a reference to the PersistableBusinessObject that this MaintenanceDocument is maintaining.
     */
    public PersistableBusinessObject getDocumentBusinessObject();

    /**
     * @return boolean - indicates whether this is an edit or new maintenace document by the existence of an old maintainable
     */
    public boolean isOldBusinessObjectInDocument();

}
