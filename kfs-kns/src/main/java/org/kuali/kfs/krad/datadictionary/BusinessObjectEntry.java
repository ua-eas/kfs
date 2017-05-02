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
package org.kuali.kfs.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * A single BusinessObject entry in the DataDictionary, which contains information relating to the display, validation,
 * and general maintenance of a BusinessObject and its attributes.
 * <p>
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process
 */
public class BusinessObjectEntry extends DataObjectEntry {

    protected Class<? extends BusinessObject> baseBusinessObjectClass;

    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        super.setDataObjectClass(businessObjectClass);

        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }

        if (getRelationships() != null) {
            for (RelationshipDefinition rd : getRelationships()) {
                rd.setSourceClass(businessObjectClass);
            }
        }
    }

    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return (Class<? extends BusinessObject>) super.getDataObjectClass();
    }

    /**
     * The baseBusinessObjectClass is an optional parameter for specifying a base class
     * for the dataObjectClass, allowing the data dictionary to index by the base class
     * in addition to the current class.
     */

    public void setBaseBusinessObjectClass(Class<? extends BusinessObject> baseBusinessObjectClass) {
        this.baseBusinessObjectClass = baseBusinessObjectClass;
    }

    public Class<? extends BusinessObject> getBaseBusinessObjectClass() {
        return baseBusinessObjectClass;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     */
    @Override
    public void completeValidation() {
        try {

            super.completeValidation();

            if (inactivationBlockingDefinitions != null && !inactivationBlockingDefinitions.isEmpty()) {
                for (InactivationBlockingDefinition inactivationBlockingDefinition : inactivationBlockingDefinitions) {
                    inactivationBlockingDefinition.completeValidation(getDataObjectClass(), null);
                }
            }
        } catch (DataDictionaryException ex) {
            // just rethrow
            throw ex;
        } catch (Exception ex) {
            throw new DataDictionaryException("Exception validating " + this, ex);
        }
    }

    /**
     * @see DataDictionaryEntryBase#afterPropertiesSet()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (inactivationBlockingDefinitions != null) {
            for (InactivationBlockingDefinition ibd : inactivationBlockingDefinitions) {
                ibd.setBusinessObjectClass(getBusinessObjectClass());
                if (StringUtils.isNotBlank(ibd.getBlockedReferencePropertyName()) &&
                    ibd.getBlockedBusinessObjectClass() == null) {
                    // if the user didn't specify a class name for the blocked reference, determine it here
                    ibd.setBlockedBusinessObjectClass(DataDictionary
                        .getAttributeClass(getDataObjectClass(), ibd.getBlockedReferencePropertyName()));
                }
                ibd.setBlockingReferenceBusinessObjectClass(getBusinessObjectClass());
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BusinessObjectEntry for " + getBusinessObjectClass();
    }
}
