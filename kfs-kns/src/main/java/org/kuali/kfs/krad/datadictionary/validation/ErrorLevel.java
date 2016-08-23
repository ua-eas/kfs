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
package org.kuali.kfs.krad.datadictionary.validation;

/**
 * Enum inherited from the Kuali Student project to track error levels in validation.
 */
public enum ErrorLevel {
    NOCONSTRAINT(-2), INAPPLICABLE(-1), OK(0), WARN(1), ERROR(2);

    int level;

    private ErrorLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static ErrorLevel min(ErrorLevel e1, ErrorLevel e2) {
        return e1.ordinal() < e2.ordinal() ? e1 : e2;
    }

    public static ErrorLevel max(ErrorLevel e1, ErrorLevel e2) {
        return e1.ordinal() > e2.ordinal() ? e1 : e2;
    }

}
