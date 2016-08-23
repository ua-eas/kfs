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

import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Collection;
import java.util.Map;


/**
 * This class provides collection retrievals to populate key value pairs of business objects.
 *
 *
 */
public interface KeyValuesService {

    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type.
     *
     * @param clazz
     * @return
     */
    public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz);

    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type. Performs a sort on the result collection on the given sort
     * field.
     *
     * @param clazz
     * @param sortField - name of the field in the class to sort results by
     * @param sortAscending - boolean indicating whether to sort ascending or descending
     * @return
     */
    public <T extends BusinessObject> Collection<T> findAllOrderBy(Class<T> clazz, String sortField, boolean sortAscending);

    /**
     * This method retrieves a collection of business objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve business objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name and its expected value.
     *
     * @param clazz
     * @param fieldValues
     * @return
     */
    public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, Object> fieldValues);

    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type.
     *
     * @param clazz
     * @return
     */
    public <T extends BusinessObject> Collection<T> findAllInactive(Class<T> clazz);

}
