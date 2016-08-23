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
package org.kuali.kfs.krad.uif.widget;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.uif.component.BindingInfo;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.field.ActionField;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.util.UrlFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Widget for rendering an Direct Inquiry link icon next to a input field
 */
public class DirectInquiry extends Inquiry {
    private static final long serialVersionUID = -2490979579285984314L;

    private ActionField directInquiryActionField;

    private boolean adjustInquiryParameters;
    private BindingInfo fieldBindingInfo;

    public DirectInquiry() {
        super();

        adjustInquiryParameters = false;
    }

    /**
     * @see WidgetBase#performFinalize(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // only set inquiry if enabled
        if (!isRender() || isReadOnly()) {
            return;
        }

        // set render to false until we find an inquiry class
        setRender(false);

        InputField field = (InputField) parent;

        // determine whether inquiry parameters will need adjusted
        if (StringUtils.isBlank(getDataObjectClassName())
            || (getInquiryParameters() == null)
            || getInquiryParameters().isEmpty()) {
            // if inquiry parameters not given, they will not be adjusted by super
            adjustInquiryParameters = true;
            fieldBindingInfo = field.getBindingInfo();
        }

        setupLink(view, model, field);
    }

    /**
     * Builds the inquiry link and onclick script based on the given inquiry class and parameters
     *
     * @param dataObject         - parent object that contains the data (used to pull inquiry
     *                           parameters)
     * @param propertyName       - name of the property the inquiry is set on
     * @param inquiryObjectClass - class of the object the inquiry should point to
     * @param inquiryParms       - map of key field mappings for the inquiry
     */
    public void buildInquiryLink(Object dataObject, String propertyName,
                                 Class<?> inquiryObjectClass, Map<String, String> inquiryParms) {
        Properties urlParameters = new Properties();

        urlParameters.put(UifParameters.DATA_OBJECT_CLASS_NAME,
            inquiryObjectClass.getName());
        urlParameters.put(UifParameters.METHOD_TO_CALL,
            UifConstants.MethodToCallNames.START);

        // Direct inquiry
        String inquiryUrl = UrlFactory.parameterizeUrl(getBaseInquiryUrl(),
            urlParameters);
        StringBuilder paramMapString = new StringBuilder();

        // Check if lightbox is set. Get lightbox options.
        String lightBoxOptions = "";
        boolean lightBoxShow = directInquiryActionField.getLightBoxDirectInquiry() != null;
        if (lightBoxShow) {
            lightBoxOptions = directInquiryActionField.getLightBoxDirectInquiry()
                .getComponentOptionsJSString();
        }

        // Build parameter string using the actual names of the fields as on the
        // html page
        for (Entry<String, String> inquiryParameter : inquiryParms.entrySet()) {
            String inquiryParameterFrom = inquiryParameter.getKey();
            if (adjustInquiryParameters && (fieldBindingInfo != null)) {
                inquiryParameterFrom = fieldBindingInfo.getPropertyAdjustedBindingPath(inquiryParameterFrom);
            }
            paramMapString.append(inquiryParameterFrom);
            paramMapString.append(":");
            paramMapString.append(inquiryParameter.getValue());
            paramMapString.append(",");
        }
        paramMapString.deleteCharAt(paramMapString.length() - 1);

        // Create onlick script to open the inquiry window on the click event
        // of the direct inquiry
        StringBuilder onClickScript = new StringBuilder("showDirectInquiry(\"");
        onClickScript.append(inquiryUrl);
        onClickScript.append("\", \"");
        onClickScript.append(paramMapString);
        onClickScript.append("\", ");
        onClickScript.append(lightBoxShow);
        onClickScript.append(", ");
        onClickScript.append(lightBoxOptions);
        onClickScript.append(");");

        directInquiryActionField.setBlockValidateDirty(true);
        directInquiryActionField.setClientSideJs(onClickScript.toString());

        setRender(true);
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(directInquiryActionField);

        return components;
    }

    /**
     * @return the directInquiryActionField
     */
    public ActionField getDirectInquiryActionField() {
        return this.directInquiryActionField;
    }

    /**
     * @param directInquiryActionField the directInquiryActionField to set
     */
    public void setDirectInquiryActionField(ActionField directInquiryActionField) {
        this.directInquiryActionField = directInquiryActionField;
    }

}
