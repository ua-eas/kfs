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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import java.io.Serializable;


/**
 * This is the marker interface for constraints. Constraints are a central concept in the Rice data dictionary validation, and are the
 * primary mechanism by which the validation of an object or one of its attributes takes place. For example, by imposing a length constraint
 * on an attribute of a business object, it's possible to indicate that only values shorter (or longer) than a specific number of characters
 * are valid for that attribute.
 * <p>
 * Any interface that extends Constraint is by definition a constraint, and may have one of the following defined:
 * <p>
 * - A sub-interface for {@see Constrainable} that advises on how a constraint maps to data dictionary metadata
 * - A {@see ConstraintProvider} that looks up constraints for a specific constrainable definition
 * - A {@see ConstraintProcessor} that processes the constraint against some object value to determine if it is valid
 *
 * @since 1.1
 */
public interface Constraint extends Serializable {

    // Empty - since this is a marker interface, all of the interesting stuff is in interfaces or classes that extend this interface

}
