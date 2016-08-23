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
package org.kuali.kfs.coreservice.framework.parameter;

import org.kuali.kfs.coreservice.api.parameter.ParameterTypeContract;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * TODO: Likely should remove all methods from this interface after KULRICE-7170 is fixed
 */
public interface ParameterTypeEbo extends ParameterTypeContract,
    ExternalizableBusinessObject, MutableInactivatable {

    /**
     * This the name for the ParameterType.  This can be null or a blank string.
     *
     * @return the name of the ParameterType
     */
    String getName();

    /**
     * Returns the version number for this object.  In general, this value should only
     * be null if the object has not yet been stored to a persistent data store.
     * This version number is generally used for the purposes of optimistic locking.
     *
     * @return the version number, or null if one has not been assigned yet
     */
    Long getVersionNumber();

    /**
     * Return the globally unique object id of this object.  In general, this value should only
     * be null if the object has not yet been stored to a persistent data store.
     *
     * @return the objectId of this object, or null if it has not been set yet
     */
    String getObjectId();

    /**
     * The active indicator for an object.
     *
     * @return true if active false if not.
     */
    boolean isActive();

    /**
     * Sets the record to active or inactive.
     */
    void setActive(boolean active);

    /**
     * The code value for this object.  In general a code value cannot be null or a blank string.
     *
     * @return the code value for this object.
     */
    String getCode();
}
