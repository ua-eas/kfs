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
package org.kuali.kfs.krad.uif.control;

import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.widget.Spinner;

import java.util.List;

/**
 * Text control that as decorated with a spinner widget (allowing the control value to be modified using the
 * spinner)
 */
public class SpinnerControl extends TextControl {
    private static final long serialVersionUID = -8267606288443759880L;

    private Spinner spinner;

    public SpinnerControl() {
        super();
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(getSpinner());

        return components;
    }

    /**
     * Spinner widget that should decorate the control
     *
     * @return Spinner
     */
    public Spinner getSpinner() {
        return spinner;
    }

    /**
     * Setter for the control's spinner widget instance
     *
     * @param spinner
     */
    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }
}
