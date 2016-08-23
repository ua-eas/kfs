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
package org.kuali.kfs.krad.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.kfs.krad.bo.AdHocRouteRecipient;
import org.kuali.kfs.krad.document.Document;

import java.util.List;
import java.util.ListIterator;

public class RouteToCompletionUtil {

    /**
     * Checks if there is atleast one Ad-Hoc Completion request for the document and based on that returns a boolean
     * value.
     */
    public static boolean checkIfAtleastOneAdHocCompleteRequestExist(Document document) {
        boolean foundAtleastOneCompleteReq = false;
        // iterating the adhoc recpients list to check if there is atleast on complete request for the document.
        foundAtleastOneCompleteReq = loopAndCheckValue(document.getAdHocRouteWorkgroups()) || loopAndCheckValue(
                document.getAdHocRoutePersons());

        return foundAtleastOneCompleteReq;
    }

    /**
     * Loops and checks if the required value is present in the loop used for checking if there is atleast one adhoc
     * completion
     * request present for a person or work group
     */
    private static boolean loopAndCheckValue(List adhoc) {
        if (adhoc == null) {
            return false;
        }

        ListIterator<AdHocRouteRecipient> groupIter = adhoc.listIterator();
        String valueToCheck = null;
        AdHocRouteRecipient recipient = null;

        boolean foundAtleastOneCompleteReq = false;
        while (groupIter.hasNext()) {
            recipient = groupIter.next();
            valueToCheck = recipient.getActionRequested();
            if (StringUtils.isNotEmpty(valueToCheck)) {
                if (KewApiConstants.ACTION_REQUEST_COMPLETE_REQ.equals(valueToCheck)) {
                    foundAtleastOneCompleteReq = true;
                    break;
                }
            }
        }

        return foundAtleastOneCompleteReq;
    }
}
