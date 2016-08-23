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

import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.field.ActionField;

import java.util.HashSet;
import java.util.Set;

/**
 * Special <code>Group</code> that renders a navigation section
 *
 * <p>
 * Only supports <code>ActionField</code> instances within the container. These
 * are used to provide the items (or individual links) within the navigation.
 * The navigationType determines how the navigation will be rendered (menu,
 * tabs, dropdown, ...)
 * </p>
 *
 *
 */
public class NavigationGroup extends Group {
	private static final long serialVersionUID = -7263923392768546340L;

	private String navigationType;

	public NavigationGroup() {
		super();
	}

	/**
	 * @see org.kuali.rice.krad.web.view.container.ContainerBase#getSupportedComponents()
	 */
	@Override
	public Set<Class<? extends Component>> getSupportedComponents() {
		Set<Class<? extends Component>> supportedComponents = new HashSet<Class<? extends Component>>();
		supportedComponents.add(ActionField.class);

		return supportedComponents;
	}

	/**
	 * Type of navigation that should be rendered. For example a menu or tab
	 * navigation. Used by the rendering script to choose an appropriate plug-in
	 *
	 * @return String navigation type
	 * @see UifConstants.NavigationType
	 */
	public String getNavigationType() {
		return this.navigationType;
	}

	/**
	 * Setter for the navigation type
	 *
	 * @param navigationType
	 */
	public void setNavigationType(String navigationType) {
		this.navigationType = navigationType;
	}

}
