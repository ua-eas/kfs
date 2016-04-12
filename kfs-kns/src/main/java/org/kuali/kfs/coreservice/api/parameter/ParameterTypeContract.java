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
package org.kuali.kfs.coreservice.api.parameter;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

/**
 * Defines the contract for a parameter type.  The parameter type is largely indicative of the way in which the
 * parameter will be used by clients.  Examples might include a parameter which is used for configuration purposes
 * or one which is used to define parameters used during validation. 
 */
public interface ParameterTypeContract extends Versioned, GloballyUnique, Inactivatable, Coded {
    
    /**
     * This the name for the ParameterType.  This can be null or a blank string.
     *
     * @return the name of the ParameterType
     */
    String getName();
}
