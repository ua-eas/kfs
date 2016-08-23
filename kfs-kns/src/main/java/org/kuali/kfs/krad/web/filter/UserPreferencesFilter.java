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
package org.kuali.kfs.krad.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.preferences.Preferences;
import org.kuali.rice.kew.api.preferences.PreferencesService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.util.KRADUtils;

/**
 * This class establishes and initializes the KEW Preferences after a user logs in.
 *
 * <p>
 * This filter assumes that a UserSession is already established.
 * </p>
 *
 *
 */
public class UserPreferencesFilter implements Filter {

    private static final Log LOG = LogFactory.getLog(UserPreferencesFilter.class);

    private FilterConfig filterConfig;
    private PreferencesService preferencesService;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final UserSession session = KRADUtils.getUserSessionFromRequest(request);

        if (session == null) {
            throw new IllegalStateException("A user session has not been established");
        }

        final String principalId = session.getPrincipalId();

        if (session.retrieveObject(KewApiConstants.PREFERENCES) == null) {
            final Preferences preferences = retrievePreferences(principalId);
            session.addObject(KewApiConstants.PREFERENCES, preferences);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }

    private Preferences retrievePreferences(String principalId) {
        Preferences preferences = this.getPreferenceService().getPreferences(principalId);
        if (preferences.isRequiresSave()) {
            LOG.info("Detected that user preferences require saving.");
            this.getPreferenceService().savePreferences(principalId, preferences);
            preferences = this.getPreferenceService().getPreferences(principalId);
        }

        return preferences;
    }


    private PreferencesService getPreferenceService() {
        if (this.preferencesService == null) {
            this.preferencesService = KewApiServiceLocator.getPreferencesService();
        }

        return this.preferencesService;
    }

}
