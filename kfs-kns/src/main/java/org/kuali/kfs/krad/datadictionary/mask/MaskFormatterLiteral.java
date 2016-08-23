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
package org.kuali.kfs.krad.datadictionary.mask;

/**
 * The maskLiteral element is used to completely hide the field value for
 * unauthorized users. The specified literal will be shown instead of the field
 * value.
 */
public class MaskFormatterLiteral implements MaskFormatter {
    private static final long serialVersionUID = 3368293409242411693L;

    protected String literal;

    public String maskValue(Object value) {
        return literal;
    }

    /**
     * Gets the literalString attribute.
     *
     * @return Returns the literal String.
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Specify the string that will be shown instead of the actual value when masked.
     */
    public void setLiteral(String literal) {
        this.literal = literal;
    }

}
