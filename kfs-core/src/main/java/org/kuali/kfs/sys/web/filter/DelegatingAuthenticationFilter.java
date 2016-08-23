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

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DelegatingAuthenticationFilter implements Filter {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegatingAuthenticationFilter.class);

    private Filter wrappedFilter;
    private ConfigurationService configurationService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("init() started");

        String filterClassName = getFilterClassName();
        if ( filterClassName == null ) {
            LOG.error("init() login.filter.className is not defined in the properties file");
            throw new ServletException("login.filter.className is not defined in the properties file");
        }

        try {
            Class c = Class.forName(filterClassName);
            Object o = c.newInstance();
            if ( o instanceof Filter ) {
                wrappedFilter = (Filter)o;
            } else {
                LOG.error("init() Filter class does not implement Filter interface: " + filterClassName);
                throw new ServletException("Filter class does not implement Filter interface: " + filterClassName);
            }
        } catch (ClassNotFoundException e) {
            LOG.error("init() Filter class not found: " + filterClassName,e);
            throw new ServletException("Filter class not found: " + filterClassName);
        } catch (InstantiationException|IllegalAccessException e) {
            LOG.error("init() Unable to create instance of Filter class: " + filterClassName,e);
            throw new ServletException("Unable to create instance of Filter class: " + filterClassName);
        }

        wrappedFilter.init(new WrappedFilterConfig(filterConfig.getServletContext(),getFilterInitParameters()));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        LOG.debug("doFilter() started");

        wrappedFilter.doFilter(request, response, filterChain);
    }

    @Override
    public void destroy() {
        wrappedFilter.destroy();
    }

    protected Map<String,String> getFilterInitParameters() {
        return getAllConfigurationPropertyKeys()
                .stream()
                .filter(key -> key.startsWith("login.filter.param."))
                .collect(Collectors.toMap(key -> getParameterName(key), key -> getConfigurationPropertyValue(key)));
    }

    protected String getParameterName(String key) {
        return key.substring(19);
    }

    protected String getFilterClassName() {
        return getConfigurationService().getPropertyValueAsString("login.filter.className");
    }

    protected String getConfigurationPropertyValue(String key) {
        return getConfigurationService().getPropertyValueAsString(key);
    }

    protected Set<String> getAllConfigurationPropertyKeys() {
        return getConfigurationService().getAllProperties().keySet();
    }

    protected ConfigurationService getConfigurationService() {
        if ( configurationService == null ) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

    class WrappedFilterConfig implements FilterConfig {
        private ServletContext servletContext;
        private Map<String,String> initParameters;

        public WrappedFilterConfig(ServletContext servletContext,Map<String,String> initParameters) {
            this.servletContext = servletContext;
            this.initParameters = initParameters;
        }

        @Override
        public String getFilterName() {
            return "DelegatingAuthenticationFilter";
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }

        @Override
        public String getInitParameter(String s) {
            return initParameters.get(s);
        }

        @Override
        public Enumeration getInitParameterNames() {
            return Collections.enumeration(initParameters.keySet());
        }
    }
}
