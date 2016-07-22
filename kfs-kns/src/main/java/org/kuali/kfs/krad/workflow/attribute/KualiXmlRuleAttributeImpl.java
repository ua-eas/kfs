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
package org.kuali.kfs.krad.workflow.attribute;

import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.rule.xmlrouting.StandardGenericXMLRuleAttribute;
import org.w3c.dom.Element;


/**
 * This class extends the workflow xml rule attribute implementation to use the information in the data dictionary to generate labels.
 * 
 * 
 *
 */
public class KualiXmlRuleAttributeImpl extends StandardGenericXMLRuleAttribute implements KualiXmlAttribute {
	private static final long serialVersionUID = -3453451186396963835L;

    /**
     * Constructs a KualiXmlRuleAttributeImpl.java.
     */
    public KualiXmlRuleAttributeImpl() {
        super();
    }

    /**
     * This method overrides the super class and modifies the XML that it operates on to put the name and the title in the place
     * where the super class expects to see them, even though they may no longer exist in the original XML.
     * 
     * @see StandardGenericXMLRuleAttribute#getConfigXML()
     */
    @Override
    public Element getConfigXML() {
        Element root = super.getConfigXML();
        KualiXmlAttributeHelper attributeHelper = new KualiXmlAttributeHelper();
        // this adds the name and title to the xml based on the data dictionary
        return attributeHelper.processConfigXML(root);
    }

    @Override
    public Element getConfigXML(ExtensionDefinition extensionDefinition) {
        return getConfigXML();
    }

}
