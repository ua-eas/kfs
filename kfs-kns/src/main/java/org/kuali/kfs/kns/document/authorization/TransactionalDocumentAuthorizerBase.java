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

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.KRADConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base class for all TransactionalDocumentAuthorizers.
 */
public class TransactionalDocumentAuthorizerBase extends DocumentAuthorizerBase
		implements TransactionalDocumentAuthorizer {
	public final Set<String> getEditModes(Document document, Person user,
			Set<String> editModes) {
		Set<String> unauthorizedEditModes = new HashSet<String>();
		for (String editMode : editModes) {
			Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
			additionalPermissionDetails.put(KimConstants.AttributeConstants.EDIT_MODE, editMode);
			if (permissionExistsByTemplate(
					document,
					KRADConstants.KNS_NAMESPACE,
					KimConstants.PermissionTemplateNames.USE_TRANSACTIONAL_DOCUMENT,
					additionalPermissionDetails)
					&& !isAuthorizedByTemplate(
							document,
							KRADConstants.KNS_NAMESPACE,
							KimConstants.PermissionTemplateNames.USE_TRANSACTIONAL_DOCUMENT,
							user.getPrincipalId(), additionalPermissionDetails,
							null)) {
				unauthorizedEditModes.add(editMode);
			}
		}
		editModes.removeAll(unauthorizedEditModes);
		return editModes;
	}
}
