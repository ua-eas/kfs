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
package org.kuali.kfs.krad.web.controller;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * This simple controller loads the module locked view when a user accesses a
 * module which has been locked for maintenance.
 */
@Controller
public class ModuleLockedController {

    /**
     * Constant defined to match with method call in moduleLocked.jsp which is
     * set to a message that is displayed when the module is locked.
     */
    public static final String MODULE_LOCKED_MESSAGE = "moduleLockedMessage";
    public static final String MODULE_PARAMETER = "moduleNamespace";

    @RequestMapping(value = "/module-locked")
    public ModelAndView moduleLocked(@RequestParam(value = MODULE_PARAMETER, required = true) String moduleNamespaceCode) {
        ModelAndView modelAndView = new ModelAndView("moduleLocked");
        ParameterService parameterSerivce = CoreFrameworkServiceLocator.getParameterService();
        String messageParamComponentCode = KRADConstants.DetailTypes.ALL_DETAIL_TYPE;
        String messageParamName = KRADConstants.SystemGroupParameterNames.OLTP_LOCKOUT_MESSAGE_PARM;
        String lockoutMessage = parameterSerivce.getParameterValueAsString(moduleNamespaceCode, messageParamComponentCode, messageParamName);

        if (StringUtils.isBlank(lockoutMessage)) {
            String defaultMessageParamName = KRADConstants.SystemGroupParameterNames.OLTP_LOCKOUT_DEFAULT_MESSAGE;
            lockoutMessage = parameterSerivce.getParameterValueAsString(KRADConstants.KNS_NAMESPACE, messageParamComponentCode, defaultMessageParamName);
        }
        modelAndView.addObject(MODULE_LOCKED_MESSAGE, lockoutMessage);
        return modelAndView;
    }
}
