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

package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.coreservice.framework.parameter.ParameterConstants;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.CsrfService;
import org.kuali.kfs.krad.util.CsrfValidator;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CsrfServiceImpl implements CsrfService {
    private ConfigurationService configurationService;
    private ParameterService parameterService;

    @Override
    public boolean validateCsrfIfNecessary(HttpServletRequest request, HttpServletResponse response) {
        if (request == null || response == null) {
            throw new IllegalArgumentException("request and response must not be null");
        }
        return !isEnabled() || isExemptPath(request) || CsrfValidator.validateCsrf(request, response);
    }

    /**
     * Returns true if the given requestUri matches one of the provided exempt paths.
     */
    protected boolean isExemptPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] exemptPaths = exemptPaths();
        if (exemptPaths != null) {
            for (String path : exemptPaths) {
                if (requestURI.contains(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String[] exemptPaths() {
        // check parameter first
        String exemptPaths = getParameterService().getParameterValueAsString(KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, ParameterConstants.ALL_COMPONENT, KRADConstants.ParameterNames.CSRF_EXEMPT_PATHS);
        if (exemptPaths == null) {
            // next check the config property
            exemptPaths = getConfigurationService().getPropertyValueAsString(KRADConstants.Config.CSRF_EXEMPT_PATHS);
        }
        if (StringUtils.isBlank(exemptPaths)) {
            return null;
        }
        return exemptPaths.split(",");
    }

    protected boolean isEnabled() {
        // first check the system parameter
        Boolean csrfEnabled = getParameterService().getParameterValueAsBoolean(KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, ParameterConstants.ALL_COMPONENT, KRADConstants.ParameterNames.CSRF_ENABLED_IND);
        if (csrfEnabled == null) {
            // next check the config property
            csrfEnabled = getConfigurationService().getPropertyValueAsBoolean(KRADConstants.Config.CSRF_ENABLED, true);
        }
        if (csrfEnabled == null) {

        }
        return csrfEnabled;
    }

    @Override
    public String getSessionToken(HttpServletRequest request) {
        return CsrfValidator.getSessionToken(request);
    }

    public ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            this.configurationService = CoreApiServiceLocator.getKualiConfigurationService();
        }
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ParameterService getParameterService() {
        if (parameterService == null) {
            this.parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
