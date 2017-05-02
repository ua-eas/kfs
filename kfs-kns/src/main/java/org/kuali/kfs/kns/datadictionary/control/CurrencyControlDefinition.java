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
package org.kuali.kfs.kns.datadictionary.control;


import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;

/**
 * The currency element defines an HTML text control for
 * entering dollar and cents amounts.  Only two decimals to
 * the right of the decimal point are allowed.  Formatted
 * value is displayed with commas.
 * <p>
 * Used Properties: size, formattedMaxLength
 */
@Deprecated
public class CurrencyControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = 1650000676894176080L;

    /**
     * the maxLength for text that has been formatted. ie if maxLength=5. [12345]. but after going through the formatter the value
     * is [12,345.00] and will no longer fit in a field whos maxLength=5. formattedMaxLength solves this problem.
     */
    protected Integer formattedMaxLength;

    public CurrencyControlDefinition() {
    }

    /**
     * @see ControlDefinition#isCurrency()
     */
    public boolean isCurrency() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "CurrencyControlDefinition";
    }

    /**
     * @return Returns the formattedMaxLength parameter for currency controls.
     */
    public Integer getFormattedMaxLength() {
        return formattedMaxLength;
    }

    /**
     * the maxLength for text that has been formatted. ie if maxLength=5. [12345]. but after going through the formatter the value
     * is [12,345.00] and will no longer fit in a field whos maxLength=5. formattedMaxLength solves this problem.
     */
    public void setFormattedMaxLength(Integer formattedMaxLength) {
        this.formattedMaxLength = formattedMaxLength;
    }

}
