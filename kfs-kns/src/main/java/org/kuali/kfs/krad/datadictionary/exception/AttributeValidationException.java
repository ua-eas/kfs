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
package org.kuali.kfs.krad.datadictionary.exception;

import org.kuali.kfs.krad.datadictionary.DataDictionaryException;

/**
 * Exception thrown when an attribute can't be found during attribute-related validation.
 */
public class AttributeValidationException extends DataDictionaryException {

    private static final long serialVersionUID = 8393895061347705832L;

    /**
     * @param message
     */
    public AttributeValidationException(String message) {
        super(message);
    }

    /**
     * @param message
     */
    public AttributeValidationException(String message, Throwable t) {
        super(message, t);
    }
}
