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
package org.kuali.kfs.krad.util.documentserializer;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.util.KRADConstants;

import java.util.StringTokenizer;

/**
 * This is a implementation of a trie/prefix tree of that contains metadata about property serializability
 * during the document serialization process.
 */
public class PropertySerializerTrie {
    private static final String PROPERTY_NAME_COMPONENT_SEPARATOR = ".";
    private PropertySerializerTrieNode rootNode;

    public PropertySerializerTrie() {
        rootNode = new PropertySerializerTrieNode(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);
    }

    /**
     * Registers a new serializable property so that all of its primitives are serialized.  All nesting properties
     * will be serialized only to render open/close tags to maintain consistency with the document structure, unless
     * they are registered as well.
     * <p>
     * For example, if only property "document.a.b" is registered, then the XML will look like the following:
     * <p>
     * &lt;document&gt;
     * &lt;a&gt;
     * &lt;b&gt;
     * &lt;primitiveOfB&gt;valueOfPrimitive&lt;/primitiveOfB&gt;
     * &lt;/b&gt;
     * &lt;/a&gt;
     * &lt;/document&gt;
     * <p>
     * That is, primitives of "document" and "document.a" will not be serialized unless those property strings are registered.
     *
     * @param propertyName
     * @param setPropertySerializabilityToObjectAndAllPrimitivesForAll
     */
    public void addSerializablePropertyName(String propertyName, boolean setPropertySerializabilityToObjectAndAllPrimitivesForAll) {
        if (propertyName == null) {
            throw new IllegalArgumentException("Null attribute name specified");
        }
        if (StringUtils.isBlank(propertyName)) {
            rootNode.setPropertySerializabilityToObjectAndAllPrimitives();
        } else {
            StringTokenizer tok = new StringTokenizer(propertyName, PROPERTY_NAME_COMPONENT_SEPARATOR, false);
            StringBuilder buf = new StringBuilder();

            if (setPropertySerializabilityToObjectAndAllPrimitivesForAll)
                rootNode.setPropertySerializabilityToObjectAndAllPrimitives();

            PropertySerializerTrieNode currentNode = rootNode;
            while (tok.hasMoreTokens()) {
                String attributeNameComponent = tok.nextToken();
                validateAttributeNameComponent(attributeNameComponent);

                buf.append(attributeNameComponent);

                // create a new node or retrieve existing node for this name component
                PropertySerializerTrieNode childNode = currentNode.getChildNode(attributeNameComponent);
                if (childNode == null) {
                    childNode = new PropertySerializerTrieNode(buf.toString(), attributeNameComponent);
                    currentNode.addChildNode(childNode);
                }

                if (tok.hasMoreTokens()) {
                    buf.append(PROPERTY_NAME_COMPONENT_SEPARATOR);
                }
                currentNode = childNode;
                if (setPropertySerializabilityToObjectAndAllPrimitivesForAll)
                    currentNode.setPropertySerializabilityToObjectAndAllPrimitives();
            }

            currentNode.setPropertySerializabilityToObjectAndAllPrimitives();
        }
    }

    /**
     * Retrieves the metadata about the given property name
     *
     * @param propertyName
     * @return
     */
    public PropertySerializabilityMetadata getPropertySerializabilityMetadata(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("Null attribute name specified");
        }
        if (StringUtils.isBlank(propertyName)) {
            return rootNode;
        } else {
            StringTokenizer tok = new StringTokenizer(propertyName, PROPERTY_NAME_COMPONENT_SEPARATOR, false);

            PropertySerializerTrieNode currentNode = rootNode;
            while (tok.hasMoreTokens()) {
                String attributeNameComponent = tok.nextToken();
                validateAttributeNameComponent(attributeNameComponent);

                // retrieve the child node for this name component
                PropertySerializerTrieNode childNode = currentNode.getChildNode(attributeNameComponent);
                if (childNode == null) {
                    // we didn't find a child node, so we know that something wasn't added with the prefix we're processing
                    return null;
                } else {
                    // keep going until we hit the last token, at which case we'll get out of this loop
                    currentNode = childNode;
                }
            }
            return currentNode;
        }
    }

    /**
     * Returns the root node of the trie
     *
     * @return
     */
    public PropertySerializabilityMetadata getRootPropertySerializibilityMetadata() {
        return rootNode;
    }

    protected void validateAttributeNameComponent(String attributeNameComponent) {
        if (StringUtils.isBlank(attributeNameComponent)) {
            throw new IllegalArgumentException("Blank attribute name component specified");
        }
    }
}
