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
package org.kuali.kfs.krad.uif.field;

/**
 * Action field security adds the take action flags to the standard component security
 *
 *
 */
public class ActionFieldSecurity extends FieldSecurity {
    private static final long serialVersionUID = 585138507596582667L;

    private boolean performActionAuthz;
    private boolean performLineActionAuthz;

    public ActionFieldSecurity() {
        super();

        performActionAuthz = false;
        performLineActionAuthz = false;
    }

    /**
     * Indicates whether the action field has take action authorization and KIM should be consulted
     *
     * @return boolean true if the action field has perform action authorization, false if not
     */
    public boolean isPerformActionAuthz() {
        return performActionAuthz;
    }

    /**
     * Setter for the perform action authorization flag
     *
     * @param performActionAuthz
     */
    public void setPerformActionAuthz(boolean performActionAuthz) {
        this.performActionAuthz = performActionAuthz;
    }

    /**
     * Indicates whether the line action field has take action authorization and KIM should be consulted
     *
     * @return boolean true if the line action field has perform action authorization, false if not
     */
    public boolean isPerformLineActionAuthz() {
        return performLineActionAuthz;
    }

    /**
     * Setter for the perform line action authorization flag
     *
     * @param performLineActionAuthz
     */
    public void setPerformLineActionAuthz(boolean performLineActionAuthz) {
        this.performLineActionAuthz = performLineActionAuthz;
    }
}
