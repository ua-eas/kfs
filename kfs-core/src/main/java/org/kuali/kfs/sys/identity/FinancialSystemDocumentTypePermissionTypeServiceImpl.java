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
package org.kuali.kfs.sys.identity;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.kim.DocumentTypePermissionTypeServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FinancialSystemDocumentTypePermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

    /**
     * @see org.kuali.kfs.krad.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatches(org.kuali.kfs.kim.bo.types.dto.AttributeSet, java.util.List)
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails,
                                                        List<Permission> permissionsList) {
        String documentTypeName = requestedDetails.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        // loop over the permissions, checking the non-document-related ones
        for (Permission kpi : permissionsList) {
            // special handling when the permission is "Claim Electronic Payment"
            // if it is present and matches, then it takes priority
            if (KFSConstants.PermissionTemplate.CLAIM_ELECTRONIC_PAYMENT.name.equals(kpi.getTemplate().getName())) {
                String qualifierDocumentTypeName = kpi.getAttributes().get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
                if (documentTypeName == null && qualifierDocumentTypeName == null ||
                    (StringUtils.isNotEmpty(documentTypeName) && StringUtils.isNotEmpty(qualifierDocumentTypeName)
                        && documentTypeName.equals(qualifierDocumentTypeName))

                    ) {
                    List<Permission> matchingPermissions = new ArrayList<Permission>();
                    matchingPermissions.add(kpi);
                    return matchingPermissions;
                }
            }
        }
        // now, filter the list to just those for the current document
        return super.performPermissionMatches(requestedDetails, permissionsList);
    }

}
