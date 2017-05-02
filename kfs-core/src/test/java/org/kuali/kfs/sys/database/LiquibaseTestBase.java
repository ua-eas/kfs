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
package org.kuali.kfs.sys.database;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LiquibaseTestBase {
    protected void testForMissingModifySql(String filename) throws IOException, SAXException, ParserConfigurationException {
        Element rootElement = parseFile(filename);
        List<Node> children = getChildNodes(rootElement);

        boolean found = false;
        for (Node changeSet : children) {
            List<Node> changes = getChildNodes(changeSet);
            for (Node change : changes) {
                if (changeContainsColumns(change) && changeContainsFieldOfType(change, "DATE")) {
                    if (isChangeSetMissingValidModifySql(changeSet)) {
                        System.out.println("Table missing a valid modifySql: " + getTableName(change));
                        found = true;
                    }
                }
            }
        }

        if (found) {
            throw new RuntimeException("Some tables are missing a valid modifySql");
        }
    }

    protected void testForDateColumn(String filename) throws IOException, SAXException, ParserConfigurationException {
        Element rootElement = parseFile(filename);
        List<Node> children = getChildNodes(rootElement);

        boolean found = false;
        for (Node changeSet : children) {
            List<Node> changes = getChildNodes(changeSet);
            for (Node change : changes) {
                if (changeContainsColumns(change) && changeContainsFieldOfType(change, "DATETIME")) {
                    System.out.println("Table contains DATETIME column instead of DATE: " + getTableName(change));
                    found = true;
                }
            }
        }

        if (found) {
            throw new RuntimeException("Some tables contain DATETIME column instead of DATE");
        }
    }

    protected List<String> findLiquibaseFiles(String path) {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
            if (is == null) {
                return Collections.emptyList();
            }
            List<String> files = IOUtils.readLines(is);
            return files.stream().map(f -> path + f).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean changeContainsColumns(Node changeSet) {
        return ("createTable".equals(changeSet.getNodeName()) || "addColumn".equals(changeSet.getNodeName()));
    }

    protected boolean isChangeSetMissingValidModifySql(Node changeSet) {
        Node modifySql = getNodeByName(changeSet, "modifySql");

        if (modifySql == null) {
            return true;
        } else {
            Element modifySqlElement = (Element) modifySql;
            String dbms = modifySqlElement.getAttribute("dbms");
            if (!"mysql".equals(dbms)) {
                System.out.println("dbms=\"mysql\" attribute missing in modifySql node");
                return true;
            }
            Node replaceNode = getNodeByName(modifySql, "replace");
            Node regexReplaceNode = getNodeByName(modifySql, "regExpReplace");
            if (replaceNode == null && regexReplaceNode == null) {
                System.out.println("replace/regExpReplace node missing");
                return true;
            }
            Element replaceNodeElement = null;
            if (replaceNode != null) {
                replaceNodeElement = (Element) replaceNode;
            } else if (regexReplaceNode != null) {
                replaceNodeElement = (Element) regexReplaceNode;
            }
            String replace = replaceNodeElement.getAttribute("replace");
            String with = replaceNodeElement.getAttribute("with");
            if (regexReplaceNode != null && replace.contains("\\b")) {
                if (!replace.startsWith("\\b") || !replace.endsWith("\\b")) {
                    System.out.println("regExpReplace attribute using word boundary character should start and end with the boundary character");
                    return true;
                }
                if (StringUtils.countMatches(replace, "\\b") > 2) {
                    System.out.println("regExpReplace attribute using word boundary character should start and end with the boundary character");
                    return true;
                }
                //Strip word boundary character
                replace = replace.substring(2, replace.length() - 2);
            }

            if (!"date".equals(replace)) {
                System.out.println("replace attribute value must be \"date\" (lower case)");
                return true;
            }
            if (!"datetime".equals(with)) {
                System.out.println("with attribute value must be \"datetime\" (lower case)");
                return true;
            }
        }
        return false;
    }

    protected String getTableName(Node createTableNode) {
        Element e = (Element) createTableNode;
        return e.getAttribute("tableName");
    }

    protected boolean changeContainsFieldOfType(Node changeNode, String columnType) {
        List<Node> children = getChildNodes(changeNode);
        for (Node child : children) {
            Element column = (Element) child;
            if (columnType.equals(column.getAttribute("type"))) {
                return true;
            }
        }
        return false;
    }

    protected Node getNodeByName(Node parentNode, String name) {
        List<Node> children = getChildNodes(parentNode);
        for (Node child : children) {
            if (name.equals(child.getNodeName())) {
                return child;
            }
        }
        return null;
    }

    protected List<Node> getChildNodes(Node parent) {
        List<Node> children = new ArrayList<>();

        NodeList childrenNodes = parent.getChildNodes();
        for (int i = 0; i < childrenNodes.getLength(); i++) {
            Node child = childrenNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                children.add(child);
            }
        }
        return children;
    }

    protected Element parseFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        InputStream fileInputStream = this.getClass().getResourceAsStream(filename);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileInputStream);
        return doc.getDocumentElement();
    }
}
