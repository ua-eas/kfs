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
package org.kuali.kfs.krad.uif.field;

import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;

/**
 * Data field security adds attribute security to the standard component security
 * <p>
 * <p>
 * The {@link AttributeSecurity} can be configured for the field to indicate there is security at the data layer
 * (configured by component (class) and property). If the field is backed by a data dictionary
 * {@link AttributeDefinition} the attribute security can be configured there and
 * will be picked up and inserted into the field security
 * </p>
 */
public class DataFieldSecurity extends FieldSecurity {
    private static final long serialVersionUID = 585138507596582667L;

    private AttributeSecurity attributeSecurity;

    public DataFieldSecurity() {
        super();
    }

    /**
     * Attribute security instance configured or picked up for the field
     *
     * @return AttributeSecurity instance
     */
    public AttributeSecurity getAttributeSecurity() {
        return attributeSecurity;
    }

    /**
     * Setter for the fields attribute security
     *
     * @param attributeSecurity
     */
    public void setAttributeSecurity(AttributeSecurity attributeSecurity) {
        this.attributeSecurity = attributeSecurity;
    }

}
