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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.MaintainableXMLConversionService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MaintainableXMLConversionServiceImpl implements MaintainableXMLConversionService, ApplicationContextAware, InitializingBean {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintainableXMLConversionServiceImpl.class);

    protected static final String SERIALIZATION_ATTRIBUTE = "serialization";
    protected static final String CLASS_ATTRIBUTE = "class";
    protected static final String DEFINED_IN_ATTRIBUTE = "defined-in";
    protected static final String MAINTENANCE_ACTION_ELEMENT_NAME = "maintenanceAction";


    protected Map<String, String> classNameRuleMap;
    protected Map<String, Map<String, String>> classPropertyRuleMap;
    protected Set<String> classRemovals;
    protected Map<String, Set<String>> classPropertyRemovals;
    protected List<String> conversionRuleFiles;

    protected ApplicationContext applicationContext;
    protected volatile KualiModuleService kualiModuleService;
    protected volatile PersistenceStructureService persistenceStructureService;

    @Override
    public String transformMaintainableXML(String xml) {
        String maintenanceAction = "<" + MAINTENANCE_ACTION_ELEMENT_NAME + ">" + StringUtils.substringAfter(xml, "<" + MAINTENANCE_ACTION_ELEMENT_NAME + ">");
        xml = StringUtils.substringBefore(xml, "<" + MAINTENANCE_ACTION_ELEMENT_NAME + ">");
        if (!CollectionUtils.isEmpty(getConversionRuleFiles())) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(new InputSource(new StringReader(xml)));
                for (Node childNode = document.getFirstChild(); childNode != null; ) {
                    Node nextChild = childNode.getNextSibling();
                    transformClassNode(document, childNode);
                    childNode = nextChild;
                }
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer trans = transFactory.newTransformer();
                trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                trans.setOutputProperty(OutputKeys.INDENT, "yes");

                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                DOMSource source = new DOMSource(document);
                trans.transform(source, result);
                // This replaceAll removes any empty lines from the output.
                xml = writer.toString().replaceAll("(?m)^\\s+\\n", "");
            } catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException | TransformerException | XPathExpressionException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return xml + maintenanceAction;
    }

    public List<String> getConversionRuleFiles() {
        return conversionRuleFiles;
    }

    public void setConversionRuleFiles(List<String> conversionRuleFiles) {
        this.conversionRuleFiles = conversionRuleFiles;
    }

    protected void transformClassNode(Document document, Node node) throws ClassNotFoundException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        String className = node.getNodeName();
        if (classNameRuleMap.containsKey(className)) {
            String newClassName = classNameRuleMap.get(className);
            document.renameNode(node, null, newClassName);
            className = newClassName;
        } else if (classRemovals.contains(className)) {
            node.getParentNode().removeChild(node);
            return;
        } else if (!className.contains(".")) {
            // Not really a class node.
            return;
        }
        Class<?> dataObjectClass = Class.forName(className);

        if (classPropertyRuleMap.containsKey(className)) {
            transformNode(document, node, dataObjectClass, classPropertyRuleMap.get(className));
        }
        transformNode(document, node, dataObjectClass, classPropertyRuleMap.get("*"));
    }

    protected void transformNode(Document document, Node node, Class<?> currentClass, Map<String, String> propertyMappings) throws ClassNotFoundException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        if (currentClass != null && classRemovals.contains(currentClass.getName())) {
            node.getParentNode().removeChild(node);
            return;
        }
        for (Node childNode = node.getFirstChild(); childNode != null; ) {
            Node nextChild = childNode.getNextSibling();
            String propertyName = childNode.getNodeName();
            if (childNode.hasAttributes()) {
                XPath xpath = XPathFactory.newInstance().newXPath();
                Node serializationAttribute = childNode.getAttributes().getNamedItem(SERIALIZATION_ATTRIBUTE);
                if (serializationAttribute != null && StringUtils.equals(serializationAttribute.getNodeValue(), "custom")) {
                    Node classAttribute = childNode.getAttributes().getNamedItem(CLASS_ATTRIBUTE);
                    if (classAttribute != null) {
                        if (StringUtils.equals(classAttribute.getNodeValue(), "org.kuali.rice.kns.util.TypedArrayList")) {
                            ((Element) childNode).removeAttribute(SERIALIZATION_ATTRIBUTE);
                            ((Element) childNode).removeAttribute(CLASS_ATTRIBUTE);
                            XPathExpression listSizeExpression = xpath.compile("//" + propertyName + "/org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl/default/size/text()");
                            String size = (String) listSizeExpression.evaluate(childNode, XPathConstants.STRING);
                            List<Node> nodesToAdd = new ArrayList<Node>();
                            if (StringUtils.isNotBlank(size) && Integer.valueOf(size) > 0) {
                                XPathExpression listTypeExpression = xpath.compile("//" + propertyName + "/org.kuali.rice.kns.util.TypedArrayList/default/listObjectType/text()");
                                String listType = (String) listTypeExpression.evaluate(childNode, XPathConstants.STRING);
                                XPathExpression listContentsExpression = xpath.compile("//" + propertyName + "/org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl/" + listType);
                                NodeList listContents = (NodeList) listContentsExpression.evaluate(childNode, XPathConstants.NODESET);
                                for (int i = 0; i < listContents.getLength(); i++) {
                                    Node tempNode = listContents.item(i);
                                    transformClassNode(document, tempNode);
                                    nodesToAdd.add(tempNode);
                                }
                            }
                            for (Node removeNode = childNode.getFirstChild(); removeNode != null; ) {
                                Node nextRemoveNode = removeNode.getNextSibling();
                                childNode.removeChild(removeNode);
                                removeNode = nextRemoveNode;
                            }
                            for (Node nodeToAdd : nodesToAdd) {
                                childNode.appendChild(nodeToAdd);
                            }
                        }
                    } else {
                        ((Element) childNode).removeAttribute(SERIALIZATION_ATTRIBUTE);

                        XPathExpression mapContentsExpression = xpath.compile("//" + propertyName + "/map/string");
                        NodeList mapContents = (NodeList) mapContentsExpression.evaluate(childNode, XPathConstants.NODESET);
                        List<Node> nodesToAdd = new ArrayList<Node>();
                        if (mapContents.getLength() > 0 && mapContents.getLength() % 2 == 0) {
                            for (int i = 0; i < mapContents.getLength(); i++) {
                                Node keyNode = mapContents.item(i);
                                Node valueNode = mapContents.item(++i);
                                Node entryNode = document.createElement("entry");
                                entryNode.appendChild(keyNode);
                                entryNode.appendChild(valueNode);
                                nodesToAdd.add(entryNode);
                            }
                        }
                        for (Node removeNode = childNode.getFirstChild(); removeNode != null; ) {
                            Node nextRemoveNode = removeNode.getNextSibling();
                            childNode.removeChild(removeNode);
                            removeNode = nextRemoveNode;
                        }
                        for (Node nodeToAdd : nodesToAdd) {
                            childNode.appendChild(nodeToAdd);
                        }
                    }
                } else {
                    Node classAttribute = childNode.getAttributes().getNamedItem(CLASS_ATTRIBUTE);
                    if (classAttribute != null) {
                        if (classRemovals.contains(classAttribute.getNodeValue())) {
                            childNode.getParentNode().removeChild(childNode);
                            childNode = null;
                        } else if (classNameRuleMap.containsKey(classAttribute.getNodeValue())) {
                            classAttribute.setNodeValue(classNameRuleMap.get(classAttribute.getNodeValue()));
                        }
                    } else {
                        Node definedInAttribute = childNode.getAttributes().getNamedItem(DEFINED_IN_ATTRIBUTE);
                        if (definedInAttribute != null) {
                            if (classRemovals.contains(definedInAttribute.getNodeValue())) {
                                childNode.getParentNode().removeChild(childNode);
                                childNode = null;
                            } else if (classNameRuleMap.containsKey(definedInAttribute.getNodeValue())) {
                                definedInAttribute.setNodeValue(classNameRuleMap.get(definedInAttribute.getNodeValue()));
                            }
                        }
                    }
                }
            }
            if (childNode != null && propertyMappings != null && propertyMappings.containsKey(propertyName)) {
                String newPropertyName = propertyMappings.get(propertyName);
                if (StringUtils.isNotBlank(newPropertyName)) {
                    document.renameNode(childNode, null, newPropertyName);
                    propertyName = newPropertyName;
                } else {
                    // If there is no replacement name then the element needs
                    // to be removed and skip all other processing
                    node.removeChild(childNode);
                    childNode = nextChild;
                    continue;
                }
            }
            if (childNode != null && childNode.hasChildNodes()) {
                if (!Collection.class.isAssignableFrom(currentClass) && !Map.class.isAssignableFrom(currentClass)) {
                    try {
                        Class<?> propertyClass = convertToExternalizableBusinessObjectImplementationIfNecessary(PropertyUtils.getPropertyType(currentClass.newInstance(), propertyName));
                        if (propertyName.equals(KRADPropertyConstants.EXTENSION)) {
                            final Map<String, DataObjectRelationship> relationshipMetadata = getPersistenceStructureService().getRelationshipMetadata(currentClass, KRADPropertyConstants.EXTENSION);
                            if (relationshipMetadata.containsKey(KRADPropertyConstants.EXTENSION)) {
                                propertyClass = relationshipMetadata.get(KRADPropertyConstants.EXTENSION).getRelatedClass();
                            }
                        }
                        if (propertyClass != null && classPropertyRuleMap.containsKey(propertyClass.getName())) {
                            transformNode(document, childNode, propertyClass, classPropertyRuleMap.get(propertyClass.getName()));
                        }

                        // Convert java.sql.Date to java.sql.Timestamp or java.util.Date
                        if (propertyClass == Timestamp.class || propertyClass == Date.class) {
                            Node firstChild = childNode.getFirstChild();
                            String value = firstChild.getNodeValue();
                            if (value != null && value.length() == 10) {
                                firstChild.setNodeValue(value + " 00:00:00" + (propertyClass == Date.class ? "AM" : ""));
                            }
                        }

                        transformNode(document, childNode, propertyClass, classPropertyRuleMap.get("*"));
                    } catch (InstantiationException | NoSuchMethodException e) {
                        LOG.warn("Could not instantiate an instance of " + currentClass.getName() + " and therefore, not transforming children");  // let's just move on now...
                    }
                } else if (Collection.class.isAssignableFrom(currentClass)) { // we've got a collection of sub-objects, let's go through those...
                    transformClassNode(document, childNode);
                }
            }
            childNode = nextChild;
        }
    }

    protected void setRuleMaps() {
        setupConfigurationMaps();
        try {
            for (String conversionRuleFile : getConversionRuleFiles()) {
                loadConversionRuleFile(conversionRuleFile);
            }
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected void loadConversionRuleFile(String conversionRuleFile) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Resource resource = null;
        Document doc = null;
        if (StringUtils.startsWith(conversionRuleFile, "classpath")) {
            resource = getApplicationContext().getResource(conversionRuleFile);
        } else {
            resource = new FileSystemResource(conversionRuleFile);
        }
        if (!resource.exists()) {
            doc = db.parse(this.getClass().getResourceAsStream(conversionRuleFile));
        } else {
            doc = db.parse(resource.getInputStream());
        }
        doc.getDocumentElement().normalize();
        XPath xpath = XPathFactory.newInstance().newXPath();

        // Get the moved classes rules
        XPathExpression exprClassNames = xpath.compile("//*[@name='maint_doc_classname_changes']/pattern");
        NodeList classNamesList = (NodeList) exprClassNames.evaluate(doc, XPathConstants.NODESET);
        for (int s = 0; s < classNamesList.getLength(); s++) {
            String matchText = xpath.evaluate("match/text()", classNamesList.item(s));
            String replaceText = xpath.evaluate("replacement/text()", classNamesList.item(s));
            classNameRuleMap.put(matchText, replaceText);
        }

        // Get the property changed rules

        XPathExpression exprClassProperties = xpath.compile(
            "//*[@name='maint_doc_changed_class_properties']/pattern");
        XPathExpression exprClassPropertiesPatterns = xpath.compile("pattern");
        NodeList propertyClassList = (NodeList) exprClassProperties.evaluate(doc, XPathConstants.NODESET);
        for (int s = 0; s < propertyClassList.getLength(); s++) {
            String classText = xpath.evaluate("class/text()", propertyClassList.item(s));
            Map<String, String> propertyRuleMap = new HashMap<String, String>();
            NodeList classPropertiesPatterns = (NodeList) exprClassPropertiesPatterns.evaluate(
                propertyClassList.item(s), XPathConstants.NODESET);
            for (int c = 0; c < classPropertiesPatterns.getLength(); c++) {
                String matchText = xpath.evaluate("match/text()", classPropertiesPatterns.item(c));
                String replaceText = xpath.evaluate("replacement/text()", classPropertiesPatterns.item(c));
                propertyRuleMap.put(matchText, replaceText);
            }
            classPropertyRuleMap.put(classText, propertyRuleMap);
        }

        /* Get the class node removal rules
           Note:  Class node removals should only be used for reference objects that can be materialized from
           a reference ID, anyway.  A good example is PersonImpl, whose structure has changed so much that it
           cannot be converted without significantly more complex logic.
        */
        XPathExpression exprClassRemovals = xpath.compile(
            "//*[@name='maint_doc_classname_removals']/pattern");
        NodeList classRemovalList = (NodeList) exprClassRemovals.evaluate(doc, XPathConstants.NODESET);
        for (int s = 0; s < classRemovalList.getLength(); s++) {
            String matchText = xpath.evaluate("match/text()", classRemovalList.item(s));
            classRemovals.add(matchText);
        }
    }

    protected void setupConfigurationMaps() {
        classNameRuleMap = new HashMap<String, String>();
        classPropertyRuleMap = new HashMap<String, Map<String, String>>();
        classRemovals = new HashSet<String>();

        // Pre-populate the class property rules with some defaults which apply to every BO
        Map<String, String> defaultPropertyRules = new HashMap<String, String>();
        defaultPropertyRules.put("boNotes", "");
        defaultPropertyRules.put("autoIncrementSet", "");
        classPropertyRuleMap.put("*", defaultPropertyRules);
    }

    protected Class<?> convertToExternalizableBusinessObjectImplementationIfNecessary(Class<?> currentClass) {
        if (currentClass != null && currentClass.isInterface() && ExternalizableBusinessObject.class.isAssignableFrom(currentClass)) {
            final ModuleService eboOwningModule = getKualiModuleService().getResponsibleModuleService(currentClass);
            return eboOwningModule.getExternalizableBusinessObjectImplementation((Class<? extends ExternalizableBusinessObject>) currentClass);
        }
        return currentClass;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRuleMaps();
    }

    public KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            this.kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }

    public PersistenceStructureService getPersistenceStructureService() {
        if (persistenceStructureService == null) {
            persistenceStructureService = KRADServiceLocator.getPersistenceStructureService();
        }
        return persistenceStructureService;
    }
}
