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
package org.kuali.kfs.krad.web.form;

import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;

/**
 * Form class for <code>MaintenanceView</code> screens
 * 
 * 
 */
public class MaintenanceForm extends DocumentFormBase {
	private static final long serialVersionUID = -5805825500852498048L;
	
	protected String dataObjectClassName;
	protected String maintenanceAction;

	public MaintenanceForm() {
		super();
		setViewTypeName(ViewType.MAINTENANCE);
	}

	@Override
	public MaintenanceDocument getDocument() {
		return (MaintenanceDocument) super.getDocument();
	}
	
	// This is to provide a setter with matching type to
	// public MaintenanceDocument getDocument() so that no
	// issues occur with spring 3.1-M2 bean wrappers
	public void setDocument(MaintenanceDocument document) {
	    super.setDocument(document);
	}

	public String getDataObjectClassName() {
		return this.dataObjectClassName;
	}

	public void setDataObjectClassName(String dataObjectClassName) {
		this.dataObjectClassName = dataObjectClassName;
	}

	public String getMaintenanceAction() {
		return this.maintenanceAction;
	}

	public void setMaintenanceAction(String maintenanceAction) {
		this.maintenanceAction = maintenanceAction;
	}

}
