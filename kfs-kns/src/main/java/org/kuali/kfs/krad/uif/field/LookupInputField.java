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
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.control.CheckboxControl;
import org.kuali.kfs.krad.uif.control.Control;
import org.kuali.kfs.krad.uif.control.MultiValueControl;
import org.kuali.kfs.krad.uif.control.RadioGroupControl;
import org.kuali.kfs.krad.uif.control.TextAreaControl;
import org.kuali.kfs.krad.uif.util.ComponentFactory;
import org.kuali.kfs.krad.uif.util.ComponentUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom <code>InputField</code> for search fields within a lookup view
 */
public class LookupInputField extends InputField {
    private static final long serialVersionUID = -8294275596836322699L;

    private boolean treatWildcardsAndOperatorsAsLiteral;
    private boolean addAllOption;

    public LookupInputField() {
        super();

        treatWildcardsAndOperatorsAsLiteral = false;
        addAllOption = false;
    }

    /**
     * The following actions are performed:
     * <p>
     * <ul>
     * <li>Add all option if enabled and control is multi-value</li>
     * </ul>
     *
     * @see ComponentBase#performFinalize(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // add all option
        if (addAllOption && (getControl() != null) && getControl() instanceof MultiValueControl) {
            MultiValueControl multiValueControl = (MultiValueControl) getControl();
            if (multiValueControl.getOptions() != null) {
                List<KeyValue> fieldOptions = multiValueControl.getOptions();
                fieldOptions.add(0, new ConcreteKeyValue("", "All"));

                multiValueControl.setOptions(fieldOptions);
            }
        }
    }

    /**
     * Override of InputField copy to setup properties necessary to make the field usable for inputting
     * search criteria
     *
     * @param attributeDefinition - AttributeDefinition instance the property values should be copied from
     * @see DataField#copyFromAttributeDefinition(View,
     * AttributeDefinition)
     */
    @Override
    public void copyFromAttributeDefinition(View view, AttributeDefinition attributeDefinition) {
        // label
        if (StringUtils.isEmpty(getLabel())) {
            setLabel(attributeDefinition.getLabel());
        }

        // short label
        if (StringUtils.isEmpty(getShortLabel())) {
            setShortLabel(attributeDefinition.getShortLabel());
        }

        // security
        if (getDataFieldSecurity().getAttributeSecurity() == null) {
            getDataFieldSecurity().setAttributeSecurity(attributeDefinition.getAttributeSecurity());
        }

        // options
        if (getOptionsFinder() == null) {
            setOptionsFinder(attributeDefinition.getOptionsFinder());
        }

        // TODO: what about formatter?

        // use control from dictionary if not specified and convert for searching
        if (getControl() == null) {
            Control control = convertControlToLookupControl(attributeDefinition);
            view.assignComponentIds(control);

            setControl(control);
        }

        // overwrite maxLength to allow for wildcards and ranges
        setMaxLength(100);

        // set default value for active field to true
        if (StringUtils.isEmpty(getDefaultValue())) {
            if ((StringUtils.equals(getPropertyName(), KRADPropertyConstants.ACTIVE))) {
                setDefaultValue(KRADConstants.YES_INDICATOR_VALUE);
            }
        }

        /*
           * TODO delyea: FieldUtils.createAndPopulateFieldsForLookup used to allow for a set of property names to be passed in via the URL
           * parameters of the lookup url to set fields as 'read only'
           */

    }

    /**
     * If control definition is defined on the given attribute definition, converts to an appropriate control for
     * searching (if necessary) and returns a copy for setting on the field
     *
     * @param attributeDefinition - attribute definition instance to retrieve control from
     * @return Control instance or null if not found
     */
    protected static Control convertControlToLookupControl(AttributeDefinition attributeDefinition) {
        if (attributeDefinition.getControlField() == null) {
            return null;
        }

        Control newControl = null;

        // convert checkbox to radio with yes/no/both options
        if (CheckboxControl.class.isAssignableFrom(attributeDefinition.getControlField().getClass())) {
            newControl = ComponentFactory.getRadioGroupControlHorizontal();
            List<KeyValue> options = new ArrayList<KeyValue>();
            options.add(new ConcreteKeyValue("Y", "Yes"));
            options.add(new ConcreteKeyValue("N", "No"));
            options.add(new ConcreteKeyValue("", "Both"));

            ((RadioGroupControl) newControl).setOptions(options);
        }
        // text areas get converted to simple text inputs
        else if (TextAreaControl.class.isAssignableFrom(attributeDefinition.getControlField().getClass())) {
            newControl = ComponentFactory.getTextControl();
        } else {
            newControl = ComponentUtils.copy(attributeDefinition.getControlField(), "");
        }

        return newControl;
    }

    /**
     * @return the treatWildcardsAndOperatorsAsLiteral
     */
    public boolean isTreatWildcardsAndOperatorsAsLiteral() {
        return this.treatWildcardsAndOperatorsAsLiteral;
    }

    /**
     * @param treatWildcardsAndOperatorsAsLiteral the treatWildcardsAndOperatorsAsLiteral to set
     */
    public void setTreatWildcardsAndOperatorsAsLiteral(boolean treatWildcardsAndOperatorsAsLiteral) {
        this.treatWildcardsAndOperatorsAsLiteral = treatWildcardsAndOperatorsAsLiteral;
    }

    /**
     * Indicates whether the option for all values (blank key, 'All' label) should be added to the lookup
     * field, note this is only supported for {@link MultiValueControl} instance
     *
     * @return boolean true if all option should be added, false if not
     */
    public boolean isAddAllOption() {
        return addAllOption;
    }

    /**
     * Setter for the add all option indicator
     *
     * @param addAllOption
     */
    public void setAddAllOption(boolean addAllOption) {
        this.addAllOption = addAllOption;
    }
}
