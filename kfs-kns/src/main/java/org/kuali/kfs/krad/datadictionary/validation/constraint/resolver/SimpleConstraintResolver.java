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
package org.kuali.kfs.krad.datadictionary.validation.constraint.resolver;

import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.capability.SimpleConstrainable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleConstraintResolver<T extends Constrainable> implements ConstraintResolver<T> {

    /**
     * Return SimpleConstraint if SimpleConstrainable, otherwise return an empty list.
     * @param definition Definition to extract a SimpleConstraint from
     * @param <C> SimpleConstraint
     * @return SimpleConstraint if SimpleConstrainable, otherwise return an empty list.
     */
    public <C extends Constraint> List<C> resolve(T definition) {
        if(definition instanceof SimpleConstrainable){
            C simpleConstraint = (C)(((SimpleConstrainable)definition).getSimpleConstraint());
            return Collections.singletonList(simpleConstraint);
        }
        else{
            return new ArrayList<C>();
        }
    }
}
