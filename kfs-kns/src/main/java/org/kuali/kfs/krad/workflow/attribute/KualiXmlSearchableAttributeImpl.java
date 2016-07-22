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
import org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute;
import org.w3c.dom.Element;


/**
 * This class is used to define Kuali searchable attributes
 * 
 * 
 *
 */
public class KualiXmlSearchableAttributeImpl extends StandardGenericXMLSearchableAttribute implements KualiXmlAttribute {
    private static final long serialVersionUID = -5759823164605651979L;

	/**
     * Constructs a KualiXmlRuleAttributeImpl.java.
     */
    public KualiXmlSearchableAttributeImpl() {
        super();
    }

    /**
     * This method overrides the super class and modifies the XML that it operates on to put the name and the title in the place
     * where the super class expects to see them, even though they may no longer exist in the original XML.
     */
    @Override
    public Element getConfigXML(ExtensionDefinition extensionDefinition) {
        Element root = super.getConfigXML(extensionDefinition);
        KualiXmlAttributeHelper attributeHelper = new KualiXmlAttributeHelper();
        // this adds the name and title to the xml based on the data dictionary
        return attributeHelper.processConfigXML(root);
    }

}
