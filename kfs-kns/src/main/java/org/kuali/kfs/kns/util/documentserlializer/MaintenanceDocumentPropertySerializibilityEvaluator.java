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
package org.kuali.kfs.kns.util.documentserlializer;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableSubSectionHeaderDefinition;
import org.kuali.kfs.krad.document.Document;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.kfs.krad.util.documentserializer.PropertySerializabilityEvaluatorBase;
import org.kuali.kfs.krad.util.documentserializer.PropertySerializerTrie;
import org.kuali.kfs.krad.util.documentserializer.PropertyType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MaintenanceDocumentPropertySerializibilityEvaluator
				extends PropertySerializabilityEvaluatorBase implements PropertySerializabilityEvaluator {

    /**
     * Reads the data dictionary to determine which properties of the document should be serialized.
     *
     * @see PropertySerializabilityEvaluator#initializeEvaluator(Document)
     */
	@Override
    public void initializeEvaluatorForDataObject(Object businessObject){
        DataDictionary dictionary = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary();
        MaintenanceDocumentEntry maintenanceDocumentEntry = (MaintenanceDocumentEntry)
        	dictionary.getMaintenanceDocumentEntryForBusinessObjectClass(businessObject.getClass());
        serializableProperties = new PropertySerializerTrie();
        populateSerializableProperties(maintenanceDocumentEntry.getMaintainableSections());
        serializableProperties.addSerializablePropertyName("boNotes", true);
        serializableProperties.addSerializablePropertyName("boNotes.attachment", true);
    }

    /**
     * @see PropertySerializabilityEvaluator#determinePropertyType(java.lang.Object)
     */
    @Override
    public PropertyType determinePropertyType(Object propertyValue) {
        if (propertyValue == null) {
            return PropertyType.PRIMITIVE;
        }

        if (propertyValue instanceof BusinessObject) {
            return PropertyType.BUSINESS_OBJECT;
        }

        if (propertyValue instanceof Collection) {
            return PropertyType.COLLECTION;
        }

        // In the case of Maintenance Documents treat Maps as PRIMITIVE
        if (propertyValue instanceof Map) {
            return PropertyType.PRIMITIVE;
        }

        return PropertyType.PRIMITIVE;
    }

    private void populateSerializableProperties(List<MaintainableSectionDefinition> maintainableSectionDefinitions){
        for(MaintainableSectionDefinition maintainableSectionDefinition: maintainableSectionDefinitions){
        	populateSerializablePropertiesWithItems("", maintainableSectionDefinition.getMaintainableItems());
        }
    }

    private void populateSerializablePropertiesWithItems(String basePath, List<MaintainableItemDefinition> maintainableItems){
    	for(MaintainableItemDefinition maintainableItemDefinition: maintainableItems){
            if(maintainableItemDefinition instanceof MaintainableFieldDefinition){
                serializableProperties.addSerializablePropertyName(getFullItemName(basePath, maintainableItemDefinition.getName()), true);
            } else if(maintainableItemDefinition instanceof MaintainableCollectionDefinition){
            	serializableProperties.addSerializablePropertyName(getFullItemName(basePath, maintainableItemDefinition.getName()), true);
            	populateSerializablePropertiesWithItems(getFullItemName(basePath, maintainableItemDefinition.getName()),
            			getAllMaintainableFieldDefinitionsForSerialization(
            			(MaintainableCollectionDefinition)maintainableItemDefinition));
            } else if(maintainableItemDefinition instanceof MaintainableSubSectionHeaderDefinition){
            	//Ignore
            }
    	}
    }

    private String getFullItemName(String basePath, String itemName){
    	return StringUtils.isEmpty(basePath) ? itemName : basePath+"."+itemName;
    }

    public List<MaintainableItemDefinition> getAllMaintainableFieldDefinitionsForSerialization(
    		MaintainableCollectionDefinition maintainableCollectionDefinition){
		List<MaintainableItemDefinition> allMaintainableItemDefinitions = new ArrayList<MaintainableItemDefinition>();

		if(maintainableCollectionDefinition.getMaintainableFields()!=null){
			allMaintainableItemDefinitions.addAll(maintainableCollectionDefinition.getMaintainableFields());
		}

		if(maintainableCollectionDefinition.getSummaryFields()!=null){
			allMaintainableItemDefinitions.addAll(
					(List<MaintainableFieldDefinition>)maintainableCollectionDefinition.getSummaryFields());
		}

		if(maintainableCollectionDefinition.getDuplicateIdentificationFields()!=null){
			allMaintainableItemDefinitions.addAll(maintainableCollectionDefinition.getDuplicateIdentificationFields());
		}

		/*if(maintainableCollectionMap!=null){
			updateMaintainableCollectionDefinitionForSerialization(maintainableCollectionMap.values());
			allMaintainableItemDefinitions.addAll(maintainableCollectionMap.values());
		}*/
		if(maintainableCollectionDefinition.getMaintainableCollections()!=null){
			allMaintainableItemDefinitions.addAll(maintainableCollectionDefinition.getMaintainableCollections());
		}

		return allMaintainableItemDefinitions;
	}

}
