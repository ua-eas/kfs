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
package org.kuali.kfs.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.CssConstants;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.container.Container;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.UifConstants.Orientation;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout manager that organizes components in a single row (horizontal) or
 * column (vertical)
 * 
 * <p>
 * Although a table based template could be used, setup is done to also support
 * a CSS based template. The items in the <code>Container</code> instance are
 * rendered sequentially wrapping each one with a span element. The padding
 * property can be configured to space the elements as needed. To achieve a
 * vertical orientation, the span style is set to block. Additional styling can
 * be set for the items by using the itemSpanStyle property.
 * </p>
 * 
 * 
 */
public class BoxLayoutManager extends LayoutManagerBase {
	private static final long serialVersionUID = 4467342272983290044L;

	private Orientation orientation;
	private String padding;

	private String itemStyle;
    private List<String> itemStyleClasses;

	private boolean layoutFieldErrors;

	public BoxLayoutManager() {
		super();

		orientation = Orientation.HORIZONTAL;
        itemStyleClasses = new ArrayList<String>();
	}

	/**
	 * The following initialization is performed:
	 * 
	 * <ul>
	 * <li>Set the itemSpanStyle</li>
	 * </ul>
	 * 
	 * @see ComponentBase#performInitialization(View,
     * java.lang.Object, Container)
	 */
	@Override
	public void performInitialization(View view, Object model, Container container) {
		super.performInitialization(view, model, container);

		if(StringUtils.isBlank(itemStyle)){
			itemStyle = "";
		}
		
		if(StringUtils.isNotEmpty(padding)) {
			if (orientation.equals(Orientation.VERTICAL)) {
				// set item to block which will cause a line break and margin
				// bottom for padding
				itemStyle += CssConstants.getCssStyle(CssConstants.Padding.PADDING_BOTTOM, padding);
			}
			else {
				// set margin right for padding
				itemStyle += CssConstants.getCssStyle(CssConstants.Padding.PADDING_RIGHT, padding);
			}
		}

        //classes to identify this layout in jQuery and to clear the float correctly in all browsers
        this.addStyleClass("fieldLine");
        this.addStyleClass("clearfix");
        
        for (Component c : container.getItems()) {
            if (c != null) {
                if (orientation.equals(Orientation.HORIZONTAL)) {
                    // in a horizontal box layout errors are placed in a div next to all fields,
                    // set the errorsField to know that we are using an alternate container for them
                    if (c instanceof InputField) {
                        ((InputField) c).getErrorsField().setAlternateContainer(true);
                        layoutFieldErrors = true;
                    }
                }

                if (container.isFieldContainer()) {
                    if (c instanceof InputField) {
                        ((InputField) c).getErrorsField().setAlternateContainer(true);
                        layoutFieldErrors = true;
                    }
                }
            }
        }
    }

	/**
	 * @see LayoutManagerBase#performFinalize(View,
	 *      java.lang.Object, Container)
	 */
	@Override
	public void performFinalize(View view, Object model, Container container) {
		super.performFinalize(view, model, container);
	}

	/**
	 * Indicates whether the components should be rendered in a horizontal or
	 * vertical column
	 * 
	 * @return Orientation orientation configured for layout
	 */
	public Orientation getOrientation() {
		return this.orientation;
	}

	/**
	 * Setter for the orientation for layout
	 * 
	 * @param orientation
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	/**
	 * Amount of separation between each item
	 * 
	 * <p>
	 * For horizontal orientation, this will be the right padding for each item.
	 * For vertical, it will be the bottom padding for each item. The value can
	 * be a fixed length (like px) or percentage
	 * </p>
	 * 
	 * @return
	 */
	public String getPadding() {
		return this.padding;
	}

	/**
	 * Setter for the item padding
	 * 
	 * @param padding
	 */
	public void setPadding(String padding) {
		this.padding = padding;
	}

	/**
	 * Used by the render to set the style on the span element that wraps the
	 * item. By using a wrapping span the items can be aligned based on the
	 * orientation and given the correct padding
	 * 
	 * @return String css style string
	 */
	public String getItemStyle() {
		return this.itemStyle;
	}

	/**
	 * Setter for the span style
	 * 
	 * @param itemStyle
	 */
	public void setItemStyle(String itemStyle) {
		this.itemStyle = itemStyle;
	}

    /**
     * List of style classes that should be applied to each span that wraps the item in the layout
     *
     * @return List<String>
     */
    public List<String> getItemStyleClasses() {
        return itemStyleClasses;
    }

    /**
     * Setter for the list of style classes that should apply to each item span
     *
     * @param itemStyleClasses
     */
    public void setItemStyleClasses(List<String> itemStyleClasses) {
        this.itemStyleClasses = itemStyleClasses;
    }

    /**
     * Builds the HTML class attribute string by combining the item styleClasses list
     * with a space delimiter
     *
     * @return String class attribute string
     */
    public String getItemStyleClassesAsString() {
        if (itemStyleClasses != null) {
            return StringUtils.join(itemStyleClasses, " ");
        }

        return "";
    }

	/**
	 * @return the layoutFieldErrors
	 */
	public boolean isLayoutFieldErrors() {
		return this.layoutFieldErrors;
	}

	/**
	 * @param layoutFieldErrors the layoutFieldErrors to set
	 */
	public void setLayoutFieldErrors(boolean layoutFieldErrors) {
		this.layoutFieldErrors = layoutFieldErrors;
	}

}
