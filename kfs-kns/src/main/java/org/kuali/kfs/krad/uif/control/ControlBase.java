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
package org.kuali.kfs.krad.uif.control;

import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;

/**
 * Base class for all <code>Control</code> implementations
 *
 * @see Control
 */
public abstract class ControlBase extends ComponentBase implements Control {
    private static final long serialVersionUID = -7898244978136312663L;

    private int tabIndex;

    private boolean disabled;
    private String disabledReason;

    public ControlBase() {
        super();

        disabled = false;
    }

    /**
     * @see Component#getComponentTypeName()
     */
    @Override
    public final String getComponentTypeName() {
        return "control";
    }

    /**
     * @see Control#getTabIndex()
     */
    public int getTabIndex() {
        return this.tabIndex;
    }

    /**
     * @see Control#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * @see Control#isDisabled()
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @see Control#setDisabled(boolean)
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @see Control#getDisabledReason()
     */
    public String getDisabledReason() {
        return disabledReason;
    }

    /**
     * @see Control#setDisabledReason(java.lang.String)
     */
    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    @Override
    public boolean getSupportsOnChange() {
        return true;
    }

    @Override
    public boolean getSupportsOnBlur() {
        return true;
    }

    @Override
    public boolean getSupportsOnClick() {
        return true;
    }

    @Override
    public boolean getSupportsOnDblClick() {
        return true;
    }

    @Override
    public boolean getSupportsOnFocus() {
        return true;
    }
}
