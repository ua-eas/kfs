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
package org.kuali.kfs.kns.lookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.KRADPropertyConstants;

/**
 * The LookupResultsSupportStrategyService implementation which supports PersistableBusinessObjects, simply enough
 *
 */
public class PersistableBusinessObjectLookupResultsSupportStrategyImpl
		implements LookupResultsSupportStrategyService {

	private BusinessObjectService businessObjectService;

	/**
	 * Returns the object id
	 *
	 * @see LookupResultsSupportStrategyService#getLookupIdForBusinessObject(org.kuali.rice.krad.bo.BusinessObject)
	 */
	public String getLookupIdForBusinessObject(BusinessObject businessObject) {
		PersistableBusinessObject pbo = (PersistableBusinessObject)businessObject;
		return pbo.getObjectId();
	}

	/**
	 * Uses the BusinessObjectService to retrieve a collection of PersistableBusinessObjects
	 *
	 * @see LookupResultsSupportStrategyService#retrieveSelectedResultBOs(java.lang.String, java.lang.Class, java.lang.String)
	 */
	public <T extends BusinessObject> Collection<T> retrieveSelectedResultBOs(Class<T> boClass, Set<String> lookupIds)
			throws Exception {

        Map<String, Collection<String>> queryCriteria = new HashMap<String, Collection<String>>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, lookupIds);
        return getBusinessObjectService().findMatching(boClass, queryCriteria);
	}


	/**
	 * Sees if the class implements the PersistableBusinessObject interface; if so, then yes, the BO qualifies!
	 * @see LookupResultsSupportStrategyService#qualifiesForStrategy(java.lang.Class)
	 */
	public boolean qualifiesForStrategy(Class<? extends BusinessObject> boClass) {
		return PersistableBusinessObject.class.isAssignableFrom(boClass);
	}

	/**
	 * @return the businessObjectService
	 */
	public BusinessObjectService getBusinessObjectService() {
		return this.businessObjectService;
	}

	/**
	 * @param businessObjectService the businessObjectService to set
	 */
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}
}
