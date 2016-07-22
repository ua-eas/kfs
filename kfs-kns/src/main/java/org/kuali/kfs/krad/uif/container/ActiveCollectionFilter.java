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
package org.kuali.kfs.krad.uif.container;

import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.kfs.krad.uif.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection filter that removes inactive lines from a collection whose line types
 * implement the <code>Inactivatable</code> interface
 *
 * 
 */
public class ActiveCollectionFilter implements CollectionFilter {
    private static final long serialVersionUID = 3273495753269940272L;

    /**
     * Iterates through the collection and if the collection line type implements <code>Inactivatable</code>,
     * active indexes are added to the show indexes list
     *
     * @see CollectionFilter#filter(View, Object, CollectionGroup)
     */
    @Override
    public List<Integer> filter(View view, Object model, CollectionGroup collectionGroup) {
        // get the collection for this group from the model
        List<Object> modelCollection =
                ObjectPropertyUtils.getPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath());

        // iterate through and add only active indexes
        List<Integer> showIndexes = new ArrayList<Integer>();
        if (modelCollection != null) {
            int lineIndex = 0;
            for (Object line : modelCollection) {
                if (line instanceof Inactivatable) {
                    boolean active = ((Inactivatable) line).isActive();
                    if (active) {
                        showIndexes.add(lineIndex);
                    }
                }
                lineIndex++;
            }
        }

        return showIndexes;
    }
}
