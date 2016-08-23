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
package org.kuali.kfs.krad.uif.view;

import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.field.ActionField;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.widget.Widget;
import org.kuali.kfs.krad.web.form.UifFormBase;

import java.util.Set;

/**
 * Configured for a <code>View</code> instance to provide conditional logic
 * based on any variable (view configuration, system parameters, ...) that does
 * not depend on the current user
 */
public interface ViewPresentationController {

    public Set<String> getActionFlags(View view, UifFormBase model);

    public Set<String> getEditModes(View view, UifFormBase model);

    /**
     * Determines if the the given view and data is allowed to be edited
     *
     * @param view  - view instance to check whether editing is allowed
     * @param model - object containing the view data
     * @return boolean true if editing on the view is allowed, false otherwise
     */
    public boolean canEditView(View view, ViewModel model);

    public boolean canEditField(View view, ViewModel model, Field field, String propertyName);

    public boolean canViewField(View view, ViewModel model, Field field, String propertyName);

    public boolean fieldIsRequired(View view, ViewModel model, Field field, String propertyName);

    public boolean canEditGroup(View view, ViewModel model, Group group, String groupId);

    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId);

    public boolean canEditWidget(View view, ViewModel model, Widget widget, String widgetId);

    public boolean canViewWidget(View view, ViewModel model, Widget widget, String widgetId);

    public boolean canPerformAction(View view, ViewModel model, ActionField actionField, String actionEvent,
                                    String actionId);

    public boolean canEditLine(View view, ViewModel model, CollectionGroup collectionGroup,
                               String collectionPropertyName, Object line);

    public boolean canViewLine(View view, ViewModel model, CollectionGroup collectionGroup,
                               String collectionPropertyName, Object line);

    public boolean canEditLineField(View view, ViewModel model, CollectionGroup collectionGroup,
                                    String collectionPropertyName, Object line, Field field, String propertyName);

    public boolean canViewLineField(View view, ViewModel model, CollectionGroup collectionGroup,
                                    String collectionPropertyName, Object line, Field field, String propertyName);

    public boolean canPerformLineAction(View view, ViewModel model, CollectionGroup collectionGroup,
                                        String collectionPropertyName, Object line, ActionField actionField, String actionEvent, String actionId);

}
