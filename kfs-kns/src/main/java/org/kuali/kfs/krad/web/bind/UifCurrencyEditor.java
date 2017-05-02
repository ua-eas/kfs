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

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.web.format.FormatException;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Class is used to format
 * <code>org.kuali.rice.core.api.util.type.KualiDecimal</code> in the local
 * currency
 */
public class UifCurrencyEditor extends PropertyEditorSupport implements Serializable {
    private static final long serialVersionUID = 6692868638156609014L;
    private static Logger LOG = Logger.getLogger(UifCurrencyEditor.class);

    /**
     * This overridden method ...
     *
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    @Override
    public String getAsText() {
        Object obj = this.getValue();

        LOG.debug("format '" + obj + "'");
        if (obj == null)
            return null;

        NumberFormat formatter = getCurrencyInstanceUsingParseBigDecimal();
        String string = null;

        try {
            Number number = (Number) obj;
            if (obj instanceof KualiInteger) {
                formatter.setMaximumFractionDigits(0);
            }
            string = formatter.format(number.doubleValue());
        } catch (IllegalArgumentException e) {
            throw new FormatException("formatting", RiceKeyConstants.ERROR_CURRENCY, obj.toString(), e);
        } catch (ClassCastException e) {
            throw new FormatException("formatting", RiceKeyConstants.ERROR_CURRENCY, obj.toString(), e);
        }

        return string;
    }

    /**
     * retrieves a currency formatter instance and sets ParseBigDecimal to true
     * to fix [KULEDOCS-742]
     *
     * @return CurrencyInstance
     */
    private NumberFormat getCurrencyInstanceUsingParseBigDecimal() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        if (formatter instanceof DecimalFormat) {
            ((DecimalFormat) formatter).setParseBigDecimal(true);
        }
        return formatter;
    }

    /**
     * This overridden method sets the property value by parsing a given String.
     * It uses the <code>convertToObject</code> method to make the code
     * available to sub classes.
     *
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String text) {
        this.setValue(convertToObject(text));
    }

    /**
     * Converts the string to a
     * <code>org.kuali.rice.core.api.util.type.KualiDecimal</code> object using the
     * local currency format.
     *
     * @param text the text from the UI to convert
     * @return the <code>org.kuali.rice.core.api.util.type.KualiDecimal</code>
     * object to be set on the bean
     */
    protected Object convertToObject(String text) {
        KualiDecimal value = null;

        LOG.debug("convertToObject '" + text + "'");

        if (text != null) {
            text = text.trim();
            NumberFormat formatter = getCurrencyInstanceUsingParseBigDecimal();
            // Add the currency symbol suffix/prefix to the text to change to
            // correct format
            if (formatter instanceof DecimalFormat) {
                String prefix = ((DecimalFormat) formatter).getPositivePrefix();
                String suffix = ((DecimalFormat) formatter).getPositiveSuffix();
                if (!prefix.equals("") && !text.startsWith(prefix)) {
                    text = prefix.concat(text);
                }
                if (!suffix.equals("") && !text.endsWith(suffix)) {
                    text = text.concat(suffix);
                }
            }
            try {
                Number parsedNumber = formatter.parse(text);
                value = new KualiDecimal(parsedNumber.toString());
            } catch (NumberFormatException e) {
                throw new FormatException("parsing", RiceKeyConstants.ERROR_CURRENCY, text, e);
            } catch (ParseException e) {
                throw new FormatException("parsing", RiceKeyConstants.ERROR_CURRENCY, text, e);
            }
        }
        return value;
    }

}
