package org.kuali.kfs.sys.identity;

import org.kuali.kfs.kns.kim.permission.PermissionTypeServiceBase;
import org.kuali.rice.kim.api.permission.Permission;

import java.util.List;
import java.util.Map;

public class ViewReportPermissionTypeServiceImpl extends PermissionTypeServiceBase {

    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails, List<Permission> permissionsList) {
        return null;
    }
}
