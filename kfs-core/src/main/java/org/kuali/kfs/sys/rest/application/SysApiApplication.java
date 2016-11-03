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
package org.kuali.kfs.sys.rest.application;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.rest.resource.AuthenticationResource;
import org.kuali.kfs.sys.rest.resource.AuthorizationResource;
import org.kuali.kfs.sys.rest.resource.BackdoorResource;
import org.kuali.kfs.sys.rest.resource.BusinessObjectApiResource;
import org.kuali.kfs.sys.rest.resource.PreferencesResource;
import org.kuali.kfs.sys.rest.resource.SystemResource;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("sys/api/v1")
public class SysApiApplication extends BaseApiApplication {

    public SysApiApplication() {
        singletons.add(new PreferencesResource());
        singletons.add(new BackdoorResource());
        singletons.add(new AuthenticationResource());
        singletons.add(new SystemResource());
        singletons.add(new AuthorizationResource());
        if (SpringContext.getBean(ConfigurationService.class).getPropertyValueAsBoolean("apis.enabled")) {
            singletons.add(new BusinessObjectApiResource("sys"));
        }
    }
}
