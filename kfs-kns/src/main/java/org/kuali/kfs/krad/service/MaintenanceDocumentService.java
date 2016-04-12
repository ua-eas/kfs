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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.maintenance.MaintenanceLock;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.List;
import java.util.Map;

/**
 * Provides methods for working with <code>MaintenanceDocument</code>(s)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MaintenanceDocumentService {

	/**
	 * Prepares the <code>MaintenanceDocument</code> on initial request
	 * 
	 * <p>
	 * This includes retrieving the data object for edit or copy, clearing
	 * fields
	 * </p>
	 * 
	 * @param objectClassName
	 *            - class name for the object being maintained
	 * @param docTypeName
	 *            - workflow doc type for the maintenance document requested
	 * @param maintenanceAction
	 *            - indicates whether this is a new, copy, or edit maintenance
	 *            action
	 * @return MaintenanceDocument prepared document instance
	 */
	public MaintenanceDocument setupNewMaintenanceDocument(
			String objectClassName, String docTypeName, String maintenanceAction);

    /**
     * Called to setup the object being maintained
     *
     * <p>
     * For edit and copy actions, the old record is retrieved and prepared for
     * editing (in the case of a copy some fields are cleared). In addition some
     * authorization checks are performed and hooks for custom
     * <code>Maintainble</code> implementations are invoked.
     * </p>
     *
     * @param document - document instance for the maintenance object
     * @param maintenanceAction - the requested maintenance action (new, new with existing,
     * copy, edit)
     * @param requestParameters - Map of parameters from the request
     */
    public void setupMaintenanceObject(MaintenanceDocument document, String maintenanceAction,
            Map<String, String[]> requestParameters);

    /**
	 * Attempts to find any other active documents that are pending on the same
	 * maintenance record.
	 * 
	 * If any are pending and locked, thereby blocking this document, then the
	 * docHeaderId/documentNumber of the blocking locked document is returned.
	 * 
	 * Otherwise, if nothing is blocking, then null is returned.
	 * 
	 * @param document
	 *            - document to test
	 * @return A String representing the docHeaderId of any blocking document,
	 *         or null if none are blocking
	 * 
	 */
	public String getLockingDocumentId(MaintenanceDocument document);

	/**
	 * Attempts to find any other active documents that are pending on the same
	 * maintenance record.
	 * 
	 * If any are pending and locked, thereby blocking this document, then the
	 * docHeaderId/documentNumber of the blocking locked document is returned.
	 * 
	 * Otherwise, if nothing is blocking, then null is returned.
	 * 
	 * @param maintainable
	 *            - maintainable representing the document to test
	 * @param documentNumber
	 *            - the documentNumber/docHeaderId of the document to test
	 * @return A String representing the docHeaderId of any blocking document,
	 *         or null if none are blocking
	 */
	public String getLockingDocumentId(Maintainable maintainable,
			String documentNumber);

	/**
	 * Call the same-named method in the Dao, since the service has access to
	 * the Dao, but the caller doesn't.
	 * 
	 * This method deletes the locks for the given document number. It is called
	 * when the document is final, thus it can be unlocked, or when the locks
	 * need to be regenerated (thus they get cleared first).
	 * 
	 * @param documentNumber
	 *            - document number whose locks should be deleted
	 */
	public void deleteLocks(String documentNumber);

	/**
	 * Call the same-named method in the Dao, since the service has access to
	 * the Dao, but the caller doesn't.
	 * 
	 * This method stores the given list of maintenance locks. Typically these
	 * will all be for the same document.
	 * 
	 * @param maintenanceLocks
	 *            - the list of maintenance locks to be stored
	 */
	public void storeLocks(List<MaintenanceLock> maintenanceLocks);

	/**
	 * This method takes a map on its way to populate a business object and replaces all
	 * user identifiers with their corresponding universal users
	 */
	public Map<String,String> resolvePrincipalNamesToPrincipalIds(BusinessObject businessObject, Map<String,String> fieldValues);

}
