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
package org.kuali.kfs.krad.uif.container;

import org.kuali.kfs.krad.uif.field.MessageField;
import org.kuali.kfs.krad.uif.component.ConfigurableBase;

import java.io.Serializable;

public class NodePrototype extends ConfigurableBase implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageField labelPrototype;
    private Group dataGroupPrototype;

    public NodePrototype() {

    }

    /**
     * @param labelPrototype the labelPrototype to set
     */
    public void setLabelPrototype(MessageField labelPrototype) {
        this.labelPrototype = labelPrototype;
    }

    /**
     * @return the labelPrototype
     */
    public MessageField getLabelPrototype() {
        return this.labelPrototype;
    }

    /**
     * @param dataGroupPrototype the dataGroupPrototype to set
     */
    public void setDataGroupPrototype(Group dataGroupPrototype) {
        this.dataGroupPrototype = dataGroupPrototype;
    }

    /**
     * @return the dataGroupPrototype
     */
    public Group getDataGroupPrototype() {
        return this.dataGroupPrototype;
    }
}
