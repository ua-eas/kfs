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
package org.kuali.kfs.kns.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinition;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.valuefinder.ValueFinder;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.List;

/**
 * The maintainableField element defines the specifications
 * for one data field.
 * JSTL: maintainableField is a Map accessed by the field name.
 * It contains entries with the following keys:
 * field (boolean String)
 * name (String)
 * required (boolean String)
 * <p>
 * name is the name of the field
 * required is true if the field must contain a non-null value
 * readOnly is true if it cannot be updated
 * template documentation from MaintenanceUtils.java:
 * Field templates are used in relation to multiple value lookups.
 * When doing a MV lookup on a collection, the returned BOs
 * are not necessarily of the same type as the elements of the
 * collection. Therefore, a means of mapping between the fields
 * for the 2 BOs are necessary. The template attribute of
 * <maintainableField> contained within <maintainableCollection>
 * tells us this mapping.
 * Example:
 * <maintainableField name="collectionAttrib" template="lookupBOAttrib">
 * means that when a list of BOs are returned, the lookupBOAttrib value
 * of the looked up BO will be placed into the collectionAttrib
 * value of the BO added to the collection
 * webUILeaveFieldFunction is the name of a javascript function to called when
 * when the user tabs out of the field.
 * webUILeaveFieldCallbackFunction
 * This is the call javascript function related to the webUILeaveFieldFunction.
 * readOnlyAfterAdd
 * This is used to indicate that the field is read-only after the record has been
 * initially created.
 */
public class MaintainableFieldDefinition extends MaintainableItemDefinition implements FieldDefinitionI {
    private static final long serialVersionUID = -1176087424343479963L;

    protected boolean required = false;
    protected boolean unconditionallyReadOnly = false;
    protected boolean readOnlyAfterAdd = false;
    protected boolean noLookup = false;
    protected boolean lookupReadOnly = false;

    protected String defaultValue;
    protected String template;
    protected Class<? extends ValueFinder> defaultValueFinderClass;

    protected String webUILeaveFieldFunction = "";
    protected String webUILeaveFieldCallbackFunction = "";
    protected List<String> webUILeaveFieldFunctionParameters;

    protected Class<? extends BusinessObject> overrideLookupClass;
    protected String overrideFieldConversions;

    protected String alternateDisplayAttributeName;
    protected String additionalDisplayAttributeName;

    protected boolean triggerOnChange;

    protected Boolean showFieldLevelHelp = null; // use default from system
    protected String fieldLevelHelpUrl = null;

    public MaintainableFieldDefinition() {
    }

    /**
     * @return true if this attribute is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * required is true if the field must contain a non-null value
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }


    /**
     * The defaultValue element will pre-load the specified value
     * into the lookup field.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     * @return custom defaultValue class
     */
    public Class<? extends ValueFinder> getDefaultValueFinderClass() {
        return defaultValueFinderClass;
    }

    /**
     * @return the unconditionallyReadOnly
     */
    public boolean isUnconditionallyReadOnly() {
        return this.unconditionallyReadOnly;
    }

    /**
     * @param unconditionallyReadOnly the unconditionallyReadOnly to set
     */
    public void setUnconditionallyReadOnly(boolean unconditionallyReadOnly) {
        this.unconditionallyReadOnly = unconditionallyReadOnly;
    }

    /**
     * Gets the overrideFieldConversions attribute.
     *
     * @return Returns the overrideFieldConversions.
     */
    public String getOverrideFieldConversions() {
        return overrideFieldConversions;
    }


    /**
     * Single value lookups expect field conversions to be passed in as a HTTP parameter when the lookups is invoked from a quickfinder icon (i.e. magnifying glass on page).
     * Field conversions are normally used to determine which fields will be returned when the "return value" link is clicked.
     * <p>
     * For example, if we're performing a quickfinder lookup and the field conversion string "a:document.someObject.a1,b:document.someObject.b1" is passed into the lookup,
     * this means that when we click on a lookup result row to be returned:
     * <p>
     * the value of property "a" from the selected result bo will be passed as the value of the HTTP parameter named "document.someObject.a1",
     * which, in turn, populates the POJO property of the same name on the form
     * the value of property "b" from the selected result bo will be passed as the value of the HTTP parameter named "document.someObject.b1",
     * which, in turn, populates the POJO property of the same name on the form
     * <p>
     * Normally, the field conversion string is automatically computed by the framework to return all of the primary key values of the looked up BO into the corresponding
     * foreign key values of the destination BO (i.e. document.someObject in the example above).  However, putting in this element will allow for the overriding of the
     * field conversions string.
     */
    public void setOverrideFieldConversions(String overrideFieldConversions) {
        this.overrideFieldConversions = overrideFieldConversions;
    }


    /**
     * Gets the overrideLookupClass attribute.
     *
     * @return Returns the overrideLookupClass.
     */
    public Class<? extends BusinessObject> getOverrideLookupClass() {
        return overrideLookupClass;
    }


    /**
     * Directly validate simple fields.
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getName())) {
            throw new AttributeValidationException("unable to find attribute or collection named '" + getName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }

        if (StringUtils.isNotBlank(getAlternateDisplayAttributeName())) {
            if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getAlternateDisplayAttributeName())) {
                throw new AttributeValidationException("unable to find attribute or collection named '" + getAlternateDisplayAttributeName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }

        if (StringUtils.isNotBlank(getAdditionalDisplayAttributeName())) {
            if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getAdditionalDisplayAttributeName())) {
                throw new AttributeValidationException("unable to find attribute or collection named '" + getAdditionalDisplayAttributeName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }

        if (defaultValueFinderClass != null && defaultValue != null) {
            throw new AttributeValidationException("Both defaultValue and defaultValueFinderClass can not be specified on attribute " + getName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintainableFieldDefinition for field " + getName();
    }


    public String getTemplate() {
        return template;
    }


    /**
     * template documentation from MaintenanceUtils.java:
     * Field templates are used in relation to multiple value lookups.
     * When doing a MV lookup on a collection, the returned BOs
     * are not necessarily of the same type as the elements of the
     * collection. Therefore, a means of mapping between the fields
     * for the 2 BOs are necessary. The template attribute of
     * <maintainableField> contained within <maintainableCollection>
     * tells us this mapping.
     * Example:
     * <maintainableField name="collectionAttrib" template="lookupBOAttrib">
     * means that when a list of BOs are returned, the lookupBOAttrib value
     * of the looked up BO will be placed into the collectionAttrib
     * value of the BO added to the collection
     */
    public void setTemplate(String template) {
        this.template = template;
    }


    public String getWebUILeaveFieldCallbackFunction() {
        return webUILeaveFieldCallbackFunction;
    }


    /**
     * webUILeaveFieldCallbackFunction
     * This is the call javascript function related to the webUILeaveFieldFunction.
     */
    public void setWebUILeaveFieldCallbackFunction(String webUILeaveFieldCallbackFunction) {
        this.webUILeaveFieldCallbackFunction = webUILeaveFieldCallbackFunction;
    }


    public String getWebUILeaveFieldFunction() {
        return webUILeaveFieldFunction;
    }


    /**
     * webUILeaveFieldFunction is the name of a javascript function to called when
     * when the user tabs out of the field.
     */
    public void setWebUILeaveFieldFunction(String webUILeaveFieldFunction) {
        this.webUILeaveFieldFunction = webUILeaveFieldFunction;
    }


    public boolean isReadOnlyAfterAdd() {
        return readOnlyAfterAdd;
    }


    /**
     * This is used to indicate that the field is read-only after the record has been
     * initially created.
     */
    public void setReadOnlyAfterAdd(boolean readOnlyAfterAdd) {
        this.readOnlyAfterAdd = readOnlyAfterAdd;
    }


    /**
     * The defaultValueFinderClass specifies the java class that will be
     * used to determine the default value of a lookup field.  The classname
     * specified in this field must implement ValueFinder
     */
    public void setDefaultValueFinderClass(Class<? extends ValueFinder> defaultValueFinderClass) {
        this.defaultValueFinderClass = defaultValueFinderClass;
    }


    /**
     * The overrideLookupClass element is used to indicate the
     * class that should be used for the magnifying glass lookup.
     * The specified class must be a subclass of the business object
     * class.
     */
    public void setOverrideLookupClass(Class<? extends BusinessObject> overrideLookupClass) {
        this.overrideLookupClass = overrideLookupClass;
    }

    /**
     * @return the noLookup
     */
    public boolean isNoLookup() {
        return this.noLookup;
    }

    /**
     * @param noLookup the noLookup to set
     */
    public void setNoLookup(boolean noLookup) {
        this.noLookup = noLookup;
    }

    public boolean isLookupReadOnly() {
        return lookupReadOnly;
    }

    public void setLookupReadOnly(boolean lookupReadOnly) {
        this.lookupReadOnly = lookupReadOnly;
    }

    public Boolean isShowFieldLevelHelp() {
        return showFieldLevelHelp;
    }

    public void setShowFieldLevelHelp(Boolean showFieldLevelHelp) {
        this.showFieldLevelHelp = showFieldLevelHelp;
    }

    public String getFieldLevelHelpUrl() {
        return fieldLevelHelpUrl;
    }

    public void setFieldLevelHelpUrl(String fieldLevelHelpUrl) {
        this.fieldLevelHelpUrl = fieldLevelHelpUrl;
    }

    /**
     * The alternateDisplayAttributeName is the name of the attribute whose value will be displayed instead
     * of the actual maintenance field attribute. Only applies when field is read-only.
     */
    public String getAlternateDisplayAttributeName() {
        return this.alternateDisplayAttributeName;
    }

    public void setAlternateDisplayAttributeName(String alternateDisplayAttributeName) {
        this.alternateDisplayAttributeName = alternateDisplayAttributeName;
    }

    /**
     * The additionalDisplayAttributeName is the name of the attribute whose value will be displayed in addition
     * to the actual maintenance field attribute. Only applies when field is read-only.
     */
    public String getAdditionalDisplayAttributeName() {
        return this.additionalDisplayAttributeName;
    }

    public void setAdditionalDisplayAttributeName(String additionalDisplayAttributeName) {
        this.additionalDisplayAttributeName = additionalDisplayAttributeName;
    }

    public boolean isTriggerOnChange() {
        return this.triggerOnChange;
    }

    public void setTriggerOnChange(boolean triggerOnChange) {
        this.triggerOnChange = triggerOnChange;
    }

    /**
     * @return the webUILeaveFieldFunctionParameters
     */
    public List<String> getWebUILeaveFieldFunctionParameters() {
        return this.webUILeaveFieldFunctionParameters;
    }

    /**
     * @param webUILeaveFieldFunctionParameters the webUILeaveFieldFunctionParameters to set
     */
    public void setWebUILeaveFieldFunctionParameters(
        List<String> webUILeaveFieldFunctionParameters) {
        this.webUILeaveFieldFunctionParameters = webUILeaveFieldFunctionParameters;
    }

}
