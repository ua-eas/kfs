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
package org.kuali.kfs.krad.datadictionary.validation.capability;

import org.kuali.kfs.krad.datadictionary.validation.constraint.MustOccurConstraint;

import java.util.List;

/**
 * Validation capability to constrain an object to has some specified acceptable number of prerequisite constraints nested
 * within must occur constraints.
 *
 *
 * @since 1.1
 */
public interface MustOccurConstrainable extends Constrainable {

	public List<MustOccurConstraint> getMustOccurConstraints();

}
