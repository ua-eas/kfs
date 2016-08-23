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
package org.kuali.kfs.coreservice.impl.component;

import org.kuali.kfs.coreservice.impl.component.ComponentSetBo;

/**
 * A Data Access Object which handles data operations related to the tracking of component sets
 * which have been published to the component system.
 */
public interface ComponentSetDao {

    ComponentSetBo getComponentSet(String componentSetId);

    /**
     * Saves the given ComponentSetBo, in the case that an optimistic locking exception occurs, it "eats" the exception
     * and returns "false".  Otherwise, if the save is successful it returns "true".
     */
    boolean saveIgnoreLockingFailure(ComponentSetBo componentSetBo);

}
