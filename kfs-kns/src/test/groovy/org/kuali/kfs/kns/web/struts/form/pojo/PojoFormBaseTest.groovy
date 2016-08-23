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
package org.kuali.kfs.kns.web.struts.form.pojo

import org.apache.struts.config.ControllerConfig
import org.junit.Before
import org.junit.Test
import org.kuali.kfs.coreservice.framework.parameter.ParameterService
import org.kuali.kfs.kim.impl.identity.TestPerson
import org.kuali.kfs.kns.web.struts.form.KualiMaintenanceForm
import org.kuali.kfs.krad.util.KRADConstants
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader
import org.kuali.rice.kim.api.identity.PersonService

import javax.xml.namespace.QName

import static org.junit.Assert.assertEquals

/**
 * Tests PojoFormBase
 */
class PojoFormBaseTest {
    def strutsControllerConfig = { "250M" } as ControllerConfig;
    String maxUploadSize;
    String maxAttachmentSize;

    @Before
    void setupFakeEnv() {
        maxUploadSize = maxAttachmentSize = null

        def config = new SimpleConfig()
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID")
        ConfigContext.init(config)

        GlobalResourceLoader.stop()

        GlobalResourceLoader.addResourceLoader(new BaseResourceLoader(new QName("Foo", "Bar")) {
            def getService(QName name) {
                ["cf.parameterService":
                         [getParameterValueAsString: { ns, cmp, param ->
                             [(KRADConstants.MAX_UPLOAD_SIZE_PARM_NM)         : maxUploadSize,
                              (KRADConstants.ATTACHMENT_MAX_FILE_SIZE_PARM_NM): maxAttachmentSize][param]
                         }] as ParameterService,
                 // KualiMaintenanceForm -> Note -> AdHocRoutePerson -> PersonService getPersonImplementationClass lookup :(
                 // stub impl class
                 personService        : { TestPerson.class } as PersonService
                ][name.getLocalPart()]
            }
        })
    }

    /**
     * Tests that PojoFormBase uses the max upload size parameter
     * to determine max file upload size
     */
    @Test
    void testGetMaxFileSizesGlobalDefault() {
        // when no parameters are defined whatsoever, uses PojoFormBase hardcoded default
        assertEquals(["250M"], new PojoFormBase().getMaxUploadSizes())

        maxUploadSize = "300M"
        assertEquals(["300M"], new PojoFormBase().getMaxUploadSizes())
    }

    /**
     * Tests how KualiMaintenanceForm derives max upload sizes
     */
    @Test
    void testGetMaxFileSizesKualiDocumentDefault() {
        // nothing defined, falls back to hardcoded default
        assertEquals(["250M"], new KualiMaintenanceForm().getMaxUploadSizes())

        // only global default is set
        maxUploadSize = "300M"
        assertEquals(["300M"], new KualiMaintenanceForm().getMaxUploadSizes())

        // if the max attachment size param is set, then the sizes list is not empty
        // and therefore global default is not used
        maxAttachmentSize = "200M"
        assertEquals(["200M"], new KualiMaintenanceForm().getMaxUploadSizes())
    }
}
