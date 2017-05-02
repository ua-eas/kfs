/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.krad.kim;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.kim.permission.PermissionTypeServiceBase;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.kim.api.KimConstants;

import java.util.Map;

public class DocumentTypeAndExistingRecordsOnlyPermissionTypeServiceImpl extends PermissionTypeServiceBase {

    @Override
    protected boolean performMatch(Map<String, String> inputMap,
                                   Map<String, String> storedMap) {

        String requestedDocumentType = inputMap.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        String permissionDocumentType = storedMap.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        if (requestedDocumentType == null || permissionDocumentType == null) {
            return false; // again, don't match if missing document types
        }
        // exit if document types don't match
        if (!requestedDocumentType.equals(permissionDocumentType)) {
            return false;
        }
        // check the existing attributes only flag
        if (!Boolean.parseBoolean(storedMap.get(KimConstants.AttributeConstants.EXISTING_RECORDS_ONLY))) {
            // if not set, then any document action allowed
            return true;
        }
        // otherwise, only edit actions are allowed (no New/Copy)
        return StringUtils.equals(inputMap.get(KRADConstants.MAINTENANCE_ACTN), KRADConstants.MAINTENANCE_EDIT_ACTION);
    }
}
