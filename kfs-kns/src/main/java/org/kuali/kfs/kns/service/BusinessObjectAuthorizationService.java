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
package org.kuali.kfs.kns.service;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.kfs.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DataObjectAuthorizationService;

/**
 * Responsible for using AttributeSecurity on
 * AttributeDefinitions, InquirableField the data dictionary business object and
 * maintenance document entries
 * 
 * TODO: refactor for general objects
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public interface BusinessObjectAuthorizationService extends DataObjectAuthorizationService {
	public BusinessObjectRestrictions getLookupResultRestrictions(
			Object dataObject, Person user);

	public InquiryRestrictions getInquiryRestrictions(
			BusinessObject businessObject, Person user);

	public MaintenanceDocumentRestrictions getMaintenanceDocumentRestrictions(
			MaintenanceDocument maintenanceDocument, Person user);

	public boolean canFullyUnmaskField(Person user,
			Class<?> dataObjectClass, String fieldName, Document document);

	public boolean canPartiallyUnmaskField(
			Person user, Class<?> businessObjectClass, String fieldName, Document document);

}
