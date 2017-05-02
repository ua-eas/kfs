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
package org.kuali.kfs.krad.web.bind;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.FormatException;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;

/**
 * PropertyEditor converts between date display strings and <code>java.sql.Date</code> objects
 */
public class UifDateEditor extends PropertyEditorSupport implements Serializable {
    private static final long serialVersionUID = 8122469337264797008L;

    /**
     * The date time service.
     */
    private transient DateTimeService dateTimeService;

    /**
     * This overridden method uses the
     * <code>org.kuali.rice.core.api.datetime.DateTimeService</code> to convert
     * the date object to the display string.
     *
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    @Override
    public String getAsText() {
        if (this.getValue() == null) {
            return null;
        }
        if ("".equals(this.getValue())) {
            return null;
        }
        return getDateTimeService().toDateString((java.util.Date) this.getValue());
    }

    /**
     * Gets the date time service.
     *
     * @return the date time service
     */
    protected DateTimeService getDateTimeService() {
        if (this.dateTimeService == null) {
            this.dateTimeService = GlobalResourceLoader.getService(CoreConstants.Services.DATETIME_SERVICE);
        }
        return this.dateTimeService;
    }

    /**
     * This overridden method converts the display string to a
     * <code>java.sql.Date</code> object using the
     * <code>org.kuali.rice.core.api.datetime.DateTimeService</code>.
     *
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.setValue(convertToObject(text));
    }

    /**
     * Convert display text to <code>java.sql.Date</code> object using the
     * <code>org.kuali.rice.core.api.datetime.DateTimeService</code>.
     *
     * @param text the display text
     * @return the <code>java.sql.Date</code> object
     * @throws IllegalArgumentException the illegal argument exception
     */
    protected Object convertToObject(String text) throws IllegalArgumentException {
        try {
            Date result = getDateTimeService().convertToSqlDate(text);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(result);
            if (calendar.get(Calendar.YEAR) < 1000 && verbatimYear(text).length() < 4) {
                throw new FormatException("illegal year format", RiceKeyConstants.ERROR_DATE, text);
            }
            return result;
        } catch (ParseException e) {
            throw new FormatException("parsing", RiceKeyConstants.ERROR_DATE, text, e);
        }
    }

    /**
     * For a given user input date, this method returns the exact string the
     * user entered after the last slash. This allows the formatter to
     * distinguish between ambiguous values such as "/06" "/6" and "/0006"
     *
     * @param date
     * @return
     */
    private String verbatimYear(String date) {
        String result = "";

        int pos = date.lastIndexOf("/");
        if (pos >= 0) {
            result = date.substring(pos);
        }

        return result;
    }

}
