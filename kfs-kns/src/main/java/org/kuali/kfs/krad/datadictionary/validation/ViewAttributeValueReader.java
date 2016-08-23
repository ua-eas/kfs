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
package org.kuali.kfs.krad.datadictionary.validation;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.uif.UifPropertyPaths;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AttributeValueReader which can read the correct values from all InputFields which exist on the View
 */
public class ViewAttributeValueReader extends BaseAttributeValueReader {

    private View view;
    private Object form;
    private List<Constrainable> inputFields = new ArrayList<Constrainable>();
    private Map<String, InputField> inputFieldMap = new HashMap<String, InputField>();

    /**
     * Constructor for ViewAttributeValueReader, the View must already be indexed and
     * the InputFields must have already be initialized for this reader to work properly
     * @param view the View to validate
     * @param form model object representing the View's form data
     */
    public ViewAttributeValueReader(View view, Object form) {
        this.view = view;
        this.form = form;

        for (InputField field : view.getAllInputFields()) {
            inputFields.add(field);
            inputFieldMap.put(field.getName(), field);
        }
    }

    /*  TODO allow it to be page specific only
        public ViewAttributeValueReader(View view, Page page, UifFormBase form) {
        this.view = view;
        this.form = form;
        for(DataField field: view.getViewIndex().getDataFieldIndex().values()){
            if(field instanceof Constrainable){
                inputFields.add((Constrainable)field);
            }
        }
    }*/


    /**
     * Gets the definition which is an InputField on the View/Page
     */
    @Override
    public Constrainable getDefinition(String attributeName) {
        InputField field = inputFieldMap.get(attributeName);
        if (field != null) {
            return field;
        }
        else{
            return null;
        }
    }

    /**
     * Gets all InputFields (which extend Constrainable)
     * @return
     */
    @Override
    public List<Constrainable> getDefinitions() {
        return inputFields;
    }

    /**
     * Returns the label associated with the InputField which has that AttributeName
     * @param attributeName
     * @return
     */
    @Override
    public String getLabel(String attributeName) {
        InputField field = inputFieldMap.get(attributeName);
        if(field != null){
            return field.getLabel();
        }
        else{
            return null;
        }
    }

    /**
     * Returns the View object
     * @return view set in the constructor
     */
    @Override
    public Object getObject() {
        return view;
    }

    /**
     * Not used for this reader, returns null
     * @return null
     */
    @Override
    public Constrainable getEntry() {
        return null;
    }

    /**
     * Returns current attributeName which represents the path
     * @return attributeName set on this reader
     */
    @Override
    public String getPath() {
        return this.attributeName;
    }

    /**
     * Gets the type of value for this AttributeName as represented on the Form
     * @param attributeName
     * @return
     */
    @Override
    public Class<?> getType(String attributeName) {
        Object fieldValue = ObjectPropertyUtils.getPropertyValue(form, attributeName);
        return fieldValue.getClass();
    }

    /**
     * If the current attribute being evaluated is a field of an addLine return false because it should not
     * be evaluated during Validation.
     * @return false if InputField is part of an addLine for a collection, true otherwise
     */
    @Override
    public boolean isReadable() {
        if(attributeName != null && attributeName.contains(UifPropertyPaths.NEW_COLLECTION_LINES)){
            return false;
        }
        return true;
    }

    /**
     * Return value of the field for the attributeName currently set on this reader
     * @param <X> return type
     * @return value of the field for the attributeName currently set on this reader
     * @throws AttributeValidationException
     */
    @Override
    public <X> X getValue() throws AttributeValidationException {
        X fieldValue = null;
        if(StringUtils.isNotBlank(this.attributeName)){
            fieldValue = ObjectPropertyUtils.<X>getPropertyValue(form, this.attributeName);
        }
        return fieldValue;
    }

    /**
     * Return value of the field for the attributeName passed in
     * @param attributeName name (which represents a path) of the value to be retrieved on the Form
     * @param <X> return type
     * @return value of that attributeName represents on the form
     * @throws AttributeValidationException
     */
    @Override
    public <X> X getValue(String attributeName) throws AttributeValidationException {
        X fieldValue = null;
        if(StringUtils.isNotBlank(attributeName)){
            fieldValue = ObjectPropertyUtils.<X>getPropertyValue(form, this.attributeName);
        }
        return fieldValue;
    }

    /**
     * Cones this AttributeValueReader
     * @return AttributeValueReader
     */
    @Override
    public AttributeValueReader clone(){
        ViewAttributeValueReader clone = new ViewAttributeValueReader(view, form);
        clone.setAttributeName(this.attributeName);
        return clone;
    }

}
