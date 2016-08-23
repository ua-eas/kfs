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
package org.kuali.kfs.krad.lookup;

import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Collection;

/**
 * Classes that implement this interface will refresh
 */
public interface SelectiveReferenceRefresher {
    /**
     * Returns a list of references that must be refreshed after a lookup performed on an attribute is performed.
     * <p>
     * A lookup on an attribute may cause many attribute values to be updated upon return from lookup.  Generally,
     * the returned attributes are the PK of the BO being looked up.  For example,
     * a lookup on an account number attribute will cause the chart of accounts code and account code to be returned.
     * <p>
     * These returned attributes may cause other references on the page to be returned.  For example, an account number lookup
     * may cause the chart code to change, and if there is also an ObjectCode reference in the BO, then any change in the chart code will cause the
     * referenced ObjectCode BO to be changed.
     *
     * @param attributeName the name of the attribute with a quickfinder of the maintained BO.
     * @return a list of reference names that could be affected by lookup return values
     */
    public Collection<String> getAffectedReferencesFromLookup(BusinessObject baseBO, String attributeName, String collectionPrefix);
}
