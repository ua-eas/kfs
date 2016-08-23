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
package org.kuali.kfs.krad.datadictionary.validation.processor;

import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

/**
 * This abstract class can be extended by constraint processor classes that
 * must be processed on every validation.
 *
 *
 */
public abstract class MandatoryElementConstraintProcessor<C extends Constraint> implements ConstraintProcessor<Object, C> {

	protected DataDictionaryService dataDictionaryService;
	protected DateTimeService dateTimeService;


	/**
	 * @see ConstraintProcessor#isOptional()
	 */
	@Override
	public boolean isOptional() {
		return false;
	}

	/**
	 * @return the dataDictionaryService
	 */
	public DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null)
			dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		return this.dataDictionaryService;
	}

	/**
	 * @param dataDictionaryService the dataDictionaryService to set
	 */
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

	/**
	 * @return the dateTimeService
	 */
	public DateTimeService getDateTimeService() {
		if (dateTimeService == null)
			dateTimeService = CoreApiServiceLocator.getDateTimeService();

		return this.dateTimeService;
	}

	/**
	 * @param dateTimeService the dateTimeService to set
	 */
	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

}
