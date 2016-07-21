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
package org.kuali.kfs.kns.lookup;

import java.util.Collection;
import java.util.Set;

import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Contract for strategies which can help LokoupResultsService with aspects (mainly id generation and result lookup) of multi value lookup support 
 *
 */
public interface LookupResultsSupportStrategyService {

	/**
	 * Returns a list of BOs that were selected.
	 * 
	 * This implementation makes an attempt to retrieve all BOs with the given object IDs, unless they have been deleted or the object ID changed.
	 * Since data may have changed since the search, the returned BOs may not match the criteria used to search.
	 * 
	 * @param lookupResultsSequenceNumber the sequence number identifying the lookup results in the database
	 * @param boClass the class of the business object to retrieve
	 * @param personId the id of the principal performing this search
	 * @param lookupResultsService an implementation of the lookupResultsService to do some of the dirty work...
	 * @return a Collection of retrieved BusinessObjects
	 * @throws Exception if anything goes wrong...well, just blow up, okay?
	 */
    public abstract <T extends BusinessObject> Collection<T> retrieveSelectedResultBOs(Class<T> boClass, Set<String> lookupIds) throws Exception;
    
    /**
     * Generates a String id which is used as an id on a checkbox for result rows returning the business object in a multiple value lookup
     * 
     * @param businessObject the lookup to generate an id for
     * @return the String id
     */
    public abstract String getLookupIdForBusinessObject(BusinessObject businessObject);
    
    /**
     * Determines if the given class is supported by this strategy
     * 
     * @param boClass the class to test the determination on
     * @return true if this strategy supports it, false otherwise
     */
    public abstract boolean qualifiesForStrategy(Class<? extends BusinessObject> boClass);
}
