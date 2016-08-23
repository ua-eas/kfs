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
package org.kuali.kfs.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.view.View;

/**
 * Represents a HTML TextArea control. Generally used for values that are very
 * large (such as a description)
 *
 *
 */
public class TextAreaControl extends ControlBase {
    private static final long serialVersionUID = -4664558047325456844L;

    private int rows;
    private int cols;
    private Integer maxLength;
    private Integer minLength;

    private boolean textExpand;
    private String watermarkText = StringUtils.EMPTY;

    public TextAreaControl() {
        super();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Defaults maxLength, minLength (if not set) to maxLength of parent field</li>
     * </ul>
     *
     * @see ComponentBase#performFinalize(View,
     *      java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (parent instanceof InputField) {
            InputField field = (InputField) parent;
            if (getMaxLength() == null) {
                setMaxLength(field.getMaxLength());
            }

            if (getMinLength() == null) {
                setMinLength(field.getMinLength());
            }
        }
    }

    /**
     * Number of rows the control should span (horizontal length)
     *
     * @return int number of rows
     */
    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Number of columns the control should span (vertical length)
     *
     * @return int number of columns
     */
    public int getCols() {
        return this.cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    /**
     * Maximum number of characters that can be inputted
     *
     * <p>If not set on control, max length of field will be used</p>
     *
     * @return int max number of characters
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Setter for the max number of input characters
     *
     * @param maxLength
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Minimum number of characters that can be inputted
     *
     * <p>If not set on control, min length of field will be used</p>
     *
     * @return int max number of characters
     */
    public Integer getMinLength() {
        return minLength;
    }

    /**
     * Setter for the min number of input characters
     *
     * @param minLength
     */
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * @return the watermarkText
     */
    public String getWatermarkText() {
        return this.watermarkText;
    }

    /**
     * @param watermarkText the watermarkText to set
     */
    public void setWatermarkText(String watermarkText) {
        //to avoid users from putting in the same value as the watermark adding some spaces here
        //see watermark troubleshooting for more info
        if (StringUtils.isNotEmpty(watermarkText)) {
            watermarkText = watermarkText + "   ";
        }
        this.watermarkText = watermarkText;
    }

    /**
     * If set to true, this control will have a button which can be clicked to expand the text area through
     * a popup window so the user has more space to type and see the data they are entering in this text field
     *
     * @return the textExpand
     */
    public boolean isTextExpand() {
        return this.textExpand;
    }

    /**
     * Setter for the text expand flag
     *
     * @param textExpand the textExpand to set
     */
    public void setTextExpand(boolean textExpand) {
        this.textExpand = textExpand;
    }


}
