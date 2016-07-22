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
package org.kuali.kfs.kns.web.format;
// end Kuali Foundation modification

import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiConstants;

/**
 * begin Kuali Foundation modification
 * This class is used to format boolean values.
 * end Kuali Foundation modification
 * 
 */
public class DelegationTypeFormatter extends Formatter {
    private static final long serialVersionUID = -4109390572922205211L;
	/*
    protected Object convertToObject(String target) {
        if (Formatter.isEmptyValue(target))
            return null;

        String stringValue = target.getClass().isArray() ? unwrapString(target) : (String) target;
        stringValue = stringValue.trim().toUpperCase();

        return KewApiConstants.DELEGATION_TYPES.get(stringValue);
        if (TRUE_VALUES.contains(stringValue))
            return Boolean.TRUE;
        if (FALSE_VALUES.contains(stringValue))
            return Boolean.FALSE;

		// begin Kuali Foundation modification
		// was: throw new FormatException(CONVERT_MSG + stringValue);
        throw new FormatException("converting", RiceKeyConstants.ERROR_BOOLEAN, stringValue);
        // end Kuali Foundation modification
    }
*/
    public Object format(Object target) {
        if (target == null) {
            return null;
        }
        // begin Kuali Foundation modification
        if (target instanceof String) {
        	DelegationType delegationType = DelegationType.fromCode(((String) target).trim().toUpperCase());
        	if (delegationType != null) {
        		return delegationType.getLabel();
        	} else {
        		return KewApiConstants.DELEGATION_BOTH_LABEL;
        	}
        } else {
            return "";
        }
    }
}
