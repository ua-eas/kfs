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
package org.kuali.kfs.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.MethodInvokerConfig;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.field.AttributeQuery;
import org.kuali.kfs.krad.uif.widget.QuickFinder;

/**
 * Represents a user control, which is a special control to handle
 * the input of a Person
 *
 * 
 */
public class UserControl extends TextControl {
    private static final long serialVersionUID = 7468340793076585869L;

    private String principalIdPropertyName;
    private String personNamePropertyName;
    private String personObjectPropertyName;

    public UserControl() {
        super();
    }

    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (!(parent instanceof InputField)) {
            return;
        }

        InputField field = (InputField) parent;
        field.getHiddenPropertyNames().add(principalIdPropertyName);

        if (!field.isReadOnly()) {
            // add information fields
            if (StringUtils.isNotBlank(personNamePropertyName)) {
                field.getInformationalDisplayPropertyNames().add(personNamePropertyName);
            } else {
                field.getInformationalDisplayPropertyNames().add(personObjectPropertyName + ".name");
            }

            // setup script to clear id field when name is modified
            String idPropertyPath = field.getBindingInfo().getPropertyAdjustedBindingPath(principalIdPropertyName);
            String onChangeScript = "setValue('" + idPropertyPath + "','');";

            if (StringUtils.isNotBlank(field.getOnChangeScript())) {
                onChangeScript = field.getOnChangeScript() + onChangeScript;
            }
            field.setOnChangeScript(onChangeScript);
        }

        if (field.isReadOnly() && StringUtils.isBlank(field.getAdditionalDisplayPropertyName())) {
            if (StringUtils.isNotBlank(personNamePropertyName)) {
                field.setAdditionalDisplayPropertyName(personNamePropertyName);
            } else {
                field.setAdditionalDisplayPropertyName(personObjectPropertyName + ".name");
            }
        }

        // setup field query for displaying name
        AttributeQuery attributeQuery = new AttributeQuery();
        MethodInvokerConfig methodInvokerConfig = new MethodInvokerConfig();
        PersonService personService = KimApiServiceLocator.getPersonService();
        methodInvokerConfig.setTargetObject(personService);
        attributeQuery.setQueryMethodInvokerConfig(methodInvokerConfig);
        attributeQuery.setQueryMethodToCall("getPersonByPrincipalName");
        attributeQuery.getQueryMethodArgumentFieldList().add(field.getPropertyName());
        attributeQuery.getReturnFieldMapping().put("principalId", principalIdPropertyName);

        if (StringUtils.isNotBlank(personNamePropertyName)) {
            attributeQuery.getReturnFieldMapping().put("name", personNamePropertyName);
        } else {
            attributeQuery.getReturnFieldMapping().put("name", personObjectPropertyName + ".name");
        }
        field.setFieldAttributeQuery(attributeQuery);

        // setup field lookup
        QuickFinder quickFinder = field.getFieldLookup();
        if (quickFinder.isRender()) {
            if (StringUtils.isBlank(quickFinder.getDataObjectClassName())) {
                quickFinder.setDataObjectClassName(Person.class.getName());
            }

            if (quickFinder.getFieldConversions().isEmpty()) {
                quickFinder.getFieldConversions().put("principalId", principalIdPropertyName);

                if (StringUtils.isNotBlank(personNamePropertyName)) {
                    quickFinder.getFieldConversions().put("name", personNamePropertyName);
                } else {
                    quickFinder.getFieldConversions().put("name", personObjectPropertyName + ".name");
                }

                quickFinder.getFieldConversions().put("principalName", field.getPropertyName());
            }
        }
    }

    /**
     * The name of the property on the parent object that holds the principal id
     *
     * @return String principalIdPropertyName
     */
    public String getPrincipalIdPropertyName() {
        return principalIdPropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the principal id
     *
     * @param principalIdPropertyName
     */
    public void setPrincipalIdPropertyName(String principalIdPropertyName) {
        this.principalIdPropertyName = principalIdPropertyName;
    }

    /**
     * The name of the property on the parent object that holds the person name
     *
     * @return String personNamePropertyName
     */
    public String getPersonNamePropertyName() {
        return personNamePropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the person name
     *
     * @param personNamePropertyName
     */
    public void setPersonNamePropertyName(String personNamePropertyName) {
        this.personNamePropertyName = personNamePropertyName;
    }

    /**
     * The name of the property on the parent object that holds the person object
     *
     * @return String personObjectPropertyName
     */
    public String getPersonObjectPropertyName() {
        return personObjectPropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the person object
     *
     * @param personObjectPropertyName
     */
    public void setPersonObjectPropertyName(String personObjectPropertyName) {
        this.personObjectPropertyName = personObjectPropertyName;
    }
}
