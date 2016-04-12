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
package org.kuali.kfs.krad.uif.container;

import org.kuali.kfs.krad.uif.component.ComponentSecurity;

/**
 * Collection Group security is used to flag that permissions exist for the associated {@link CollectionGroup}
 * in KIM and should be checked to determine the associated group, line, and field state. In particular this adds
 * the edit line and view line flags
 *
 * <p>
 * In addition, properties such as additional role and permission details can be configured to use when
 * checking the KIM permissions
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionGroupSecurity extends ComponentSecurity {
    private static final long serialVersionUID = 1134455196763917062L;

    private boolean editLineAuthz;
    private boolean viewLineAuthz;

    public CollectionGroupSecurity() {
        super();

        editLineAuthz = false;
        viewLineAuthz = false;
    }

    /**
     * Indicates whether the collection group line has edit authorization and KIM should be consulted
     *
     * @return boolean true if the line has edit authorization, false if not
     */
    public boolean isEditLineAuthz() {
        return editLineAuthz;
    }

    /**
     * Setter for the edit line authorization flag
     *
     * @param editLineAuthz
     */
    public void setEditLineAuthz(boolean editLineAuthz) {
        this.editLineAuthz = editLineAuthz;
    }

    /**
     * Indicates whether the collection group line has view authorization and KIM should be consulted
     *
     * @return boolean true if the line has view authorization, false if not
     */
    public boolean isViewLineAuthz() {
        return viewLineAuthz;
    }

    /**
     * Setter for the view line authorization flag
     *
     * @param viewLineAuthz
     */
    public void setViewLineAuthz(boolean viewLineAuthz) {
        this.viewLineAuthz = viewLineAuthz;
    }

}
