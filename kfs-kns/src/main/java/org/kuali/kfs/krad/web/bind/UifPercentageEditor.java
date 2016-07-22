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

import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.core.web.format.FormatException;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * PropertyEditor converts between percentage strings and
 * <code>org.kuali.rice.core.api.util.type.KualiPercent</code> objects
 * 
 * 
 */
public class UifPercentageEditor extends PropertyEditorSupport implements Serializable {
    private static final long serialVersionUID = -3562156375311932094L;

    /** The default scale for percentage values. */
    public final static int PERCENTAGE_SCALE = 2;

    /** The default format for percentage values. */
    public final static String PERCENTAGE_FORMAT = "#,##0.00";

    /**
     * This overridden method converts
     * <code>org.kuali.rice.core.api.util.type.KualiPercent</code> objects to the
     * display string.
     * 
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    @Override
    public String getAsText() {
        Object value = this.getValue();
        // Previously returned N/A
        if (value == null)
            return "";

        String stringValue = "";
        try {
            if (value instanceof KualiDecimal) {
                value = ((KualiDecimal) this.getValue()).bigDecimalValue();
            }
            BigDecimal bigDecValue = (BigDecimal) value;
            bigDecValue = bigDecValue.setScale(PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
            stringValue = NumberFormat.getInstance().format(bigDecValue.doubleValue());
        } catch (IllegalArgumentException iae) {
            throw new FormatException("formatting", RiceKeyConstants.ERROR_PERCENTAGE, this.getValue().toString(), iae);
        }

        return stringValue + " percent";
    }

    /**
     * This overridden method converts the display string to a
     * <code>org.kuali.rice.core.api.util.type.KualiPercent</code> object.
     * 
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            DecimalFormat formatter = new DecimalFormat(PERCENTAGE_FORMAT);
            Number parsedNumber = formatter.parse(text.trim());
            this.setValue(new KualiPercent(parsedNumber.doubleValue()));
        } catch (NumberFormatException e) {
            throw new FormatException("parsing", RiceKeyConstants.ERROR_PERCENTAGE, text, e);
        } catch (ParseException e) {
            throw new FormatException("parsing", RiceKeyConstants.ERROR_PERCENTAGE, text, e);
        }
    }

}
