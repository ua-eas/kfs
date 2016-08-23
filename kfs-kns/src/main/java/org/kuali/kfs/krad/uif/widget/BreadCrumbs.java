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
package org.kuali.kfs.krad.uif.widget;

import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.view.HistoryEntry;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.component.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The breadcrumb widget contains various settings for setting up
 * Breadcrumb/History support on the view.
 *
 *
 */
public class BreadCrumbs extends WidgetBase {
    private static final long serialVersionUID = -2864287914665842251L;

    private boolean displayHomewardPath;
    private boolean displayPassedHistory;
    private boolean displayBreadcrumbsWhenOne;
    private List<HistoryEntry> homewardPathList;

    public BreadCrumbs() {
        homewardPathList = new ArrayList<HistoryEntry>();
    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li>Evaluate expression on howeward path list</li>
     * </ul>
     *
     * @see Component#performApplyModel(View,
     *      java.lang.Object)
     */
    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (homewardPathList != null) {
            Map<String, Object> context = new HashMap<String, Object>();
            context.putAll(view.getContext());

            for (HistoryEntry historyEntry : homewardPathList) {
                KRADServiceLocatorWeb.getExpressionEvaluatorService().evaluateObjectExpressions(historyEntry, model,
                        context);
            }
        }
    }

    /**
     * Determines if the homewardPath is to be displayed. Even when this is
     * setting is on the code may determine to turn off homewardPath display
     * based on user interaction and ui elements being displayed (ie lightbox)
     *
     * @return the displayHomewardPath
     */
    public boolean isDisplayHomewardPath() {
        return this.displayHomewardPath;
    }

    /**
     * @param displayHomewardPath the displayHomewardPath to set
     */
    public void setDisplayHomewardPath(boolean displayHomewardPath) {
        this.displayHomewardPath = displayHomewardPath;
    }

    /**
     * Determines if the passedHistory is to be displayed. In most cases this
     * should not be set through the xml as this is toggled off and on through
     * code during different ui procedures.
     *
     * @return the displayPassedHistory
     */
    public boolean isDisplayPassedHistory() {
        return this.displayPassedHistory;
    }

    /**
     * @param displayPassedHistory the displayPassedHistory to set
     */
    public void setDisplayPassedHistory(boolean displayPassedHistory) {
        this.displayPassedHistory = displayPassedHistory;
    }

    /**
     * The homewardPath to be displayed on this representative of the logical
     * "location" of the view within the site hierarchy, can be set to anything
     * desired.
     *
     * @return the homewardPathList
     */
    public List<HistoryEntry> getHomewardPathList() {
        return this.homewardPathList;
    }

    /**
     * @param homewardPathList the homewardPathList to set
     */
    public void setHomewardPathList(List<HistoryEntry> homewardPathList) {
        this.homewardPathList = homewardPathList;
    }

    /**
     * If true, breadcrumbs will not be displayed if only one breadcrumb is
     * going to be shown, this improves visual clarity of the page
     *
     * @return the displayBreadcrumbsWhenOne
     */
    public boolean isDisplayBreadcrumbsWhenOne() {
        return this.displayBreadcrumbsWhenOne;
    }

    /**
     * @param displayBreadcrumbsWhenOne the displayBreadcrumbsWhenOne to set
     */
    public void setDisplayBreadcrumbsWhenOne(boolean displayBreadcrumbsWhenOne) {
        this.displayBreadcrumbsWhenOne = displayBreadcrumbsWhenOne;
    }

}
