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
package org.kuali.kfs.kns.util;

import java.io.Serializable;

/**
 * This class is used to keep tab state whether it is open or closed.
 *
 *
 */

public class TabState implements Serializable {

    private static final long serialVersionUID = -628061041191967501L;
    private boolean open;

    /**
     * simple boolean getter
     *
     * @return
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * boolean setter
     *
     * @param open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

}
