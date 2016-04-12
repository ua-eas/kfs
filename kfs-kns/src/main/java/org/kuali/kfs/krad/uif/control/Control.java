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
package org.kuali.kfs.krad.uif.control;

import org.kuali.kfs.krad.uif.component.Component;

/**
 * Represents an interactive element in the UI (typically an HTML control)
 * <p>
 * Each control that can be rendered in the UIF should be an implement the
 * <code>Control</code> interface. The control is a regular component, thus has
 * a corresponding template that will render the control for the UI. Controls
 * provide the mechanism for gathering data from the User or for the User to
 * initiate an action. HTML controls must be rendered within a <code>Form</code>
 * element.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Control extends Component {

    /**
     * Unique index of the control within the tab order
     *
     * <p>
     * Tab index provides a way to set the order users will tab through the
     * controls. The control with index 1 will receive focus when the page is
     * rendered. Tabing from the field will then take the user to the control
     * with index 2, then index 3, and so on.
     * </p>
     *
     * @return int the tab index for the control
     */
    public int getTabIndex();

    /**
     * Setter for the controls tab order index
     *
     * @param tabIndex
     */
    public void setTabIndex(int tabIndex);

    /**
     * Indicates whether the control is disabled (doesn't allow input)
     *
     * @return boolean true if the control is disabled, false if not
     */
    public boolean isDisabled();

    /**
     * Setter for the disabled indicator
     *
     * @param disabled
     */
    public void setDisabled(boolean disabled);

    /**
     * If the control is disabled, gives a reason for why which will be displayed as a tooltip
     * on the control
     *
     * @return String disabled reason text
     * @see {@link #isDisabled()}
     */
    public String getDisabledReason();

    /**
     * Setter for the disabled reason text
     *
     * @param disabledReason
     */
    public void setDisabledReason(String disabledReason);
}


