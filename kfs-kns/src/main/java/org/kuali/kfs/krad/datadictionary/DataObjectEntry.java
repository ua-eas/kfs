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
import org.kuali.kfs.krad.bo.Exporter;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.kfs.krad.datadictionary.validation.capability.MustOccurConstrainable;

import java.util.List;

/**
 * Generic dictionary entry for an object that does not have to implement BusinessObject. It provides support
 * for general objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectEntry extends DataDictionaryEntryBase implements MustOccurConstrainable {

    protected String name;
    protected Class<?> dataObjectClass;

    protected String titleAttribute;
    protected String objectLabel;
    protected String objectDescription;

    protected List<String> primaryKeys;
    protected Class<? extends Exporter> exporterClass;

    protected List<MustOccurConstraint> mustOccurConstraints;

    protected List<String> groupByAttributesForEffectiveDating;

    protected HelpDefinition helpDefinition;


    protected boolean boNotesEnabled = false;

    protected List<InactivationBlockingDefinition> inactivationBlockingDefinitions;
    
    @Override
    public void completeValidation() {
        //KFSMI-1340 - Object label should never be blank
        if (StringUtils.isBlank(getObjectLabel())) {
            throw new AttributeValidationException("Object label cannot be blank for class " + dataObjectClass.getName());
        }

        super.completeValidation();
    }

    /**
     * @see DataDictionaryEntry#getJstlKey()
     */
    @Override
    public String getJstlKey() {
        if (dataObjectClass == null) {
            throw new IllegalStateException("cannot generate JSTL key: dataObjectClass is null");
        }

        return (dataObjectClass != null) ? dataObjectClass.getSimpleName() : dataObjectClass.getSimpleName();
    }

    /**
     * @see DataDictionaryEntry#getFullClassName()
     */
    @Override
    public String getFullClassName() {
        return dataObjectClass.getName();
    }

    /**
     * @see DataDictionaryEntryBase#getEntryClass()
     */
    @Override
    public Class<?> getEntryClass() {
        return dataObjectClass;
    }

    /**
     * @return the dataObjectClass
     */
    public Class<?> getDataObjectClass() {
        return this.dataObjectClass;
    }

    /**
     * @param dataObjectClass the dataObjectClass to set
     */
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the objectLabel.
     */
    public String getObjectLabel() {
        return objectLabel;
    }

    /**
     * The objectLabel provides a short name of the business
     * object for use on help screens.
     *
     * @param objectLabel The objectLabel to set.
     */
    public void setObjectLabel(String objectLabel) {
        this.objectLabel = objectLabel;
    }

    /**
     * @return Returns the description.
     */
    public String getObjectDescription() {
        return objectDescription;
    }

    /**
     * The objectDescription provides a brief description
     * of the business object for use on help screens.
     *
     * @param description The description to set.
     */
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }

    /**
     * Gets the helpDefinition attribute.
     *
     * @return Returns the helpDefinition.
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * Sets the helpDefinition attribute value.
     *
     * The objectHelp element provides the keys to
     * obtain a help description from the system parameters table.
     *
     * parameterNamespace the namespace of the parameter containing help information
     * parameterName the name of the parameter containing help information
     * parameterDetailType the detail type of the parameter containing help information
     *
     * @param helpDefinition The helpDefinition to set.
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DataObjectEntry for " + getDataObjectClass();
    }

    /**
     * @return the mustOccurConstraints
     */
    public List<MustOccurConstraint> getMustOccurConstraints() {
        return this.mustOccurConstraints;
    }

    /**
     * @param mustOccurConstraints the mustOccurConstraints to set
     */
    public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
        this.mustOccurConstraints = mustOccurConstraints;
    }

    /**
     * @return the titleAttribute
     */
    public String getTitleAttribute() {
        return this.titleAttribute;
    }

    /**
     * The titleAttribute element is the name of the attribute that
     * will be used as an inquiry field when the lookup search results
     * fields are displayed.
     *
     * For some business objects, there is no obvious field to serve
     * as the inquiry field. in that case a special field may be required
     * for inquiry purposes.
     */
    public void setTitleAttribute(String titleAttribute) {
        this.titleAttribute = titleAttribute;
    }

    /**
     * @return the primaryKeys
     */
    public List<String> getPrimaryKeys() {
        return this.primaryKeys;
    }

    /**
     * @param primaryKeys the primaryKeys to set
     */
    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public Class<? extends Exporter> getExporterClass() {
        return this.exporterClass;
    }

    public void setExporterClass(Class<? extends Exporter> exporterClass) {
        this.exporterClass = exporterClass;
    }

    /**
     * Provides list of attributes that should be used for grouping
     * when performing effective dating logic in the framework
     *
     * @return List<String> list of attributes to group by
     */
    public List<String> getGroupByAttributesForEffectiveDating() {
        return this.groupByAttributesForEffectiveDating;
    }

    /**
     * Setter for the list of attributes to group by
     *
     * @param groupByAttributesForEffectiveDating
     */
    public void setGroupByAttributesForEffectiveDating(List<String> groupByAttributesForEffectiveDating) {
        this.groupByAttributesForEffectiveDating = groupByAttributesForEffectiveDating;
    }
    
    
    /**
     * Gets the boNotesEnabled flag for the Data object
     *
     * <p>
     * true indicates that notes and attachments will be permanently
     * associated with the business object
     * false indicates that notes and attachments are associated
     * with the document used to create or edit the business object.
     * </p>
     * 
     * @return the boNotesEnabled flag
     */    
    public boolean isBoNotesEnabled() {
        return boNotesEnabled;
    }

    /**
     * Setter for the boNotesEnabled flag
     */    
    public void setBoNotesEnabled(boolean boNotesEnabled) {
        this.boNotesEnabled = boNotesEnabled;
    }
    
    /**
     * Gets the inactivationBlockingDefinitions for the Data object
     *
     * <p>
     * 
     * </p>
     * 
     * @return the list of <code>InactivationBlockingDefinition</code> 
     */ 
    public List<InactivationBlockingDefinition> getInactivationBlockingDefinitions() {
        return this.inactivationBlockingDefinitions;
    }

    /**
     * Setter for the inactivationBlockingDefinitions
     */
    public void setInactivationBlockingDefinitions(
            List<InactivationBlockingDefinition> inactivationBlockingDefinitions) {
        this.inactivationBlockingDefinitions = inactivationBlockingDefinitions;
    }    
}
