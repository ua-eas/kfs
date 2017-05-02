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
package org.kuali.kfs.krad.datadictionary.validation.constraint.provider;

import org.kuali.kfs.krad.datadictionary.DataObjectEntry;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.resolver.ConstraintResolver;
import org.kuali.kfs.krad.datadictionary.validation.constraint.resolver.MustOccurConstraintsResolver;

import java.util.HashMap;

/**
 * An object that looks up constraints for an object dictionary entry by constraint type. This can either by instantiated by dependency
 * injection, in which case a map of class names to constraint resolvers can be injected, or the default map can be constructed by
 * calling the init() method immediately after instantiation.
 */
public class DataObjectEntryConstraintProvider extends BaseConstraintProvider<DataObjectEntry> {


    @Override
    public void init() {
        resolverMap = new HashMap<String, ConstraintResolver<DataObjectEntry>>();
        resolverMap.put(MustOccurConstraint.class.getName(), new MustOccurConstraintsResolver<DataObjectEntry>());
    }

    /**
     * @see ConstraintProvider#isSupported(Constrainable)
     */
    @Override
    public boolean isSupported(Constrainable definition) {

        if (definition instanceof DataObjectEntry)
            return true;

        return false;
    }

}
