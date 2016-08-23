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
package org.kuali.kfs.sys.web.filter;

import co.kuali.core.auth.AuthConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

public class CoreAuthenticationFilterConfiguration implements AuthConfiguration {
    private ConfigurationService configurationService;

    @Override
    public String getAuthBaseUrl() {
        return getConfigurationService().getPropertyValueAsString("core.authentication.filter.authBaseUrl");
    }

    @Override
    public Long getSecondsToCacheAuthTokenResponse() {
        String value = getConfigurationService().getPropertyValueAsString("core.authentication.filter.secondsToCacheAuthTokenResponse");
        if (StringUtils.isEmpty(value) ) {
            return 300L;
        } else {
            return Long.parseLong(value);
        }
    }

    protected ConfigurationService getConfigurationService() {
        if ( configurationService == null ) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

}
