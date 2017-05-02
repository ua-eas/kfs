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
package org.kuali.kfs.kns.web.struts.form;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.kns.util.PagingBannerUtils;
import org.kuali.kfs.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Form to handle multiple value lookups
 */
public class MultipleValueLookupForm extends LookupForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MultipleValueLookupForm.class);

    private KualiTableRenderFormMetadata tableMetadata;

    private String lookupResultsSequenceNumber;

    /**
     * @see LookupForm#addRequiredNonEditableProperties()
     */
    @Override
    public void addRequiredNonEditableProperties() {
        super.addRequiredNonEditableProperties();
        registerRequiredNonEditableProperty(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
        registerRequiredNonEditableProperty(KRADConstants.LOOKED_UP_COLLECTION_NAME);
    }

    /**
     * The number of rows that match the query criteria
     */
    private int resultsActualSize;

    /**
     * The number of rows that match the query criteria or
     * the max results limit size (if applicable), whichever is less
     */
    private int resultsLimitedSize;

    /**
     * when the looked results screen was rendered, the index of the column that the results were sorted on.  -1 for unknown, index numbers
     * starting at 0
     */
    private String previouslySortedColumnIndex;

    /**
     * Comment for <code>columnToSortIndex</code>
     */
    private int columnToSortIndex;

    /**
     * the name of the collection being looked up by the calling page.  This value will be returned unmodified to the
     * calling page (indicated by super.getBackLocation()), which should use it to determine in which collection the
     * selected results will be returned.
     */
    private String lookedUpCollectionName;

    /**
     * Those object IDs that were selected before the current page is rendered
     */
    private Set<String> previouslySelectedObjectIdSet;
    /**
     * Those object IDs that are rendered on the current page
     */
    private Set<String> displayedObjectIdSet;
    /**
     * Those object IDs that are selected/checked on the current page
     */
    private Set<String> selectedObjectIdSet;
    /**
     * The object IDs that are selected after the struts action is complete; the obj IDs in the keys of this Map will be considered checked in the UI
     */
    private Map<String, String> compositeObjectIdMap;

    public MultipleValueLookupForm() {
        tableMetadata = new KualiTableRenderFormMetadata();
        setHtmlDataType(HtmlData.INPUT_HTML_DATA_TYPE);
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        if (StringUtils.isNotBlank(request.getParameter(KRADConstants.TableRenderConstants.VIEWED_PAGE_NUMBER))) {
            setViewedPageNumber(Integer.parseInt(request.getParameter(KRADConstants.TableRenderConstants.VIEWED_PAGE_NUMBER)));
        } else {
            setViewedPageNumber(0); // first page is page 0
        }

        if (KRADConstants.TableRenderConstants.SWITCH_TO_PAGE_METHOD.equals(getMethodToCall())) {
            final String paramPrefix = KRADConstants.DISPATCH_REQUEST_PARAMETER + "." + KRADConstants.TableRenderConstants.SWITCH_TO_PAGE_METHOD + ".";
            setSwitchToPageNumber(PagingBannerUtils.getNumbericalValueAfterPrefix(paramPrefix, request.getParameterNames()));
            if (getSwitchToPageNumber() == -1) {
                throw new RuntimeException("Couldn't find page number");
            }
        }

        if (KRADConstants.TableRenderConstants.SORT_METHOD.equals(getMethodToCall())) {
            final String paramPrefix = KRADConstants.DISPATCH_REQUEST_PARAMETER + "." + KRADConstants.TableRenderConstants.SORT_METHOD + ".";
            setColumnToSortIndex(
                PagingBannerUtils.getNumbericalValueAfterPrefix(paramPrefix, request.getParameterNames()));
            if (getColumnToSortIndex() == -1) {
                throw new RuntimeException("Couldn't find column to sort");
            }
        }

        setPreviouslySelectedObjectIdSet(parsePreviouslySelectedObjectIds(request));
        setSelectedObjectIdSet(parseSelectedObjectIdSet(request));
        setDisplayedObjectIdSet(parseDisplayedObjectIdSet(request));

        setSearchUsingOnlyPrimaryKeyValues(parseSearchUsingOnlyPrimaryKeyValues(request));
        if (isSearchUsingOnlyPrimaryKeyValues()) {
            setPrimaryKeyFieldLabels(getLookupable().getPrimaryKeyFieldLabels());
        }
    }


    /**
     * This method converts the composite object IDs into a String
     *
     * @return
     */
    public String getCompositeSelectedObjectIds() {
        return LookupUtils.convertSetOfObjectIdsToString(getCompositeObjectIdMap().keySet());
    }

    protected Set<String> parsePreviouslySelectedObjectIds(HttpServletRequest request) {
        String previouslySelectedObjectIds = request.getParameter(KRADConstants.MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM);
        return LookupUtils.convertStringOfObjectIdsToSet(previouslySelectedObjectIds);
    }

    protected Set<String> parseSelectedObjectIdSet(HttpServletRequest request) {
        Set<String> set = new HashSet<String>();

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (paramName.startsWith(KRADConstants.MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX) && StringUtils.isNotBlank(request.getParameter(paramName))) {
                set.add(StringUtils.substringAfter(paramName, KRADConstants.MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX));
            }
        }
        return set;
    }

    protected Set<String> parseDisplayedObjectIdSet(HttpServletRequest request) {
        Set<String> set = new HashSet<String>();

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (paramName.startsWith(KRADConstants.MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX) && StringUtils.isNotBlank(request.getParameter(paramName))) {
                set.add(StringUtils.substringAfter(paramName, KRADConstants.MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX));
            }
        }
        return set;
    }

    /**
     * Iterates through the request params, looks for the parameter representing the method to call in the format like
     * methodToCall.sort.1.(::;true;::).x, and returns the boolean value in the (::; and ;::) delimiters.
     *
     * @param request
     * @return
     * @see MultipleValueLookupForm#parseSearchUsingOnlyPrimaryKeyValues(String)
     */
    protected boolean parseSearchUsingOnlyPrimaryKeyValues(HttpServletRequest request) {
        // the param we're looking for looks like: methodToCall.sort.1.(::;true;::).x , we want to parse out the "true" component
        String paramPrefix = KRADConstants.DISPATCH_REQUEST_PARAMETER + "." + getMethodToCall() + ".";
        for (Enumeration i = request.getParameterNames(); i.hasMoreElements(); ) {
            String parameterName = (String) i.nextElement();
            if (parameterName.startsWith(paramPrefix) && parameterName.endsWith(".x")) {
                return parseSearchUsingOnlyPrimaryKeyValues(parameterName);
            }
        }
        // maybe doing an initial search, so no value will be present
        return false;
    }

    /**
     * Parses the method to call parameter passed in as a post parameter
     * <p>
     * The parameter should be something like methodToCall.sort.1.(::;true;::).x, this method will return the value
     * between (::; and ;::) as a boolean
     *
     * @param methodToCallParam the method to call in a format described above
     * @return the value between the delimiters, false if there are no delimiters
     */
    protected boolean parseSearchUsingOnlyPrimaryKeyValues(String methodToCallParam) {
        String searchUsingOnlyPrimaryKeyValuesStr = StringUtils.substringBetween(methodToCallParam, KRADConstants.METHOD_TO_CALL_PARM12_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM12_RIGHT_DEL);
        if (StringUtils.isBlank(searchUsingOnlyPrimaryKeyValuesStr)) {
            return false;
        }
        return Boolean.parseBoolean(searchUsingOnlyPrimaryKeyValuesStr);
    }

    public int getViewedPageNumber() {
        return tableMetadata.getViewedPageNumber();
    }

    public void setViewedPageNumber(int pageNumberBeingViewedForMultivalueLookups) {
        tableMetadata.setViewedPageNumber(pageNumberBeingViewedForMultivalueLookups);
    }

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultSequenceNumber;
    }

    public int getTotalNumberOfPages() {
        return tableMetadata.getTotalNumberOfPages();
    }

    public void setTotalNumberOfPages(int totalNumberOfPages) {
        tableMetadata.setTotalNumberOfPages(totalNumberOfPages);
    }

    public int getFirstRowIndex() {
        return tableMetadata.getFirstRowIndex();
    }

    public void setFirstRowIndex(int firstRowIndex) {
        tableMetadata.setFirstRowIndex(firstRowIndex);
    }

    public int getLastRowIndex() {
        return tableMetadata.getLastRowIndex();
    }

    public void setLastRowIndex(int lastRowIndex) {
        tableMetadata.setLastRowIndex(lastRowIndex);
    }

    public int getSwitchToPageNumber() {
        return tableMetadata.getSwitchToPageNumber();
    }

    protected void setSwitchToPageNumber(int switchToPageNumber) {
        tableMetadata.setSwitchToPageNumber(switchToPageNumber);
    }

    public Set<String> getPreviouslySelectedObjectIdSet() {
        return previouslySelectedObjectIdSet;
    }

    public void setPreviouslySelectedObjectIdSet(Set<String> previouslySelectedObjectIds) {
        this.previouslySelectedObjectIdSet = previouslySelectedObjectIds;
    }

    public Set<String> getSelectedObjectIdSet() {
        return selectedObjectIdSet;
    }

    public void setSelectedObjectIdSet(Set<String> selectedObjectIdSet) {
        this.selectedObjectIdSet = selectedObjectIdSet;
    }

    public Set<String> getDisplayedObjectIdSet() {
        return displayedObjectIdSet;
    }

    public void setDisplayedObjectIdSet(Set<String> displayedObjectIdSet) {
        this.displayedObjectIdSet = displayedObjectIdSet;
    }

    public Map<String, String> getCompositeObjectIdMap() {
        return compositeObjectIdMap;
    }

    public void setCompositeObjectIdMap(Map<String, String> compositeObjectIdMap) {
        this.compositeObjectIdMap = compositeObjectIdMap;
    }

    public int getColumnToSortIndex() {
        return columnToSortIndex;
    }

    public void setColumnToSortIndex(int columnToSortIndex) {
        this.columnToSortIndex = columnToSortIndex;
    }

    public String getPreviouslySortedColumnIndex() {
        return previouslySortedColumnIndex;
    }

    public void setPreviouslySortedColumnIndex(String previouslySortedColumnIndex) {
        this.previouslySortedColumnIndex = previouslySortedColumnIndex;
    }

    /**
     * gets the name of the collection being looked up by the calling page.  This value will be returned unmodified to the
     * calling page (indicated by super.getBackLocation()), which should use it to determine in which collection the
     * selected results will be returned.
     *
     * @return
     */
    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    /**
     * sets the name of the collection being looked up by the calling page.  This value will be returned unmodified to the
     * calling page (indicated by super.getBackLocation()), which should use it to determine in which collection the
     * selected results will be returned
     *
     * @param lookedUpCollectionName
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    public int getResultsActualSize() {
        return resultsActualSize;
    }

    public void setResultsActualSize(int resultsActualSize) {
        this.resultsActualSize = resultsActualSize;
    }

    public int getResultsLimitedSize() {
        return resultsLimitedSize;
    }

    public void setResultsLimitedSize(int resultsLimitedSize) {
        this.resultsLimitedSize = resultsLimitedSize;
    }

    public void jumpToFirstPage(int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToFirstPage(listSize, maxRowsPerPage);
    }

    public void jumpToLastPage(int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToLastPage(listSize, maxRowsPerPage);
    }

    public void jumpToPage(int pageNumber, int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToPage(pageNumber, listSize, maxRowsPerPage);
    }
}
