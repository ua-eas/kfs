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

import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.view.View;

import java.util.Map;

/**
 * Provides service methods for retrieving and updating <code>View</code> instances. The UIF
 * interacts with this service from the client layer to pull information from the View dictionary
 * and manage the View instance through its lifecycle
 *
 *
 */
public interface ViewService {

    /**
     * Returns the <code>View</code> entry identified by the given id
     *
     * <p>
     * The id matches the id configured for the View through the dictionary. A new view instance
     * is returned that is in the created state
     * </p>
     *
     * @param viewId - unique id for view configured on its definition
     * @return View instance associated with the id or Null if id is not found
     */
    public View getViewById(String viewId);

    /**
     * Retrieves the <code>View</code> instance that is of the given view type and matches the
     * given parameters (that are applicable for that type). If more than one views exists for the
     * type and parameters, the view type may choose a default or throw an exception
     *
     * <p>
     * If a view if found for the type parameters, a new instance is returned that is in the
     * created state
     * </p>
     *
     * @param viewType - name that identifies the view type
     * @param parameters - Map of parameter key/value pairs that are used to select the
     * view, the parameters allowed depend on the view type
     * @return View instance or Null if a matching view was not found
     */
    public View getViewByType(UifConstants.ViewType viewType, Map<String, String> parameters);

    /**
     * Executes the view lifecycle on the given <code>View</code> instance which will
     * prepare it for rendering
     *
     * <p>
     * Any configuration sent through the options Map is used to initialize the
     * View. This map contains present options the view is aware of and will
     * typically come from request parameters. e.g. For maintenance Views there
     * is the maintenance type option (new, edit, copy)
     * </p>
     *
     * <p>
     * After view retrieval, applies updates to the view based on the model data which
     * Performs dynamic generation of fields (such as collection rows),
     * conditional logic, and state updating (conditional hidden, read-only,
     * required).
     * </p>
     *
     * @param view - view instance that should be built
     * @param model - object instance containing the view data
     * @param parameters - Map of key values pairs that provide configuration for the
     * <code>View</code>, this is generally comes from the request
     * and can be the request parameter Map itself. Any parameters
     * not valid for the View will be filtered out
     */
    public void buildView(View view, Object model, Map<String, String> parameters);

    // TODO: remove once can get beans by type
    public ViewTypeService getViewTypeService(UifConstants.ViewType viewType);

}
