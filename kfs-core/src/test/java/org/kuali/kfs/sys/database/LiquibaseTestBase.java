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
package org.kuali.kfs.sys.database;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LiquibaseTestBase {
    protected void testForMissingModifySql(String filename) throws IOException, SAXException, ParserConfigurationException {
        List<String> badTables = new ArrayList<>();

        Element rootElement = parseFile(filename);
        NodeList children = rootElement.getChildNodes();

        for ( int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            if ( child.getNodeType() == Node.ELEMENT_NODE ) {
                if ( isCreateTableMissingModifySql(child) ) {
                    Node createTableNode = getNodeByName(child,"createTable");
                    badTables.add(getTableName(createTableNode));
                }
            }
        }

        if (badTables.size() > 0) {
            badTables.forEach(t -> System.out.println("Table missing modifySql: " + t));
            throw new RuntimeException("Some tables are missing modifySql");
        }
    }

    protected boolean isCreateTableMissingModifySql(Node changeSet) {
        Node createTableNode = getNodeByName(changeSet,"createTable");
        Node modifySql = getNodeByName(changeSet,"modifySql");

        if ( tableContainsDate(createTableNode) ) {
            if ( modifySql == null ) {
                return true;
            }
        }
        return false;
    }

    protected String getTableName(Node createTableNode) {
        Element e = (Element)createTableNode;
        return e.getAttribute("tableName");
    }

    protected boolean tableContainsDate(Node createTableNode) {
        NodeList children = createTableNode.getChildNodes();
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element column = (Element)child;
                if ( "DATE".equals(column.getAttribute("type")) ) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Node getNodeByName(Node parentNode,String name) {
        NodeList children = parentNode.getChildNodes();
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item(i);
            if ( (child.getNodeType() == Node.ELEMENT_NODE) && (name.equals(child.getNodeName())) ) {
                return child;
            }
        }
        return null;
    }

    protected Element parseFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        InputStream fileInputStream = this.getClass().getResourceAsStream(filename);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileInputStream);
        return doc.getDocumentElement();
    }
}
