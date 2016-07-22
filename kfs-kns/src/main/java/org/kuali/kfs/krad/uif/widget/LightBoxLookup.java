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

/**
 * Used for rendering a lightbox in the UI to display the result of a submit in
 * a light box. This is used for the quickfinder lookup.
 * 
 * 
 */
public class LightBoxLookup extends WidgetBase {
    private static final long serialVersionUID = -8571541274489677888L;

    private String actionParameterMapString;

    public LightBoxLookup() {
        super();
    }

    /**
     * Setter for the action parameter map javascript string
     * 
     * @param the
     *            action parameter map javascript string
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

}
