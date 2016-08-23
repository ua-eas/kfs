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
package org.kuali.kfs.krad.inquiry;

import org.kuali.kfs.krad.uif.widget.Inquiry;

import java.util.Map;

/**
 * Provides the contract for implementing an inquiry within the
 * inquiry framework
 *
 *
 */
public interface Inquirable {

    /**
     * Sets the class for the data object the inquirable should retrieve
     *
     * <p>
     * Must be set before invoking any other operations on the <code>Inquirable</code>,
     * including the retrieveDataObject method
     * </p>
     *
     * @param dataObjectClass the class of the dataObject that this inquirable should
     * retrieve
     */
    public void setDataObjectClass(Class<?> dataObjectClass);

    /**
     * Responsible for retrieving the data object from its data source
     * (database, service call, etc) based on the given map of field
     * name/value pairs
     *
     * <p>
     * Given map can contain more than fields (primary key or other) necessary
     * for retrieving the data object. Method will use the fields necessary
     * based on the metadata for the data object class configured on the inquirable
     * </p>
     *
     * @param fieldValues - a map of string field names and values
     * @return the data object or null if not found
     */
    public Object retrieveDataObject(Map<String, String> fieldValues);

    /**
     * Invoked by the <code>ViewHelperService</code> to build a link to the
     * inquiry
     *
     * <p>
     * Note this is used primarily for custom <code>Inquirable</code>
     * implementations to customize the inquiry class or parameters for an
     * inquiry. Instead of building the full inquiry link, implementations can
     * make a callback to
     * Inquiry.buildInquiryLink(Object, String,
     * Class<?>, Map<String, String>) given an inquiry class and parameters to
     * build the link field.
     * </p>
     *
     * @param dataObject - parent object for the inquiry property
     * @param propertyName - name of the property the inquiry is being built for
     * @param inquiry - instance of the inquiry widget being built for the property
     */
    public void buildInquirableLink(Object dataObject, String propertyName, Inquiry inquiry);
}
