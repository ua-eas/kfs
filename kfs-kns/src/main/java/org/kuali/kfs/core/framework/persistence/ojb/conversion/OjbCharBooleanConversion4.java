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
package org.kuali.kfs.core.framework.persistence.ojb.conversion;

/**
 * This class converts the "Y" or "N" value from the database into a true or false in Java.
 *
 *
 * @deprecated Use OjbCharBooleanConversion2 instead
 */
public final class OjbCharBooleanConversion4 extends OjbCharBooleanConversionBase {
    private static final long serialVersionUID = 5192588414458129183L;
    private static String S_TRUE = "Y";
    private static String S_FALSE = "N";

    /**
     * no args constructor
     */
    public OjbCharBooleanConversion4() {
        super();
    }

    protected String getTrueValue() {
        return "Y";
    }

    protected String getFalseValue() {
        return "N";
    }
}
