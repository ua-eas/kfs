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

package org.kuali.kfs.apitest.utils;

import java.util.List;
import java.util.Map;

public class SearchResult {
    private int totalCount;
    private int limit;
    private int skip;
    private List<Map<String, Object>> results;

    protected SearchResult(int totalCount, int limit, int skip, List<Map<String, Object>> results) {
        this.totalCount = totalCount;
        this.limit = limit;
        this.skip = skip;
        this.results = results;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getLimit() {
        return limit;
    }

    public int getSkip() {
        return skip;
    }

    public List<Map<String, Object>> getResults() {
        return results;
    }

    public Map<String, Object> getFirstResult() {
        return results.get(0);
    }

    public Map<String, Object> getLastResult() {
        return results.get(results.size()-1);
    }

    public int getResultsCount() {
        return results.size();
    }
}
