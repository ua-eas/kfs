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

import java.util.Collection;
import java.util.Map;

/**
 * Defines business logic methods that support the Lookup framework
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LookupService {

    /**
     * Returns a collection of objects based on the given search parameters.
     * Will not limit results, so the returned Collection could be huge.
     *                                                         o
     * @param example
     * @param formProps
     * @return
     */
    public <T extends Object> Collection<T> findCollectionBySearchUnbounded(Class<T> example,
            Map<String, String> formProps);

    /**
     * Returns a collection of objects based on the given search parameters.
     * 
     * @return Collection returned from the search
     */
    public <T extends Object> Collection<T> findCollectionBySearch(Class<T> example, Map<String, String> formProps);

    public <T extends Object> Collection<T> findCollectionBySearchHelper(Class<T> example,
            Map<String, String> formProperties, boolean unbounded);

    /**
     * Retrieves a Object based on the search criteria, which should uniquely
     * identify a record.
     * 
     * @return Object returned from the search
     */
    public <T extends Object> T findObjectBySearch(Class<T> example, Map<String, String> formProps);

    public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps);
}
