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
package org.kuali.kfs.krad.web.bind;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;

/**
 * PropertyEditor for booleans supports y/n which the spring version does not
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifBooleanEditor extends PropertyEditorSupport implements Serializable {
	private static final long serialVersionUID = -6333792216543862346L;

	private static final String TRUE_VALUES = "/true/yes/y/on/1/";
	private static final String FALSE_VALUES = "/false/no/n/off/0/";
	
	private static final String TRUE_VALUE = "true";
	private static final String FALSE_VALUE = "false";

    @Override
	public String getAsText() {
		if(this.getValue() == null) {
			return "";
		}
		else if(((Boolean)this.getValue()).booleanValue()) {
			return TRUE_VALUE;
		}
		else {
			return FALSE_VALUE;
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		String input = null;
		
		if(text != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("/").append(text.toLowerCase()).append("/");
			input = builder.toString();
			
			if(TRUE_VALUES.contains(input)) {
				this.setValue(Boolean.TRUE);
			}
			else if(FALSE_VALUES.contains(input)) {
				this.setValue(Boolean.FALSE);
			}
			else {
				input = null;
			}
		}

		if(input == null) {
			throw new IllegalArgumentException("Invalid boolean input: " + text);
		}
	}

}
