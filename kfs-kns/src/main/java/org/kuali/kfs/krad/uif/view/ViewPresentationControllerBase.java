/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link ViewPresentationController} that implements no logic by default
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewPresentationControllerBase implements ViewPresentationController, Serializable {
    private static final long serialVersionUID = -3199587372204398503L;

    /**
     * @see ViewPresentationController#getActionFlags(View,
     * UifFormBase)
     */
    public Set<String> getActionFlags(View view, UifFormBase model) {
        return new HashSet<String>();
    }

    /**
     * @see ViewPresentationController#getEditModes(View,
     * UifFormBase)
     */
    public Set<String> getEditModes(View view, UifFormBase model) {
        return new HashSet<String>();
    }

    /**
     * @see ViewPresentationController#canEditView(View, ViewModel)
     */
    public boolean canEditView(View view, ViewModel model) {
        return true;
    }

    /**
     * @see ViewPresentationController#canEditField(View,
     * ViewModel, Field, java.lang.String)
     */
    public boolean canEditField(View view, ViewModel model, Field field, String propertyName) {
        return true;
    }

    /**
     * @see ViewPresentationController#canViewField(View,
     * ViewModel, Field, java.lang.String)
     */
    public boolean canViewField(View view, ViewModel model, Field field, String propertyName) {
        return true;
    }

    /**
     * @see ViewPresentationController#fieldIsRequired(View,
     * ViewModel, Field, java.lang.String)
     */
    public boolean fieldIsRequired(View view, ViewModel model, Field field, String propertyName) {
        return false;
    }

    /**
     * @see ViewPresentationController#canEditGroup(View,
     * ViewModel, Group, java.lang.String)
     */
    public boolean canEditGroup(View view, ViewModel model, Group group, String groupId) {
        return true;
    }

    /**
     * @see ViewPresentationController#canViewGroup(View,
     * ViewModel, Group, java.lang.String)
     */
    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId) {
        return true;
    }

    /**
     * @see ViewPresentationController#canEditWidget(View,
     * ViewModel, Widget, java.lang.String)
     */
    public boolean canEditWidget(View view, ViewModel model, Widget widget, String widgetId) {
        return true;
    }

    /**
     * @see ViewPresentationController#canViewWidget(View,
     * ViewModel, Widget, java.lang.String)
     */
    public boolean canViewWidget(View view, ViewModel model, Widget widget, String widgetId) {
        return true;
    }

    /**
     * @see ViewPresentationController#canPerformAction(View,
     * ViewModel, ActionField, java.lang.String,
     * java.lang.String)
     */
    public boolean canPerformAction(View view, ViewModel model, ActionField actionField, String actionEvent,
            String actionId) {
        return true;
    }

    /**
     * @see ViewPresentationController#canEditLine(View,
     * ViewModel, CollectionGroup,
     * java.lang.String, java.lang.Object)
     */
    public boolean canEditLine(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line) {
        return true;
    }

    /**
     * @see ViewPresentationController#canViewLine(View,
     * ViewModel, CollectionGroup,
     * java.lang.String, java.lang.Object)
     */
    public boolean canViewLine(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line) {
        return true;
    }

    /**
     * @see ViewPresentationController#canEditLineField(View,
     * ViewModel, CollectionGroup,
     * java.lang.String, java.lang.Object, Field, java.lang.String)
     */
    public boolean canEditLineField(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Field field, String propertyName) {
        return true;
    }

    /**
     * @see ViewPresentationController#canViewLineField(View,
     * ViewModel, CollectionGroup,
     * java.lang.String, java.lang.Object, Field, java.lang.String)
     */
    public boolean canViewLineField(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Field field, String propertyName) {
        return true;
    }

    /**
     * @see ViewPresentationController#canPerformLineAction(View,
     * ViewModel, CollectionGroup,
     * java.lang.String, java.lang.Object, ActionField, java.lang.String,
     * java.lang.String)
     */
    public boolean canPerformLineAction(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, ActionField actionField, String actionEvent, String actionId) {
        return true;
    }

}
