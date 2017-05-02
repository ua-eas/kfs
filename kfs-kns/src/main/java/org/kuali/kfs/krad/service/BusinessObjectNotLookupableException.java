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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.lookup.Lookupable;
import org.kuali.rice.core.api.exception.KualiException;

/**
 * Thrown when a BusinessObject is expected to implement Lookupable, but does not.
 *
 * @see Lookupable
 */
public class BusinessObjectNotLookupableException extends KualiException {
    public BusinessObjectNotLookupableException(String message) {
        super(message);
    }

    public BusinessObjectNotLookupableException(String message, boolean hideIncidentReport) {
        super(message, hideIncidentReport);
    }

    public BusinessObjectNotLookupableException(String message, Throwable t) {
        super(message, t);
    }

    public BusinessObjectNotLookupableException(Throwable t) {
        super(t);
    }
}
