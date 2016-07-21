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
package org.kuali.kfs.coreservice.impl.style;

import org.apache.log4j.Logger;
import org.kuali.kfs.coreservice.api.style.Style;
import org.kuali.kfs.coreservice.api.style.StyleRepositoryService;
import org.kuali.rice.core.api.impex.xml.XmlConstants;
import org.kuali.rice.core.api.impex.xml.XmlIngestionException;
import org.kuali.rice.core.api.util.xml.XmlException;
import org.kuali.rice.core.api.util.xml.XmlJotter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Parser for Style content type, managed by StyleService
 */
public class StyleXmlParserImpl implements StyleXmlParser {
	private static final Logger LOG = Logger.getLogger(StyleXmlParserImpl.class);

	private StyleRepositoryService styleRepositoryService;
	
    /**
     * Returns a valid DocumentBuilder
     * @return a valid DocumentBuilder
     */
    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            // well folks, there is not much we can do if we get a ParserConfigurationException
            // so might as well isolate the evilness here, and just balk if this occurs
            String message = "Error obtaining document builder";
            LOG.error(message, pce);
            throw new RuntimeException(message, pce);
        }
    }

    public void loadXml(InputStream inputStream, String principalId) {
    	List<Style> styles = parseStyles(inputStream);
    	for (Style style : styles) {
    		styleRepositoryService.saveStyle(style);
    	}
    }
    
    public List<Style> parseStyles(InputStream inputStream) {
    	DocumentBuilder db = getDocumentBuilder();
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        try {
            doc = db.parse(inputStream);
        } catch (Exception e) {
            throw generateException("Error parsing Style XML file", e);
        }
        NodeList styles;
        try {
        	styles = (NodeList) xpath.evaluate("//" + XmlConstants.STYLE_STYLES, doc.getFirstChild(), XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
        	throw generateException("Error evaluating XPath expression", e);
        }

        List<Style> parsedStyles = new ArrayList<Style>();
        for (int i = 0; i < styles.getLength(); i++) {
        	Node edl = styles.item(i);
        	NodeList children = edl.getChildNodes();
        	for (int j = 0; j < children.getLength(); j++) {
        		Node node = children.item(j);
        		if (node.getNodeType() == Node.ELEMENT_NODE) {
        			Element e = (Element) node;
        			if (XmlConstants.STYLE_STYLE.equals(node.getNodeName())) {
        				LOG.debug("Digesting style: " + e.getAttribute("name"));
        				Style.Builder styleBuilder = parseStyle(e);
        				parsedStyles.add(styleBuilder.build());
        			}
                }
            }
        }
        return parsedStyles;
    }
    
    /**
     * Parses an EDocLiteStyle
     *
     * @param e
     *            element to parse
     * @return an EDocLiteStyle
     */
    private static Style.Builder parseStyle(Element e) {
        String name = e.getAttribute("name");
        if (name == null || name.length() == 0) {
            throw generateMissingAttribException(XmlConstants.STYLE_STYLE, "name");
        }
        Style.Builder style = Style.Builder.create(name);
        Element stylesheet = null;
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && "xsl:stylesheet".equals(child.getNodeName())) {
                stylesheet = (Element) child;
                break;
            }
        }
        if (stylesheet == null) {
            throw generateMissingChildException(XmlConstants.STYLE_STYLE, "xsl:stylesheet");
        }
        try {
            style.setXmlContent(XmlJotter.jotNode(stylesheet, true));
        } catch (XmlException te) {
            throw generateSerializationException(XmlConstants.STYLE_STYLE, te);
        }
        return style;
    }

    private static XmlIngestionException generateMissingAttribException(String element, String attrib) {
        return generateException("Style '" + element + "' element must contain a '" + attrib + "' attribute", null);
    }

    private static XmlIngestionException generateMissingChildException(String element, String child) {
        return generateException("Style '" + element + "' element must contain a '" + child + "' child element", null);
    }

    private static XmlIngestionException generateSerializationException(String element, XmlException cause) {
        return generateException("Error serializing Style '" + element + "' element", cause);
    }
    
    private static XmlIngestionException generateException(String error, Throwable cause) {
    	return new XmlIngestionException(error, cause);
    }
    
    public void setStyleRepositoryService(StyleRepositoryService styleRepositoryService) {
    	this.styleRepositoryService = styleRepositoryService;
    }

}
