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
package org.kuali.kfs.kns.web.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.krad.util.KRADConstants;

/**
 * Feedback form which is used to collect feedback from a user and
 * then email it to a feedback mailing list
 */
public class KualiFeedbackHandlerForm extends KualiForm {

	private static final long serialVersionUID = -7641833777580490471L;

	private boolean cancel = false;
	private String description;
	private String documentId = ""; 
	private String componentName;

	/**
	 * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void populate(HttpServletRequest request) {
		super.populate(request);
		// ie explorer needs this.
		if(StringUtils.isNotBlank(request.getParameter(KRADConstants.CANCEL_METHOD + ".x")) &&
           StringUtils.isNotBlank(request.getParameter(KRADConstants.CANCEL_METHOD + ".y"))){
			    this.setCancel(true);
		}                
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		this.setMethodToCall(null);
		this.setRefreshCaller(null);
		this.setAnchor(null);
		this.setCurrentTabIndex(0);

		this.cancel = false;
		this.documentId = null;
		this.description = null;
		this.componentName = null;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
}
