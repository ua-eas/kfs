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
package org.kuali.kfs.sys.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.UserPreferencesService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserPreferencesServiceImpl implements UserPreferencesService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UserPreferencesServiceImpl.class);

    private PreferencesDao preferencesDao;

    @Override
    @Transactional
    public Map<String, Object> getUserPreferences(String principalName) {
        LOG.debug("getUserPreferences() started");

        final Map<String, Object> userPreferences = preferencesDao.getUserPreferences(principalName);

        if (!MapUtils.isEmpty(userPreferences)) {
            return userPreferences;
        }

        // create a new default UserPreferences
        final Map<String, Object> defaultUserPreferences = createDefaultUserPreferences();
        preferencesDao.saveUserPreferences(principalName, defaultUserPreferences);

        return defaultUserPreferences;
    }

    @Override
    @NonTransactional
    public Map<String, Object> createDefaultUserPreferences() {
        Map<String, Object> defaultUserPreferences = new ConcurrentHashMap<>();
        defaultUserPreferences.put(KFSPropertyConstants.SIDEBAR_OUT, Boolean.TRUE);
        List<String> checkedLinkFilters = Arrays.asList(KFSConstants.NavigationLinkCategories.ACTVITIES, KFSConstants.NavigationLinkCategories.REFERENCE, KFSConstants.NavigationLinkCategories.ADMINISTRATION);
        defaultUserPreferences.put(KFSPropertyConstants.CHECKED_LINK_FILTERS, checkedLinkFilters);
        return defaultUserPreferences;
    }

    @Override
    @Transactional
    public void saveUserPreferences(String principalName, String preferences) {
        LOG.debug("saveUserPreferences() started");

        preferencesDao.saveUserPreferences(principalName, preferences);
    }

    @Override
    @Transactional
    public void saveUserPreferencesKey(String principalName, String key, String preferences) {
        LOG.debug("saveUserPreferencesKey() started");

        Map<String, Object> userPrefs = getUserPreferences(principalName);
        if (userPrefs == null) {
            userPrefs = new ConcurrentHashMap<>();
        }
        userPrefs.put(key, preferences);

        ObjectMapper mapper = new ObjectMapper();
        try {
            saveUserPreferences(principalName, mapper.writeValueAsString(userPrefs));
        } catch (JsonProcessingException e) {
            LOG.error("saveUserPreferencesKey() Error processing json", e);
            throw new RuntimeException("Error processing json");
        }
    }

    @NonTransactional
    public void setPreferencesDao(PreferencesDao preferencesDao) {
        this.preferencesDao = preferencesDao;
    }
}
