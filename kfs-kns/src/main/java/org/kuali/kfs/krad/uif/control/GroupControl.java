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
package org.kuali.kfs.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.view.View;

/**
 * Represents a group control, which is a special control to handle
 * the input of a KIM Group by group name
 */
public class GroupControl extends TextControl {
    private static final long serialVersionUID = 5598459655735440981L;

    private String namespaceCodePropertyName;
    private String groupIdPropertyName;

    public GroupControl() {
        super();
    }

    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (!(parent instanceof InputField)) {
            return;
        }

        InputField field = (InputField) parent;

        if (StringUtils.isNotBlank(groupIdPropertyName)) {
            field.getHiddenPropertyNames().add(groupIdPropertyName);
        }

        if (StringUtils.isBlank(field.getFieldLookup().getDataObjectClassName())) {
            field.getFieldLookup().setDataObjectClassName("org.kuali.rice.kim.impl.group.GroupBo");
        }

        if (field.getFieldLookup().getFieldConversions().isEmpty()) {
            if (StringUtils.isNotBlank(groupIdPropertyName)) {
                field.getFieldLookup().getFieldConversions().put("id", groupIdPropertyName);
            }

            field.getFieldLookup().getFieldConversions().put("name", field.getPropertyName());

            if (StringUtils.isNotBlank(namespaceCodePropertyName)) {
                field.getFieldLookup().getFieldConversions().put("namespaceCode", namespaceCodePropertyName);
            }
        }

        if (field.getFieldLookup().getLookupParameters().isEmpty()) {
            if (StringUtils.isNotBlank(namespaceCodePropertyName)) {
                field.getFieldLookup().getLookupParameters().put(namespaceCodePropertyName, "namespaceCode");
            }
        }
    }

    /**
     * The name of the property on the parent object that holds the group namespace
     *
     * @return String namespaceCodePropertyName
     */
    public String getNamespaceCodePropertyName() {
        return namespaceCodePropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the group namespace
     *
     * @param namespaceCodePropertyName
     */
    public void setNamespaceCodePropertyName(String namespaceCodePropertyName) {
        this.namespaceCodePropertyName = namespaceCodePropertyName;
    }

    /**
     * The name of the property on the parent object that holds the group id
     *
     * @return String groupIdPropertyName
     */
    public String getGroupIdPropertyName() {
        return groupIdPropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the group id
     *
     * @param groupIdPropertyName
     */
    public void setGroupIdPropertyName(String groupIdPropertyName) {
        this.groupIdPropertyName = groupIdPropertyName;
    }
}
