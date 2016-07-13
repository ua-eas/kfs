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

import java.util.Collection;
import java.util.Map;

/**
 * Defines basic methods that Lookup Dao's must provide
 */
public interface LookupDao {
	<T extends Object> Collection<T> findCollectionBySearchHelper(Class<T> example, Map<String, String> formProps, boolean unbounded, boolean usePrimaryKeyValuesOnly);

	/**
	 * Retrieves a Object based on the search criteria, which should uniquely
	 * identify a record.
	 *
	 * @return Object returned from the search
	 */
	<T extends Object> T findObjectByMap(T example, Map<String, String> formProps);

	/**
	 * Returns a count of objects based on the given search parameters.
	 *
	 * @return Long returned from the search
	 */
	Long findCountByMap(Object example, Map<String, String> formProps);

	/**
	 * Create OJB criteria based on business object, search field and value
	 *
	 * @return true if the criteria is created successfully; otherwise, return
	 *         false
	 */
	boolean createCriteria(Object example, String searchValue, String propertyName, Object criteria);

	/**
	 * Create OJB criteria based on business object, search field and value
	 *
	 * @return true if the criteria is created successfully; otherwise, return
	 *         false
	 */
	boolean createCriteria(Object example, String searchValue, String propertyName, boolean caseInsensitive, boolean treatWildcardsAndOperatorsAsLiteral, Object criteria);
}
