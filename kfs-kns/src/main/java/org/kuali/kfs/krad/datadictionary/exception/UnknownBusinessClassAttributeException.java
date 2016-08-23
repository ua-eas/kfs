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
package org.kuali.kfs.krad.datadictionary.exception;

/**
 * Use this when the data dictionary cannot find a matching business object's attribute entry for a populated business object that
 * runs through the data dictionary validation service and its methods.
 */
public class UnknownBusinessClassAttributeException extends RuntimeException {
    private static final long serialVersionUID = -2021739544938001742L;

    /**
     * Create an UnknownBusinessClassAttributeException with the given message
     *
     * @param message
     */
    public UnknownBusinessClassAttributeException(String message) {
        super(message);
    }

    /**
     * Create an UnknownBusinessClassAttributeException with the given message and cause
     *
     * @param message
     * @param cause
     */
    public UnknownBusinessClassAttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}
