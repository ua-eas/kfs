/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.coreservice.impl.config;

import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.framework.config.module.ModuleConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows for configuring a client to integrate with the "core services" module in Kuali Rice.
 *
 * <p>The CoreServiceConfigurer supports two run modes:
 *   <ol>
 *       <li>REMOTE - loads the client which interacts remotely with the services</li>
 *       <li>LOCAL - loads the service implementations and web components locally</li>
 *   </ol>
 * </p>
 *
 * <p>Client applications should generally only use "remote" run mode (which is the default).</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CoreServiceConfigurer extends ModuleConfigurer {

    private static final String MODULE_NAME = "coreservice";

    public CoreServiceConfigurer() {
        super(MODULE_NAME);
        setValidRunModes(Arrays.asList(RunMode.REMOTE, RunMode.LOCAL));
    }

    @Override
	public List<String> getPrimarySpringFiles() {
        List<String> springFileLocations = new ArrayList<String>();
        springFileLocations.add("classpath:org/kuali/kfs/coreservice/config/CoreServiceLocalSpringBeans.xml");
		return springFileLocations;
	}

}
