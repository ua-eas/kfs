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

import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Service for working with stylesheets.  This service provides pure data-oriented
 * operations as well as operations dealing with pre-compiled stylesheets.  It's
 * intended that most clients will interact with this service in lieu of the
 * lower-level {@link StyleRepositoryService}.
 * 
 * @see Style
 * @see StyleRepositoryService
 *
 */
public interface StyleService {
		
	/**
	 * @see StyleRepositoryService#getStyle(String)
	 */
    public Style getStyle(String styleName);

    /**
     * @see StyleRepositoryService#getAllStyleNames()
     */
    public List<String> getAllStyleNames();

    /**
     * @see StyleRepositoryService#saveStyle(Style)
     */
    @CacheEvict(value={Style.Cache.NAME}, allEntries = true)
    public void saveStyle(Style data);
    
    /**
     * Gets a compiled version of the style with the given name.
     * 
     * @param styleName the name of the style for which to retrieve a compiled version
     * 
     * @return a compiled version of the stylesheet as a {@link Templates} instance
     * 
     * @throws TransformerConfigurationException if compilation of the stylesheet fails
     * @throws IllegalArgumentException if the given styleName is null or blank
     */
    @Cacheable(value= Style.Cache.NAME, key="'styleName=' + #p0")
    public Templates getStyleAsTranslet(String styleName) throws TransformerConfigurationException;

	
}
