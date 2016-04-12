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
package org.kuali.kfs.krad.bo;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object Attribute Entry Business Object
 * 
 * 
 */
public class BusinessObjectAttributeEntry extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 8136616401437024033L;
    private String dictionaryBusinessObjectName;
    private String attributeName;
    private String attributeLabel;
    private String attributeShortLabel;
    private String attributeMaxLength;
    private String attributeValidatingExpression;
    private String attributeControlType;
    private String attributeSummary;
    private String attributeDescription;
    private String attributeFormatterClassName;


    /**
     * @return Returns the attributeControlType.
     */
    public String getAttributeControlType() {
        return attributeControlType;
    }

    /**
     * @param attributeControlType The attributeControlType to set.
     */
    public void setAttributeControlType(String attributeControlType) {
        this.attributeControlType = attributeControlType;
    }

    /**
     * @return Returns the attributeDescription.
     */
    public String getAttributeDescription() {
        return attributeDescription;
    }

    /**
     * @param attributeDescription The attributeDescription to set.
     */
    public void setAttributeDescription(String attributeDescription) {
        this.attributeDescription = attributeDescription;
    }

    /**
     * @return Returns the attributeFormatterClassName.
     */
    public String getAttributeFormatterClassName() {
        return attributeFormatterClassName;
    }

    /**
     * @param attributeFormatterClassName The attributeFormatterClassName to set.
     */
    public void setAttributeFormatterClassName(String attributeFormatterClassName) {
        this.attributeFormatterClassName = attributeFormatterClassName;
    }

    /**
     * @return Returns the attributeLabel.
     */
    public String getAttributeLabel() {
        return attributeLabel;
    }

    /**
     * @param attributeLabel The attributeLabel to set.
     */
    public void setAttributeLabel(String attributeLabel) {
        this.attributeLabel = attributeLabel;
    }

    /**
     * @return Returns the attributeMaxLength.
     */
    public String getAttributeMaxLength() {
        return attributeMaxLength;
    }

    /**
     * @param attributeMaxLength The attributeMaxLength to set.
     */
    public void setAttributeMaxLength(String attributeMaxLength) {
        this.attributeMaxLength = attributeMaxLength;
    }

    /**
     * @return Returns the attributeName.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param attributeName The attributeName to set.
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return Returns the attributeShortLabel.
     */
    public String getAttributeShortLabel() {
        return attributeShortLabel;
    }

    /**
     * @param attributeShortLabel The attributeShortLabel to set.
     */
    public void setAttributeShortLabel(String attributeShortLabel) {
        this.attributeShortLabel = attributeShortLabel;
    }

    /**
     * @return Returns the attributeSummary.
     */
    public String getAttributeSummary() {
        return attributeSummary;
    }

    /**
     * @param attributeSummary The attributeSummary to set.
     */
    public void setAttributeSummary(String attributeSummary) {
        this.attributeSummary = attributeSummary;
    }

    /**
     * @return Returns the attributeValidatingExpression.
     */
    public String getAttributeValidatingExpression() {
        return attributeValidatingExpression;
    }

    /**
     * @param attributeValidatingExpression The attributeValidatingExpression to set.
     */
    public void setAttributeValidatingExpression(String attributeValidatingExpression) {
        this.attributeValidatingExpression = attributeValidatingExpression;
    }

    /**
     * @return Returns the dataObjectClass.
     */
    public String getDictionaryBusinessObjectName() {
        return dictionaryBusinessObjectName;
    }

    /**
     * @param businessObjectClass The dataObjectClass to set.
     */
    public void setDictionaryBusinessObjectName(String businessObjectClass) {
        this.dictionaryBusinessObjectName = businessObjectClass;
    }
}
