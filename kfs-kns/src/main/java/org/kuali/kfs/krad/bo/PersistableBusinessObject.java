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
package org.kuali.kfs.krad.bo;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krad.bo.BusinessObject;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Declares a common interface for all {@link BusinessObject} classes which can have their
 * state persisted.  A business object which is persistable defines some additional methods
 * which allow for various operations to be executed that relate to the persistent nature of
 * the business object.  A persistable business object also has some additional data
 * attributes which include the version number, the object id, and the extension.
 * <p>
 * <p>The version number indicates the version of the business object when it was retrieved
 * from persistent storage.  This allows for services to check this version number
 * during persistence operations to prevent silent overwrites of business object state.
 * These kinds of scenarios might arise as a result of concurrent modification to the business
 * object in the persistent store (i.e. two web application users updating the same record
 * in a database).  The kind of check which would be performed using the version number is commonly
 * referred to as "optimistic locking".
 * <p>
 * <p>The object id represents a globally unique identifier for the business object.  In practice,
 * this can be used by other portions of the system to link to persistable business objects which
 * might be stored in different locations or even different persistent data stores.  In general, it
 * is not the responsibility of the client who implements a persistable business object to handle
 * generating this value.  The framework will handle this automatically at the point in time when
 * the business object is persisted.  If the client does need to do this themselves, however, care
 * should be taken that an appropriate globally unique value generator algorithm is used
 * (such as the one provided by {@link UUID}).
 * <p>
 * <p>The extension object is primarily provided for the purposes of allowing implementer
 * customization of the business object without requiring the original business object to be
 * modified.  The additional {@link PersistableBusinessObjectExtension} which is linked with the
 * parent business object can contain additional data attributes and methods.  The framework will
 * automatically request that this extension object be persisted when the parent business object
 * is persisted.  This is generally the most useful in cases where an application is defining
 * business objects that will be used in redistributable software packages (such as the
 * actual Kuali Foundation projects themselves).  If using the framework for the purposes
 * of implementing an internal application, the use of a business object extensions
 * is likely unnecessary.
 */
public interface PersistableBusinessObject extends BusinessObject, Versioned, GloballyUnique {

    /**
     * Sets the business object's version number.  It is rarely advisable
     * for client code to manually set this value as the framework should
     * generally handle the management of version numbers internally.
     *
     * @param versionNumber the version number to set on this business object
     */
    void setVersionNumber(Long versionNumber);

    /**
     * Sets the unique identifier for the object
     *
     * @param objectId
     */
    void setObjectId(String objectId);

    /**
     * Get the last modified date of this business object
     *
     * @return
     */
    Timestamp getModifyDate();

    /**
     * Update the modified date of this business object
     *
     * @param modifyDate modified date
     */
    void setModifyDate(Timestamp modifyDate);

    PersistableBusinessObjectExtension getExtension();

    void setExtension(PersistableBusinessObjectExtension extension);

    /**
     * @see BusinessObject#refresh()
     */
    void refreshNonUpdateableReferences();

    /**
     * This method is used to refresh a reference object that hangs off of a document. For example, if the attribute's keys were
     * updated for a reference object, but the reference object wasn't, this method would go out and retrieve the reference object.
     *
     * @param referenceObjectName
     */
    void refreshReferenceObject(String referenceObjectName);

    /**
     * If this method is not implemented appropriately for PersistableBusinessObject with collections, then PersistableBusinessObject with collections will not persist deletions correctly.
     * Elements that have been deleted will reappear in the DB after retrieval.
     *
     * @return List of collections which need to be monitored for changes by OJB
     */
    List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists();

    /**
     * Returns the boolean indicating whether this record is a new record of a maintenance document collection.
     * Used to determine whether the record can be deleted on the document.
     */
    boolean isNewCollectionRecord();

    /**
     * Sets the boolean indicating this record is a new record of a maintenance document collection.
     * Used to determine whether the record can be deleted on the document.
     */
    void setNewCollectionRecord(boolean isNewCollectionRecord);

    /**
     * Hook to link in any editable user fields.
     */
    void linkEditableUserFields();


}
