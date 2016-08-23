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
package org.kuali.kfs.krad.uif.container;

import org.kuali.kfs.krad.uif.view.View;

import java.io.Serializable;
import java.util.List;

/**
 * Provides filtering on collection data within a <code>CollectionGroup</code>
 */
public interface CollectionFilter extends Serializable {

    /**
     * Invoked to filter the collection data before the collection group is
     * built. Note the collection should be retrieved from the model and the valid
     * row indexes must be returned in the return list
     *
     * @param view            - view instance for the collection group
     * @param model           - object containing the view data and from which the collection should be pulled/updated
     * @param collectionGroup - collection group instance containing configuration for the collection
     * @return the list that contains valid row indexes
     */
    public List<Integer> filter(View view, Object model, CollectionGroup collectionGroup);
}
