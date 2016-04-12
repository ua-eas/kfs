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
package org.kuali.kfs.krad.uif.util;

import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.PropertyReplacer;
import org.kuali.kfs.krad.uif.container.CollectionFilter;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.container.Container;
import org.kuali.kfs.krad.uif.container.PageGroup;
import org.kuali.kfs.krad.uif.field.ActionField;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.modifier.ComponentModifier;
import org.kuali.kfs.krad.uif.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for trimming component instances for storage
 *
 * <p>
 * Invoked to trim the view instance before storing on the form as the post view. Certain information is keep
 * around to support post methods that need to operate on the previous view configuration. Examples include component
 * refresh and collection add/delete line.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewCleaner {

    /**
     * Cleans a view instance removing all pages except the current page and then invoking the view
     * index to perform cleaning on contained components
     *
     * @param view - view instance to clean
     */
    public static void cleanView(View view) {
        view.setApplicationHeader(null);
        view.setApplicationFooter(null);
        view.setNavigation(null);
        view.setPage(null);
        view.setViewMenuLink(null);
        view.setClientSideState(new HashMap<String, Object>());

        // clear all view pages exception the current page
        PageGroup currentPage = view.getCurrentPage();

        List<Component> pages = new ArrayList<Component>();
        pages.add(currentPage);
        view.setItems(pages);

        cleanContainer(view);

        view.getViewIndex().clearIndexesAfterRender();
    }

    /**
     * Cleans a collection group instance removing the items and collection prototypes (note add line fields
     * are keep around to support the add line action)
     *
     * @param collectionGroup - collection group instance to clean
     */
    public static void cleanCollectionGroup(CollectionGroup collectionGroup) {
        collectionGroup.setAddLineLabelField(null);
        collectionGroup.setAddLineActionFields(new ArrayList<ActionField>());
        collectionGroup.setActionFields(new ArrayList<ActionField>());
        collectionGroup.setSubCollections(new ArrayList<CollectionGroup>());
        collectionGroup.setActiveCollectionFilter(null);
        collectionGroup.setFilters(new ArrayList<CollectionFilter>());

        cleanContainer(collectionGroup);
    }

    /**
     * General purpose method to clean any container, removes all nested components except the items list
     *
     * @param container - container instance to clean
     */
    public static void cleanContainer(Container container) {
        container.setHeader(null);
        container.setFooter(null);
        container.setHelp(null);
        container.setLayoutManager(null);
        container.setInstructionalMessageField(null);
        container.setComponentOptions(new HashMap<String, String>());
        container.setComponentModifiers(new ArrayList<ComponentModifier>());
        container.setPropertyReplacers(new ArrayList<PropertyReplacer>());
    }

    /**
     * Cleans an input field instance removing the control and inherited component properties
     *
     * @param inputField - input field instance to clean
     */
    public static void cleanInputField(InputField inputField) {
        inputField.setControl(null);
        inputField.setInstructionalMessageField(null);
        inputField.setConstraintMessageField(null);
        inputField.setFieldDirectInquiry(null);
        inputField.setFieldInquiry(null);
        inputField.setLabelField(null);
        inputField.setComponentOptions(new HashMap<String, String>());
        inputField.setComponentModifiers(new ArrayList<ComponentModifier>());
        inputField.setPropertyReplacers(new ArrayList<PropertyReplacer>());
    }
}
