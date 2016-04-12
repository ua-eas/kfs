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
package org.kuali.kfs.coreservice.api.component;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

/**
 * This is the contract for a Component.  This represents functional/logical piece
 * within a rice application or rice ecosystem.
 */
public interface ComponentContract extends Versioned, GloballyUnique, Inactivatable, Coded {

    /**
     * This is the name value for the component.  It cannot be null or a blank string.
     * @return name
     */
    String getName();

    /**
     * This is the namespace for the component.  It cannot be null or a blank string.
     * <p>
     * It is a way of assigning the component to a logical grouping within a rice application or rice ecosystem.
     * </p>
     *
     * @return namespace code
     */
    String getNamespaceCode();

    /**
     * Returns the id of the component set this component belongs to if this component was published as part of such
     * a component set.  Will return a null value if this component was not published as part of a component set.
     *
     * @return the id of the component set this component was published under, or null if this component is not part of
     * a published set
     */
    String getComponentSetId();


}
