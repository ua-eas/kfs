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
package org.kuali.kfs.krad.service;

import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

public class KRADServiceLocator {
    public static final String ATTACHMENT_SERVICE = "cf.attachmentService";
    public static final String PERSISTENCE_SERVICE = "cf.persistenceService";
    public static final String PERSISTENCE_STRUCTURE_SERVICE = "cf.persistenceStructureService";
    public static final String NOTE_SERVICE = "cf.noteService";
    public static final String BUSINESS_OBJECT_SERVICE = "cf.businessObjectService";
    public static final String KUALI_CONFIGURATION_SERVICE = "cf.kualiConfigurationService";
    public static final String XML_OBJECT_SERIALIZER_SERVICE = "cf.xmlObjectSerializerService";
    public static final String XML_OBJECT_SERIALIZER_IGNORE_MISSING_FIELDS_SERVICE = "xmlObjectSerializerIgnoreMissingFieldsService";
    public static final String SERIALIZER_SERVICE = "cf.businessObjectSerializerService";
    public static final String SEQUENCE_ACCESSOR_SERVICE = "cf.sequenceAccessorService";
    public static final String KEY_VALUES_SERVICE = "cf.keyValuesService";


    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static AttachmentService getAttachmentService() {
        return getService(ATTACHMENT_SERVICE);
    }

    public static PersistenceService getPersistenceService() {
        return getService(PERSISTENCE_SERVICE);
    }

    public static PersistenceStructureService getPersistenceStructureService() {
        return getService(PERSISTENCE_STRUCTURE_SERVICE);
    }

    public static NoteService getNoteService() {
        return getService(NOTE_SERVICE);
    }

    public static BusinessObjectService getBusinessObjectService() {
        return getService(BUSINESS_OBJECT_SERVICE);
    }

    public static ConfigurationService getKualiConfigurationService() {
        return getService(KUALI_CONFIGURATION_SERVICE);
    }

    public static XmlObjectSerializerService getXmlObjectSerializerService() {
        return getService(XML_OBJECT_SERIALIZER_SERVICE);
    }

    public static XmlObjectSerializerService getXmlObjectSerializerIgnoreMissingFieldsService() {
        return getService(XML_OBJECT_SERIALIZER_IGNORE_MISSING_FIELDS_SERVICE);
    }

    public static BusinessObjectSerializerService getBusinessObjectSerializerService() {
        return getService(SERIALIZER_SERVICE);
    }

    public static SequenceAccessorService getSequenceAccessorService() {
        return getService(SEQUENCE_ACCESSOR_SERVICE);
    }

    public static KeyValuesService getKeyValuesService() {
        return getService(KEY_VALUES_SERVICE);
    }
}
