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
package org.kuali.kfs.krad.bo;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

import java.sql.Timestamp;

/**
 * Business objects that have effective dating (from to dates) should implement this interface. This
 * translates the effective dates in terms of active/inactive status so the features built for
 * {@link MutableInactivatable} in the frameworks can be taken advantage of
 */
public interface InactivatableFromTo extends MutableInactivatable {

	/**
	 * Sets the date for which record will be active
	 * 
	 * @param from
	 *            - Timestamp value to set
	 */
	public void setActiveFromDate(Timestamp from);
	
	/**
	 * Gets the date for which the record become active
	 *
	 * @return Timestamp
	 */
	public Timestamp getActiveFromDate();

	/**
	 * Sets the date for which record will be active to
	 * 
	 * @param from
	 *            - Timestamp value to set
	 */
	public void setActiveToDate(Timestamp to);
	
	/**
	 * Gets the date for which the record become inactive
	 *
	 * @return Timestamp
	 */
	public Timestamp getActiveToDate();

	/**
	 * Gets the date for which the record is being compared to in determining active/inactive
	 * 
	 * @return Timestamp
	 */
	public Timestamp getActiveAsOfDate();

	/**
	 * Sets the date for which the record should be compared to in determining active/inactive, if
	 * not set then the current date will be used
	 * 
	 * @param activeAsOfDate
	 *            - Timestamp value to set
	 */
	public void setActiveAsOfDate(Timestamp activeAsOfDate);

}
