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
package org.kuali.kfs.krad.exception;

import org.kuali.rice.core.api.exception.KualiException;

/**
 * This class represents an exception that is thrown when the configuration service tries to redefine a configuration property with
 * a key which is already in use.
 * 
 * 
 */
public class DuplicateKeyException extends KualiException {

    private static final long serialVersionUID = 6111570264943143198L;

    /**
     * @param message
     */
    public DuplicateKeyException(String message) {
        super(message);
    }

    /**
     * @param message
     */
    public DuplicateKeyException(String message, Throwable t) {
        super(message, t);
    }
}
