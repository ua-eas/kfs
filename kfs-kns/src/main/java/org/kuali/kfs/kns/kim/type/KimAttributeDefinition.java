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
package org.kuali.kfs.kns.kim.type;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.exception.ClassValidationException;

import java.util.Map;

/**
 * @deprecated A krad integrated type service base class will be provided in the future.
 * This is only used for the legacy {@link DataDictionaryTypeServiceBase}.
 */
@Deprecated
public final class KimAttributeDefinition extends AttributeDefinition {
	private static final long serialVersionUID = 7006569761728813805L;

	protected Map<String, String> lookupInputPropertyConversions;
	protected Map<String, String> lookupReturnPropertyConversions;
	protected String lookupBoClass;
    protected String sortCode;
	protected String kimAttrDefnId;
	protected String kimTypeId;

	/**
	 * @return the sortCode
	 */
	public String getSortCode() {
		return this.sortCode;
	}

	/**
	 * @param sortCode
	 *            the sortCode to set
	 */
	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}

	public String getKimAttrDefnId() {
		return this.kimAttrDefnId;
	}

	public void setKimAttrDefnId(String kimAttrDefnId) {
		this.kimAttrDefnId = kimAttrDefnId;
	}

	/**
	 * @return the kimTypeId
	 */
	public String getKimTypeId() {
		return this.kimTypeId;
	}

	/**
	 * @param kimTypeId the kimTypeId to set
	 */
	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}


	/**
	 * @return the lookupInputPropertyConversions
	 */
	public Map<String, String> getLookupInputPropertyConversions() {
		return this.lookupInputPropertyConversions;
	}

	/**
	 * @param lookupInputPropertyConversions
	 *            the lookupInputPropertyConversions to set
	 */
	public void setLookupInputPropertyConversions(Map<String, String> lookupInputPropertyConversions) {
		this.lookupInputPropertyConversions = lookupInputPropertyConversions;
	}

	/**
	 * @return the lookupReturnPropertyConversions
	 */
	public Map<String, String> getLookupReturnPropertyConversions() {
		return this.lookupReturnPropertyConversions;
	}

	/**
	 * @param lookupReturnPropertyConversions
	 *            the lookupReturnPropertyConversions to set
	 */
	public void setLookupReturnPropertyConversions(Map<String, String> lookupReturnPropertyConversions) {
		this.lookupReturnPropertyConversions = lookupReturnPropertyConversions;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder( this )
			.append( "name", getName() )
			.append( "label", getLabel() )
			.append( "lookupBoClass", this.lookupBoClass )
			.append( "required", isRequired() )
			.append( "lookupInputPropertyConversions", this.lookupInputPropertyConversions )
			.append( "lookupReturnPropertyConversions", this.lookupReturnPropertyConversions )
			.toString();
	}

	public String getLookupBoClass() {
		return this.lookupBoClass;
	}

	public void setLookupBoClass(String lookupBoClass) {
		this.lookupBoClass = lookupBoClass;
	}

    public boolean isHasLookupBoDefinition() {
        return true;
    }


	@SuppressWarnings("unchecked")
	@Override
	public void completeValidation(Class rootObjectClass, Class otherObjectClass) {
		if (lookupBoClass != null) {
        	try {
        		Class lookupBoClassObject = ClassUtils.getClass(ClassLoaderUtils.getDefaultClassLoader(), getLookupBoClass());
        		if (!BusinessObject.class.isAssignableFrom(lookupBoClassObject)) {
        			throw new ClassValidationException("lookupBoClass is not a valid instance of " + BusinessObject.class.getName() + " instead was: " + lookupBoClassObject.getName());
        		}
        	} catch (ClassNotFoundException e) {
        		throw new ClassValidationException("lookupBoClass could not be found: " + getLookupBoClass(), e);
        	}
        }
		super.completeValidation(rootObjectClass, otherObjectClass);
	}
    
    

	
}
