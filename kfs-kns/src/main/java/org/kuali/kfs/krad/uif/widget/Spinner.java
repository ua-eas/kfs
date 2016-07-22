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
package org.kuali.kfs.krad.uif.widget;

import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.view.View;

/**
 * Widget that decorates a control transforming into a spinner
 *
 * <p>
 * Spinners allow the incrementing or decrementing of the controls value with an arrow up and down icon on
 * the right side of the control. How the value is incremented, min/max values, and so on can be configured
 * through the {@link Component#getComponentOptions()} property
 * </p>
 *
 * 
 */
public class Spinner extends WidgetBase {
    private static final long serialVersionUID = -659830874214415990L;

    public Spinner() {
        super();
    }

    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
    }
}
