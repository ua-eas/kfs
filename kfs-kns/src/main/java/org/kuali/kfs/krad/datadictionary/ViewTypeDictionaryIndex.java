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
package org.kuali.kfs.krad.datadictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds view index information for a view type, where the index keys are built
 * from the supported view type parameters
 */
public class ViewTypeDictionaryIndex {
    private Map<String, String> viewIndex;

    public ViewTypeDictionaryIndex() {
        viewIndex = new HashMap<String, String>();
    }

    public Map<String, String> getViewIndex() {
        return this.viewIndex;
    }

    public void setViewIndex(Map<String, String> viewIndex) {
        this.viewIndex = viewIndex;
    }

    public void put(String index, String beanName) {
        if (viewIndex.containsKey(index)) {
            throw new DataDictionaryException("Two Views must not share the same type index: " + index);
        }

        viewIndex.put(index, beanName);
    }

    public String get(String index) {
        if (viewIndex.containsKey(index)) {
            return viewIndex.get(index);
        }

        return null;
    }

}
