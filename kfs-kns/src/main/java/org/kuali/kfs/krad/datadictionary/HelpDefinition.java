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
package org.kuali.kfs.krad.datadictionary;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * The help element provides the keys to obtain a
 * help description from the database.
 * <p>
 * On document JSP pages, a help icon may be rendered.  If this tag is specified, then
 * the filename of this page will be located in the value of the parameter specified by the namespace, detail type, and name.
 * <p>
 * The value of the parameter is relative to the value of the "externalizable.help.url" property in ConfigurationService (see KualiHelpAction).
 * parameterNamespace: namespace of the parameter that has the path to the help page
 * parameterName: name of the parameter that has the path to the help page
 * parameterDetailType: detail type of the parameter that has the path to the help page
 */
public class HelpDefinition extends DataDictionaryDefinitionBase implements Serializable {
    private static final long serialVersionUID = -6869646654597012863L;

    protected String parameterNamespace;
    protected String parameterDetailType;
    protected String parameterName;

    /**
     * Constructs a HelpDefinition.
     */
    public HelpDefinition() {
    }

    /**
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        // No real validation to be done here other than perhaps checking to be
        // sure that the security workgroup is a valid workgroup.
    }

    /**
     * @return
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * parameterName: name of the parameter that has the path to the help page
     */
    public void setParameterName(String parameterName) {
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("invalid (blank) parameterName");
        }
        this.parameterName = parameterName;
    }

    /**
     * @return
     */
    public String getParameterNamespace() {
        return parameterNamespace;
    }

    /**
     * parameterNamespace: namespace of the parameter that has the path to the help page
     */
    public void setParameterNamespace(String parameterNamespace) {
        if (StringUtils.isBlank(parameterNamespace)) {
            throw new IllegalArgumentException("invalid (blank) parameterNamespace");
        }
        this.parameterNamespace = parameterNamespace;
    }

    public String getParameterDetailType() {
        return this.parameterDetailType;
    }

    /**
     * parameterDetailType: detail type of the parameter that has the path to the help page
     */
    public void setParameterDetailType(String parameterDetailType) {
        if (StringUtils.isBlank(parameterDetailType)) {
            throw new IllegalArgumentException("invalid (blank) parameterDetailType");
        }
        this.parameterDetailType = parameterDetailType;
    }

}
