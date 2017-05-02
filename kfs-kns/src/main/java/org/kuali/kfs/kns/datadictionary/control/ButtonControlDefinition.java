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
package org.kuali.kfs.kns.datadictionary.control;


import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;

/**
 * The button element is used to render an HTML button
 * control.
 */
@Deprecated
public class ButtonControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -4014804850405243606L;

    protected String imageSrc;
    protected String styleClass;

    public ButtonControlDefinition() {
    }

    /**
     * @see ControlDefinition#isButton()
     */
    public boolean isButton() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.getClass().getName();
    }

    /**
     * @return the mageSrc
     */
    public String getImageSrc() {
        return this.imageSrc;
    }

    /**
     * @param mageSrc the mageSrc to set
     */
    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    /**
     * @return the styleClass
     */
    public String getStyleClass() {
        return this.styleClass;
    }

    /**
     * @param styleClass the styleClass to set
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }


}
