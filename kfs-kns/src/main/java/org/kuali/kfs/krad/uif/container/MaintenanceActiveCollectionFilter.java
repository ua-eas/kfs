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
package org.kuali.kfs.krad.uif.container;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection filter for maintenance groups that removes inactive lines if certain
 * conditions are met
 */
public class MaintenanceActiveCollectionFilter implements CollectionFilter {
    private static final long serialVersionUID = -6045332235106531456L;

    private String oldBindingObjectPath;

    /**
     * Iterates through the collection and if the collection line type implements <code>Inactivatable</code>
     * active indexes are added to the show indexes list
     * <p>
     * <p>
     * In the case of a new line being added, the user is not allowed to hide the record (even if it is inactive).
     * Likewise in the case of an edit where the active flag has changed between the old and new side, the user
     * is not allowed to hide
     * </p>
     *
     * @see CollectionFilter#filter(View, Object, CollectionGroup)
     */
    @Override
    public List<Integer> filter(View view, Object model, CollectionGroup collectionGroup) {

        // get the collection for this group from the model
        List<Object> newCollection =
            ObjectPropertyUtils.getPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath());

        // Get collection from old data object
        List<Object> oldCollection = null;
        String oldCollectionBindingPath = null;
        oldCollectionBindingPath = StringUtils.replaceOnce(collectionGroup.getBindingInfo().getBindingPath(),
            collectionGroup.getBindingInfo().getBindingObjectPath(), oldBindingObjectPath);
        oldCollection = ObjectPropertyUtils.getPropertyValue(model, oldCollectionBindingPath);

        // iterate through and add only active indexes
        List<Integer> showIndexes = new ArrayList<Integer>();
        for (int i = 0; i < newCollection.size(); i++) {
            Object line = newCollection.get(i);
            if (line instanceof Inactivatable) {
                boolean active = ((Inactivatable) line).isActive();
                if ((oldCollection != null) && (oldCollection.size() > i)) {
                    // if active status has changed, show record
                    Inactivatable oldLine = (Inactivatable) oldCollection.get(i);
                    if (oldLine.isActive()) {
                        showIndexes.add(i);
                    }
                } else {
                    // TODO: if newly added line, show record
                    // If only new and no old add the newline
                    if (active) {
                        showIndexes.add(i);
                    }
                }
            }
        }

        return showIndexes;
    }

    /**
     * Gives the binding path to the old data object for comparison, used to
     * get the active status of the old object
     *
     * @return String binding path
     */
    public String getOldBindingObjectPath() {
        return oldBindingObjectPath;
    }

    /**
     * Setter for the path to the old data object
     *
     * @param oldBindingObjectPath
     */
    public void setOldBindingObjectPath(String oldBindingObjectPath) {
        this.oldBindingObjectPath = oldBindingObjectPath;
    }
}
