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
package org.kuali.kfs.kns.kim.responsibility;

import org.kuali.kfs.kns.kim.type.DataDictionaryTypeServiceBase;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.framework.responsibility.ResponsibilityTypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @deprecated A krad integrated type service base class will be provided in the future.
 */
@Deprecated
public class KimResponsibilityTypeServiceBase extends DataDictionaryTypeServiceBase
		implements ResponsibilityTypeService {

	@Override
	public final List<Responsibility> getMatchingResponsibilities( Map<String, String> requestedDetails, List<Responsibility> responsibilitiesList ) {
		if (requestedDetails == null) {
            throw new RiceIllegalArgumentException("requestedDetails is null");
        }

        if (responsibilitiesList == null) {
            throw new RiceIllegalArgumentException("responsibilitiesList is null");
        }

        requestedDetails = translateInputAttributes(requestedDetails);
		validateRequiredAttributesAgainstReceived(requestedDetails);
		return Collections.unmodifiableList(performResponsibilityMatches(requestedDetails, responsibilitiesList));
	}

	/**
	 * Internal method for matching Responsibilities.  Override this method to customize the matching behavior.
	 * 
	 * This base implementation uses the {@link #performMatch(Map, Map)} method
	 * to perform an exact match on the Responsibility details and return all that are equal.
	 */
	protected List<Responsibility> performResponsibilityMatches(Map<String, String> requestedDetails, List<Responsibility> responsibilitiesList) {
		List<Responsibility> matchingResponsibilities = new ArrayList<Responsibility>();
		for (Responsibility responsibility : responsibilitiesList) {
			if ( performMatch(requestedDetails, responsibility.getAttributes())) {
				matchingResponsibilities.add( responsibility );
			}
		}
		return matchingResponsibilities;
	}
}
