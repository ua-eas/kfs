/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.krad.uif.widget;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

/**
 * Allows client-side reordering of the group contents
 */
public class Reorderer extends WidgetBase {
    private static final long serialVersionUID = 6142957061046219120L;

    private String movableStyleClass;

    public Reorderer() {
        super();
    }

    /**
     * The following initialization is performed:
     * <p>
     * <ul>
     * <li>Adds the movable style class to each group item</li>
     * <li>Prepares the movable widget option based on the movable style class</li>
     * </ul>
     */
    @Override
    public void performFinalize(View view, Object model, Component component) {
        super.performFinalize(view, model, component);

        if (!(component instanceof Group)) {
            throw new RiceIllegalArgumentException("Parent component for Reorderer widget must be a group.");
        }

        if (StringUtils.isNotBlank(movableStyleClass)) {
            for (Component item : ((Group) component).getItems()) {
                item.addStyleClass(movableStyleClass);
            }

            // add the default movable class to the selectors option if not already configured
            if (!getComponentOptions().containsKey(UifConstants.ReordererOptionKeys.SELECTORS)) {
                String selectorsOption =
                    "{" + UifConstants.ReordererOptionKeys.MOVABLES + " : 'span." + movableStyleClass + "' }";
                getComponentOptions().put(UifConstants.ReordererOptionKeys.SELECTORS, selectorsOption);
            }
        }
    }

    /**
     * Returns the style class for the item spans that will identify a movable element
     * <p>
     * <p>
     * Given style class will be used to build a jQuery selector that is then passed to the
     * reorderer widget through the options
     * </p>
     *
     * @return String style class
     */
    public String getMovableStyleClass() {
        return movableStyleClass;
    }

    /**
     * Setter for the style class that identifies movable elements (spans)
     *
     * @param movableStyleClass
     */
    public void setMovableStyleClass(String movableStyleClass) {
        this.movableStyleClass = movableStyleClass;
    }
}
