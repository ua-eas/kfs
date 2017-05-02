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
package org.kuali.kfs.krad.bo;

import java.util.List;


/**
 * This is a marker interface used to determine whether we are dealing with a GlobalBusinessObject or something else
 * <p>
 * If implementations of this class implement {@link PersistableBusinessObject} as well, then it is strongly recommended that
 * classes override {@link PersistableBusinessObject#buildListOfDeletionAwareLists()} as well.  If this is not done correctly, then
 * deleted collection elements will not be persisted, and upon reload from the DB, the deleted items will appear in the collection.
 */
public interface GlobalBusinessObject {

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber();

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber);

    /**
     * This method applies the global changed fields to the list of BOs contained within, and returns the list, with all the
     * relevant values updated.
     *
     * @return Returns a List of BusinessObjects that are ready for persisting, with any relevant values changed
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist();

    /**
     * This method generates a list of BusinessObjects that need to be deleted as part of the final processing for a global
     * maintenance document. These records should be deleted before the records from getGlobalChangesToPersist() are persisted.
     *
     * @return A List of BusinessObjects that should be deleted as part of this global maint doc's final processing.
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist();

    /**
     * This method examines the underlying document and determines whether it can be persisted as part of the enclosing
     * MaintenanceDocument. If it returns false, then the Maintenance Document it is part of should not be saved, as a SQL Exception
     * is likely to result.
     *
     * @return True if the document can be safely persisted, False if not.
     */
    public boolean isPersistable();

    /**
     * Returns a list of all global detail objects on this document.  This method needs to return all detail
     * objects, even if they are of different types.
     *
     * @return
     */
    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects();
}
