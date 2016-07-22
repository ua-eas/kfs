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
package org.kuali.kfs.kns.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Filters parameters coming in through Struts requests to exclude those that could be damaging to the class loader in
 * response to CVE-2014-0114.
 *
 * @deprecated Patches Struts 1 which is end-of-life and will eventually be removed from Rice.
 *
 * 
 */
@Deprecated
public class ParameterFilter implements Filter {

    private String excludeParams;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.excludeParams = filterConfig.getInitParameter("excludeParams");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new FilteredServletRequest(request, excludeParams), response);
    }

    public void destroy() { }

    private static class FilteredServletRequest extends HttpServletRequestWrapper {

        private final Pattern excludeParams;

        private FilteredServletRequest(ServletRequest request, String excludeParams) {
            super((HttpServletRequest) request);

            this.excludeParams = Pattern.compile(excludeParams);
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Enumeration getParameterNames() {
            List<String> finalParameterNames = new ArrayList<String>();

            ArrayList<String> requestParameterNames = Collections.list(super.getParameterNames());

            for (String parameterName : requestParameterNames) {
                if (!excludeParams.matcher(parameterName).matches()) {
                    finalParameterNames.add(parameterName);
                }
            }

            return Collections.enumeration(finalParameterNames);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Map getParameterMap() {
            Map requestParameterMap = super.getParameterMap();

            HashMap<String, Object> finalParameterMap = new HashMap<String, Object>();

            for (Object key : requestParameterMap.keySet()) {
                if (key instanceof String) {
                    String stringKey = (String) key;

                    if (!excludeParams.matcher(stringKey).matches()) {
                        finalParameterMap.put(stringKey, requestParameterMap.get(key));
                    }
                }
            }

            return finalParameterMap;
        }

        @Override
        public String[] getParameterValues(String name) {
            if (!excludeParams.matcher(name).matches()) {
                return super.getParameterValues(name);
            } else {
                return null;
            }
        }

        @Override
        public String getParameter(String name) {
            if (!excludeParams.matcher(name).matches()) {
                return super.getParameter(name);
            } else {
                return null;
            }
        }
    }

}
