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

import org.kuali.kfs.krad.datadictionary.validation.capability.PrerequisiteConstrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;

import java.util.List;

/**
 * An object that returns the list of prerequisite constraints for a definition implementing the capability {@link PrerequisiteConstrainable}.
 */
public class PrerequisiteConstraintsResolver<T extends PrerequisiteConstrainable> implements ConstraintResolver<T> {

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Constraint> List<C> resolve(T definition) {
        return (List<C>) definition.getPrerequisiteConstraints();
    }

}
