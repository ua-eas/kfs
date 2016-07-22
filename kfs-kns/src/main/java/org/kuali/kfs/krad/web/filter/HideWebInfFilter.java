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
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple filter that 404s any urls to embedded module WEB-INF directories.
 * Another solution would be for the container to disable directory browsing, however
 * files may still be accessed directly.  This filter will pre-emptively catch the URL
 * which means that application code cannot actually handle those URLs (for instance,
 * to do its own error handling).
 *
 * There is probably a better way to do this, e.g. a filter to bean proxy in some spring context,
 * but the sample app doesn't really have a web context of its own to put this in.
 *
 * 
 *
 */
public class HideWebInfFilter implements Filter {

	private static final Pattern WEB_INF_PATTERN = Pattern.compile(".*WEB-INF.*");
	
    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        // nothing
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {
        if ((req instanceof HttpServletRequest)) { 

            HttpServletRequest hsr = (HttpServletRequest) req;
    
            if (WEB_INF_PATTERN.matcher(hsr.getRequestURI()).matches()) {
                HttpServletResponse hsresp = (HttpServletResponse) res;
                hsresp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        fc.doFilter(req, res);
    }

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig arg0) throws ServletException {
        // nada
    }
}
