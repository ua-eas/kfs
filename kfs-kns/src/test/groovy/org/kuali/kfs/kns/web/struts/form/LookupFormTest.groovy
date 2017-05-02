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
package org.kuali.kfs.kns.web.struts.form

import org.apache.struts.mock.MockHttpServletRequest
import org.junit.Before
import org.junit.Test
import org.kuali.kfs.coreservice.framework.parameter.ParameterService
import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl
import org.kuali.kfs.kns.lookup.KualiLookupableImpl
import org.kuali.kfs.kns.service.BusinessObjectDictionaryService
import org.kuali.kfs.krad.bo.BusinessObjectBase
import org.kuali.kfs.krad.service.DataObjectAuthorizationService
import org.kuali.kfs.krad.util.KRADConstants
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader

import javax.xml.namespace.QName

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * tests lookup form
 */
class LookupFormTest {
    class TestBO extends BusinessObjectBase {
        public void refresh() {}
    }

    @Before
    void setupFakeEnv() {
        def config = new SimpleConfig()
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID");
        config.putProperty(KRADConstants.BACK_LOCATION_ALLOWED_REGEX, ".*");
        ConfigContext.init(config);

        GlobalResourceLoader.stop();

        GlobalResourceLoader.addResourceLoader(new BaseResourceLoader(new QName("Foo", "Bar")) {
            def getService(QName name) {
                ["cf.parameterService"               :
                         [getParameterValueAsString : { s0, s1, s2 -> null },
                          getParameterValueAsBoolean: { s0, s1, s2, s3 -> false }
                         ] as ParameterService,
                 "cf.dataObjectAuthorizationService" :
                         [attributeValueNeedsToBeEncryptedOnFormsAndLinks: { s0, s1 -> false }] as DataObjectAuthorizationService,
                 "cf.businessObjectDictionaryService":
                         [getLookupableID: { s0 -> null }] as BusinessObjectDictionaryService,
                 "cf.kualiLookupable"                : {
                     def l = new KualiLookupableImpl() {
                         void setBusinessObjectClass(Class boClass) {}

                         List getRows() { [] as List }

                         String getExtraButtonSource() { null }

                         String getExtraButtonParams() { null }
                     }
                     l.setLookupableHelperService(new KualiLookupableHelperServiceImpl())
                     return l
                 }()
                ][name.getLocalPart()]
            }
        });
    }

    @Test(expected = RuntimeException)
    void testFormRequiresBusinessObject() {
        new LookupForm().populate(new MockHttpServletRequest())
    }

    @Test
    void testFormViewFlags() {
        def form = new LookupForm();
        def req = new MockHttpServletRequest();

        assertTrue(form.headerBarEnabled)
        assertTrue(form.lookupCriteriaEnabled)
        req.addParameter(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TestBO.class.getName())
        req.addParameter(LookupForm.HEADER_BAR_ENABLED_PARAM, "false")
        req.addParameter(LookupForm.SEARCH_CRITERIA_ENABLED_PARAM, "false")
        req.addParameter(KRADConstants.RETURN_LOCATION_PARAMETER, "http://test");

        form.populate(req)

        assertFalse(form.headerBarEnabled)
        assertFalse(form.lookupCriteriaEnabled)
    }
}
