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
package org.kuali.kfs.krad.uif.field;

import org.kuali.kfs.krad.uif.component.ComponentBase;

/**
 * Field that encloses an iframe element
 *
 *
 */
public class IframeField extends FieldBase {
	private static final long serialVersionUID = 5797473302619055088L;

	private String source;
	private String height;
	private String frameborder;
	private String hspace;
	private String vspace;

	public IframeField() {
		super();
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getHeight() {
		return this.height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getFrameborder() {
		return this.frameborder;
	}

	public void setFrameborder(String frameborder) {
		this.frameborder = frameborder;
	}

	public String getHspace() {
		return this.hspace;
	}

	public void setHspace(String hspace) {
		this.hspace = hspace;
	}

	public String getVspace() {
		return this.vspace;
	}

	public void setVspace(String vspace) {
		this.vspace = vspace;
	}

	/**
	 * @see ComponentBase#getSupportsOnLoad()
	 */
	@Override
	public boolean getSupportsOnLoad() {
		return true;
	}

}
