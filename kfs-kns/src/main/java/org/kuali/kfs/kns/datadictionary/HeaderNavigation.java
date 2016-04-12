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
package org.kuali.kfs.kns.datadictionary;

import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.kfs.krad.datadictionary.HelpDefinition;

import java.io.Serializable;

/**
            The headerNavigation element defines a set of additional
            tabs which will appear on the document.
 *
 *                The headerNavigationTab defines an additional tab which
                will appear on the document.

 */
@Deprecated
public class HeaderNavigation extends DataDictionaryDefinitionBase implements Serializable {
    private static final long serialVersionUID = 4317341111287854436L;
    
	protected String headerTabMethodToCall;
    protected String headerTabNavigateTo;
    protected String headerTabDisplayName;
    protected HelpDefinition helpDefinition;
    protected boolean disabled = false;
    
    public HeaderNavigation() {}

    public HeaderNavigation(String headerTabNavigateTo, String headerTabDisplayName) {
        this.headerTabNavigateTo = headerTabNavigateTo;
        this.headerTabDisplayName = headerTabDisplayName;
    }

    /**
     * Gets the navigationKey attribute.
     * 
     * @return Returns the navigationKey.
     */
    public String getHeaderTabMethodToCall() {
        return headerTabMethodToCall;
    }

    /**
     * Sets the navigationKey attribute value.
     * 
     * @param navigationKey The navigationKey to set.
     */
    public void setHeaderTabMethodToCall(String navigationKey) {
        this.headerTabMethodToCall = navigationKey;
    }

    /**
     * Gets the navigationStyle attribute.
     * 
     * @return Returns the navigationStyle.
     */
    public String getHeaderTabDisplayName() {
        return headerTabDisplayName;
    }

    /**
     * The displayName element is the name of the additional tab.
     */
    public void setHeaderTabDisplayName(String headerTabDisplayName) {
        this.headerTabDisplayName = headerTabDisplayName;
    }

    /**
     * Gets the suffix attribute.
     * 
     * @return Returns the suffix.
     */
    public String getHeaderTabNavigateTo() {
        return headerTabNavigateTo;
    }
    
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * The pageHelp element provides the keys to
                    obtain a help description from the database.

                    On document JSP pages, a help icon may be rendered.  If this tag is specified, then
                    the filename of this page will be located in the value of the parameter specified by the namespace, detail type, and name.

                    The value of the parameter is relative to the value of the "externalizable.help.url" property in ConfigurationService (see KualiHelpAction).
                    parameterNamespace: namespace of the parameter that has the path to the help page
                    parameterName: name of the parameter that has the path to the help page
                    parameterDetailType: detail type of the parameter that has the path to the help page
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * The navigateTo element is the forward name in the struts-config file.
     */
    public void setHeaderTabNavigateTo(String suffix) {
        this.headerTabNavigateTo = suffix;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    /**
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        // No real validation to be done here other than perhaps checking to be
        // sure that the security workgroup is a valid workgroup.
    }
}
