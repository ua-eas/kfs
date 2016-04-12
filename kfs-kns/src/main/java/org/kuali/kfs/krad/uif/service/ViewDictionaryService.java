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
package org.kuali.kfs.krad.uif.service;

import org.kuali.kfs.krad.inquiry.Inquirable;

/**
 * Provides methods to query the dictionary meta-data for view entries and their
 * corresponding component entries
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewDictionaryService {

    /**
     * Queries the dictionary to find the <code>InquiryView</code> configured
     * for the data object class and returns the configured Inquirable for the
     * view. If more than one inquiry view exists for the data object class, the
     * one that matches the given viewName, or the default if viewName is blank
     * is used
     *
     * @param dataObjectClass - class for the inquiry data object
     * @param viewName - name of the inquiry view, can be blank in which case the
     * 'default' name will be used
     * @return Inquirable<?> configured inquirable for the view, or null if view
     *         is not found
     */
    public Inquirable getInquirable(Class<?> dataObjectClass, String viewName);

    /**
     * Indicates whether the given data object class has an associated
     * <code>InquiryView</code> configured and thus can have inquiry links built
     *
     * @param dataObjectClass - object class to get inquiry view for
     * @return boolean true if the class has an inquiry view, false if no
     *         inquiry view exists for the class
     */
    public boolean isInquirable(Class<?> dataObjectClass);

    /**
     * Indicates whether the given data object class has an associated
     * <code>LookupView</code> configured and thus can have quickfinders
     * associated with the class
     *
     * @param dataObjectClass - object class to get lookup view for
     * @return boolean true if the class has an lookup view, false if no lookup
     *         view exists for the class
     */
    public boolean isLookupable(Class<?> dataObjectClass);

    /**
     * Indicates whether the given data object class has an associated
     * <code>MaintenanceView</code> configured
     *
     * @param dataObjectClass - object class to get maintenance view for
     * @return boolean true if the class has an maintenance view, false if no
     *         maintenance view exists for the class
     */
    public boolean isMaintainable(Class<?> dataObjectClass);

    /**
     * Attempts to find an associated <code>LookupView</code> for the
     * given data object class and if found returns the configured result
     * set limit, if multiple lookup views are found the default is used
     *
     * @param dataObjectClass - object class to get lookup view for
     * @return Integer configured result set limit for lookup, or null if not found (note
     *         property could also be null on the view itself)
     */
    public Integer getResultSetLimitForLookup(Class<?> dataObjectClass);
}
