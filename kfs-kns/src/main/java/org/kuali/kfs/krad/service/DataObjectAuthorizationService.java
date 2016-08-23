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
package org.kuali.kfs.krad.service;

import org.kuali.rice.kim.api.identity.Person;

/**
 * Provides methods for checking authorization for actions
 * on a given data object class including the security of fields
 * within the class
 */
public interface DataObjectAuthorizationService {

    /**
     * Indicates whether the given attribute of the given data object class has any
     * security defined (such as read-only, masked, ...) and therefore data for the
     * attribute should be securely passed
     *
     * @param dataObjectClass - class that contains the attribute
     * @param attributeName   - name of the attribute (property) within the class
     * @return boolean true if the attribute should be secured, false if security is not needed
     */
    public boolean attributeValueNeedsToBeEncryptedOnFormsAndLinks(Class<?> dataObjectClass, String attributeName);

    /**
     * Indicates whether the given user has permission to create records of the given data
     * object class with the given document type
     *
     * @param dataObjectClass - class of data object to check authorization for
     * @param user            - person requesting action
     * @param docTypeName     - name of the document type that provides the action
     * @return boolean true if the user has create authorization, false if not
     */
    public boolean canCreate(Class<?> dataObjectClass, Person user, String docTypeName);

    /**
     * Indicates whether the given user has permission to maintain (edit/delete) the
     * give data object instance with the given document type
     *
     * @param dataObject  - data object instance to check authorization for
     * @param user        - person requesting action
     * @param docTypeName - name of the document type that provides the action
     * @return boolean true if the user has maintain authorization, false if not
     */
    public boolean canMaintain(Object dataObject, Person user, String docTypeName);
}
