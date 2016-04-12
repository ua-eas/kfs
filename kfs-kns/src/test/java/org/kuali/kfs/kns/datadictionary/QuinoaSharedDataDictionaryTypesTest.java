/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuinoaSharedDataDictionaryTypesTest {
    private Document quinoaSharedDataDictionaryTypesDocument;
    private Element documentElement;

    @Before
    public void setup() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        quinoaSharedDataDictionaryTypesDocument = builder.parse(ClassLoader.class.getResourceAsStream("/org/kuali/kfs/kns/datadictionary/QuinoaSharedDataDictionaryTypes.xml"));
        documentElement = quinoaSharedDataDictionaryTypesDocument.getDocumentElement();
    }

    @Test
    public void testPersonImplContainsProperAttributes() throws Exception {
        Optional<Node> personImplBean = getBeanOfName("PersonImpl-parentBean");
        Assert.assertTrue("Unable to find PersonImpl-parentBean bean in file", personImplBean.isPresent());

        Optional<Node> attributesBean = getPropertyOfBean(personImplBean.get(), "attributes");
        Assert.assertTrue("Unable to find attributes for PersonImpl-parentBean", attributesBean.isPresent());

        List<Node> propertyChildren = getElementNodes(attributesBean.get().getChildNodes());
        Assert.assertEquals("Should be one child under the attributes node",1,propertyChildren.size());
        Node attributesList = propertyChildren.get(0);

        List<Node> attributeNodes = getElementNodes(attributesList.getChildNodes());
        List<String> attributes = attributeNodes
                .stream()
                .map(attrNode -> getBean(attrNode))
                .collect(Collectors.toList());

        Assert.assertTrue(attributes.contains("PersonImpl-emailAddress"));
        Assert.assertTrue(attributes.contains("PersonImpl-entityId"));
        Assert.assertTrue(attributes.contains("PersonImpl-firstName"));
        Assert.assertTrue(attributes.contains("PersonImpl-lastName"));
        Assert.assertTrue(attributes.contains("PersonImpl-middleName"));
        Assert.assertTrue(attributes.contains("PersonImpl-name"));
        Assert.assertTrue(attributes.contains("PersonImpl-employeeId"));
        Assert.assertTrue(attributes.contains("PersonImpl-principalId"));
        Assert.assertTrue(attributes.contains("PersonImpl-principalName"));
        Assert.assertTrue(attributes.contains("PersonImpl-primaryDepartmentCode"));
    }

    private Optional<Node> getPropertyOfBean(Node bean,String name) {
        return getElementNodes(bean.getChildNodes())
                .stream()
                .filter(b -> name.equals(getName(b)))
                .findFirst();
    }

    private Optional<Node> getBeanOfName(String name) {
        List<Node> beans = getElementNodes(documentElement.getChildNodes());

        return beans
                .stream()
                .filter(b -> name.equals(getId(b)))
                .findFirst();
    }

    private String getBean(Node bean) {
        return getAttributeValue(bean,"bean");
    }

    private String getName(Node bean) {
        return getAttributeValue(bean,"name");
    }

    private String getId(Node bean) {
        return getAttributeValue(bean,"id");
    }

    private String getAttributeValue(Node bean,String attribute) {
        Element beanElement = (Element)bean;
        return beanElement.getAttribute(attribute);
    }

    private List<Node> getElementNodes(NodeList nodes) {
        List<Node> outNodes = new ArrayList<Node>();
        for ( int i = 0; i < nodes.getLength(); i++ ) {
            Node child = nodes.item(i);
            if ( child.getNodeType() == Node.ELEMENT_NODE ) {
                outNodes.add(child);
            }
        }
        return outNodes;
    }}
