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
package org.kuali.kfs.kns.web.taglib.html;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.taglib.html.CheckboxTag;
import org.kuali.kfs.kns.web.struts.form.pojo.PojoForm;
import org.kuali.kfs.kns.util.WebUtils;

import javax.servlet.jsp.JspException;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in.
 *
 *
 *
 */
public class KNSCheckboxTag extends CheckboxTag {

	/**
	 * @see org.apache.struts.taglib.html.CheckboxTag#doEndTag()
	 */
	@Override
    public int doEndTag() throws JspException {
        int returnVal = super.doEndTag();
        if (!getDisabled() && !getReadonly()) {
        	String name = prepareName();
        	if (StringUtils.isNotBlank(name)) {
	        	ActionForm form = WebUtils.getKualiForm(pageContext);
	            if(form!=null && form instanceof PojoForm) {
	            	((PojoForm) form).registerEditableProperty(name);
	            }
        	}
        }
        return returnVal;
    }
}
