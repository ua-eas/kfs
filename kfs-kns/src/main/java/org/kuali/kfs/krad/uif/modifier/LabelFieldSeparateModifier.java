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
package org.kuali.kfs.krad.uif.modifier;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pulls <code>LabelField</code> instances out of a contained field so they will
 * be placed separately in the <code>LayoutManager</code>
 */
public class LabelFieldSeparateModifier extends ComponentModifierBase {
    private static final long serialVersionUID = -4304947796868636298L;

    public LabelFieldSeparateModifier() {
        super();
    }

    /**
     * Iterates through the <code>Group</code> items and if the label field is
     * not null and should be rendered, adds it to the new field list
     * immediately before the <code>Field</code> item the label applies to.
     * Finally the new list of components is set on the group
     *
     * @see ComponentModifier#performModification(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performModification(View view, Object model, Component component) {
        if ((component != null) && !(component instanceof Group)) {
            throw new IllegalArgumentException("Compare field initializer only support Group components, found type: "
                + component.getClass());
        }

        if (component == null) {
            return;
        }

        // list that will be built
        List<Component> groupFields = new ArrayList<Component>();

        Group group = (Group) component;
        for (Component item : group.getItems()) {
            if (item instanceof Field) {
                Field field = (Field) item;

                // pull out label field
                if (field.getLabelField() != null && field.getLabelField().isRender()) {
                    field.getLabelField().addStyleClass("displayWith-" + field.getId());
                    if (!field.isRender() && StringUtils.isBlank(field.getProgressiveRender())) {
                        field.getLabelField().setRender(false);
                    } else if (!field.isRender() && StringUtils.isNotBlank(field.getProgressiveRender())) {
                        field.getLabelField().setRender(true);
                        String prefixStyle = "";
                        if (StringUtils.isNotBlank(field.getLabelField().getStyle())) {
                            prefixStyle = field.getLabelField().getStyle();
                        }
                        field.getLabelField().setStyle(prefixStyle + ";" + "display: none;");
                    }

                    groupFields.add(field.getLabelField());

                    // set boolean to indicate label field should not be
                    // rendered with the attribute
                    field.setLabelFieldRendered(true);
                }
            }

            groupFields.add(item);
        }

        // update group
        group.setItems(groupFields);
    }

    /**
     * @see ComponentModifier#getSupportedComponents()
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> components = new HashSet<Class<? extends Component>>();
        components.add(Group.class);

        return components;
    }

}
