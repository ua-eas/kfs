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
package org.kuali.kfs.krad.uif.widget;

/**
 * Growls sets up settings for growls global to the current view and its pages
 * Some basic options of the plugin are exposed through this class, however additional options
 * can be passed through setComponentOptions as usual.
 * However, the header and theme option is set by the growl processing in PageGroup
 * automatically.
 * See the jquery jGrowl plugin for more details.
 */
public class Growls extends WidgetBase {
    private static final long serialVersionUID = -8701090110933484411L;

    private boolean sticky;
    private int timeShown;
    private String position;

    /**
     * If true, the growl will stick to the page until the user dismisses it
     *
     * @return the sticky
     */
    public boolean isSticky() {
        return this.sticky;
    }

    /**
     * @param sticky the sticky to set
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
        this.getComponentOptions().put("sticky", Boolean.toString(sticky));
    }

    /**
     * The time growls are shown in milliseconds
     *
     * @return the timeShown
     */
    public int getTimeShown() {
        return this.timeShown;
    }

    /**
     * @param timeShown the timeShown to set
     */
    public void setTimeShown(int timeShown) {
        this.timeShown = timeShown;
        this.getComponentOptions().put("life", Integer.toString(timeShown));
    }

    /**
     * The position for the growls to appear in the window
     * There are five options available: top-left, top-right, bottom-left, bottom-right, center.
     *
     * @return the position
     */
    public String getPosition() {
        return this.position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(String position) {
        this.position = position;
        this.getComponentOptions().put("position", position);
    }
}
