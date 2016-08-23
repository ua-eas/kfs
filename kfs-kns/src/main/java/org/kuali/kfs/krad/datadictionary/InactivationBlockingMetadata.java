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
package org.kuali.kfs.krad.datadictionary;

/**
 * This interface provides read-only metadata about inactivation blocking.  This metadata object is associated with a business object.
 * The source of this information often comes from the data dictionary file.
 */
public interface InactivationBlockingMetadata {

    /**
     * The property name of the reference that is blocked
     */
    public String getBlockedReferencePropertyName();

    /**
     * The type of the object that is blocked
     *
     * @return
     */
    public Class getBlockedBusinessObjectClass();

    /**
     * The bean name of the service that is responsible for determining whether there are any records that block inactivation
     */
    public String getInactivationBlockingDetectionServiceBeanName();

    /**
     * The type of the object that is blocking another record
     */
    public Class getBlockingReferenceBusinessObjectClass();

    /**
     * Returns the human-meaningful name of the relationship
     *
     * @return
     */
    public String getRelationshipLabel();
}
