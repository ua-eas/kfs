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
package org.kuali.kfs.krad.datadictionary.validation.constraint.resolver;

import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;

import java.util.List;

/**
 * An interface that provides a lookup of constraints for a specific constrainable attribute definition. Implemented by constraint
 * providers as a mechanism to store functional lookups in a map, keyed by constraint type, for example.
 *
 * {@see AttributeDefinitionConstraintProvider} for a number of examples.
 *
 *
 * @param <T>
 * @since 1.1
 */
public interface ConstraintResolver<T extends Constrainable> {

	public <C extends Constraint> List<C> resolve(T definition);

}
