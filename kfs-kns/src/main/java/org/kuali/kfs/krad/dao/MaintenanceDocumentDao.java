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
package org.kuali.kfs.krad.dao;

import java.util.List;

import org.kuali.kfs.krad.maintenance.MaintenanceLock;

/**
 * This interface defines basic methods that MaintenanceDocument Dao's must provide
 * 
 * 
 */
public interface MaintenanceDocumentDao {

//    public Collection getPendingDocumentsForClass(Class dataObjectClass);

    /**
     * 
     * This method looks for a document that is locking the given lockingRepresentation. If one is found, then it
     * retrieves the documentNumber, and returns it.
     * 
     * @param lockingRepresentation - locking representation to check for
     * @param documentNumber - document number to ignore, optional argument
     * @return returns an empty string if no locking document is found, otherwise returns the documentNumber of the locking document
     * 
     */
    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber);

    /**
     * This method deletes the locks for the given document number.  It is called when the document is final,
     * thus it can be unlocked, or when the locks need to be regenerated (thus they get cleared first).
     * 
     * @param documentNumber - document number whose locks should be deleted
     */
    public void deleteLocks(String documentNumber);

    /**
     * This method stores the given list of maintenance locks.  Typically these will all be for the same document.
     * 
     * @param maintenanceLocks - the list of maintenance locks to be stored
     */
    public void storeLocks(List<MaintenanceLock> maintenanceLocks);

}
