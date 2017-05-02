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
package org.kuali.kfs.sys.dataaccess;

import java.util.Map;

public interface PreferencesDao {

    Map<String, Object> findInstitutionPreferences();

    void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences);

    Map<String, Object> findInstitutionPreferencesCache(String principalName);

    void cacheInstitutionPreferences(String principalName, Map<String, Object> institutionPreferences);

    void clearInstitutionPreferencesCache();

    Map<String, Object> getUserPreferences(String principalName);

    void saveUserPreferences(String principalName, String preferences);

    void saveUserPreferences(String principalName, Map<String, Object> preferences);
}
