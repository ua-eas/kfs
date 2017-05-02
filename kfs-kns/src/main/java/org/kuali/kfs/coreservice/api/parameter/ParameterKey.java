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
package org.kuali.kfs.coreservice.api.parameter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * This class represents the 4-part key which uniquely identifies a parameter.
 *
 * @see ParameterContract
 * @see Parameter
 */
@XmlRootElement(name = ParameterKey.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ParameterKey.Constants.TYPE_NAME, propOrder = {
    ParameterKey.Elements.APPLICATION_ID,
    ParameterKey.Elements.NAMESPACE_CODE,
    ParameterKey.Elements.COMPONENT_CODE,
    ParameterKey.Elements.NAME,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ParameterKey extends AbstractDataTransferObject {

    private static final long serialVersionUID = -4405355319548951283L;

    @XmlElement(name = Elements.APPLICATION_ID, required = true)
    private final String applicationId;

    @XmlElement(name = Elements.NAMESPACE_CODE, required = true)
    private final String namespaceCode;

    @XmlElement(name = Elements.COMPONENT_CODE, required = true)
    private final String componentCode;

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * This constructor should never be called except during JAXB unmarshalling.
     */
    private ParameterKey() {
        this.applicationId = null;
        this.namespaceCode = null;
        this.componentCode = null;
        this.name = null;
    }

    /**
     * Constructs a ParameterKey from the given values.
     */
    private ParameterKey(String applicationId, String namespaceCode, String componentCode, String name) {
        if (StringUtils.isBlank(applicationId)) {
            throw new IllegalArgumentException("applicationId is blank");
        }
        if (StringUtils.isBlank(namespaceCode)) {
            throw new IllegalArgumentException("namespaceCode is blank");
        }
        if (StringUtils.isBlank(componentCode)) {
            throw new IllegalArgumentException("componentCode is blank");
        }
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        this.applicationId = applicationId;
        this.namespaceCode = namespaceCode;
        this.componentCode = componentCode;
        this.name = name;
    }

    /**
     * Creates a ParameterKey from the given required values.
     *
     * @param applicationId the application id, cannot be null or blank
     * @param namespaceCode the namespace code, cannot be null or blank
     * @param componentCode the component code, cannot be null or blank
     * @param name          the parameter name, cannot be null or blank
     * @return the fully-constructed ParameterKey
     * @throws IllegalArgumentException if any arguments are null or blank
     */
    public static ParameterKey create(String applicationId, String namespaceCode, String componentCode, String name) {
        return new ParameterKey(applicationId, namespaceCode, componentCode, name);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public String getName() {
        return name;
    }

    public String getCacheKey() {
        return this.applicationId + ":" + this.namespaceCode + ":" + this.componentCode + ":" + this.name;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "parameterKey";
        final static String TYPE_NAME = "ParameterKeyType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String APPLICATION_ID = "applicationId";
        final static String NAMESPACE_CODE = "namespaceCode";
        final static String COMPONENT_CODE = "componentCode";
        final static String NAME = "name";
    }

}
