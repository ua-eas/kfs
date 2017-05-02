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
package org.kuali.kfs.kns.inquiry;

import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.List;
import java.util.Map;

/**
 * Defines business logic methods that support the Inquiry framework
 */
@Deprecated
public interface Inquirable extends org.kuali.kfs.krad.inquiry.Inquirable {

    @Deprecated
    public void setBusinessObjectClass(Class businessObjectClass);

    @Deprecated
    public BusinessObject getBusinessObject(Map fieldValues);

    @Deprecated
    public HtmlData getInquiryUrl(BusinessObject businessObject,
                                  String attributeName, boolean forceInquiry);

    @Deprecated
    public String getHtmlMenuBar();

    @Deprecated
    public String getTitle();

    @Deprecated
    public List<Section> getSections(BusinessObject bo);

    @Deprecated
    public void addAdditionalSections(List columns, BusinessObject bo);

    /**
     * Indicates whether inactive records for the given collection should be
     * display.
     *
     * @param collectionName - name of the collection (or sub-collection) to check inactive
     *                       record display setting
     * @return true if inactive records should be displayed, false otherwise
     */
    @Deprecated
    public boolean getShowInactiveRecords(String collectionName);

    /**
     * Returns the Map used to control the state of inactive record collection
     * display. Exposed for setting from the maintenance jsp.
     */
    @Deprecated
    public Map<String, Boolean> getInactiveRecordDisplay();

    /**
     * Indicates to maintainble whether or not inactive records should be
     * displayed for the given collection name.
     *
     * @param collectionName - name of the collection (or sub-collection) to set inactive
     *                       record display setting
     * @param showInactive   - true to display inactive, false to not display inactive
     *                       records
     */
    @Deprecated
    public void setShowInactiveRecords(String collectionName,
                                       boolean showInactive);
}
