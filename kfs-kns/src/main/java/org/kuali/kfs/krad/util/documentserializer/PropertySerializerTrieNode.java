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
package org.kuali.kfs.krad.util.documentserializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A node in the trie.
 *
 */
public class PropertySerializerTrieNode implements PropertySerializabilityMetadata {
    private String pathString;
    private String propertyNameComponent;
    private PropertySerializability propertySerializability;

    private List<PropertySerializerTrieNode> childNodes;

    public PropertySerializerTrieNode(String pathString, String propertyNameComponent) {
        this.pathString = pathString;
        this.propertyNameComponent = propertyNameComponent;
        this.childNodes = null;
        this.propertySerializability = PropertySerializability.SERIALIZE_OBJECT;
    }

    public void addChildNode(PropertySerializerTrieNode child) {
        if (childNodes == null) {
            childNodes = new ArrayList<PropertySerializerTrieNode>();
        }
        childNodes.add(child);
    }

    /**
     * The name of this property, relative to the parent node (i.e. the child node name relative to its parents).
     *
     * @return
     */
    public String getPropertyNameComponent() {
        return propertyNameComponent;
    }

    /**
     * Retrieves the child node with the given name
     *
     * @param propertyNameComponent
     * @return
     */
    public PropertySerializerTrieNode getChildNode(String propertyNameComponent) {
        if (childNodes == null) {
            return null;
        }
        for (int i = 0; i < childNodes.size(); i++) {
            PropertySerializerTrieNode childNode = childNodes.get(i);
            if (childNode.getPropertyNameComponent().equals(propertyNameComponent)) {
                return childNode;
            }
        }
        return null;
    }

    /**
     * @see PropertySerializabilityMetadata#getSerializableChildProperty(java.lang.String)
     */
    public PropertySerializabilityMetadata getSerializableChildProperty(String propertyNameComponent) {
        return getChildNode(propertyNameComponent);
    }

    /**
     * @see PropertySerializabilityMetadata#getPathString()
     */
    public String getPathString() {
        return pathString;
    }

    /**
     * @see PropertySerializabilityMetadata#getPropertySerializability()
     */
    public PropertySerializability getPropertySerializability() {
        return propertySerializability;
    }

    /**
     * Marks that all primitives of this object will be serialized.
     */
    public void setPropertySerializabilityToObjectAndAllPrimitives() {
        this.propertySerializability = PropertySerializability.SERIALIZE_OBJECT_AND_ALL_PRIMITIVES;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Path String: ").append(pathString).append(" Name component: ").append(propertyNameComponent);
        if (childNodes == null || childNodes.isEmpty()) {
            buf.append(" No child nodes.");
        }
        else {
            buf.append(" Child nodes: ");
            for (Iterator<PropertySerializerTrieNode> i = childNodes.iterator(); i.hasNext();) {
                buf.append(i.next().getPropertyNameComponent());
                if (i.hasNext()) {
                    buf.append(", ");
                }
            }
        }
        return super.toString();
    }
}
