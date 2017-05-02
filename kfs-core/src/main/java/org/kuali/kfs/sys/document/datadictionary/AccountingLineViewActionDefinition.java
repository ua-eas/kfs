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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewActionsField;
import org.kuali.kfs.sys.document.web.TableJoining;

public class AccountingLineViewActionDefinition extends DataDictionaryDefinitionBase implements AccountingLineViewRenderableElementDefinition {
    private String actionMethod;
    private String actionLabel;
    private String buttonStyle;
    private String buttonLabel;
    private String buttonIcon;

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
     * Sets the buttonStyle attribute value.
     *
     * @param buttonStyle The buttonStyle to set.
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

    /**
     * Since this metadata definition has no children, there's nothing to validate and this method checks nothing.
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        // not checkin' nothing
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement(java.lang.Class)
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        return new AccountingLineViewActionsField();
    }
}
