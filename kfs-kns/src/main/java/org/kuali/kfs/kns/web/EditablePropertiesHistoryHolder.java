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
package org.kuali.kfs.kns.web;

import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;


/**
 * A class which will hold a Map of editable properties, dropping editable properties when too many
 * are filled in.
 */
@Deprecated
public class EditablePropertiesHistoryHolder implements java.io.Serializable {
    private Map<String, Set<String>> editablePropertiesMap;
    private Integer maxLength = null;
    private Queue<String> historyOrder;
    private static final String EDITABLE_PROPERTIES_HISTORY_SIZE_PROPERTY_NAME = "kns.editable.properties.history.size";
    private transient ConfigurationService configurationService;

    /**
     * Constructs the EditablePropertiesHistoryHolder
     */
    public EditablePropertiesHistoryHolder() {
        editablePropertiesMap = new HashMap<String, Set<String>>();
        historyOrder = new LinkedList<String>();
    }

    /**
     * @return the maximum length of the history that this will hold
     */
    public int getMaxHistoryLength() {
        if (maxLength == null) {
            final String historyLengthAsString = getConfigurationService().getPropertyValueAsString(
                EditablePropertiesHistoryHolder.EDITABLE_PROPERTIES_HISTORY_SIZE_PROPERTY_NAME);
            if (historyLengthAsString == null) {
                maxLength = new Integer(20);
            } else {
                try {
                    maxLength = new Integer(historyLengthAsString);
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException("Cannot convert property " + EditablePropertiesHistoryHolder.EDITABLE_PROPERTIES_HISTORY_SIZE_PROPERTY_NAME + " with value " + historyLengthAsString + " to integer", nfe);
                }
            }
        }
        return maxLength.intValue();
    }

    /**
     * Adds a Set of editable property names to the history, keyed with the given guid String.  If the editable properties exceeds the buffer size,
     * the earliest editable properties will be bumped
     *
     * @param editableProperties the Set of editable property names to save in the history
     * @return a String to act as a key (or guid) to the editable properties
     */
    public String addEditablePropertiesToHistory(Set<String> editableProperties) {
        String guid = generateNewGuid();

        if (getHistoryOrder().size() > getMaxHistoryLength()) {
            final String guidForRemoval = getHistoryOrder().remove();
            getEditablePropertiesMap().remove(guidForRemoval);
        }
        getHistoryOrder().add(guid);
        getEditablePropertiesMap().put(guid, editableProperties);

        return guid;
    }

    /**
     * @return a newly generated Guid to act as a key to an editable properties Set
     */
    public String generateNewGuid() {
        final String guid = UUID.randomUUID().toString();
        return guid;
    }

    /**
     * Returns the editable properties registered with the current guid
     *
     * @param guid the guid to find editable properties for
     * @return a Set<String> of editable properties
     */
    public Set<String> getEditableProperties(String guid) {
        return getEditablePropertiesMap().get(guid);
    }

    /**
     * Clears out the editable properties associated with the given guid
     *
     * @param guid the guid to clear out editable properties for
     */
    public void clearEditableProperties(String guid) {
        getEditablePropertiesMap().put(guid, createNewEditablePropertiesEntry());
    }

    /**
     * @return the order of the entries as they chronologically were created
     */
    protected Queue<String> getHistoryOrder() {
        return historyOrder;
    }

    /**
     * @return the Map which associates editable property guids with Sets of editable property names
     */
    protected Map<String, Set<String>> getEditablePropertiesMap() {
        return editablePropertiesMap;
    }

    /**
     * @return a new Entry to hold the names of editable properties
     */
    protected Set<String> createNewEditablePropertiesEntry() {
        return new HashSet<String>();
    }

    /**
     * @return an implementation of the ConfigurationService
     */
    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = KRADServiceLocator.getKualiConfigurationService();
        }
        return configurationService;
    }
}
