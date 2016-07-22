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
package org.kuali.kfs.krad.uif.widget;

import org.kuali.kfs.krad.uif.component.ComponentBase;

import java.util.HashMap;

/**
 * Used for rendering a lightbox in the UI to display action links in dialog
 * popups
 * 
 * 
 */
public class LightBox extends WidgetBase {

    private static final long serialVersionUID = -4004284762546700975L;

    private String actionParameterMapString;

    private String height;
    private String width;

    private boolean lookupReturnByScript;

    public LightBox() {
        super();
    }

    /**
     * Setter for the action parameter map javascript string
     *
     * @param actionParameterMapString the action parameter map javascript string
     */
    public void setActionParameterMapString(String actionParameterMapString) {
        this.actionParameterMapString = actionParameterMapString;
    }

    /**
     * Action parameter map javascript string
     * <p>
     * The action parameter map string will be used to write these parameters to
     * the form.
     * </p>
     *
     * @return the action parameter map javascript string
     */
    public String getActionParameterMapString() {
        return actionParameterMapString;
    }

    /**
     * @return height of light box
     */
    public String getHeight() {
        return height;
    }

    /**
     * Setter for the height of the light box
     * Can be percentage. ie. 75%
     *
     * @param height
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * @return width of light box
     */
    public String getWidth() {
        return width;
    }

     /**
     * Setter for the width of the light box
     * Can be percentage. ie. 75%
     *
     * @param width
     */
    public void setWidth(String width) {
        this.width = width;
    }

     /**
     * @see ComponentBase#getComponentOptionsJSString()
     */
    @Override
    public String getComponentOptionsJSString() {
        if (getComponentOptions() == null) {
            setComponentOptions(new HashMap<String, String>());
        }

        // Add the width and height properties to the ComponentOptions
        // before the JS String gets generated.
        if (width != null) {
            getComponentOptions().put("width", width);
        }
        if (height != null) {
            getComponentOptions().put("height", height);
        }
        return super.getComponentOptionsJSString();
    }

    /**
     * @return the lookupReturnByScript flag
     */
    public boolean isLookupReturnByScript() {
        return lookupReturnByScript;
    }

/**
     * Setter for the flag to indicate that lookups will return the value
     * by script and not a post
     *
     * @param lookupReturnByScript the lookupReturnByScript flag
     */
    public void setLookupReturnByScript(boolean lookupReturnByScript) {
        this.lookupReturnByScript = lookupReturnByScript;
    }
}
