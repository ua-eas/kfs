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
package org.kuali.kfs.kns.web.struts.config;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.config.ControllerConfig;
import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.krad.util.KRADConstants;

/**
 * Kuali customization of ControllerConfig which delegates max upload size lookup to
 * parameter service: KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.MAX_UPLOAD_SIZE_PARM_NM
 * The value must be a string compatible with Struts maxFileSize attribute.
 */
public class KualiControllerConfig extends ControllerConfigWrapper {
    public KualiControllerConfig(ControllerConfig config) {
        super(config);
    }

    /**
     * Returns the global max file upload size, which is dynamically derived from the Rice parameter service.
     * This technically breaks the implicit contract in ControllerConfig that the config is frozen after startup.
     *
     * @return the global max file upload size
     */
    @Override
    public String getMaxFileSize() {
        String maxFileSize = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.MAX_UPLOAD_SIZE_PARM_NM);
        if (StringUtils.isNotBlank(maxFileSize)) {
            return maxFileSize;
        }
        return super.getMaxFileSize();
    }

    /**
     * Overridden to throw an UnsupportedOperationException.  Once our KualiControllerConfig is
     * in place, it does not make sense to support this setter.
     */
    @Override
    public void setMaxFileSize(String s) {
        throw new UnsupportedOperationException("Cannot set max file size through KualiControllerConfig");
    }
}
