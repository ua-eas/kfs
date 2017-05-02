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
package org.kuali.kfs.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifConstants.Position;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.view.View;

import java.util.List;

/**
 * Contains a label for another <code>Field</code> instance
 * <p>
 * <p>
 * The <code>LabelField</code> exists so that the label can be placed separate
 * from the component in a layout manager such as the
 * <code>GridLayoutManager</code>. It addition it can be used to style the label
 * (from the inherited styleClass and style properties)
 * </p>
 */
public class LabelField extends FieldBase {
    private static final long serialVersionUID = -6491546893195180114L;

    private String labelText;
    private String labelForComponentId;

    private boolean renderColon;

    private Position requiredMessagePlacement;
    private MessageField requiredMessageField;

    public LabelField() {
        renderColon = true;

        requiredMessagePlacement = Position.LEFT;
    }

    /**
     * The following finalization is performed:
     * <p>
     * <ul>
     * <li>If label text is blank, set render to false for field</li>
     *
     * @see ComponentBase#performFinalize(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (StringUtils.isBlank(getLabelText())) {
            setRender(false);
        }
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(requiredMessageField);

        return components;
    }

    /**
     * Indicates the id for the component the label applies to
     * <p>
     * Used for setting the labelFor attribute of the corresponding HTML
     * element. Note this gets set automatically by the framework during the
     * initialize phase
     * </p>
     *
     * @return String component id
     */
    public String getLabelForComponentId() {
        return this.labelForComponentId;
    }

    /**
     * Setter for the component id the label applies to
     *
     * @param labelForComponentId
     */
    public void setLabelForComponentId(String labelForComponentId) {
        this.labelForComponentId = labelForComponentId;
    }

    /**
     * Text that will display as the label
     *
     * @return String label text
     */
    public String getLabelText() {
        return this.labelText;
    }

    /**
     * Setter for the label text
     *
     * @param labelText
     */
    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    /**
     * Indicates whether a colon should be rendered after the label text,
     * generally used when the label appears to the left of the field's control
     * or value
     *
     * @return boolean true if a colon should be rendered, false if it should
     * not be
     */
    public boolean isRenderColon() {
        return this.renderColon;
    }

    /**
     * Setter for the render colon indicator
     *
     * @param renderColon
     */
    public void setRenderColon(boolean renderColon) {
        this.renderColon = renderColon;
    }

    /**
     * <code>MessageField</code> instance that will display a required indicator
     * <p>
     * <p>
     * To indicate a field must have a value (required input) the required
     * message field can be set to display an indicator or message along with
     * the label. The message field also dictates the styling of the required
     * message
     * </p>
     *
     * @return MessageField instance
     */
    public MessageField getRequiredMessageField() {
        return this.requiredMessageField;
    }

    /**
     * Setter for the required message field
     *
     * @param requiredMessageField
     */
    public void setRequiredMessageField(MessageField requiredMessageField) {
        this.requiredMessageField = requiredMessageField;
    }

    /**
     * Indicates where the required message field should be placed in relation
     * to the label field, valid options are 'LEFT' and 'RIGHT'
     *
     * @return Position the requiredMessage placement
     */
    public Position getRequiredMessagePlacement() {
        return this.requiredMessagePlacement;
    }

    /**
     * Setter for the required message field placement
     *
     * @param requiredMessagePlacement
     */
    public void setRequiredMessagePlacement(Position requiredMessagePlacement) {
        this.requiredMessagePlacement = requiredMessagePlacement;
    }

}
