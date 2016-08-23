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
package org.kuali.kfs.krad.kim;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.common.assignee.Assignee;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - wliang don't forget to fill this in.
 */
public class PermissionDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {


    private static PermissionService permissionService;
    private String permissionTemplateNamespace;
    private String permissionTemplateName;

    /**
     * @return the permissionTemplateNamespace
     */
    public String getPermissionTemplateNamespace() {
        return this.permissionTemplateNamespace;
    }

    /**
     * @param permissionTemplateNamespace the permissionTemplateNamespace to set
     */
    public void setPermissionTemplateNamespace(String permissionTemplateNamespace) {
        this.permissionTemplateNamespace = permissionTemplateNamespace;
    }

    /**
     * @return the permissionTemplateName
     */
    public String getPermissionTemplateName() {
        return this.permissionTemplateName;
    }

    /**
     * @param permissionTemplateName the permissionTemplateName to set
     */
    public void setPermissionTemplateName(String permissionTemplateName) {
        this.permissionTemplateName = permissionTemplateName;
    }

    protected List<Assignee> getPermissionAssignees(Map<String, String> qualification) {
        return getPermissionService().getPermissionAssigneesByTemplate(permissionTemplateNamespace,
            permissionTemplateName, new HashMap<String, String>(qualification), new HashMap<String, String>(
                qualification));
    }

    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (roleName == null) {
            throw new RiceIllegalArgumentException("roleName was null");
        }
        List<Assignee> permissionAssignees = getPermissionAssignees(qualification);
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        for (Assignee permissionAssigneeInfo : permissionAssignees) {
            if (StringUtils.isNotBlank(permissionAssigneeInfo.getPrincipalId())) {
                members.add(RoleMembership.Builder.create(null/*roleId*/, null, permissionAssigneeInfo.getPrincipalId(), MemberType.PRINCIPAL, null).build());
            } else if (StringUtils.isNotBlank(permissionAssigneeInfo.getGroupId())) {
                members.add(RoleMembership.Builder.create(null/*roleId*/, null, permissionAssigneeInfo.getGroupId(), MemberType.GROUP, null).build());
            }
        }
        return members;
    }


    @Override
    public boolean hasDerivedRole(
        String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId was null or blank");
        }

        if (groupIds == null) {
            throw new RiceIllegalArgumentException("groupIds was null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName was null or blank");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null");
        }

        // FIXME: dangerous - data changes could cause an infinite loop - should add thread-local to trap state and abort
        return getPermissionService().isAuthorizedByTemplate(principalId, permissionTemplateNamespace,
            permissionTemplateName, new HashMap<String, String>(qualification), new HashMap<String, String>(
                qualification));
    }

    /**
     * @return the documentService
     */
    protected PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = KimApiServiceLocator.getPermissionService();
        }
        return permissionService;
    }

}
