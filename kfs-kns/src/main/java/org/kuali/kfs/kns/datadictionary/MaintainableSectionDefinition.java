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
package org.kuali.kfs.kns.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.kfs.krad.datadictionary.DataDictionaryException;

import java.util.ArrayList;
import java.util.List;

/**
 * The maintainableSection element defines one section of the
 * maintenance document.
 * <p>
 * JSTL: maintainableSection is a Map which is accessed by an
 * integer representing the sequential occurrence of the section.
 * e.g. "0", "1", etc.  This map contains entries with the following
 * keys:
 * index (String) - e.g. "0" for first section, etc.
 * title (String)
 * maintainableItems (Map)
 */
@Deprecated
public class MaintainableSectionDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = -8615694293159113523L;

    protected String title;

    protected List<MaintainableItemDefinition> maintainableItems = new ArrayList<MaintainableItemDefinition>();

    protected boolean hidden = false;

    protected boolean defaultOpen = true;

    protected String helpUrl;

    public MaintainableSectionDefinition() {
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Default the ID to the title for now.
     *
     * @see DataDictionaryDefinitionBase#getId()
     */
    @Override
    public String getId() {
        if (StringUtils.isBlank(id)) {
            return title;
        }
        return id;
    }


    /**
     * Sets title of the Section.
     *
     * @throws IllegalArgumentException if the given title is blank
     */
    public void setTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("invalid (blank) title");
        }

        this.title = title;
    }

    /**
     * @return Collection of all MaintainableFieldDefinitions associated with this MaintainableSection, in the order in which they
     * were added
     */
    public List<MaintainableItemDefinition> getMaintainableItems() {
        return maintainableItems;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (StringUtils.contains(title, ",") && StringUtils.isBlank(id)) {
            throw new DataDictionaryException("The title for maintainable section \"" + title + "\" for class " + rootBusinessObjectClass.getName() +
                " contains a comma.  In this case, the id property must be defined and it may not contain a comma");
        }
        if (StringUtils.contains(id, ",")) {
            throw new DataDictionaryException("The id for maintainable section \"" + id + "\" for class " + rootBusinessObjectClass.getName() +
                " contains a comma, which is not allowed.");
        }
        for (MaintainableItemDefinition maintainableItem : maintainableItems) {
            maintainableItem.completeValidation(rootBusinessObjectClass, null);
        }
    }

    public String toString() {
        return "MaintainableSectionDefinition '" + getTitle() + "'";
    }


    public boolean isHidden() {
        return this.hidden;
    }


    /**
     * Whether to hide the entire section, tab and all.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    /**
     * The maintainableItems element defines the components of a
     * section.  These may include fields, sub-section headers,
     * and fields.
     * <p>
     * JSTL: maintainableItems is a Map which is accessed by a
     * key of "maintainableItems".  This map contains entries with
     * the following keys:
     * name of first item in the section
     * name of second item in the section
     * etc.
     * The corresponding value is an ExportMap which is dependent
     * upon the type of the item as follows:
     * <p>
     * subSectionHeader ExportMap
     * In this case, the ExportMap contains the following
     * keys and values:
     * *Key**     **Value**
     * name        name of subSectionHeader
     * <p>
     * maintainableField ExportMap
     * In this case, the ExportMap contains the following
     * keys and values:
     * *Key**     **Value**
     * field       true
     * name        name of maintainableField
     * required    true or false
     * <p>
     * maintainableCollection ExportMap
     * In this case, the ExportMap contains the following
     * keys and values:
     * *Key**                **Value**
     * collection             true
     * name                   name of collection
     * dataObjectClass    name of collection class
     */
    public void setMaintainableItems(List<MaintainableItemDefinition> maintainableItems) {
        for (MaintainableItemDefinition maintainableItem : maintainableItems) {
            if (maintainableItem == null) {
                throw new IllegalArgumentException("invalid (null) maintainableItem");
            }
        }

        this.maintainableItems = maintainableItems;
    }

    /**
     * @return the defaultOpen
     */
    public boolean isDefaultOpen() {
        return this.defaultOpen;
    }

    /**
     * @param defaultOpen the defaultOpen to set
     */
    public void setDefaultOpen(boolean defaultOpen) {
        this.defaultOpen = defaultOpen;
    }

    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }

}
