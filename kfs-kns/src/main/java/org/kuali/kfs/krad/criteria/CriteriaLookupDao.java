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
package org.kuali.kfs.krad.criteria;

import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

/**
 * Defines basic methods that CriteriaLookup Dao's must provide
 *
 *
 */
public interface CriteriaLookupDao {

    /**
     * Looks up a type based on a query criteria.
     *
     * @param queryClass the class to lookup
     * @param criteria the criteria to lookup against. cannot be null.
     * @param <T> the type that is being looked up.
     * @return the results. will never be null.
     * @throws IllegalArgumentException if the criteria is null
     */
    <T> GenericQueryResults<T> lookup(final Class<T> queryClass, final QueryByCriteria criteria);

    /**
     * Looks up a type based on a query criteria.
     *
     * @param queryClass the class to lookup
     * @param criteria the criteria to lookup against. cannot be null.
     * @param <T> the type that is being looked up.
     * @return the results. will never be null.
     * @throws IllegalArgumentException if the criteria is null
     */
    <T> GenericQueryResults<T> lookup(final Class<T> queryClass, final QueryByCriteria criteria, LookupCustomizer<T> customizer);
}
