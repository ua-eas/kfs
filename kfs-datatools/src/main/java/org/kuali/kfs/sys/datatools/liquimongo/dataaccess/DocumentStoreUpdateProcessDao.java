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
package org.kuali.kfs.sys.datatools.liquimongo.dataaccess;


import org.kuali.kfs.sys.datatools.liquimongo.businessobject.DocumentStoreChange;

/**
 * DAO to keep track of all the schema changes made to the MongoDB database.
 */
public interface DocumentStoreUpdateProcessDao {
    /**
     * Collection name for changes
     */
    String CHANGE_SCHEMA = "Schema";

    /**
     * Check to see if schema change process is locked.
     *
     * @return true if locked
     */
    boolean isSchemaChangeLocked();

    /**
     * Add a lock on the schema change process
     */
    void lockSchemaChange();

    /**
     * Remove the lock on the schema change process
     */
    void unlockSchemaChange();

    /**
     * Determine if a requested change has been made.
     *
     * @param change change requested
     * @return True if change has already been made, False if not
     */
    boolean hasSchemaChangeHappened(DocumentStoreChange change);

    /**
     * Record that the requested change has been made.
     *
     * @param change change requested
     */
    void saveSchemaChange(DocumentStoreChange change);
}
