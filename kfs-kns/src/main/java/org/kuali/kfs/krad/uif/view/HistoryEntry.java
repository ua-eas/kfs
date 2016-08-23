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
package org.kuali.kfs.krad.uif.view;

import org.kuali.kfs.krad.uif.component.ConfigurableBase;

import java.io.Serializable;

/**
 * A simple object that keeps track of various HistoryInformation
 * <p>
 * TODO a variety of these settings are not used in the current implementation of breadcrumbs
 * and history, they may be removed later if they prove unuseful in future changes
 */
public class HistoryEntry extends ConfigurableBase implements Serializable {
    private static final long serialVersionUID = -8310916657379268794L;

    private String viewId;
    private String pageId;
    private String title;
    private String url;
    private String formKey;

    public HistoryEntry() {
        super();
    }

    public HistoryEntry(String viewId, String pageId, String title, String url, String formKey) {
        super();

        this.viewId = viewId;
        this.pageId = pageId;
        this.title = title;
        this.url = url;
        this.formKey = formKey;
    }

    public String toParam() {
        return viewId
            + History.VAR_TOKEN
            + pageId
            + History.VAR_TOKEN
            + title
            + History.VAR_TOKEN
            + url
            + History.VAR_TOKEN
            + formKey;
    }

    /**
     * The viewId of the view
     *
     * @return the viewId
     */
    public String getViewId() {
        return this.viewId;
    }

    /**
     * @param viewId the viewId to set
     */
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    /**
     * The pageId of the page on the view
     *
     * @return the pageId
     */
    public String getPageId() {
        return this.pageId;
    }

    /**
     * @param pageId the pageId to set
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * The title of the view
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The url of this HistoryEntry
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the formKey
     */
    public String getFormKey() {
        return this.formKey;
    }

    /**
     * The formKey of the form in the view
     * TODO unsure of use
     *
     * @param formKey the formKey to set
     */
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

}
