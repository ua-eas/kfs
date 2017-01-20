/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.sys.datatools.liquimongo.change;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Each type of change made to mongoDb will have an instance of this interface.
 */
public interface DocumentStoreChangeHandler {
    String COLLECTION_NAME = "collectionName";
    String QUERY = "query";
    String DOCUMENT = "document";

    /**
     * Determine if this change handler can handle the change being requested.
     *
     * @param change The change requested
     * @return True if it can make the change, false if not
     */
    boolean handlesChange(JsonNode change);

    /**
     * Make the requested change.
     *
     * @param change The change requested
     */
    void makeChange(JsonNode change);

}
