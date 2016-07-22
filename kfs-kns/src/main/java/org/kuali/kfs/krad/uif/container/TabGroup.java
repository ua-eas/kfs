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

import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.widget.Tabs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A group that presents its child Groups as tabs.  Items in this group's item list must be Groups
 * themselves.
 *
 * 
 * @see Group
 */
public class TabGroup extends Group {
    private static final long serialVersionUID = 3L;

    private Tabs tabsWidget;

    public TabGroup() {
        super();
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(tabsWidget);

        return components;
    }

    /**
     * Only groups are supported for this group.
     *
     * @see org.kuali.rice.krad.web.view.container.ContainerBase#getSupportedComponents()
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> supportedComponents = new HashSet<Class<? extends Component>>();
        supportedComponents.add(Group.class);

        return supportedComponents;
    }

    /**
     * Gets the widget which contains any configuration for the tab widget component used to render
     * this TabGroup
     *
     * @return the tabsWidget
     */
    public Tabs getTabsWidget() {
        return this.tabsWidget;
    }

    /**
     * @param tabsWidget the tabsWidget to set
     */
    public void setTabsWidget(Tabs tabsWidget) {
        this.tabsWidget = tabsWidget;
    }

}
