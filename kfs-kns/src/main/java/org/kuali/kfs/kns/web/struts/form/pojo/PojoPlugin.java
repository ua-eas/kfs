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
package org.kuali.kfs.kns.web.struts.form.pojo;

// deleted some imports
// end Kuali Foundation modification


import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.kuali.kfs.kns.web.struts.config.KualiControllerConfig;

import javax.servlet.ServletException;
import java.util.logging.Logger;

/**
 * begin Kuali Foundation modification
 * This class is the POJO Plugin implementation of the PlugIn interface.
 * end Kuali Foundation modification
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
// Kuali Foundation modification: class originally named SL plugin
public class PojoPlugin implements PlugIn {
    static final Logger logger = Logger.getLogger(PojoPlugin.class.getName());

    public static void initBeanUtils() {
        // begin Kuali Foundation modification
        ConvertUtilsBean convUtils = new ConvertUtilsBean();
        PropertyUtilsBean propUtils = new PojoPropertyUtilsBean();
        BeanUtilsBean pojoBeanUtils = new BeanUtilsBean(convUtils, propUtils);

        BeanUtilsBean.setInstance(pojoBeanUtils);
        logger.fine("Initialized BeanUtilsBean with " + pojoBeanUtils);
        // end Kuali Foundation modification
    }

    public PojoPlugin() {
    }

    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
        initBeanUtils();
        // override the Struts ControllerConfig with our own wrapper that knows how to
        // dynamically find max file upload size according to Rice run-time settings
        config.setControllerConfig(new KualiControllerConfig(config.getControllerConfig()));
    }

    public void destroy() {
    }
}
