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
package org.kuali.kfs.coreservice.api.style;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

/**
 * This is the contract for a Style.  A style represents a stylesheet that is used for transforming data from
 * one format to another (currently only XSL is supported).
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface StyleContract extends Identifiable, Versioned, GloballyUnique, Inactivatable {

	/**
	 * Returns the name of this style.  All styles have a name and this value
	 * can never be null or blank.  The name must be unique within the entire
	 * repository of existing styles.
	 * 
	 * @return the name of this style
	 */
	String getName();
	
	/**
	 * Returns the XML definition of this style as a String.
	 * 
	 * @return the xml definition of this style
	 */
	String getXmlContent();
}
