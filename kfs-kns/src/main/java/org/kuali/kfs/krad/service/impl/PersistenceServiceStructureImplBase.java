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
package org.kuali.kfs.krad.service.impl;

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.ObjectReferenceDescriptor;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.persistence.ojb.BaseOjbConfigurer;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.kfs.krad.exception.ClassNotPersistableException;

import java.util.ArrayList;
import java.util.List;

public class PersistenceServiceStructureImplBase {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersistenceServiceStructureImplBase.class);
	private DescriptorRepository descriptorRepository;

	/**
	 * Constructs a PersistenceServiceImpl instance.
	 */
	public PersistenceServiceStructureImplBase() {
		String ojbPropertyFileLocation = ConfigContext.getCurrentContextConfig().getProperty(BaseOjbConfigurer.RICE_OJB_PROPERTIES_PARAM);
        String currentValue = System.getProperty(BaseOjbConfigurer.OJB_PROPERTIES_PROP);
		try {
			System.setProperty(BaseOjbConfigurer.OJB_PROPERTIES_PROP, ojbPropertyFileLocation);
			org.apache.ojb.broker.metadata.MetadataManager metadataManager = org.apache.ojb.broker.metadata.MetadataManager.getInstance();
			descriptorRepository = metadataManager.getGlobalRepository();
	    } finally {
	        if (currentValue == null) {
	            System.getProperties().remove(BaseOjbConfigurer.OJB_PROPERTIES_PROP);
	        } else {
	            System.setProperty(BaseOjbConfigurer.OJB_PROPERTIES_PROP, currentValue);
	        }
	    }
	}

	/**
	 * @return DescriptorRepository containing everything OJB knows about persistable classes
	 */
	protected DescriptorRepository getDescriptorRepository() {
		return descriptorRepository;
	}

	/**
	 *
	 * This method returns a list of primary key field names.  This method uses the CacheNoCopy caching method.
	 * "NoCopy" is the faster of the caching annotations, but because it does not make a copy the list that is
	 * returned must not be modified.  To enforce this the returned list is wrapped in a Collections.unmodifiableList
	 * method.  This will cause an exception to be thrown if the list is altered.
	 *
	 * @param clazz
	 * @return unmodifiableList of field names.  Any attempt to alter list will result in an UnsupportedOperationException
	 */
	public List listPrimaryKeyFieldNames(Class clazz) {
	   	// Legacy OJB
		List fieldNamesLegacy = new ArrayList();
		ClassDescriptor classDescriptor = getClassDescriptor(clazz);
		FieldDescriptor keyDescriptors[] = classDescriptor.getPkFields();

		for (int i = 0; i < keyDescriptors.length; ++i) {
			FieldDescriptor keyDescriptor = keyDescriptors[i];
			fieldNamesLegacy.add(keyDescriptor.getAttributeName());
		}
		return fieldNamesLegacy;
	}

	/* Not used anywhere... need to check KFS and batch stuff */
	/**
	 * @param classDescriptor
	 * @return name of the database table associated with given classDescriptor,
	 *         stripped of its leading schemaName
	 */
	/*
	@CacheNoCopy
	protected String getTableName(ClassDescriptor classDescriptor) {
		String schemaName = classDescriptor.getSchema();
		String fullTableName = classDescriptor.getFullTableName();

		String tableName = null;
		if (StringUtils.isNotBlank(schemaName)) {
			tableName = StringUtils.substringAfter(fullTableName, schemaName + ".");
		}
		if (StringUtils.isBlank(tableName)) {
			tableName = fullTableName;
		}

		return tableName;
	}
	*/

	/**
	 * @param persistableClass
	 * @return ClassDescriptor for the given Class
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 * @throws ClassNotPersistableException
	 *             if the given Class is unknown to OJB
	 */
	// Legacy OJB - no need for JPA equivalent
	protected ClassDescriptor getClassDescriptor(Class persistableClass) {
		if (persistableClass == null) {
			throw new IllegalArgumentException("invalid (null) object");
		}

		ClassDescriptor classDescriptor = null;
		DescriptorRepository globalRepository = getDescriptorRepository();
		try {
			classDescriptor = globalRepository.getDescriptorFor(persistableClass);
		} catch (ClassNotPersistenceCapableException e) {
			throw new ClassNotPersistableException("class '" + persistableClass.getName() + "' is not persistable", e);
		}

		return classDescriptor;
	}

	/**
	 * @see PersistenceStructureService#getBusinessObjectAttributeClass(java.lang.Class,
	 *      java.lang.String)
	 */
	public Class<? extends PersistableBusinessObjectExtension> getBusinessObjectAttributeClass(Class<? extends PersistableBusinessObject> clazz, String attributeName) {
		String baseAttributeName = attributeName;
		String subAttributeString = null;
		if (attributeName.contains(".")) {
			baseAttributeName = attributeName.substring(0, attributeName.indexOf('.'));
			subAttributeString = attributeName.substring(attributeName.indexOf('.') + 1);
		}

	   	// Legacy OJB
		Class attributeClassLegacy = null;
        ClassDescriptor classDescriptor = null;
        try{
		   classDescriptor = this.getClassDescriptor(clazz);
        }catch (ClassNotPersistableException e){
           LOG.warn("Class descriptor for "+ clazz.getName() +"was not found");
        }

		ObjectReferenceDescriptor refDescriptor = null;
        if(classDescriptor != null){
    	    refDescriptor = classDescriptor.getObjectReferenceDescriptorByName(baseAttributeName);
        }

		if (refDescriptor != null) {
			attributeClassLegacy = refDescriptor.getItemClass();
		}

		// recurse if necessary
		if (subAttributeString != null) {
			attributeClassLegacy = getBusinessObjectAttributeClass(attributeClassLegacy, subAttributeString);
		}

		return attributeClassLegacy;
	}

}
