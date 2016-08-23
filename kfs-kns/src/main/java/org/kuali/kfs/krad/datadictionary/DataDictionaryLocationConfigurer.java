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
package org.kuali.kfs.krad.datadictionary;

import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * Puts a data dictionary file location in the data dictionary
 *
 *
 */
public class DataDictionaryLocationConfigurer implements InitializingBean {

	private List<String> dataDictionaryPackages;

	private DataDictionaryService dataDictionaryService;

	public DataDictionaryLocationConfigurer(DataDictionaryService dataDictionaryService){
		this.dataDictionaryService = dataDictionaryService;
	}

	public void afterPropertiesSet() throws Exception {
		if (dataDictionaryPackages == null || dataDictionaryPackages.isEmpty()) {
			throw new ConfigurationException("datatDictionaryPackages empty when initializing DataDictionaryLocation bean.");
		}
		if(dataDictionaryService!=null)
			dataDictionaryService.addDataDictionaryLocations(getDataDictionaryPackages());
		else
			KRADServiceLocatorWeb.getDataDictionaryService().addDataDictionaryLocations(getDataDictionaryPackages());
	}

	public List<String> getDataDictionaryPackages() {
		return dataDictionaryPackages;
	}

	public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
		this.dataDictionaryPackages = dataDictionaryPackages;
	}

}
