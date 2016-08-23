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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.component.ScriptEventSupport;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.widget.LightBox;

/**
 * Field that encloses a link element
 *
 *
 */
public class LinkField extends FieldBase {
	private static final long serialVersionUID = -1908504471910271148L;

	private String linkLabel;
	private String target;
	private String hrefText;
	private LightBox lightBox;

	public LinkField() {
      super();
	}

	/**
	 * The following initialization is performed:
     *
	 * <ul>
	 * <li>Set the linkLabel if blank to the Field label</li>
	 * </ul>
	 *
	 * @see ComponentBase#performInitialization(View, java.lang.Object)
	 */
	@Override
	public void performInitialization(View view, Object model) {
		super.performInitialization(view, model);

		if (StringUtils.isBlank(linkLabel)) {
			linkLabel = this.getLabel();
		}
	}

	public String getLinkLabel() {
		return this.linkLabel;
	}

	public void setLinkLabel(String linkLabel) {
		this.linkLabel = linkLabel;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getHrefText() {
		return this.hrefText;
	}

	public void setHrefText(String hrefText) {
		this.hrefText = hrefText;
	}

	/**
	 * @param lightBox the lightBox to set
	 */
	public void setLightBox(LightBox lightBox) {
		this.lightBox = lightBox;
	}

	/**
	 * @return the lightBox
	 */
	public LightBox getLightBox() {
		return lightBox;
	}

    /**
     * @see ScriptEventSupport#getSupportsOnClick()
     */
    public boolean getSupportsOnClick() {
        return true;
    }

}
