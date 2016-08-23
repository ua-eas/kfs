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
package org.kuali.kfs.kns.util;

import java.util.Map;

/**
 * Inquiry screens and maintenance documents may render a collection of BOs on a screen.  These
 * BOs may implement {@link org.kuali.rice.core.api.mo.common.active.MutableInactivatable}, which means that the BO has an active
 * flag of true or false.  Some screens may give the user the ability to not render (i.e. hide) inactive
 * collection elements.  This class has several utilities to control that behavior.
 */
public final class InactiveRecordsHidingUtils {

    private InactiveRecordsHidingUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    /**
     * Returns whether a collection has been set to show inactive records.  Note that if a collection has not been set to show inactive inactive records, then
     * this method will return false.
     *
     * @param inactiveRecordDisplay a Map used to keep state between invocations of this method and {@link #setShowInactiveRecords(Map, String, boolean)}
     * @param collectionName        the name of the collection
     * @return
     */
    public static boolean getShowInactiveRecords(Map<String, Boolean> inactiveRecordDisplay, String collectionName) {
        // by default, show the actives
        boolean showInactive = true;

        if (collectionName == null) {
            throw new IllegalArgumentException("collection name cannot be null");
        }
        // remove periods from the collection name due to parsing limitation in Apache beanutils
        collectionName = collectionName.replace('.', '_');

        if (inactiveRecordDisplay.containsKey(collectionName)) {
            Object inactiveSetting = inactiveRecordDisplay.get(collectionName);

            // warren: i copied this code from somewhere else, and have no idea why they're testing to see whether it's a
            // Boolean, but I'm guessing that it has to do w/ the PojoFormBase not setting things correctly
            if (inactiveSetting instanceof Boolean) {
                showInactive = ((Boolean) inactiveSetting).booleanValue();
            } else {
                showInactive = Boolean.parseBoolean(((String[]) inactiveSetting)[0]);
            }
        }

        return showInactive;
    }

    /**
     * Sets whether a method should show inactive records
     *
     * @param inactiveRecordDisplay a Map used to keep state between invocations of this method and {@link #getShowInactiveRecords(Map, String)}
     * @param collectionName        the name of the collection
     * @param showInactive          whether to show inactive records
     */
    public static void setShowInactiveRecords(Map<String, Boolean> inactiveRecordDisplay, String collectionName, boolean showInactive) {
        if (collectionName == null) {
            throw new IllegalArgumentException("collection name cannot be null");
        }

        // remove periods from the collection name due to parsing limitation in Apache beanutils
        collectionName = collectionName.replace('.', '_');

        inactiveRecordDisplay.put(collectionName, new Boolean(showInactive));
    }

    public static String formatCollectionName(String collectionName) {
        return collectionName.replace('.', '_');
    }
}
