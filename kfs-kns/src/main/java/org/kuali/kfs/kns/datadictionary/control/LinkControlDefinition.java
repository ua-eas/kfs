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
package org.kuali.kfs.kns.datadictionary.control;

import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;

/**
 * The link element is used to render an HTML link control.
 */
@Deprecated
public class LinkControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -7568912421829207545L;

    protected String styleClass;
    protected String target;
    protected String hrefText;

    public LinkControlDefinition() {
    }

    /**
     * @see ControlDefinition#isLink()
     */
    public boolean isLink() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.getClass().getName();
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

    /**
     * @return the target
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the hrefText
     */
    public String getHrefText() {
        return this.hrefText;
    }

    /**
     * @param hrefText the hrefText to set
     */
    public void setHrefText(String hrefText) {
        this.hrefText = hrefText;
    }

}
