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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.InactivatableFromTo;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.InactivateableFromToService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.LookupService;
import org.kuali.kfs.krad.util.BeanPropertyComparator;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementation of InactivateableFromToService that uses the lookup service for query implementation
 * 
 * @see InactivateableFromToService
 */
public class InactivateableFromToServiceImpl implements InactivateableFromToService {

	protected DateTimeService dateTimeService;
	protected LookupService lookupService;
    protected DataDictionaryService dataDictionaryService;

	/**
	 * Uses lookup service which will convert the active criteria to active begin/to field criteria
	 * 
	 * @see InactivateableFromToService#findMatchingActive(java.lang.Class, java.util.Map)
	 */
	public List<InactivatableFromTo> findMatchingActive(Class<? extends InactivatableFromTo> clazz, Map fieldValues) {
		fieldValues.put(KRADPropertyConstants.ACTIVE, "true");

		return (List<InactivatableFromTo>) lookupService.findCollectionBySearchUnbounded(clazz, fieldValues);
	}

	/**
	 * Uses lookup service which will convert the active criteria to active begin/to field criteria
	 * 
	 * @see InactivateableFromToService#findMatchingActiveAsOfDate(java.lang.Class, java.util.Map,
	 *      java.util.Date)
	 */
	public List<InactivatableFromTo> findMatchingActiveAsOfDate(Class<? extends InactivatableFromTo> clazz,
			Map fieldValues, Date activeAsOfDate) {
		fieldValues.put(KRADPropertyConstants.ACTIVE, "true");
		fieldValues.put(KRADPropertyConstants.ACTIVE_AS_OF_DATE, dateTimeService.toDateString(activeAsOfDate));

		return (List<InactivatableFromTo>) lookupService.findCollectionBySearchUnbounded(clazz, fieldValues);
	}

	/**
	 * @see InactivateableFromToService#filterOutNonActive(java.util.List)
	 */
	public List<InactivatableFromTo> filterOutNonActive(List<InactivatableFromTo> filterList) {
		return filterOutNonActive(filterList, dateTimeService.getCurrentDate());
	}

	/**
	 * @see InactivateableFromToService#filterOutNonActive(java.util.List, java.util.Date)
	 */
	public List<InactivatableFromTo> filterOutNonActive(List<InactivatableFromTo> filterList, Date activeAsOfDate) {
		List<InactivatableFromTo> filteredList = new ArrayList<InactivatableFromTo>();

		for (InactivatableFromTo inactivateable : filterList) {
			inactivateable.setActiveAsOfDate(new java.sql.Timestamp(activeAsOfDate.getTime()));
			if (inactivateable.isActive()) {
				filteredList.add(inactivateable);
			}
		}

		return filteredList;
	}

	/**
	 * Uses lookup service which will convert the active and current criteria to active begin/to field criteria
	 * 
	 * @see InactivateableFromToService#findMatchingCurrent(java.lang.Class, java.util.Map)
	 */
	public List<InactivatableFromTo> findMatchingCurrent(Class<? extends InactivatableFromTo> clazz,
			Map fieldValues) {
		fieldValues.put(KRADPropertyConstants.ACTIVE, "true");
		fieldValues.put(KRADPropertyConstants.CURRENT, "true");

		return (List<InactivatableFromTo>) lookupService.findCollectionBySearchUnbounded(clazz, fieldValues);
	}

	/**
	 * Uses lookup service which will convert the active and current criteria to active begin/to field criteria
	 * 
	 * @see InactivateableFromToService#findMatchingCurrent(java.lang.Class, java.util.Map, java.util.Date)
	 */
	public List<InactivatableFromTo> findMatchingCurrent(Class<? extends InactivatableFromTo> clazz,
			Map fieldValues, Date currentAsOfDate) {
		fieldValues.put(KRADPropertyConstants.ACTIVE, "true");
		fieldValues.put(KRADPropertyConstants.CURRENT, "true");
		fieldValues.put(KRADPropertyConstants.ACTIVE_AS_OF_DATE, dateTimeService.toDateString(currentAsOfDate));

		return (List<InactivatableFromTo>) lookupService.findCollectionBySearchUnbounded(clazz, fieldValues);
	}

	/**
	 * @see InactivateableFromToService#filterOutNonCurrent(java.util.List)
	 */
	public List<InactivatableFromTo> filterOutNonCurrent(List<InactivatableFromTo> filterList) {
		return filterOutNonCurrent(filterList, dateTimeService.getCurrentDate());
	}

	/**
	 * @see InactivateableFromToService#filterOutNonCurrent(java.util.List, java.util.Date)
	 */
	public List<InactivatableFromTo> filterOutNonCurrent(List<InactivatableFromTo> filterList, Date currentAsOfDate) {
		List<InactivatableFromTo> activeList = filterOutNonActive(filterList, currentAsOfDate);

		if (activeList.isEmpty()) {
			return activeList;
		}

		List<InactivatableFromTo> currentList = new ArrayList<InactivatableFromTo>();

		List<String> groupByList = getDataDictionaryService().getGroupByAttributesForEffectiveDating(
                activeList.get(0).getClass());
		if (groupByList != null) {
			List<String> sortByList = new ArrayList<String>(groupByList);
			sortByList.add(KRADPropertyConstants.ACTIVE_FROM_DATE);

			// sort list so we get records together with same group by
			Collections.sort(activeList, new BeanPropertyComparator(sortByList, true));

			// reverse so we get max active begin date first within the group by
			Collections.reverse(activeList);

			String previousGroupByString = "";
			Date previousActiveFromDate = null;
			for (InactivatableFromTo inactivateable : activeList) {
				String groupByString = buildGroupByValueString((BusinessObject) inactivateable, groupByList);
				if (!StringUtils.equals(groupByString, previousGroupByString)) {
					// always add first record of new group by since list is sorted by active begin date descending
					currentList.add(inactivateable);
				}
				// active from date should not be null here since we are dealing with only active records
				else if (inactivateable.getActiveFromDate().equals(previousActiveFromDate)) {
					// have more than one record for the group by key with same active begin date, so they both are current
					currentList.add(inactivateable);
				}

				previousGroupByString = groupByString;
				previousActiveFromDate = inactivateable.getActiveFromDate();
			}
		} else {
			currentList = activeList;
		}

		return currentList;
	}

	/**
	 * Builds a string containing the values from the given business object for the fields in the given list, concatenated together using a
	 * bar. Null values are treated as an empty string
	 * 
	 * @param businessObject
	 *            - business object instance to get values from
	 * @param groupByList
	 *            - list of fields to get values for
	 * @return String
	 */
	protected String buildGroupByValueString(BusinessObject businessObject, List<String> groupByList) {
		String groupByValueString = "";

		for (String groupByField : groupByList) {
			Object fieldValue = ObjectUtils.getPropertyValue(businessObject, groupByField);
			groupByValueString += "|";
			if (fieldValue != null) {
				groupByValueString += fieldValue;
			}
		}

		return groupByValueString;
	}

	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
