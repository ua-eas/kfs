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
package org.kuali.kfs.krad.uif.field;

import org.kuali.kfs.krad.uif.component.Component;

/**
 * Component that contains one or more user interface elements and can be placed
 * into a <code>Container</code>
 *
 * <p>
 * Provides a wrapper for various user interface elements so they can be treated
 * uniformly by a container and rendered using a <code>LayoutManager</code>.
 * Implementations exist for various types of elements and properties to
 * configure that element.
 * </p>
 *
 *
 */
public interface Field extends Component {

	/**
	 * Label text for the field
	 *
	 * <p>
	 * The label is generally used to identify the field in the user interface
	 * </p>
	 *
	 * @return String label text
	 */
	public String getLabel();

	/**
	 * Setter for the field's label text
	 *
	 * @param label
	 */
	public void setLabel(String label);

	/**
	 * Short label for the field
	 *
	 * <p>
	 * For areas of the user interface that have limited area (such as table
	 * headers), the short label can be used to identify the field
	 * </p>
	 *
	 * @return String short label
	 */
	public String getShortLabel();

	/**
	 * Setter for the field's short label text
	 *
	 * @param shortLabel
	 */
	public void setShortLabel(String shortLabel);

	/**
	 * <code>LabelField</code> instance for the field
	 *
	 * <p>
	 * The label field contains the labeling text for the field in addition to
	 * configuration for rendering in the user interface (such as the styling
	 * for the label area)
	 * </p>
	 *
	 * @return LabelField instance
	 */
	public LabelField getLabelField();

	/**
	 * Setter for the field's label field
	 *
	 * @param labelField
	 */
	public void setLabelField(LabelField labelField);

	/**
	 * Indicates whether the contained <code>LabelField</code> has been rendered
	 * as part of another field and thus should not be rendered with the
	 * attribute
	 *
	 * @return boolean true if the label field has been rendered, false if it
	 *         should be rendered with the attribute
	 */
	public boolean isLabelFieldRendered();

	/**
	 * Setter for the label field rendered indicator
	 *
	 * @param labelFieldRendered
	 */
	public void setLabelFieldRendered(boolean labelFieldRendered);

    /**
     * Field Security object that indicates what authorization (permissions) exist for the field
     *
     * @return FieldSecurity instance
     */
    public FieldSecurity getFieldSecurity();

}
