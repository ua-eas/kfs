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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Prerequisite constraints require that some other attribute be non-empty in order for the constraint to be valid. 
 * So, a 7-digit US phone number might have a prerequisite of an area code, or an address street2 might have a prerequisite
 * that street1 is non-empty. 
 * 
 * 
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PrerequisiteConstraint extends BaseConstraint {
	@XmlElement
    protected String propertyName;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

    @Override
    public String getLabelKey(){
        if(StringUtils.isBlank(this.labelKey)){
            return UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "prerequisiteFallback";
        }
        else{
            return super.getLabelKey();
        }
    }

    @Override
    /**
     * @see BaseConstraint#getValidationMessageParams()
     * @return the validation message list if defined. If not defined,  return  the property name
     */
    public List<String> getValidationMessageParams() {
        if(super.getValidationMessageParams() == null) {
            ArrayList<String> params = new ArrayList<String>(1);
            params.add(getPropertyName());
            return params;
        } else {
            return super.getValidationMessageParams();
        }
    }
}
