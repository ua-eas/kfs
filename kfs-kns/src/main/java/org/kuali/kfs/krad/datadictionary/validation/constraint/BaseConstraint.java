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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;


/**
 * A class that implements the required accessor for label keys. This provides a convenient base class
 * from which other constraints can be derived.
 * <p>
 * This class is a direct copy of one that was in Kuali Student.
 *
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseConstraint implements Constraint {
    @XmlElement
    protected String labelKey;
    @XmlElement
    protected Boolean applyClientSide;

    List<String> validationMessageParams;

    public BaseConstraint() {
        applyClientSide = Boolean.valueOf(true);
    }

    /**
     * LabelKey should be a single word key.  This key is used to find a message to use for this
     * constraint from available messages.  The key is also used for defining/retrieving validation method
     * names when applicable for ValidCharactersContraints.
     * <p>
     * If a comma separated list of keys is used, a message will be generated that is a comma separated list of
     * the messages retrieved for each key.
     *
     * @return
     * @see ValidCharactersConstraint
     */
    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    /**
     * If this is true, the constraint should be applied on the client side when the user interacts with
     * a field - if this constraint can be interpreted for client side use. Default is true.
     *
     * @return the applyClientSide
     */
    public Boolean getApplyClientSide() {
        return this.applyClientSide;
    }

    /**
     * @param applyClientSide the applyClientSide to set
     */
    public void setApplyClientSide(Boolean applyClientSide) {
        this.applyClientSide = applyClientSide;
    }


    /**
     * Parameters to be used in the string retrieved by this constraint's labelKey, ordered by number of
     * the param
     *
     * @return the validationMessageParams
     */
    public List<String> getValidationMessageParams() {
        return this.validationMessageParams;
    }

    /**
     * Parameters to be used in the string retrieved by this constraint's labelKey, ordered by number of
     * the param
     *
     * @return the validationMessageParams
     */
    public String[] getValidationMessageParamsArray() {
        if (this.getValidationMessageParams() != null) {
            return this.getValidationMessageParams().toArray(new String[this.getValidationMessageParams().size()]);
        } else {
            return null;
        }

    }

    /**
     * @param validationMessageParams the validationMessageParams to set
     */
    public void setValidationMessageParams(List<String> validationMessageParams) {
        this.validationMessageParams = validationMessageParams;
    }


}
