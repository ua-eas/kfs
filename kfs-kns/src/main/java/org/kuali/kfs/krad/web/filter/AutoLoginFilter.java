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
package org.kuali.kfs.krad.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Automatically logs in with the user specified via filter init parameter {@link AutoLoginFilter#USER_PARAM_NAME}.
 * <p>
 * There are no guarantees made that the user specified is a valid user in the system.
 * </p>
 * <p>
 * In rice this Filter can be used via config like that following assuming the bootstrap filter is used: <br />
 * {@code <param name="filter.login.class">org.kuali.kra.test.infrastructure.AutoLoginFilter</param>} <br />
 * {@code <param name="filtermapping.login.1">/*</param>} <br />
 * {@code <param name="filter.login.autouser">admin</param>} <br />
 * </p>
 */
public class AutoLoginFilter implements Filter {
	public static final String USER_PARAM_NAME = "autouser";
    
	private FilterConfig filterConfig;
	
	/** {@inheritDoc} */
	public void init(FilterConfig config) throws ServletException {
	    this.filterConfig = config;
	}

	/** {@inheritDoc} */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (filterConfig.getInitParameter(USER_PARAM_NAME) == null) {
		    throw new IllegalStateException("the " + USER_PARAM_NAME + " param is not set");
		}
	    
	    chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public String getRemoteUser() {
                return AutoLoginFilter.this.filterConfig.getInitParameter(USER_PARAM_NAME);
            }
        }, response);
	}

	/** {@inheritDoc} */
	public void destroy() {
	    this.filterConfig = null;
	}
}
