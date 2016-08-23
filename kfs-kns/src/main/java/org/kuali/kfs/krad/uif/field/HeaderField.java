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
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.view.View;

/**
 * Field that contains a header element and optionally a <code>Group</code> to
 * present along with the header text
 * <p>
 * <p>
 * Generally the group is used to display content to the right of the header,
 * such as links for the group or other information
 * </p>
 */
public class HeaderField extends FieldGroup {
    private static final long serialVersionUID = -6950408292923393244L;

    private String headerText;
    private String headerLevel;
    private String headerStyleClasses;
    private String headerStyle;
    private String headerDivStyleClasses;
    private String headerDivStyle;

    public HeaderField() {
        super();
    }

    /**
     * The following finalization is performed:
     * <p>
     * <ul>
     * <li>Set render on group to false if no items are configured</li>
     * </ul>
     *
     * @see ComponentBase#performFinalize(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // don't render header group if no items were configured
        if ((getGroup() != null) && (getGroup().getItems().isEmpty())) {
            getGroup().setRender(false);
        }
    }

    /**
     * Text that should be displayed on the header
     *
     * @return String header text
     */
    public String getHeaderText() {
        return this.headerText;
    }

    /**
     * Setter for the header text
     *
     * @param headerText
     */
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    /**
     * HTML header level (h1 ... h6) that should be applied to the header text
     *
     * @return String header level
     */
    public String getHeaderLevel() {
        return this.headerLevel;
    }

    /**
     * Setter for the header level
     *
     * @param headerLevel
     */
    public void setHeaderLevel(String headerLevel) {
        this.headerLevel = headerLevel;
    }

    /**
     * Style class that should be applied to the header text (h tag)
     * <p>
     * <p>
     * Note the style class given here applies to only the header text. The
     * style class property inherited from the <code>Component</code> interface
     * can be used to set the class for the whole field div (which could
     * include a nested <code>Group</code>)
     * </p>
     *
     * @return String style class
     * @see org.kuali.rice.krad.uif.Component.getStyleClasses()
     */
    public String getHeaderStyleClasses() {
        return this.headerStyleClasses;
    }

    /**
     * Setter for the header style class
     *
     * @param headerStyleClasses
     */
    public void setHeaderStyleClasses(String headerStyleClasses) {
        this.headerStyleClasses = headerStyleClasses;
    }

    /**
     * Style that should be applied to the header text
     * <p>
     * <p>
     * Note the style given here applies to only the header text. The style
     * property inherited from the <code>Component</code> interface can be used
     * to set the style for the whole field div (which could include a nested
     * <code>Group</code>)
     * </p>
     *
     * @return String header style
     * @see org.kuali.rice.krad.uif.Component.getStyle()
     */
    public String getHeaderStyle() {
        return this.headerStyle;
    }

    /**
     * Setter for the header style
     *
     * @param headerStyle
     */
    public void setHeaderStyle(String headerStyle) {
        this.headerStyle = headerStyle;
    }

    /**
     * Style class that should be applied to the header div
     * <p>
     * <p>
     * Note the style class given here applies to the div surrounding the header tag only
     * </p>
     *
     * @return String style class
     * @see org.kuali.rice.krad.uif.Component.getStyleClasses()
     */
    public String getHeaderDivStyleClasses() {
        return headerDivStyleClasses;
    }

    /**
     * Setter for the header div class
     *
     * @param headerStyleClasses
     */
    public void setHeaderDivStyleClasses(String headerDivStyleClasses) {
        this.headerDivStyleClasses = headerDivStyleClasses;
    }

    /**
     * Style that should be applied to the header div
     * <p>
     * <p>
     * Note the style given here applies to the div surrounding the header tag only
     * </p>
     *
     * @return String header style
     * @see org.kuali.rice.krad.uif.Component.getStyle()
     */
    public String getHeaderDivStyle() {
        return headerDivStyle;
    }

    /**
     * Setter for the header div
     *
     * @param headerStyle
     */
    public void setHeaderDivStyle(String headerDivStyle) {
        this.headerDivStyle = headerDivStyle;
    }
}
