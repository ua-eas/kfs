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
package org.kuali.kfs.krad.datadictionary.validation.constraint.resolver;

import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.capability.LengthConstrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;

import java.util.Collections;
import java.util.List;

/**
 * An object that returns the constrainable definition itself as a list for a definition implementing the capability {@link Constrainable}.
 * This definition must also implement the interface {@link Constraint}, or a ClassCastException will be thrown.
 * <p>
 * An example is {@link LengthConstrainable}, where members of the definition itself need to be made available to the ConstraintProcessor.
 */
public class DefinitionConstraintResolver<T extends Constrainable> implements ConstraintResolver<T> {

    @Override
    public <C extends Constraint> List<C> resolve(T definition) throws ClassCastException {
        if (definition instanceof Constraint) {
            @SuppressWarnings("unchecked")
            C constraint = (C) definition;
            return Collections.singletonList(constraint);
        }
/*        else if(definiton instanceof InputField){
            C constraint = (C)definition.get
        }*/
        throw new ClassCastException("DefinitionConstraintResolver can only be used for a definition that implements both Constraint and Constrainable, or derives from a class that does.");
    }

}
