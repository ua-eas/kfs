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
package org.kuali.kfs.coreservice.impl.style;

import org.kuali.kfs.coreservice.impl.style.StyleBo;

import java.util.List;

/**
 * Defines data access operations related to {@link StyleBo}.
 * 
 * @see StyleBo
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface StyleDao {
	
	/**
	 * Updates or creates the given style in the data store. 
	 * 
	 * @param style the style to save, if null then this method will do nothing
	 */
    void saveStyle(StyleBo style);

    /**
     * Returns the style with the given name from the data store.
     * 
     * @param styleName the name of the style to retrieve
     * @return the style with the given name, or null if it does not exist
     */
    StyleBo getStyle(String styleName);
    
    /**
     * Return a list of the names of all styles that exist in the data store.
     * This method ...
     * 
     * @return
     */
    List<String> getAllStyleNames();

}
