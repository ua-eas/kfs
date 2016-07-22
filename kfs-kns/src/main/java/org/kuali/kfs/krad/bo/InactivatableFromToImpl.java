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

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.sql.Timestamp;


@MappedSuperclass
public abstract class InactivatableFromToImpl extends PersistableBusinessObjectBase implements InactivatableFromTo {

	private static final long serialVersionUID = 1L;

	@Column(name = "ACTV_FRM_DT")
	protected Timestamp activeFromDate;
	@Column(name = "ACTV_TO_DT")
	protected Timestamp activeToDate;
	@Transient
	protected Timestamp activeAsOfDate;
	@Transient
	protected boolean current;

	/**
	 * Returns active if the {@link #getActiveAsOfDate()} (current time used if not set) is between
	 * the from and to dates. Null dates are considered to indicate an open range.
	 */
	public boolean isActive() {
        return InactivatableFromToUtils.isActive(new DateTime(activeFromDate), new DateTime(activeToDate), new DateTime(activeAsOfDate));
	}

	public void setActive(boolean active) {
		// do nothing
	}

	public void setActiveFromDate(Timestamp from) {
		this.activeFromDate = from;
	}

	public void setActiveToDate(Timestamp to) {
		this.activeToDate = to;
	}

	public Timestamp getActiveFromDate() {
		return this.activeFromDate;
	}

	public Timestamp getActiveToDate() {
		return this.activeToDate;
	}

	public Timestamp getActiveAsOfDate() {
		return this.activeAsOfDate;
	}

	public void setActiveAsOfDate(Timestamp activeAsOfDate) {
		this.activeAsOfDate = activeAsOfDate;
	}

	public boolean isCurrent() {
		return this.current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}
}
