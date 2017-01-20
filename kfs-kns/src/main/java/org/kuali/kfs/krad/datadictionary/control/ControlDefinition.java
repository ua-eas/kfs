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
package org.kuali.kfs.krad.datadictionary.control;

import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinition;

/**
 * ControlDefinition
 */
@Deprecated
public interface ControlDefinition extends DataDictionaryDefinition {

    public boolean isDatePicker();

    public void setDatePicker(boolean datePicker);

    public boolean isExpandedTextArea();

    public void setExpandedTextArea(boolean eTextArea);

    /**
     * @return true if this ControlDefinition instance represents an HTML checkbox control
     */
    public boolean isCheckbox();

    /**
     * @return true if this ControlDefinition instance represents an HTML hidden control
     */
    public boolean isHidden();

    /**
     * @return true if this ControlDefinition instance represents an HTML radiobutton control
     */
    public boolean isRadio();

    /**
     * @return true if this ControlDefinition instance represents an HTML select control
     */
    public boolean isSelect();

    /**
     * @return true if this ControlDefinition instance represents an HTML select control
     */
    public boolean isMultiselect();

    /**
     * @return true if this ControlDefinition instance represents an HTML text control
     */
    public boolean isText();

    /**
     * @return true if this ControlDefinition instance represents an HTML textarea control
     */
    public boolean isTextarea();

    /**
     * @return any Script associated with this control
     */
    public String getScript();

    /**
     * @return true if the ControlDefinition instance represents a currency control
     */
    public boolean isCurrency();

    /**
     * @return true if the ControlDefinition instance represents a kualiUser control
     */
    public boolean isKualiUser();

    /**
     * @return true if the ControlDefinition instance represents a workflow workgroup control
     */
    public boolean isWorkflowWorkgroup();

    /**
     * @return true if this ControlDefinition instance represents an HTML File control
     */
    public boolean isFile();

    /**
     * @return true if the ControlDefinition instance represents a lookupHidden control
     */
    public boolean isLookupHidden();

    /**
     * @return true if the ControlDefinition instance represents a lookupReadonly control
     */
    public boolean isLookupReadonly();

    /**
     * @return true if the ControlDefinition instance represents a button control
     */
    public boolean isButton();

    /**
     * @return true if the ControlDefinition instance represents a link control
     */
    public boolean isLink();

    /**
     * @return true if the ControlDefinition instance represents a ranged (will render from and to fields) date control
     */
    public boolean isRanged();

    /**
     * Sets the Class used to retrieve the complete range of values for radiobutton and select controls.
     *
     * @param valuesFinderClass
     */
    public void setValuesFinderClass(String valuesFinderClass);

    /**
     * Sets the BO Class used for the KeyLabelBusinessObjectValueFinder to retrieve the complete range of values for radiobutton and select controls.
     *
     * @param businessObjectClass
     */
    public void setBusinessObjectClass(String businessObjectClass);


    /**
     * Sets the keyAttribute used for building radiobutton and select controls.
     *
     * @param keyAttribute
     */
    public void setKeyAttribute(String keyAttribute);

    /**
     * Sets the labelAttribute used for building radiobutton and select controls.
     *
     * @param labelAttribute
     */
    public void setLabelAttribute(String labelAttribute);

    public void setIncludeBlankRow(Boolean includeBlankRow);

    /**
     * @param includeKeyInLabel whether to include the key with the label to be displayed or not.
     */
    public void setIncludeKeyInLabel(Boolean includeKeyInLabel);

    /**
     * Sets the Script
     *
     * @param script
     */
    public void setScript(String script);

    /**
     * @return Class used to retrieve the complete range of values for radiobutton and select controls.
     */
    public String getValuesFinderClass();

    /**
     * @return BO Class used for the KeyLabelBusinessObjectValueFinder to retrieve the complete range of values for radiobutton and select controls.
     */
    public String getBusinessObjectClass();

    /**
     * @return the keyAttribute used for radiobutton and select controls.
     */
    public String getKeyAttribute();

    /**
     * @return the labelAttribute used for radiobutton and select controls.
     */
    public String getLabelAttribute();

    public Boolean getIncludeBlankRow();

    /**
     * Gets the flag that indicates if the labels the ValuesFinder class returns should include the key.
     *
     * @param includeKeyInLabel
     */
    public Boolean getIncludeKeyInLabel();

    /**
     * Sets the size parameter for text controls.
     *
     * @param size
     */
    public void setSize(Integer size);

    /**
     * @return size parameters for text controls
     */
    public Integer getSize();

    /**
     * Sets the rows parameter for textarea controls.
     *
     * @param rows
     */
    public void setRows(Integer rows);

    /**
     * @return rows parameters for textarea controls
     */
    public Integer getRows();

    /**
     * Sets the cols parameter for textarea controls.
     *
     * @param cols
     */
    public void setCols(Integer cols);

    /**
     * @return cols parameter for textarea controls.
     */
    public Integer getCols();

}
