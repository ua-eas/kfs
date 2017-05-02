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
package org.kuali.kfs.krad.uif.container;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.BindingInfo;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.DataBinding;
import org.kuali.kfs.krad.uif.field.MessageField;
import org.kuali.kfs.krad.uif.util.ComponentUtils;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Group component that is backed by a <code>Tree</code> data structure and typically
 * rendered as a tree in the user interface
 */
public class TreeGroup extends Group implements DataBinding {
    private static final long serialVersionUID = 5841343037089286740L;

    private String propertyName;
    private BindingInfo bindingInfo;

    private Map<Class<?>, NodePrototype> nodePrototypeMap;
    private NodePrototype defaultNodePrototype;

    private Tree<Group, MessageField> treeGroups;

    private org.kuali.kfs.krad.uif.widget.Tree tree;

    public TreeGroup() {
        super();

        treeGroups = new Tree<Group, MessageField>();
    }

    /**
     * The following actions are performed:
     * <p>
     * <ul>
     * <li>Set fieldBindModelPath to the collection model path (since the fields
     * have to belong to the same model as the collection)</li>
     * <li>Set defaults for binding</li>
     * <li>Calls view helper service to initialize prototypes</li>
     * </ul>
     */
    @Override
    public void performInitialization(View view, Object model) {
        setFieldBindingObjectPath(getBindingInfo().getBindingObjectPath());

        super.performInitialization(view, model);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(view, getPropertyName());
        }

        // TODO: set object path for prototypes equal to the tree group object path?

        initializeNodePrototypeComponents(view, model);
    }

    protected void initializeNodePrototypeComponents(View view, Object model) {
        view.getViewHelperService().performComponentInitialization(view, model,
            defaultNodePrototype.getLabelPrototype());
        view.getViewHelperService().performComponentInitialization(view, model,
            defaultNodePrototype.getDataGroupPrototype());

        if (nodePrototypeMap != null) {
            for (Map.Entry<Class<?>, NodePrototype> prototypeEntry : nodePrototypeMap.entrySet()) {
                NodePrototype prototype = prototypeEntry.getValue();
                if (prototype != null) {

                    if (prototype.getLabelPrototype() != null) {
                        view.getViewHelperService().performComponentInitialization(view, model,
                            prototype.getLabelPrototype());
                    } else {
                        throw new IllegalStateException("encountered null NodePrototype.labelPrototype");
                    }

                    if (prototype.getDataGroupPrototype() != null) {
                        view.getViewHelperService().performComponentInitialization(view, model,
                            prototype.getDataGroupPrototype());
                    } else {
                        throw new IllegalStateException("encountered null NodePrototype.dataGroupPrototype");
                    }
                } else {
                    throw new IllegalStateException("encountered null NodePrototype");
                }
            }
        }
    }

    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        buildTreeGroups(view, model);
    }

    /**
     * Builds the components that will be rendered as part of the tree group
     * <p>
     * <p>
     * The component tree group mirrors the tree data structure on the model. For each node of
     * the data structure, a corresponding <code>MessageField</code>  will be created for the node
     * label, and a <code>Group</code> component for the node data. These are placed into a new
     * node for the component tree. After the tree is built it is set as a property on the tree group
     * to be read by the renderer
     * </p>
     *
     * @param view  - view instance the tree group belongs to
     * @param model - object containing the view data from which the tree data will be retrieved
     */
    protected void buildTreeGroups(View view, Object model) {
        // get Tree data property
        Tree<Object, String> treeData = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());

        // build component tree that corresponds with tree data
        Tree<Group, MessageField> treeGroups = new Tree<Group, MessageField>();

        String bindingPrefix = getBindingInfo().getBindingPrefixForNested();
        Node<Group, MessageField> rootNode =
            buildTreeNode(treeData.getRootElement(), bindingPrefix + /* TODO: hack */ ".rootElement", "root");
        treeGroups.setRootElement(rootNode);

        setTreeGroups(treeGroups);
    }

    protected Node<Group, MessageField> buildTreeNode(Node<Object, String> nodeData, String bindingPrefix,
                                                      String parentNode) {
        if (nodeData == null) {
            return null;
        }

        Node<Group, MessageField> node = new Node<Group, MessageField>();
        node.setNodeType(nodeData.getNodeType());

        NodePrototype prototype = getNodePrototype(nodeData);

        MessageField messageField = ComponentUtils.copy(prototype.getLabelPrototype(), parentNode);
        ComponentUtils.pushObjectToContext(messageField, UifConstants.ContextVariableNames.NODE, nodeData);
        messageField.setMessageText(nodeData.getNodeLabel());
        node.setNodeLabel(messageField);

        Group nodeGroup = ComponentUtils.copyComponent(prototype.getDataGroupPrototype(), bindingPrefix + ".data",
            parentNode);
        ComponentUtils.pushObjectToContext(nodeGroup, UifConstants.ContextVariableNames.NODE, nodeData);

        String nodePath = bindingPrefix + ".data";
        if (StringUtils.isNotBlank(getBindingInfo().getBindingObjectPath())) {
            nodePath = getBindingInfo().getBindingObjectPath() + "." + nodePath;
        }
        ComponentUtils.pushObjectToContext(nodeGroup, UifConstants.ContextVariableNames.NODE_PATH, nodePath);
        node.setData(nodeGroup);

        List<Node<Group, MessageField>> nodeChildren = new ArrayList<Node<Group, MessageField>>();

        int childIndex = 0;
        for (Node<Object, String> childDataNode : nodeData.getChildren()) {
            String nextBindingPrefix = bindingPrefix + ".children[" + childIndex + "]";
            Node<Group, MessageField> childNode = buildTreeNode(childDataNode, nextBindingPrefix,
                "_node_" + childIndex + ("root".equals(parentNode) ? "_parent_" : "_parent") + parentNode);

            nodeChildren.add(childNode);

            // Don't forget about me:
            ++childIndex;
        }
        node.setChildren(nodeChildren);

        return node;
    }

    /**
     * Gets the NodePrototype to use for the given Node
     */
    private NodePrototype getNodePrototype(Node<Object, String> nodeData) {
        NodePrototype result = null;
        if (nodeData != null && nodeData.getData() != null) {
            Class<?> dataClass = nodeData.getData().getClass();
            result = nodePrototypeMap.get(dataClass);

            // somewhat lame fallback - to do this right we'd find all entries that are assignable from the data class
            // and then figure out which one is the closest relative
            if (result == null) {
                for (Map.Entry<Class<?>, NodePrototype> prototypeEntry : nodePrototypeMap.entrySet()) {
                    if (prototypeEntry.getKey().isAssignableFrom(dataClass)) {
                        result = prototypeEntry.getValue();
                        break;
                    }
                }
            }
        }

        if (result == null) {
            result = defaultNodePrototype;
        }

        return result;
    }

    /**
     * @see Component#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(tree);
        addNodeComponents(treeGroups.getRootElement(), components);

        return components;
    }

    /**
     * @see Component#getComponentPrototypes()
     */
    @Override
    public List<Component> getComponentPrototypes() {
        List<Component> components = super.getComponentPrototypes();

        if (defaultNodePrototype != null) {
            components.add(defaultNodePrototype.getLabelPrototype());
            components.add(defaultNodePrototype.getDataGroupPrototype());
        }

        if (nodePrototypeMap != null) {
            for (Map.Entry<Class<?>, NodePrototype> prototypeEntry : nodePrototypeMap.entrySet()) {
                NodePrototype prototype = prototypeEntry.getValue();
                if (prototype != null) {
                    components.add(prototype.getLabelPrototype());
                    components.add(prototype.getDataGroupPrototype());
                }
            }
        }

        return components;
    }

    /**
     * Retrieves the <code>Component</code> instances from the node for building the nested
     * components list
     *
     * @param node       - node to pull components from
     * @param components - list to add components to
     */
    protected void addNodeComponents(Node<Group, MessageField> node, List<Component> components) {
        if (node != null) {
            components.add(node.getNodeLabel());
            components.add(node.getData());

            for (Node<Group, MessageField> nodeChild : node.getChildren()) {
                addNodeComponents(nodeChild, components);
            }
        }
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public BindingInfo getBindingInfo() {
        return bindingInfo;
    }

    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * @return the defaultNodePrototype
     */
    public NodePrototype getDefaultNodePrototype() {
        return this.defaultNodePrototype;
    }

    /**
     * @param defaultNodePrototype the defaultNodePrototype to set
     */
    public void setDefaultNodePrototype(NodePrototype defaultNodePrototype) {
        this.defaultNodePrototype = defaultNodePrototype;
    }

    /**
     * @return the nodePrototypeMap
     */
    public Map<Class<?>, NodePrototype> getNodePrototypeMap() {
        return this.nodePrototypeMap;
    }

    /**
     * @param nodePrototypeMap the nodePrototypeMap to set
     */
    public void setNodePrototypeMap(Map<Class<?>, NodePrototype> nodePrototypeMap) {
        this.nodePrototypeMap = nodePrototypeMap;
    }

    public Tree<Group, MessageField> getTreeGroups() {
        return treeGroups;
    }

    public void setTreeGroups(Tree<Group, MessageField> treeGroups) {
        this.treeGroups = treeGroups;
    }

    public org.kuali.kfs.krad.uif.widget.Tree getTree() {
        return tree;
    }

    public void setTree(org.kuali.kfs.krad.uif.widget.Tree tree) {
        this.tree = tree;
    }
}
