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
package org.kuali.kfs.kns.datadictionary.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;

/**
 * A single HTML text control.
 */
@Deprecated
public class FileControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = 8778523411471162513L;

    // logger
    private static Log LOG = LogFactory.getLog(FileControlDefinition.class);

    public FileControlDefinition() {
        LOG.debug("creating new FileControlDefinition");
    }

    /**
     * @see ControlDefinition#isText()
     */
    public boolean isFile() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "FileControlDefinition";
    }
}


