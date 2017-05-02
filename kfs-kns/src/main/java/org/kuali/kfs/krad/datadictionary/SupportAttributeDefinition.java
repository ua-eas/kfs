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
package org.kuali.kfs.krad.datadictionary;

import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.util.ExternalizableBusinessObjectUtils;

/**
 * Support attributes define additional attributes that can be used to generate
 * lookup field conversions and lookup parameters.
 * <p>
 * Field conversions and lookup parameters are normally generated using foreign key relationships
 * defined within OJB and the DD.  Because Person objects are linked in a special way (i.e. they may
 * come from an external data source and not from the DB, such as LDAP), it is often necessary to define
 * extra fields that are related to each other, sort of like a supplemental foreign key.
 * <p>
 * sourceName is the name of the POJO property of the business object
 * targetName is the name of attribute that corresponds to the sourceName in the looked up BO
 * identifier when true, only the field marked as an identifier will be passed in as a lookup parameter
 * at most one supportAttribute for each relationship should be defined as identifier="true"
 */
public class SupportAttributeDefinition extends PrimitiveAttributeDefinition {
    private static final long serialVersionUID = -1719022365280776405L;

    protected boolean identifier;

    public SupportAttributeDefinition() {
    }

    public boolean isIdentifier() {
        return identifier;
    }

    /**
     * identifier when true, only the field marked as an identifier will be passed in as a lookup parameter
     * at most one supportAttribute for each relationship should be defined as identifier="true"
     */
    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    /**
     * Directly validate simple fields.
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getSourceName())) {
            throw new AttributeValidationException("unable to find attribute '" + getSourceName() + "' in relationship class '" + rootBusinessObjectClass + "' (" + "" + ")");
        }
        if (!DataDictionary.isPropertyOf(otherBusinessObjectClass, getTargetName())
            && !ExternalizableBusinessObjectUtils.isExternalizableBusinessObjectInterface(otherBusinessObjectClass)) {
            throw new AttributeValidationException("unable to find attribute '" + getTargetName() + "' in related class '" + otherBusinessObjectClass.getName() + "' (" + "" + ")");
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SupportAttributeDefinition (" + getSourceName() + "," + getTargetName() + "," + identifier + ")";
    }

}

