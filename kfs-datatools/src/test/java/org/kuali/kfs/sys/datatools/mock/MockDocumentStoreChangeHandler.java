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
package org.kuali.kfs.sys.datatools.mock;

import org.kuali.kfs.sys.datatools.liquimongo.change.DocumentStoreChangeHandler;
import com.fasterxml.jackson.databind.JsonNode;

public class MockDocumentStoreChangeHandler implements DocumentStoreChangeHandler {
    public int changesMade = 0;

    @Override
    public boolean handlesChange(JsonNode change) {
        return true;
    }

    @Override
    public void makeChange(JsonNode change) {
        changesMade++;
    }

    @Override
    public void revertChange(JsonNode change) {
        changesMade--;        
    }

}
