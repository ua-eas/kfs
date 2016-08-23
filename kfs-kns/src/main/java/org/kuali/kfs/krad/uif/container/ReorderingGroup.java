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

import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.widget.Reorderer;

import java.util.List;

/**
 * Group implementation that supports reordering of the group items
 * <p>
 * <p>
 * Uses a {@link Reorderer} widget to perform the reordering client side
 * </p>
 */
public class ReorderingGroup extends Group {
    private static final long serialVersionUID = -9069458348367183223L;

    private Reorderer reorderer;

    public ReorderingGroup() {
        super();
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(reorderer);

        return components;
    }

    /**
     * Widget that will perform the reordering of the group's items client side
     *
     * @return Reorderer widget instance
     */
    public Reorderer getReorderer() {
        return reorderer;
    }

    /**
     * Setter for the groups reorderer widget
     *
     * @param reorderer
     */
    public void setReorderer(Reorderer reorderer) {
        this.reorderer = reorderer;
    }
}
