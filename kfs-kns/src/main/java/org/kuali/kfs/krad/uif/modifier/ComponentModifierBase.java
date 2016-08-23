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
package org.kuali.kfs.krad.uif.modifier;

import org.kuali.kfs.krad.uif.component.Ordered;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ConfigurableBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for <code>ComponentModifier</code> implementations
 *
 * <p>
 * Holds run phase property and defaults to the INITIALIZE phase, and the order
 * property for setting the order in which the component modifier will be
 * invoked
 * </p>
 *
 *
 */
public abstract class ComponentModifierBase extends ConfigurableBase implements ComponentModifier {
	private static final long serialVersionUID = -8284332412469942130L;

	private String runPhase;
	private String runCondition;
	private int order;

	public ComponentModifierBase() {
        super();

		runPhase = UifConstants.ViewPhases.INITIALIZE;
		order = 0;
	}

    /**
     * Default performInitialization impl (does nothing)
     *
     * @see ComponentModifierBase#performInitialization
     */
    @Override
    public void performInitialization(View view, Object model, Component component) {

    }

    /**
     * @see ComponentModifierBase#getComponentPrototypes()
     */
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        return components;
    }

    /**
	 * @see ComponentModifier#getRunPhase()
	 */
	public String getRunPhase() {
		return this.runPhase;
	}

	/**
	 * Setter for the component initializer run phase
	 *
	 * @param runPhase
	 */
	public void setRunPhase(String runPhase) {
		this.runPhase = runPhase;
	}

	/**
	 * @see ComponentModifier#getRunCondition()
	 */
	public String getRunCondition() {
		return this.runCondition;
	}

	/**
	 * Setter for the component modifiers run condition
	 *
	 * @param runCondition
	 */
	public void setRunCondition(String runCondition) {
		this.runCondition = runCondition;
	}

	/**
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * @see Ordered#setOrder(int)
	 */
	public void setOrder(int order) {
		this.order = order;
	}

}
