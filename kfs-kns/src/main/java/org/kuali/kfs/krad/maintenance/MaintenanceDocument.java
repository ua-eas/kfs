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

import org.kuali.kfs.krad.document.Document;

/**
 * Common interface for all maintenance documents.
 */
public interface MaintenanceDocument extends Document {

    /**
     * @return String containing the xml representation of the maintenance document
     */
    public String getXmlDocumentContents();

    /**
     * @return Maintainable which holds the new maintenance record
     */
    public Maintainable getNewMaintainableObject();

    /**
     * @return Maintainable which holds the old maintenance record
     */
    public Maintainable getOldMaintainableObject();

    /**
     * Sets the xml contents of the maintenance document
     *
     * @param documentContents - String xml
     */
    public void setXmlDocumentContents(String documentContents);

    /**
     * @param newMaintainableObject - Initializes the new maintainable
     */
    public void setNewMaintainableObject(Maintainable newMaintainableObject);

    /**
     * @param newMaintainableObject - Initializes the old maintainable
     */
    public void setOldMaintainableObject(Maintainable oldMaintainableObject);

    /**
     * Returns a reference to the data object that this MaintenanceDocument is maintaining
     */
    public Object getDocumentDataObject();

    /**
     * Builds the xml document string from the contents of the old and new maintainbles.
     */
    public void populateXmlDocumentContentsFromMaintainables();

    /**
     * Populates the old and new maintainables from the xml document contents string.
     */
    public void populateMaintainablesFromXmlDocumentContents();

    /**
     * @return boolean - indicates whether this is an edit or new maintenace document by the existence of an old maintainable
     */
    public boolean isOldDataObjectInDocument();

    /**
     * Returns true if this maintenance document is creating a new Business Object, false if its an edit.
     */
    public boolean isNew();

    /**
     * Returns true if this maintenance document is editing an existing Business Object, false if its creating a new one.
     */
    public boolean isEdit();

    /**
     * Returns true if this maintenance document is creating a new Business Object out of an existing Business Object,
     * for example, a new division vendor out of an existing parent vendor.
     */
    public boolean isNewWithExisting();

    /**
     * A flag which indicates whether the primary keys have been cleared on a Copy-type of document. This will be true if the 'clear
     * keys on a copy' has been done, and it will be false if not.
     *
     * @return true if the primary keys have been cleared already, false if not.
     */
    public boolean isFieldsClearedOnCopy();

    /**
     * This method sets the value of the fieldsClearedOnCopy.
     *
     * @param fieldsClearedOnCopy - true or false
     */
    public void setFieldsClearedOnCopy(boolean keysClearedOnCopy);

    /**
     * This method...
     *
     * @return
     */
    public boolean getDisplayTopicFieldInNotes();

    /**
     * This method...
     */
    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes);

}
