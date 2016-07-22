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
package org.kuali.kfs.krad.uif.service;

import org.kuali.kfs.krad.uif.UifConstants;
import org.springframework.beans.PropertyValues;

import java.util.Map;

/**
 * Provides methods handing <code>View</code> instance of certain types
 * 
 * <p>
 * The type service is invoked to handle type parameters that can be used for
 * additional indexing of views and retrieval.
 * </p>
 * 
 * <p>
 * As the view dictionary entries are indexed the associated view type will be
 * retrieved and if there is an associated <code>ViewTypeService</code> it will
 * be invoked to provide parameter information for further indexing. This is
 * useful to index a view based on other properties, like a class name.
 * </p>
 * 
 * 
 */
public interface ViewTypeService {

	/**
	 * Gives the view type name that is supported by the type service
	 * 
	 * <p>
	 * The name is used to associated a type (and thus a view type service) with
	 * a view instance through the view type name property. Thus must be unique
	 * among all view types implemented
	 * </p>
	 * 
	 * @return ViewType view type name
	 */
	public UifConstants.ViewType getViewTypeName();

	/**
	 * Pulls values for the supported parameters from the views configured property values. These
     * name/value pairs are used to index the view for later retrieval
	 * 
	 * @param propertyValues - property values configured on the view bean definition
	 * @return Map<String, String> of parameters where map key is the parameter
	 *         name, and the map value is the parameter value
	 */
	public Map<String, String> getParametersFromViewConfiguration(PropertyValues propertyValues);

	/**
	 * Pulls entries from the given map that are supported parameters for the view type. In addition,
     * defaults can be set or additional parameters set as needed. Used by the <code>ViewService</code> to retrieve a
	 * <code>View</code> instance based on the incoming request parameters
	 * 
	 * @param requestParameters
	 *            - Map of request parameters to pull view type parameters from
	 * @return Map<String, String> of parameters where map key is the parameter
	 *         name, and the map value is the parameter value
	 */
	public Map<String, String> getParametersFromRequest(Map<String, String> requestParameters);

}
