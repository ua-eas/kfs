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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.kuali.rice.core.api.data.DataType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * A simple constraint stores 'basic' constraints for a field.  This constraint is meant to be used as a
 * constraint for WhenConstraints in CaseConstraint, and is also used internally in InputField.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleConstraint extends BaseConstraint implements ExistenceConstraint, RangeConstraint, LengthConstraint {

    @XmlElement
    private Boolean required;

    @XmlElement
    private Integer maxLength;

    @XmlElement
    private Integer minLength;

    @XmlElement
    protected String exclusiveMin;

    @XmlElement
    protected String inclusiveMax;

    //Don't know if we will support min/max occurs at this time
    @XmlElement
    private Integer minOccurs;

    @XmlElement
    private Integer maxOccurs;

    private DataType dataType;

    /**
     * If true the field is required
     *
     * @return the required
     */
    public Boolean getRequired() {
        return this.required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * The maximum amount of characters this field's value can be
     *
     * @return the maxLength
     */
    public Integer getMaxLength() {
        return this.maxLength;
    }

    /**
     * @param maxLength the maxLength to set
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * The minimum amount of characters this field's value has to be
     *
     * @return the minLength
     */
    public Integer getMinLength() {
        return this.minLength;
    }

    /**
     * @param minLength the minLength to set
     */
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * Exclusive minimum value for this field
     *
     * @return the exclusiveMin
     */
    public String getExclusiveMin() {
        return this.exclusiveMin;
    }

    /**
     * @param exclusiveMin the exclusiveMin to set
     */
    public void setExclusiveMin(String exclusiveMin) {
        this.exclusiveMin = exclusiveMin;
    }

    /**
     * Inclusive max value for this field
     *
     * @return the inclusiveMax
     */
    public String getInclusiveMax() {
        return this.inclusiveMax;
    }

    /**
     * @param inclusiveMax the inclusiveMax to set
     */
    public void setInclusiveMax(String inclusiveMax) {
        this.inclusiveMax = inclusiveMax;
    }

    /**
     * The minimum amount of items in this fields list of values - not yet used/do not use
     *
     * @return the minOccurs
     */
    public Integer getMinOccurs() {
        return this.minOccurs;
    }

    /**
     * @param minOccurs the minOccurs to set
     */
    public void setMinOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    /**
     * The maximum amount of items in this field's list of values - not yet used/do not use
     *
     * @return the maxOccurs
     */
    public Integer getMaxOccurs() {
        return this.maxOccurs;
    }

    /**
     * @param maxOccurs the maxOccurs to set
     */
    public void setMaxOccurs(Integer maxOccurs) {
        this.maxOccurs = maxOccurs;
    }


    /**
     * @see ExistenceConstraint#isRequired()
     */
    @Override
    public Boolean isRequired() {
        return getRequired();
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}

