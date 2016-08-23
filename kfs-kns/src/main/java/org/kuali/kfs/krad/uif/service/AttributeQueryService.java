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
package org.kuali.kfs.krad.uif.service;

import org.kuali.kfs.krad.uif.field.AttributeQueryResult;
import org.kuali.kfs.krad.uif.view.View;

import java.util.Map;

/**
 * Provides methods for executing <code>AttributeQuery</code> instances
 * and preparing the <code>AttributeQueryResult</code> with the result of the query
 */
public interface AttributeQueryService {

    /**
     * Executes the <code>AttributeQuery</code> associated with the <code>Suggest</code> widget within
     * the field given by the Id. The given Map of key/value pairs are used to populate the criteria part of the
     * attribute query or as arguments to the query method. The fieldTerm parameter gives the current value
     * of the field that should be matched on. The query is expected to return a list of values to suggest
     *
     * @param view            - view instance for which the field belongs
     * @param fieldId         - id for the attribute field to perform the query for
     * @param fieldTerm       - the partial value of the query field to match
     * @param queryParameters - map of key/value pairs that are parameters to the query
     * @return AttributeQueryResult instance populated with the List<String> data field of result data
     */
    public AttributeQueryResult performFieldSuggestQuery(View view, String fieldId, String fieldTerm,
                                                         Map<String, String> queryParameters);

    /**
     * Executes the <code>AttributeQuery</code> associated with the field given by the id. The given Map of key/value
     * pairs are used to populate the criteria part of the attribute query or as arguments to the query method.
     * The query is expected to return a Map of field name/value pairs (unlike the suggest query which just returns
     * values for one field)
     *
     * @param view            - view instance for which the field belongs
     * @param fieldId         - id for the attribute field to perform the query for
     * @param queryParameters - map of key/value pairs that are parameters to the query
     * @return AttributeQueryResult instance populated with the Map<String, String> of result field data
     */
    public AttributeQueryResult performFieldQuery(View view, String fieldId, Map<String, String> queryParameters);
}
