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
package org.kuali.kfs.kns.web.ui;

import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;

import java.io.Serializable;
import java.util.List;

/**
 * 
 */
@Deprecated
public class ResultRow implements Serializable {
    private static final long serialVersionUID = 2880508981008533913L;
    private List<Column> columns;
    private String returnUrl;
    private String actionUrls;
    private String objectId;
    private String rowId;
    private BusinessObject businessObject;
    private HtmlData returnUrlHtmlData;
    /**
     * Whether to show the return URL (for single value lookups invoked from a document or nested lookup) or the return checkbox (for
     * multiple value lookups)
     */
    private boolean rowReturnable;

    public ResultRow(List<Column> columns, String returnUrl, String actionUrls) {
        this.columns = columns;
        this.returnUrl = returnUrl;
        this.actionUrls = actionUrls;
        this.rowReturnable = true;
    }

    /**
     * @return Returns the returnUrl.
     */
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     * @param returnUrl The returnUrl to set.
     */
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    /**
     * @return Returns the columns.
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * @param columns The columns to set.
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * @return Returns the actions url
     */
    public String getActionUrls() {
        return actionUrls;
    }

    /**
     * @param actionsUrl the actions url
     */
    public void setActionUrls(String actionUrls) {
        this.actionUrls = actionUrls;
    }

    /**
     * Gets the Object ID of the BO that this row represents
     * @return
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Sets the Object ID of the BO that this row represents
     * @param objectId
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * Gets whether to show the return URL (for single value lookups invoked from a document or nested lookup) or the return checkbox (for
     * multiple value lookups)
     * 
     * @return
     */
    public boolean isRowReturnable() {
        return this.rowReturnable;
    }

    /**
     * Sets whether to show the return URL (for single value lookups invoked from a document or nested lookup) or the return checkbox (for
     * multiple value lookups)
     * 
     * @param rowReturnable
     */
    public void setRowReturnable(boolean rowReturnable) {
        this.rowReturnable = rowReturnable;
    }

	/**
	 * Returns the BusinessObject associated with this row.  This may be null
	 * 
	 * @return the businessObject, or null if the businessObject was not set
	 */
	public BusinessObject getBusinessObject() {
		return this.businessObject;
	}

	/**
	 * @param businessObject the businessObject to set
	 */
	public void setBusinessObject(BusinessObject businessObject) {
		this.businessObject = businessObject;
	}

	/**
	 * @return the rowId
	 */
	public String getRowId() {
		return this.rowId;
	}

	/**
	 * @param rowId the rowId to set
	 */
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	/**
	 * @return the returnUrlHtmlData
	 */
	public HtmlData getReturnUrlHtmlData() {
		return this.returnUrlHtmlData;
	}

	/**
	 * @param returnUrlHtmlData the returnUrlHtmlData to set
	 */
	public void setReturnUrlHtmlData(HtmlData returnUrlHtmlData) {
		this.returnUrlHtmlData = returnUrlHtmlData;
	}


	

}
