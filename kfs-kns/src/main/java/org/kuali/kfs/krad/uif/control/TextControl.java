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
package org.kuali.kfs.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.widget.DatePicker;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.component.Component;

import java.util.List;

/**
 * Represents a HTML Text control, generally rendered as a input field of type
 * 'text'. This can display and receive a single value
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TextControl extends ControlBase implements SizedControl {
	private static final long serialVersionUID = -8267606288443759880L;

	private int size;
    private Integer maxLength;
    private Integer minLength;

	private DatePicker datePicker;
	private String watermarkText = StringUtils.EMPTY;
	private boolean textExpand;
	
	public TextControl() {
		super();
	}

	/**
	 * @see ComponentBase#getComponentsForLifecycle()
	 */
	@Override
	public List<Component> getComponentsForLifecycle() {
		List<Component> components = super.getComponentsForLifecycle();

		components.add(datePicker);

		return components;
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
	 * @see SizedControl#getSize()
	 */
	public int getSize() {
		return this.size;
	}

    /**
     * @see SizedControl#setSize(int)
     */
    public void setSize(int size) {
        this.size = size;
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
	 * Renders a calendar that can be used to select a date value for the text
	 * control. The <code>Calendar</code> instance contains configuration such
	 * as the date format string
	 * 
	 * @return Calendar
	 */
	public DatePicker getDatePicker() {
		return this.datePicker;
	}

	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

	/**
     * Gets the watermark text for this TextControl.
     * <p>
     * A watermark typically appears as light gray text within the text input element whenever the
     * element is empty and does not have focus. This provides a hint to the user as to what the input
     * is used for, or the type of input that is required.
     * </p>
	 * @return the watermarkText
	 */
	public String getWatermarkText() {
		return this.watermarkText;
	}

	/**
     * Sets the watermark text for this TextControl
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
     * a popup window so the user has more space to type and see the data they are entering in this text field.
     *
     * @return the textExpand
     */
    public boolean isTextExpand() {
        return this.textExpand;
    }

    /**
     * Sets whether this control will have a button to expand the text area through a popup window.
     * @param textExpand the textExpand to set
     */
    public void setTextExpand(boolean textExpand) {
        this.textExpand = textExpand;
    }
	
	
}
