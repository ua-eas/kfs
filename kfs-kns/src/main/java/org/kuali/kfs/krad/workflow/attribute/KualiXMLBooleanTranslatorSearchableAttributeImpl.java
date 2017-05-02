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
package org.kuali.kfs.krad.workflow.attribute;

import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.w3c.dom.Element;

/**
 * This is an XML KEW search attribute that can be used where the XML of the attribute has an xpath expression that returns a
 * boolean. This attribute takes that boolean expression and translates it into true and false values based on the
 * {@link #getValueForXPathTrueEvaluation()} and {@link #getValueForXPathFalseEvaluation()} method's return variables.
 * <p>
 * NOTE: This will not longer be necessary if the version of xPath being used is every upgrade to 2.x or higher
 */
public class KualiXMLBooleanTranslatorSearchableAttributeImpl extends KualiXmlSearchableAttributeImpl {
    private static final long serialVersionUID = -4627314389844574461L;

    public static final String VALUE_FOR_TRUE = "Yes";
    public static final String VALUE_FOR_FALSE = "No";


    /**
     * This overriden method does the translation of the given xPath expression from the XML definition of the attribute and
     * translates it into the true and false values based on the {@link #getValueForXPathTrueEvaluation()} and
     * {@link #getValueForXPathFalseEvaluation()} method's return variables
     */
    @Override
    public Element getConfigXML(ExtensionDefinition extensionDefinition) {
        String[] xpathElementsToInsert = new String[3];
        xpathElementsToInsert[0] = "concat( substring('" + getValueForXPathTrueEvaluation() + "', number(not(";
        xpathElementsToInsert[1] = "))*string-length('" + getValueForXPathTrueEvaluation() + "')+1), substring('" + getValueForXPathFalseEvaluation() + "', number(";
        xpathElementsToInsert[2] = ")*string-length('" + getValueForXPathFalseEvaluation() + "')+1))";
        Element root = super.getConfigXML(extensionDefinition);
        return new KualiXmlAttributeHelper().processConfigXML(root, xpathElementsToInsert);
    }

    public String getValueForXPathTrueEvaluation() {
        return VALUE_FOR_TRUE;
    }

    public String getValueForXPathFalseEvaluation() {
        return VALUE_FOR_FALSE;
    }

}
