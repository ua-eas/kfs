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
package org.kuali.kfs.krad.web.bind;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * PropertyEditor converts between date display strings and
 * <code>java.sql.Timestamp</code> objects using the
 * <code>org.kuali.rice.core.api.datetime.DateTimeService</code>
 * 
 * 
 */
public class UifTimestampEditor extends UifDateEditor implements Serializable {
    private static final long serialVersionUID = -2776193044590819394L;

    /**
     * This overridden method uses the
     * <code>org.kuali.rice.core.api.datetime.DateTimeService</code> to convert
     * the time stamp object to the display string.
     * 
     * @see UifDateEditor#getAsText()
     */
    @Override
    public String getAsText() {
        if (this.getValue() == null) {
            return null;
        }
        if ("".equals(this.getValue())) {
            return null;
        }

        return getDateTimeService().toDateTimeString((Timestamp) this.getValue());
    }

    /**
     * This overridden method converts the display string to a
     * <code>java.sql.Timestamp</code> object using the
     * <code>org.kuali.rice.core.api.datetime.DateTimeService</code>.
     * 
     * @see UifDateEditor#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.setValue(new Timestamp(((Date) super.convertToObject(text)).getTime()));
    }

}
