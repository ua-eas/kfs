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

import org.kuali.kfs.krad.uif.component.ComponentSecurity;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.field.ActionField;
import org.kuali.kfs.krad.uif.field.DataField;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.widget.Widget;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.kuali.rice.kim.api.identity.Person;

import java.util.Set;

/**
 * Performs user based authorization for actions and components contained in a {@link View}
 * <p>
 * <p>
 * Note only user authorization is done by the authorizer class. For non-user based logic, use the
 * {@link ViewPresentationController}
 * </p>
 */
public interface ViewAuthorizer {

    /**
     * Returns the set of action flags that are authorized for the given user
     * <p>
     * <p>
     * Action flags are created for views to indicate some action or feature should be enabled. These flags can be
     * used within expressions for configuring the view content.
     * <p>
     * For example:
     * <bean parent="ActionField" p:methodToCall="save" p:actionLabel="save"
     * p:render="@{#actionFlags[#Constants.KUALI_ACTION_CAN_SAVE]}"/>
     * </p>
     * <p>
     * <p>
     * For each action flag, KIM is consulted to determine if a permission exist for the template associated with
     * the action flag. If so, a check is then made to determine if the user has that permission. If the permission
     * fails for the user, the action flag is removed from the returned set.
     * </p>
     * <p>
     * <p>
     * The Set of available action flags should first be exported by the
     * {@link ViewPresentationController#getActionFlags(View, UifFormBase)} method. The
     * set returned from this method will be passed as the method argument here by the framework.
     * </p>
     *
     * @param view    - view instance the action flags apply to
     * @param model   - object containing the view data
     * @param user    - user we are authorizing the actions for
     * @param actions - set of action flags to authorize
     * @return Set<String> set of action flags that have been authorized, this will be equal to or a subset of the
     * actions passed in
     */
    public Set<String> getActionFlags(View view, ViewModel model, Person user, Set<String> actions);

    /**
     * Returns the set of edit modes that are authorized for the given user
     * <p>
     * <p>
     * An edit mode is a string that identifies a set of editable fields within the view. These are generally used
     * when the entire view is not editable, but only certain fields. A field can be associated with an edit mode in
     * two ways. The first is by using the edit mode in an expression when setting the field readOnly property.
     * <p>
     * For example:
     * <property name="readOnly" value="@{!#editModes['specialEdit'] and !fullEdit}" />
     * <p>
     * The second way is with the
     * {@link ViewPresentationController#canEditField(View, ViewModel, Field, String)}
     * method which can look at the edit modes map on the view to determine if the given field should be editable.
     * </p>
     * <p>
     * <p>
     * For each edit mode, KIM is consulted to determine if a permission exist for the 'Use View' template and
     * the edit mode detail. If so, a check is then made to determine if the user has that permission. If the
     * permission
     * fails for the user, the edit mode is removed from the returned set.
     * </p>
     * <p>
     * <p>
     * The Set of available edit modes should first be exported by the
     * {@link ViewPresentationController#getEditModes(View, UifFormBase)} method. The
     * set returned from this method will be passed as the method argument here by the framework.
     * </p>
     *
     * @param view      - view instance the edit modes apply to
     * @param model     - object containing the view data
     * @param user      - user we are authorizing the actions for
     * @param editModes - set of edit modes to authorize
     * @return Set<String> set of edit modes that have been authorized, this will be equal to or a subset of the
     * edit mode set passed in
     */
    public Set<String> getEditModes(View view, ViewModel model, Person user, Set<String> editModes);

    /**
     * Determines if the given user is authorized to open the given view
     *
     * @param view  - view instance to check authorization for
     * @param model - object containing the view data
     * @param user  - user to authorize
     * @return boolean true if the user is authorized to open the view, false otherwise
     */
    public boolean canOpenView(View view, ViewModel model, Person user);

    /**
     * Determines if the given user is authorized to edit the given view
     *
     * @param view  - view instance to check authorization for
     * @param model - object containing the view data
     * @param user  - user to authorize
     * @return boolean true if the user is authorized to edit the view, false otherwise
     */
    public boolean canEditView(View view, ViewModel model, Person user);

    /**
     * Checks whether the mask authorization exists for the given property and if so whether the given user has the
     * ability to unmask the value
     *
     * @param view         - view instance the field belongs to
     * @param model        - object containing the view data
     * @param field        - field associated for the property and from which the
     *                     {@link ComponentSecurity} will be retrieved
     * @param propertyName - name of the property associated with the field
     * @param user         - user we are authorizing
     * @return boolean true if the value can be unmasked, false if it should be masked
     */
    public boolean canUnmaskField(View view, ViewModel model, DataField field, String propertyName, Person user);

    /**
     * Checks whether the partial mask authorization exists for the given property and if so whether the given user
     * has the ability to unmask the value
     *
     * @param view         - view instance the field belongs to
     * @param model        - object containing the view data
     * @param field        - field associated for the property and from which the
     *                     {@link ComponentSecurity} will be retrieved
     * @param propertyName - name of the property associated with the field
     * @param user         - user we are authorizing
     * @return boolean true if the value can be unmasked, false if it should be partially masked
     */
    public boolean canPartialUnmaskField(View view, ViewModel model, DataField field, String propertyName, Person user);

    public boolean canEditField(View view, ViewModel model, Field field, String propertyName, Person user);

    public boolean canViewField(View view, ViewModel model, Field field, String propertyName, Person user);

    public boolean canEditGroup(View view, ViewModel model, Group group, String groupId, Person user);

    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId, Person user);

    public boolean canEditWidget(View view, ViewModel model, Widget widget, String widgetId, Person user);

    public boolean canViewWidget(View view, ViewModel model, Widget widget, String widgetId, Person user);

    public boolean canPerformAction(View view, ViewModel model, ActionField actionField, String actionEvent,
                                    String actionId, Person user);

    public boolean canEditLine(View view, ViewModel model, CollectionGroup collectionGroup,
                               String collectionPropertyName, Object line, Person user);

    public boolean canViewLine(View view, ViewModel model, CollectionGroup collectionGroup,
                               String collectionPropertyName, Object line, Person user);

    public boolean canEditLineField(View view, ViewModel model, CollectionGroup collectionGroup,
                                    String collectionPropertyName, Object line, Field field, String propertyName, Person user);

    public boolean canViewLineField(View view, ViewModel model, CollectionGroup collectionGroup,
                                    String collectionPropertyName, Object line, Field field, String propertyName, Person user);

    public boolean canPerformLineAction(View view, ViewModel model, CollectionGroup collectionGroup,
                                        String collectionPropertyName, Object line, ActionField actionField, String actionEvent, String actionId,
                                        Person user);

}
