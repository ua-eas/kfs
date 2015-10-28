package org.kuali.kfs.sys.web;

import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.InstitutionPreferencesService;

public class InstitutionConfigWebFunctions {

    public static boolean hasPermission() {
        String principalName = GlobalVariables.getUserSession().getPrincipalName();
        return SpringContext.getBean(InstitutionPreferencesService.class).hasConfigurationPermission(principalName);
    }
}
