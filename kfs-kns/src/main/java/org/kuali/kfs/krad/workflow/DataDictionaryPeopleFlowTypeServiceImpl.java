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
package org.kuali.kfs.krad.workflow;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kew.api.KEWPropertyConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.repository.type.KewAttributeDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeAttribute;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService;
import org.kuali.kfs.krad.service.DataDictionaryRemoteFieldService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.BeanPropertyComparator;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataDictionaryPeopleFlowTypeServiceImpl implements PeopleFlowTypeService {

    /**
     * @see org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService#filterToSelectableRoleIds(java.lang.String,
     *      java.util.List<java.lang.String>)
     */
    public List<String> filterToSelectableRoleIds(@WebParam(name = "kewTypeId") String kewTypeId,
            @WebParam(name = "roleIds") List<String> roleIds) {
        return roleIds;
    }

    /**
     * @see org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService#resolveRoleQualifiers(java.lang.String,
     *      java.lang.String, org.kuali.rice.kew.api.document.Document, org.kuali.rice.kew.api.document.DocumentContent)
     */
    public Map<String, String> resolveRoleQualifiers(@WebParam(name = "kewTypeId") String kewTypeId,
            @WebParam(name = "roleId") String roleId, @WebParam(name = "document") Document document,
            @WebParam(name = "documentContent") DocumentContent documentContent) {
        return new HashMap<String, String>();
    }

    /**
     * @see org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService#getAttributeFields(java.lang.String)
     */
    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "kewTypeId") String kewTypeId) {
        List<RemotableAttributeField> fields = new ArrayList<RemotableAttributeField>();

        KewTypeDefinition typeDefinition = KewApiServiceLocator.getKewTypeRepositoryService().getTypeById(kewTypeId);
        List<KewTypeAttribute> typeAttributes = new ArrayList<KewTypeAttribute>(typeDefinition.getAttributes());

        // sort type attributes by sort code
        List<String> sortAttributes = new ArrayList<String>();
        sortAttributes.add(KEWPropertyConstants.SEQUENCE_NUMBER);
        Collections.sort(typeAttributes, new BeanPropertyComparator(sortAttributes));

        // build a remotable field for each active type attribute
        for (KewTypeAttribute typeAttribute : typeAttributes) {
            if (!typeAttribute.isActive()) {
                continue;
            }

            RemotableAttributeField attributeField = null;
            if (StringUtils.isBlank(typeAttribute.getAttributeDefinition().getComponentName())) {
                attributeField = buildRemotableFieldWithoutDataDictionary(typeAttribute.getAttributeDefinition());
            } else {
                attributeField = getDataDictionaryRemoteFieldService().buildRemotableFieldFromAttributeDefinition(
                        typeAttribute.getAttributeDefinition().getComponentName(),
                        typeAttribute.getAttributeDefinition().getName());
            }

            fields.add(attributeField);
        }

        return fields;
    }

    /**
     * Builds a {@link RemotableAttributeField} instance when there is no component configured (and therefore we can
     * not lookup the data dictionary)
     *
     * <p>
     * Very basic field, should have labels configured and defaults to using text control
     * </p>
     *
     * @param attributeDefinition - KEW attribute definition configured from which the name, label, and description
     * will be pulled
     * @return RemotableAttributeField instance built from the given KEW attribute definition
     */
    protected RemotableAttributeField buildRemotableFieldWithoutDataDictionary(
            KewAttributeDefinition attributeDefinition) {
        RemotableAttributeField.Builder definition = RemotableAttributeField.Builder.create(
                attributeDefinition.getName());

        definition.setLongLabel(attributeDefinition.getLabel());
        definition.setShortLabel(attributeDefinition.getLabel());
        definition.setHelpDescription(attributeDefinition.getDescription());

        // default control to text
        RemotableTextInput.Builder controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(30);
        definition.setControl(controlBuilder);

        return definition.build();
    }

    /**
     * @see org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService#validateAttributes(java.lang.String,
     *      java.util.Map<java.lang.String,java.lang.String>)
     */
    public List<RemotableAttributeError> validateAttributes(@WebParam(name = "kewTypeId") String kewTypeId,
            @WebParam(name = "attributes") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> attributes) throws RiceIllegalArgumentException {
        return null;
    }

    /**
     * @see org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService#validateAttributesAgainstExisting(java.lang.String,
     *      java.util.Map<java.lang.String,java.lang.String>, java.util.Map<java.lang.String,java.lang.String>)
     */
    public List<RemotableAttributeError> validateAttributesAgainstExisting(
            @WebParam(name = "kewTypeId") String kewTypeId, @WebParam(name = "newAttributes") @XmlJavaTypeAdapter(
            value = MapStringStringAdapter.class) Map<String, String> newAttributes,
            @WebParam(name = "oldAttributes") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> oldAttributes) throws RiceIllegalArgumentException {
        return null;
    }

    @Override
    public List<Map<String, String>> resolveMultipleRoleQualifiers(String s, String s1, Document document, DocumentContent documentContent) {
        return null;
    }

    public DataDictionaryRemoteFieldService getDataDictionaryRemoteFieldService() {
        return KRADServiceLocatorWeb.getDataDictionaryRemoteFieldService();
    }
}
