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
package org.kuali.kfs.coreservice.api.namespace;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

/**
 * Defines the contract for a Namespace.  A namespace is a mechanism for partitioning of data into
 * areas of responsibility.  Since much of the Kuali Rice middleware is shared across multiple integrating
 * applications, this notion of a namespace is a critical element in keeping related data elements
 * grouped together and isolated from those to which they should have no relation or access.
 */
public interface NamespaceContract extends Versioned, GloballyUnique, Inactivatable, Coded {

    /**
     * This the id of the application which owns this Namespace.  If this namespace has no application owner,
     * then this method will return null.
     *
     * <p>
     * It is a way of assigning the Namespace to a specific rice application or rice ecosystem.
     * </p>
     *
     * @return application id
     */
    String getApplicationId();

    /**
     * This the name for the Namespace.  This can be null or a blank string.
     *
     * @return name
     */
    String getName();
}
