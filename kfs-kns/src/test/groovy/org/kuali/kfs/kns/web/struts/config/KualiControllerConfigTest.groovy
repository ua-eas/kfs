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
package org.kuali.kfs.kns.web.struts.config

import org.apache.struts.config.ControllerConfig
import org.junit.Before
import org.junit.Test
import org.kuali.kfs.coreservice.framework.parameter.ParameterService
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader

import javax.xml.namespace.QName

import static org.junit.Assert.assertEquals

/**
 * Tests KualiControllerConfig
 *
 */
class KualiControllerConfigTest {
    def strutsControllerConfig = { "250M" } as ControllerConfig;
    String parameterMaxFileSize;

    @Before
    void setupFakeEnv() {
        parameterMaxFileSize = null

        def config = new SimpleConfig()
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID")
        ConfigContext.init(config)

        GlobalResourceLoader.stop()

        GlobalResourceLoader.addResourceLoader(new BaseResourceLoader(new QName("Foo", "Bar")) {
            def getService(QName name) {
                ["cf.parameterService":
                         [getParameterValueAsString: { ns, cmp, param -> parameterMaxFileSize }] as ParameterService
                ][name.getLocalPart()]
            }
        })

    }

    @Test
    void testGetMaxFileSizeFallbackToStrutsDefault() {
        def config = new KualiControllerConfig(strutsControllerConfig)
        config.freeze()
        assertEquals("250M", config.getMaxFileSize())

        parameterMaxFileSize = ""
        assertEquals("250M", config.getMaxFileSize())
    }

    @Test
    void testGetMaxFileSizeUsesParameter() {
        parameterMaxFileSize = "300M"
        def config = new KualiControllerConfig(strutsControllerConfig)
        config.freeze()

        assertEquals("300M", config.getMaxFileSize())

        // ensure parameter change is reflected in struts even though config is frozen
        parameterMaxFileSize = "200M"
        assertEquals("200M", config.getMaxFileSize())
    }
}
