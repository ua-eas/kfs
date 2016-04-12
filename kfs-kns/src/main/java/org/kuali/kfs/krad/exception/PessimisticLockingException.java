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

/**
 * This class used by Kuali's Pessimistic Locking mechanism
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PessimisticLockingException extends RuntimeException {

    private static final long serialVersionUID = -6565593932651796413L;

    /**
     * This constructs a {@link PessimisticLockingException} object to be used
     * but the system's manual pessimistic locking mechanism
     * 
     * @param message - the message that defines the exception
     */
    public PessimisticLockingException(String message) {
        super(message);
    }

}
