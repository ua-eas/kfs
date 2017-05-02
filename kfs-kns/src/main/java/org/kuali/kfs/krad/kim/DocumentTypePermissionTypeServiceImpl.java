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
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.permission.PermissionBo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a description of what this class does - mpham don't forget to fill
 * this in.
 */
public class DocumentTypePermissionTypeServiceImpl extends PermissionTypeServiceBase {
    protected transient DocumentTypeService documentTypeService;

    @Override
    protected List<String> getRequiredAttributes() {
        return Collections.singletonList(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
    }

    @Override
    protected boolean isCheckRequiredAttributes() {
        return true;
    }

    /**
     * Loops over the given permissions and returns the most specific permission that matches.
     * <p>
     * That is, if a permission exists for the document type, then the permission for any
     * parent document will not be considered/returned.
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails,
                                                        List<Permission> permissionsList) {
        // pull all the potential parent doc type names from the permission list
        Set<String> permissionDocTypeNames = new HashSet<String>(permissionsList.size());
        for (Permission permission : permissionsList) {
            PermissionBo bo = PermissionBo.from(permission);
            String docTypeName = bo.getDetails().get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            if (StringUtils.isNotBlank(docTypeName)) {
                permissionDocTypeNames.add(docTypeName);
            }
        }
        // find the parent documents which match
        DocumentType docType = getDocumentTypeService().getDocumentTypeByName(requestedDetails.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
        String matchingDocTypeName = getClosestParentDocumentTypeName(docType, permissionDocTypeNames);
        // re-loop over the permissions and build a new list of the ones which have the
        // matching document type names in their details
        List<Permission> matchingPermissions = new ArrayList<Permission>();
        for (Permission kpi : permissionsList) {
            PermissionBo bo = PermissionBo.from(kpi);
            String docTypeName = bo.getDetails().get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            // only allow a match on the "*" type if no matching document types were found
            if ((StringUtils.isEmpty(matchingDocTypeName) && StringUtils.equals(docTypeName, "*"))
                || (StringUtils.isNotEmpty(matchingDocTypeName) && matchingDocTypeName.equals(docTypeName))) {
                matchingPermissions.add(kpi);
            }
        }

        return matchingPermissions;
    }

    protected DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KewApiServiceLocator.getDocumentTypeService();
        }
        return this.documentTypeService;
    }

}
