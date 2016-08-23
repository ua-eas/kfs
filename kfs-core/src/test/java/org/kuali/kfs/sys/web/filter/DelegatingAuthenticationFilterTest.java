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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DelegatingAuthenticationFilterTest {
    private DelegatingAuthenticationFilter authenticationFilter;

    @Before
    public void setUp() {
    }

    @Test
    public void testNoFilterClassName() {
        Map<String, String> parameters = new HashMap<>();
        authenticationFilter = new TestAuthenticationFilter(null, parameters);

        try {
            authenticationFilter.init(new TestFilterConfig());
            Assert.fail("Should have thrown exception");
        } catch (ServletException e) {
            // Expected
        }
    }

    @Test
    public void testInvalidFilterClassName() {
        Map<String, String> parameters = new HashMap<>();
        authenticationFilter = new TestAuthenticationFilter("blah", parameters);

        try {
            authenticationFilter.init(new TestFilterConfig());
            Assert.fail("Should have thrown exception");
        } catch (ServletException e) {
            // Expected
        }
    }

    @Test
    public void testWrongClassFilterClassName() {
        Map<String, String> parameters = new HashMap<>();
        authenticationFilter = new TestAuthenticationFilter("java.lang.String", parameters);

        try {
            authenticationFilter.init(new TestFilterConfig());
            Assert.fail("Should have thrown exception");
        } catch (ServletException e) {
            // Expected
        }
    }

    @Test
    public void testWrongClassCantCreateFilterClassName() {
        Map<String, String> parameters = new HashMap<>();
        // Calendar has a protected constructor so we can't create a new instance
        authenticationFilter = new TestAuthenticationFilter("java.util.Calendar", parameters);

        try {
            authenticationFilter.init(new TestFilterConfig());
            Assert.fail("Should have thrown exception");
        } catch (ServletException e) {
            // Expected
        }
    }

    @Test
    public void testInitCalled() throws ServletException {
        Map<String, String> parameters = new HashMap<>();
        authenticationFilter = new TestAuthenticationFilter("org.kuali.kfs.sys.web.filter.TestFilter", parameters);

        authenticationFilter.init(new TestFilterConfig());
        Assert.assertTrue(TestFilter.initCalled);
    }

    @Test
    public void testInitParameters() throws ServletException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("test1", "value1");
        parameters.put("test2", "value2");

        authenticationFilter = new TestAuthenticationFilter("org.kuali.kfs.sys.web.filter.TestFilter", parameters);

        authenticationFilter.init(new TestFilterConfig());
        Assert.assertTrue(TestFilter.initCalled);
        Assert.assertEquals("value1", TestFilter.test1Parameter);
        Assert.assertEquals("value2", TestFilter.test2Parameter);
    }

    @Test
    public void testDoFilter() throws ServletException, IOException {
        Map<String, String> parameters = new HashMap<>();
        authenticationFilter = new TestAuthenticationFilter("org.kuali.kfs.sys.web.filter.TestFilter", parameters);
        authenticationFilter.init(new TestFilterConfig());

        authenticationFilter.doFilter(null, null, null);
        Assert.assertTrue(TestFilter.doFilterCalled);
    }

    @Test
    public void testDestroy() throws ServletException {
        Map<String, String> parameters = new HashMap<>();
        authenticationFilter = new TestAuthenticationFilter("org.kuali.kfs.sys.web.filter.TestFilter", parameters);
        authenticationFilter.init(new TestFilterConfig());

        authenticationFilter.destroy();
        Assert.assertTrue(TestFilter.destroyCalled);
    }

    @Test
    public void testConfiguration() throws ServletException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("test1", "badvalue1");
        parameters.put("test2", "badvalue2");
        parameters.put("login.filter.param.test1", "value1");
        parameters.put("login.filter.param.test2", "value2");

        authenticationFilter = new TestAuthenticationFilterConfiguration("org.kuali.kfs.sys.web.filter.TestFilter", parameters);

        authenticationFilter.init(new TestFilterConfig());
        Assert.assertTrue(TestFilter.initCalled);
        Assert.assertEquals("value1", TestFilter.test1Parameter);
        Assert.assertEquals("value2", TestFilter.test2Parameter);
    }

    class TestAuthenticationFilter extends DelegatingAuthenticationFilter {
        private String filterClassName;
        private Map<String, String> parameters;

        public TestAuthenticationFilter(String filterClassName, Map<String, String> parameters) {
            this.filterClassName = filterClassName;
            this.parameters = parameters;
        }

        @Override
        protected String getFilterClassName() {
            return filterClassName;
        }

        @Override
        protected Map<String, String> getFilterInitParameters() {
            return parameters;
        }
    }

    class TestAuthenticationFilterConfiguration extends DelegatingAuthenticationFilter {
        private String filterClassName;
        private Map<String, String> parameters;

        public TestAuthenticationFilterConfiguration(String filterClassName, Map<String, String> parameters) {
            this.filterClassName = filterClassName;
            this.parameters = parameters;
        }

        @Override
        protected String getFilterClassName() {
            return filterClassName;
        }

        @Override
        protected String getConfigurationPropertyValue(String key) {
            return parameters.get(key);
        }

        @Override
        protected Set<String> getAllConfigurationPropertyKeys() {
            return parameters.keySet();
        }
    }

    class TestFilterConfig implements FilterConfig {
        @Override
        public String getFilterName() {
            return "DelegatingAuthenticationFilter";
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public String getInitParameter(String s) {
            return null;
        }

        @Override
        public Enumeration getInitParameterNames() {
            return null;
        }
    }
}
