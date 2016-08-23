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
package org.kuali.kfs.krad.datadictionary.validation.constraint.provider;

import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;

import java.util.List;

/**
 * An object that determines a list of constraints for a given Constrainable definition for an attribute
 * in the data dictionary. The ConstraintProvider interface must be implemented by any class that contributes
 * Constraints to the DictionaryValidationService. Multiple ConstraintProviders can be registered simultaneously,
 * and each can contribute constraints for any number of constraint types.
 *
 * These constraints can be looked up in a variety of ways. They may be:
 *
 * (1) member variables of the Constrainable definition itself {@see CaseConstrainable.class}
 * (2) the Constrainable definition itself may extend Constraint {@see LengthConstrainable.class}
 * (3) provided from some external source, or generated on the fly
 *
 * The goal here is to provide a mechanism that enables implementing institutions to inject new Constraints and ConstraintProcessor
 * classes into the DictionaryValidationService implementation via dependency injection.
 *
 *
 * @since 1.1
 */
public interface ConstraintProvider<T extends Constrainable> {

	public List<Constraint> getConstraints(T definition, Class<? extends Constraint> constraintType);

	public boolean isSupported(Constrainable definition);

}
