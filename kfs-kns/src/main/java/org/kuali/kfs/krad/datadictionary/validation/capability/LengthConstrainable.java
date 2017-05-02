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
package org.kuali.kfs.krad.datadictionary.validation.capability;

import org.kuali.kfs.krad.datadictionary.validation.constraint.LengthConstraint;

/**
 * This interface defines methods that must be implemented by classes that want to be processed as
 */
public interface LengthConstrainable extends Constrainable, LengthConstraint {

    // To match up with legacy code for AttributeDefinition, length constraint members are fields
    // on the definition, making the capability a sub-interface of the constraint

}
