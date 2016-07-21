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
package org.kuali.kfs.krad.uif.field;

import org.kuali.kfs.krad.uif.component.ComponentSecurity;

/**
 * Field security adds the edit in line and view in line flags to the standard component security
 *
 * <p>
 * These flags are only applicable when the field is part of a collection group. They indicate there is security
 * on the field within the collection line
 * </p>
 *
 * 
 */
public class FieldSecurity extends ComponentSecurity {

    private boolean editInLineAuthz;
    private boolean viewInLineAuthz;

    public FieldSecurity() {
        super();

        editInLineAuthz = false;
        viewInLineAuthz = false;
    }

    /**
     * Indicates whether the field has edit in line authorization and KIM should be consulted
     *
     * @return boolean true if the field has edit in line authorization, false if not
     */
    public boolean isEditInLineAuthz() {
        return editInLineAuthz;
    }

    /**
     * Setter for the edit in line authorization flag
     *
     * @param editInLineAuthz
     */
    public void setEditInLineAuthz(boolean editInLineAuthz) {
        this.editInLineAuthz = editInLineAuthz;
    }

    /**
     * Indicates whether the field has view in line unmask authorization and KIM should be consulted
     *
     * @return boolean true if the field has view in line unmask authorization, false if not
     */
    public boolean isViewInLineAuthz() {
        return viewInLineAuthz;
    }

    /**
     * Setter for the view in line authorization flag
     *
     * @param viewInLineAuthz
     */
    public void setViewInLineAuthz(boolean viewInLineAuthz) {
        this.viewInLineAuthz = viewInLineAuthz;
    }

}
