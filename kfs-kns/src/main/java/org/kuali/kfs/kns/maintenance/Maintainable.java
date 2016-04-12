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
package org.kuali.kfs.kns.maintenance;

import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.lookup.SelectiveReferenceRefresher;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Defines basic methods that all maintainable objects must provide
 */
@Deprecated
public interface Maintainable extends org.kuali.kfs.krad.maintenance.Maintainable, SelectiveReferenceRefresher {

    public String getDocumentTitle(MaintenanceDocument document);

    /**
     * Returns instance of the business object that is being maintained.
     */
    @Deprecated
    public PersistableBusinessObject getBusinessObject();

    /**
     * Called from a lookup return by the maintenance action.
     */
    @Deprecated
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document);

    /**
     * Sets an instance of a business object to be maintained.
     */
    @Deprecated
    public void setBusinessObject(PersistableBusinessObject object);

    @Deprecated
    public Class getBoClass();

    @Deprecated
    public void setBoClass(Class boClass);

    /**
     * This method will cause the Maintainable implementation to save/store the
     * relevant business object(s). This typically is called only after the
     * maint document has gone through state to final.
     */
    @Deprecated
    public void saveBusinessObject();

    @Deprecated
    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName,
            Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo);

    @Deprecated
    public List<String> getDuplicateIdentifierFieldsFromDataDictionary(String docTypeName, String collectionName);

    @Deprecated
    public List<String> getMultiValueIdentifierList(Collection maintCollection, List<String> duplicateIdentifierFields);

    @Deprecated
    public boolean hasBusinessObjectExisted(BusinessObject bo, List<String> existingIdentifierList,
            List<String> duplicateIdentifierFields);

    /**
     * Blanks out or sets the default of any value specified as restricted
     * within the {@link MaintenanceDocumentRestrictions} instance.
     *
     * This method should only be called if this maintainable represents the new
     * maintainable of the maintenance document.
     *
     * @param maintenanceDocumentRestrictions
     */
    @Deprecated
    public void clearBusinessObjectOfRestrictedValues(MaintenanceDocumentRestrictions maintenanceDocumentRestrictions);

    public boolean isBoNotesEnabled();

    /**
     * Gives chance to a maintainable object to prepare and return a
     * maintainable object which might be external to the system
     *
     * @return
     */
    @Deprecated
    public void prepareBusinessObject(BusinessObject businessObject);

    // 3070
    @Deprecated
    public void deleteBusinessObject();

    @Deprecated
    boolean isOldBusinessObjectInDocument();

    /**
     * Indicates whether inactive records for the given collection should be
     * display.
     *
     * @param collectionName - name of the collection (or sub-collection) to check inactive
     * record display setting
     * @return true if inactive records should be displayed, false otherwise
     */
    @Deprecated
    public boolean getShowInactiveRecords(String collectionName);

    /**
     * Returns the Map used to control the state of inactive record collection
     * display. Exposed for setting from the maintenance jsp.
     */
    @Deprecated
    public Map<String, Boolean> getInactiveRecordDisplay();

    /**
     * Indicates to maintainble whether or not inactive records should be
     * displayed for the given collection name.
     *
     * @param collectionName - name of the collection (or sub-collection) to set inactive
     * record display setting
     * @param showInactive - true to display inactive, false to not display inactive
     * records
     */
    @Deprecated
    public void setShowInactiveRecords(String collectionName, boolean showInactive);

    /**
     * Populates the new collection lines based on key/value pairs.
     *
     * @param fieldValues
     * @return
     */
    @Deprecated
    public Map<String, String> populateNewCollectionLines(Map<String, String> fieldValues,
            MaintenanceDocument maintenanceDocument, String methodToCall);

    /**
     * Gets the holder for the "add line" for a collection on the business
     * object
     *
     * @param collectionName
     * @return
     */
    @Deprecated
    public PersistableBusinessObject getNewCollectionLine(String collectionName);

    /**
     * Adds the new line for the given collection to the business object's
     * collection.
     *
     * @param collectionName
     */
    @Deprecated
    public void addNewLineToCollection(String collectionName);

    /**
     * KULRICE-4264 - a hook to change the state of the business object, which
     * is the "new line" of a collection, before it is validated
     *
     * @param colName
     * @param colClass
     * @param addBO
     */
    @Deprecated
    public void processBeforeAddLine(String colName, Class colClass, BusinessObject addBO);

    /**
     * Set default values.
     *
     * @param docTypeName
     */
    @Deprecated
    public void setGenerateDefaultValues(String docTypeName);

    /**
     * Set default values for blank required fields.
     *
     * @param docTypeName
     */
    @Deprecated
    public void setGenerateBlankRequiredValues(String docTypeName);

    /**
     * Returns a list of Section objects that specify how to render the view for
     * the maintenance object.
     *
     * @param oldMaintainable - If this is the new maintainable, the old is passed in for
     * reference. If it is the old maintainable, then null will be
     * passed in
     * @return
     */
    @Deprecated
    public List getSections(MaintenanceDocument maintenanceDocument, Maintainable oldMaintainable);

    /**
     * This method populates the business object based on key/value pairs.
     *
     * @param fieldValues
     * @param maintenanceDocument
     * @return
     */
    @Deprecated
    public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument,
            String methodToCall);

    /**
     * Returns a string that will be displayed as title on the maintenance
     * screen.
     */
    @Deprecated
    public String getMaintainableTitle();

    public void setupNewFromExisting(MaintenanceDocument document,
            Map<String, String[]> parameters);

    public void processAfterCopy(MaintenanceDocument document,
            Map<String, String[]> requestParameters);

    public void processAfterEdit(MaintenanceDocument document,
            Map<String, String[]> requestParameters);

    public void processAfterNew(MaintenanceDocument document,
            Map<String, String[]> requestParameters);

    public void processAfterPost(MaintenanceDocument document,
            Map<String, String[]> requestParameters);
}
