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
package org.kuali.kfs.kns.web.taglib.html;

import org.apache.struts.taglib.html.HiddenTag;

import javax.servlet.jsp.JspException;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KNSHiddenTag extends HiddenTag {

	protected boolean renderHiddenField = true;
	
	@Override
    public int doStartTag() throws JspException {
		int returnVal = SKIP_BODY;
		if(renderHiddenField)
			returnVal = super.doStartTag();
		return returnVal;
	}

	/**
	 * @return the renderHiddenField
	 */
	public boolean isRenderHiddenField() {
		return this.renderHiddenField;
	}

	/**
	 * @param renderHiddenField the renderHiddenField to set
	 */
	public void setRenderHiddenField(boolean renderHiddenField) {
		this.renderHiddenField = renderHiddenField;
	}
}
