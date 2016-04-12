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
package org.kuali.kfs.kns.document.authorization;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.kns.authorization.BusinessObjectAuthorizer;
import org.kuali.kfs.krad.document.Document;

import java.util.Set;

/**
 * The DocumentAuthorizer class associated with a given Document is used to
 * dynamically determine what editing mode and what actions are allowed for a
 * given user on a given document instance.
 * 
 * 
 */
public interface DocumentAuthorizer extends BusinessObjectAuthorizer, org.kuali.kfs.krad.document.DocumentAuthorizer {
	public Set<String> getDocumentActions(Document document, Person user,
			Set<String> documentActions);

    @Override
	public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode, String createdBySelfOnly, Person user);
	
	public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, Person user);
}
