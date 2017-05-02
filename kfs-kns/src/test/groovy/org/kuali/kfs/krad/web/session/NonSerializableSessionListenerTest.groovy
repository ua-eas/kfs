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
package org.kuali.kfs.krad.web.session

import org.junit.Test
import org.kuali.rice.core.api.config.property.Config
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.springframework.mock.web.MockHttpSession

import javax.servlet.http.HttpSessionBindingEvent

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * Tests that NonSerializableSessionListener serialization checks are disabled in production
 */
class NonSerializableSessionListenerTest {
    static class TestNonSerializableSessionListener extends NonSerializableSessionListener {
        private serializationChecked = false

        protected void checkSerialization(final HttpSessionBindingEvent se, String action) {
            serializationChecked = true
            super.checkSerialization(se, action)
        }
    }

    @Test
    void listenerIsExecutedInNonProductionEnvironment() {
        def config = new SimpleConfig()
        config.putProperty(Config.ENVIRONMENT, "dev")
        config.putProperty("enableSerializationCheck", "true");
        ConfigContext.init(config)

        def listener = new TestNonSerializableSessionListener()
        listener.attributeAdded(new HttpSessionBindingEvent(new MockHttpSession(), "attrib", "value"))
        assertTrue(listener.serializationChecked)
    }

    @Test
    void listenerIsNotExecutedInProductionEnvironment() {
        def config = new SimpleConfig()
        config.putProperty(Config.PROD_ENVIRONMENT_CODE, "prod")
        config.putProperty(Config.ENVIRONMENT, "prod")
        config.putProperty("enableSerializationCheck", "true");
        ConfigContext.init(config)

        def listener = new TestNonSerializableSessionListener()
        listener.attributeAdded(new HttpSessionBindingEvent(new MockHttpSession(), "attrib", "value"))
        assertFalse(listener.serializationChecked)
    }
}
