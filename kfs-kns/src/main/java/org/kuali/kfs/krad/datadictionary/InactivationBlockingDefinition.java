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
package org.kuali.kfs.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * 
 *
 */
public class InactivationBlockingDefinition extends DataDictionaryDefinitionBase implements InactivationBlockingMetadata {
	private static final long serialVersionUID = -8765429636173190984L;
	
	protected  Class<? extends BusinessObject> blockingReferenceBusinessObjectClass;
    protected  String blockedReferencePropertyName;
    protected  Class<? extends BusinessObject> blockedBusinessObjectClass;
    protected  String inactivationBlockingDetectionServiceBeanName;
    protected  String relationshipLabel;
    protected Class<? extends BusinessObject> businessObjectClass;

    public InactivationBlockingDefinition() {
    }    

    /**
     * This overridden method ...
     * 
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
	public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (StringUtils.isBlank(inactivationBlockingDetectionServiceBeanName)) {
        	if (StringUtils.isBlank(blockedReferencePropertyName)) {
	        	// the default inactivation blocking detection service (used when inactivationBlockingDetectionServiceBeanName is blank) requires that the property name be set
	            throw new AttributeValidationException("inactivationBlockingDetectionServiceBeanName and  blockedReferencePropertyName can't both be blank in InactivationBlockingDefinition for class " +
	                    rootBusinessObjectClass.getClass().getName());
        	}
        }
        if (getBlockedBusinessObjectClass() == null) {
        	throw new AttributeValidationException("Unable to determine blockedReferenceBusinessObjectClass in InactivationBlockingDefinition for class " +
	                    rootBusinessObjectClass.getClass().getName());
        }
        if (!BusinessObject.class.isAssignableFrom(getBlockedBusinessObjectClass())) {
            throw new AttributeValidationException("InactivationBlockingDefinitions must block a reference of type BusinessObject.  Class name: " +
                    rootBusinessObjectClass.getClass().getName() + " blockedReferencePropertyName " + blockedReferencePropertyName + 
                    " class that should have been a BusinessObject: " + getBlockedBusinessObjectClass());
        }
    }

    /**
     * This overridden method ...
     * 
     * @see InactivationBlockingMetadata#getBlockedReferencePropertyName()
     */
    public String getBlockedReferencePropertyName() {
        return this.blockedReferencePropertyName;
    }

    public void setBlockedReferencePropertyName(String blockedReferencePropertyName) {
        this.blockedReferencePropertyName = blockedReferencePropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see InactivationBlockingMetadata#getBlockedBusinessObjectClass()
     */
    public Class<? extends BusinessObject> getBlockedBusinessObjectClass() {
        return this.blockedBusinessObjectClass;
    }

    public void setBlockedBusinessObjectClass(Class<? extends BusinessObject> blockedBusinessObjectClass) {
        this.blockedBusinessObjectClass = blockedBusinessObjectClass;
    }

    /**
     * This overridden method ...
     * 
     * @see InactivationBlockingMetadata#getInactivationBlockingDetectionServiceBeanName()
     */
    public String getInactivationBlockingDetectionServiceBeanName() {
        return this.inactivationBlockingDetectionServiceBeanName;
    }

    public void setInactivationBlockingDetectionServiceBeanName(String inactivationBlockingDetectionServiceImpl) {
        this.inactivationBlockingDetectionServiceBeanName = inactivationBlockingDetectionServiceImpl;
    }

    /**
     * This overridden method ...
     * 
     * @see InactivationBlockingMetadata#getBlockingReferenceBusinessObjectClass()
     */
    public Class<? extends BusinessObject> getBlockingReferenceBusinessObjectClass() {
        return this.blockingReferenceBusinessObjectClass;
    }

    public void setBlockingReferenceBusinessObjectClass(Class<? extends BusinessObject> blockingReferenceBusinessObjectClass) {
        this.blockingReferenceBusinessObjectClass = blockingReferenceBusinessObjectClass;
    }

	public String getRelationshipLabel() {
		return this.relationshipLabel;
	}

	public void setRelationshipLabel(String relationshipLabel) {
		this.relationshipLabel = relationshipLabel;
	}

	public Class<? extends BusinessObject> getBusinessObjectClass() {
		return this.businessObjectClass;
	}

	public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
		this.businessObjectClass = businessObjectClass;
	}
	
	@Override
	public String toString() {
		return "InactivationBlockingDefinition: blockedClass=" + blockedBusinessObjectClass.getName() 
				+ " /blockingReferenceProperty=" + blockedReferencePropertyName
				+ " /blockingClass=" + blockingReferenceBusinessObjectClass.getName();
	}
}
