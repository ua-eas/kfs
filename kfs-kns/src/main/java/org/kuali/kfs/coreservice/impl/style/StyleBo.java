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

import org.kuali.kfs.coreservice.api.style.Style;
import org.kuali.kfs.coreservice.api.style.StyleContract;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

/**
 * A BusinessObject implementation of the StyleContract which is mapped to the
 * database for persistence.
 */
public class StyleBo extends PersistableBusinessObjectBase implements StyleContract {

	private static final long serialVersionUID = 2020611019976731725L;

	private String id;
	private String name;
	private String xmlContent;
	private boolean active = true;
    
    /**
     * Converts the given StyleBo to a Style object.
     * 
     * @param styleBo the StyleBo to convert
     * @return the resulting Style object, or null if the given styleBo was null
     */
    static Style to(StyleBo styleBo) {
    	if (styleBo == null) {
    		return null;
    	}

    	return Style.Builder.create(styleBo).build();
    }
    
    /**
     * Constructs a StyleBo from the given Style.
     * 
     * @param style the Style to convert
     * @return the resulting StyleBo object, or null if the given style was null
     */
    static StyleBo from(Style style) {
    	if (style == null) {
    		return null;
    	}

    	StyleBo styleBo = new StyleBo();
    	styleBo.setId(style.getId());
    	styleBo.setName(style.getName());
    	styleBo.setXmlContent(style.getXmlContent());
    	styleBo.setActive(style.isActive());
    	styleBo.setVersionNumber(style.getVersionNumber());
    	styleBo.setObjectId(style.getObjectId());
    	return styleBo;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
