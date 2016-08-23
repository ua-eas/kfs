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
package org.kuali.kfs.sys.document.web;

/**
 * A class that represents an action that can be taken on an accounting line.
 */
public class AccountingLineViewAction {
    private String actionMethod;
    private String actionLabel;
    private String buttonStyle;
    private String buttonLabel;
    private String buttonIcon;

    public AccountingLineViewAction(String actionMethod, String actionLabel, String buttonStyle, String buttonLabel) {
        this.actionMethod = actionMethod;
        this.actionLabel = actionLabel;
        this.buttonStyle = buttonStyle;
        this.buttonLabel = buttonLabel;
    }

    public AccountingLineViewAction(String actionMethod, String actionLabel, String buttonStyle, String buttonLabel, String buttonIcon) {
        this.actionMethod = actionMethod;
        this.actionLabel = actionLabel;
        this.buttonStyle = buttonStyle;
        this.buttonLabel = buttonLabel;
        this.buttonIcon = buttonIcon;
    }

    /**
     * Gets the actionLabel attribute.
     *
     * @return Returns the actionLabel.
     */
    public String getActionLabel() {
        return actionLabel;
    }

    /**
     * Sets the actionLabel attribute value.
     *
     * @param actionLabel The actionLabel to set.
     */
    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    /**
     * Gets the actionMethod attribute.
     *
     * @return Returns the actionMethod.
     */
    public String getActionMethod() {
        return actionMethod;
    }

    /**
     * Sets the actionMethod attribute value.
     *
     * @param actionMethod The actionMethod to set.
     */
    public void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    }

    /**
     * Gets the buttonStyle attribute.
     *
     * @return Returns the buttonStyle.
     */
    public String getButtonStyle() {
        return buttonStyle;
    }

    /**
     * Sets the imageName attribute value.
     *
     * @param buttonStyle The imageName to set.
     */
    public void setButtonStyle(String buttonStyle) {
        this.buttonStyle = buttonStyle;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    public String getButtonIcon() {
        return buttonIcon;
    }

    public void setButtonIcon(String buttonIcon) {
        this.buttonIcon = buttonIcon;
    }
}
