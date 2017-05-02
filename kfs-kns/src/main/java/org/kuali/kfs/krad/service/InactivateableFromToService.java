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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.bo.InactivatableFromTo;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Provides methods for retrieval of business objects implementing InactivateableFromTo and needing effective dating logic
 *
 * @see org.kuali.rice.kns.bo.InactivateableFromTo
 */
public interface InactivateableFromToService {

    /**
     * Performs search on given class and criteria and returns only results that active based on the active to/from dates and the current
     * date
     *
     * @param clazz       - InactivateableFromTo class to search
     * @param fieldValues - Search key values
     * @return List of InactivateableFromTo instances that match search criteria and are active
     */
    public List<InactivatableFromTo> findMatchingActive(Class<? extends InactivatableFromTo> clazz, Map fieldValues);

    /**
     * Performs search on given class and criteria and returns only results that active based on the active to/from dates and the given
     * active as of date
     *
     * @param clazz          - InactivateableFromTo class to search
     * @param fieldValues    - Search key values
     * @param activeAsOfDate - Date to compare to for determining active status
     * @return List of InactivateableFromTo instances that match search criteria and are active as of the given date
     */
    public List<InactivatableFromTo> findMatchingActiveAsOfDate(Class<? extends InactivatableFromTo> clazz,
                                                                Map fieldValues, Date activeAsOfDate);

    /**
     * Removes instances from the given list that are inactive based on the current date
     *
     * @param filterList - List of InactivateableFromTo instances to filter
     * @return List of InactivateableFromTo instances from the given list that are active as of the current date
     */
    public List<InactivatableFromTo> filterOutNonActive(List<InactivatableFromTo> filterList);

    /**
     * Removes instances from the given list that are inactive based on the given date
     *
     * @param filterList     - List of InactivateableFromTo instances to filter
     * @param activeAsOfDate - Date to compare to for determining active status
     * @return List of InactivateableFromTo instances from the given list that are active as of the given date
     */
    public List<InactivatableFromTo> filterOutNonActive(List<InactivatableFromTo> filterList, Date activeAsOfDate);

    /**
     * Performs search on given class and criteria and returns that are active and most current. That is if two records are active the more
     * current one will be the one with a later active begin date
     *
     * @param clazz       - InactivateableFromTo class to search
     * @param fieldValues - Search key values
     * @return List of InactivateableFromTo instances that match search criteria and are current
     */
    public List<InactivatableFromTo> findMatchingCurrent(Class<? extends InactivatableFromTo> clazz,
                                                         Map fieldValues);

    /**
     * Performs search on given class and criteria and returns that are active and most current based on the given date. That is if two
     * records are active the more current one will be the one with a later active begin date
     *
     * @param clazz           - InactivateableFromTo class to search
     * @param fieldValues     - Search key values
     * @param currentAsOfDate - Date to compare to for determining active and current status
     * @return List of InactivateableFromTo instances that match search criteria and are current
     */
    public List<InactivatableFromTo> findMatchingCurrent(Class<? extends InactivatableFromTo> clazz,
                                                         Map fieldValues, Date currentAsOfDate);

    /**
     * Removes instances from the given list that are not current based on the current date
     *
     * @param filterList - List of InactivateableFromTo instances to filter
     * @return List of InactivateableFromTo instances from the given list that are current as of the current date
     */
    public List<InactivatableFromTo> filterOutNonCurrent(List<InactivatableFromTo> filterList);

    /**
     * Removes instances from the given list that are not current based on the given date
     *
     * @param filterList      - List of InactivateableFromTo instances to filter
     * @param currentAsOfDate - Date to compare to for determining active and current status
     * @return List of InactivateableFromTo instances from the given list that are current as of the given date
     */
    public List<InactivatableFromTo> filterOutNonCurrent(List<InactivatableFromTo> filterList, Date currentAsOfDate);

}
