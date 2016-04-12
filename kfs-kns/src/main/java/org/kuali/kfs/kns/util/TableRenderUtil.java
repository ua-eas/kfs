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
package org.kuali.kfs.kns.util;

/**
 * This class provides utilities to support the rendering of tables in Kuali without using display tag.
 * 
 * Normally, displaytag handles the rendering of Kuali tables on various screens, but
 * there are situations where displaytag is inadequate for the task (e.g. multiple value lookups).
 * In particular, display tag does not handle POSTing of forms when switching between pages and sorting.
 * 
 */
public final class TableRenderUtil {
	
	private TableRenderUtil() {
		throw new UnsupportedOperationException("do not call");
	}
	
    /**
     * Returns the minimum number of pages needed to display a result set of the given page
     * 
     * @param resultSize number of results
     * @param maxRowsPerPage maximum number of rows 
     * 
     * @return
     */
    public static int computeTotalNumberOfPages(int resultSize, int maxRowsPerPage) {
        int numPages = resultSize / maxRowsPerPage;
        if (resultSize % maxRowsPerPage != 0) {
            // partial page
            numPages++;
        }
        return numPages;
    }
    
    /**
     * This method computes the list index of the first row of the given page
     * 
     * @param pageNumber first page is index 0
     * @param resultSize the size of the list being rendered
     * @param maxRowsPerPage max number of rows on a page
     * @return the index in the result list of the first row of the given page 
     */
    public static int computeStartIndexForPage(int pageNumber, int resultSize, int maxRowsPerPage) {
        if (pageNumber < 0 && pageNumber >= computeTotalNumberOfPages(resultSize, maxRowsPerPage)) {
            return -1;
        }
        return pageNumber * maxRowsPerPage;
    }
    
    /**
     * This method computes the index of the last row of the given page
     * 
     * @param pageNumber first page is index 0
     * @param resultSize the size of the list being rendered
     * @param maxRowsPerPage max number of rows on a page
     * @return the index in the result list of the last row of the given page 
     */
    public static int computeLastIndexForPage(int pageNumber, int resultSize, int maxRowsPerPage) {
        int startIndex = computeStartIndexForPage(pageNumber, resultSize, maxRowsPerPage);
        if (startIndex == -1) {
            return -1;
        }
        if (startIndex + maxRowsPerPage - 1 < resultSize) {
            return startIndex + maxRowsPerPage - 1;
        }
        // partial page
        return resultSize - 1;
   }
}
