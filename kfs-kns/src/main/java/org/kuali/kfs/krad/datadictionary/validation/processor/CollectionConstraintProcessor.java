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
package org.kuali.kfs.krad.datadictionary.validation.processor;

import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;

import java.util.Collection;

/**
 * This is a marker interface for 'collection constraint processors', that is - a constraint processor that tests collections
 * rather than their elements. Maybe the best example of a collection-based constraint is a constraint on the number of elements
 * in that collection -- for example, a constraint that ensures that there are between 1 and 10 elements in a collection.
 */
public interface CollectionConstraintProcessor<T extends Collection<?>, C extends Constraint> extends ConstraintProcessor<T, C> {
    // Empty
}
