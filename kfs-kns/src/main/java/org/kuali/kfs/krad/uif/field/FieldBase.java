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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.kfs.krad.uif.UifConstants.Position;
import org.kuali.kfs.krad.uif.component.ComponentSecurity;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.util.ComponentFactory;

import java.util.List;

/**
 * Base class for <code>Field</code> implementations
 *
 * <p>
 * Sets the component type name so that all field templates have a fixed
 * contract
 * </p>
 *
 * <p>
 * Holds a nested <code>LabelField</code> with configuration for rendering the
 * label and configuration on label placement.
 * </p>
 *
 *
 */
public class FieldBase extends ComponentBase implements Field {
    private static final long serialVersionUID = -5888414844802862760L;

    private String shortLabel;
    private LabelField labelField;

    private Position labelPlacement;

    private boolean labelFieldRendered;

    public FieldBase() {
        labelFieldRendered = false;
        labelPlacement = Position.LEFT;
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * </ul>
     *
     * @see ComponentBase#performInitialization(View, java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Set the labelForComponentId to this component id</li>
     * <li>Set the label text on the label field from the field's label property
     * </li>
     * <li>Set the render property on the label's required message field if this
     * field is marked as required</li>
     * <li>If label placement is right, set render colon to false</li>
     * </ul>
     *
     * @see ComponentBase#performFinalize(View,
     *      java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (labelField != null) {
            labelField.setLabelForComponentId(this.getId());

            if ((getRequired() != null) && getRequired().booleanValue()) {
                labelField.getRequiredMessageField().setRender(true);
            } else {
                setRequired(new Boolean(false));
                labelField.getRequiredMessageField().setRender(true);
                String prefixStyle = "";
                if (StringUtils.isNotBlank(labelField.getRequiredMessageField().getStyle())) {
                    prefixStyle = labelField.getRequiredMessageField().getStyle();
                }
                labelField.getRequiredMessageField().setStyle(prefixStyle + ";" + "display: none;");
            }

            if (labelPlacement.equals(Position.RIGHT)) {
                labelField.setRenderColon(false);
            }

            if (labelPlacement.equals(Position.TOP) || labelPlacement.equals(Position.BOTTOM)){
                labelField.addStyleClass("uif-labelBlock");
            }
        }
    }

    /**
     * @see Component#getComponentTypeName()
     */
    @Override
    public final String getComponentTypeName() {
        return "field";
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(labelField);

        return components;
    }

    /**
     * @see Field#getLabel()
     */
    public String getLabel() {
        if (labelField != null) {
            return labelField.getLabelText();
        }

        return null;
    }

    /**
     * @see Field#setLabel(java.lang.String)
     */
    public void setLabel(String label) {
        if (StringUtils.isNotBlank(label) && labelField == null) {
            labelField = ComponentFactory.getLabelField();
        }

        if (labelField != null) {
            labelField.setLabelText(label);
        }
    }

    /**
     * @see Field#getShortLabel()
     */
    public String getShortLabel() {
        return this.shortLabel;
    }

    /**
     * @see Field#setShortLabel(java.lang.String)
     */
    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    /**
     * Sets whether the label should be displayed
     *
     * <p>
     * Convenience method for configuration that sets the render indicator on
     * the fields <code>LabelField</code> instance
     * </p>
     *
     * @param showLabel boolean true if label should be displayed, false if the label
     * should not be displayed
     */
    public void setShowLabel(boolean showLabel) {
        if (labelField != null) {
            labelField.setRender(showLabel);
        }
    }

    /**
     * @see Field#getLabelField()
     */
    public LabelField getLabelField() {
        return this.labelField;
    }

    /**
     * @see Field#setLabelField(LabelField)
     */
    public void setLabelField(LabelField labelField) {
        this.labelField = labelField;
    }

    /**
     * Indicates where the label is placed in relation to the field (valid options are
     * LEFT, RIGHT, BOTTOM, and TOP
     *
     * @return Position position of label
     */
    public Position getLabelPlacement() {
        return this.labelPlacement;
    }

    /**
     * Setter for the label's position in relation to the field (control if editable)
     *
     * @param labelPlacement
     */
    public void setLabelPlacement(Position labelPlacement) {
        this.labelPlacement = labelPlacement;
    }

    /**
     * @see Field#isLabelFieldRendered()
     */
    public boolean isLabelFieldRendered() {
        return this.labelFieldRendered;
    }

    /**
     * @see Field#setLabelFieldRendered(boolean)
     */
    public void setLabelFieldRendered(boolean labelFieldRendered) {
        this.labelFieldRendered = labelFieldRendered;
    }

    /**
     * @see Field#getFieldSecurity()
     */
    public FieldSecurity getFieldSecurity() {
        return (FieldSecurity) super.getComponentSecurity();
    }

    /**
     * Override to assert a {@link FieldSecurity} instance is set
     *
     * @param componentSecurity - instance of FieldSecurity
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        if (!(componentSecurity instanceof FieldSecurity)) {
            throw new RiceRuntimeException("Component security for Field should be instance of FieldSecurity");
        }

        super.setComponentSecurity(componentSecurity);
    }

    /**
     * @see ComponentBase#getComponentSecurityClass()
     */
    @Override
    protected Class<? extends ComponentSecurity> getComponentSecurityClass() {
        return FieldSecurity.class;
    }
}
