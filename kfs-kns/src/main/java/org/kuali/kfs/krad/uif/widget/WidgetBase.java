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

import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;

/**
 * Base class for Widgets
 * <p>
 * <p>
 * Sets the component type name for all widget components and provides default
 * implementation of performFinalize
 * </p>
 */
public abstract class WidgetBase extends ComponentBase implements Widget {
    private static final long serialVersionUID = -917582902829056830L;

    public WidgetBase() {
        super();
    }

    /**
     * @see Component#getComponentTypeName()
     */
    @Override
    public String getComponentTypeName() {
        return "widget";
    }

}
