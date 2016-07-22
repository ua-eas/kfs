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
package org.kuali.kfs.kns.datadictionary.validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class provides legacy processing for maintenance documents in the dictionary validation service implementation. 
 * 
 * @deprecated As of release 2.0
 */
@Deprecated
public class MaintenanceDocumentAttributeValueReader extends DictionaryObjectAttributeValueReader {

	protected Map<String, Class<?>> attributeTypeMap;
	protected Map<String, Object> attributeValueMap;
	//protected Map<String, PropertyDescriptor> beanInfo;
	
	private final static Logger LOG = Logger.getLogger(MaintenanceDocumentAttributeValueReader.class);
	
	private List<Constrainable> attributeDefinitions;
	private Map<String, AttributeDefinition> attributeDefinitionMap;
	
	public MaintenanceDocumentAttributeValueReader(Object object, String entryName, MaintenanceDocumentEntry entry, PersistenceStructureService persistenceStructureService) {
		super(object, entryName, entry);
		
		//if (object != null)
		//	this.beanInfo = getBeanInfo(object.getClass());
		
		this.attributeTypeMap = new HashMap<String, Class<?>>();
		this.attributeValueMap = new HashMap<String, Object>();		
		
		this.attributeDefinitions = new LinkedList<Constrainable>();
		this.attributeDefinitionMap = new HashMap<String, AttributeDefinition>();
		for (MaintainableSectionDefinition sectionDefinition : entry.getMaintainableSections()) {
			List<? extends MaintainableItemDefinition> itemDefinitions = sectionDefinition.getMaintainableItems();
			
			for (MaintainableItemDefinition itemDefinition : itemDefinitions) {
				if (itemDefinition instanceof MaintainableFieldDefinition) {
					String itemDefinitionName = itemDefinition.getName();
					AttributeDefinition attributeDefinition = KRADServiceLocatorWeb.getDataDictionaryService().getAttributeDefinition(object.getClass().getName(), itemDefinitionName);
						
						//entry.getAttributeDefinition(attributeName);
					boolean isAttributeDefined = attributeDefinition != null;
						//getDataDictionaryService().isAttributeDefined(businessObject.getClass(), itemDefinition.getName());
			        if (isAttributeDefined) {
			        	attributeDefinitions.add(attributeDefinition);
			        	attributeDefinitionMap.put(itemDefinitionName, attributeDefinition);
                        LOG.info("itemDefName: " + itemDefinitionName);

						try {
                            Object  attributeValue = PropertyUtils.getNestedProperty(object, itemDefinitionName);

							if (attributeValue != null && StringUtils.isNotBlank(attributeValue.toString())) {
				    			Class<?> propertyType = ObjectUtils.getPropertyType(object, itemDefinitionName, persistenceStructureService);
				    			attributeTypeMap.put(itemDefinitionName, propertyType);
                                if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) ||
                                        TypeUtils.isDecimalClass(propertyType) ||
                                        TypeUtils.isTemporalClass(propertyType) ||
                                        TypeUtils.isBooleanClass(propertyType)) {
                                    // check value format against dictionary
                                    if (!TypeUtils.isTemporalClass(propertyType)) {
                                        attributeValueMap.put(itemDefinitionName, attributeValue);
                                    }
                                }
				    		}
						} catch (IllegalArgumentException e) {
							LOG.warn("Failed to invoke read method on object when looking for " + itemDefinitionName + " as a field of " + entry.getDocumentTypeName(), e);
						} catch (IllegalAccessException e) {
							LOG.warn("Failed to invoke read method on object when looking for " + itemDefinitionName + " as a field of " + entry.getDocumentTypeName(), e);
						} catch (InvocationTargetException e) {
							LOG.warn("Failed to invoke read method on object when looking for " + itemDefinitionName + " as a field of " + entry.getDocumentTypeName(), e);						
			        	} catch (NoSuchMethodException e) {
                            LOG.warn("Failed to find property description on object when looking for " + itemDefinitionName + " as a field of " + entry.getDocumentTypeName(), e);
                        }
			    		
			        }
				}
			}
		}
	}
	
	/**
	 * @see AttributeValueReader#getDefinition(java.lang.String)
	 */
	@Override
	public Constrainable getDefinition(String attributeName) {
		return attributeDefinitionMap != null ? attributeDefinitionMap.get(attributeName) : null;
	}

	/**
	 * @see AttributeValueReader#getDefinitions()
	 */
	@Override
	public List<Constrainable> getDefinitions() {
		return attributeDefinitions;
	}

	@Override
	public String getLabel(String attributeName) {
		AttributeDefinition attributeDefinition = attributeDefinitionMap != null ? attributeDefinitionMap.get(attributeName) : null;
		return attributeDefinition != null ? attributeDefinition.getLabel()  : attributeName;
	}
	
	/**
	 * @see AttributeValueReader#getType(java.lang.String)
	 */
	@Override
	public Class<?> getType(String attributeName) {
		return attributeTypeMap != null ? attributeTypeMap.get(attributeName) : null;
	}

	/**
	 * @see AttributeValueReader#getValue(java.lang.String)
	 */
	@Override
	public <X> X getValue(String attributeName) throws AttributeValidationException {
		return (X) attributeValueMap.get(attributeName);
	}

	//private Map<String, PropertyDescriptor> getBeanInfo(Class<?> clazz) {
	//	final Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
	//	for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(clazz)) {
	//		properties.put(propertyDescriptor.getName(), propertyDescriptor);
	//	}
	//	return properties;
	//}
	
}
