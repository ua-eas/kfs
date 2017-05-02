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
package org.kuali.kfs.krad.service.impl;


import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.BusinessObjectSerializerService;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.DocumentSerializerService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.kfs.krad.util.documentserializer.PropertySerializabilityEvaluator;

public class BusinessObjectSerializerServiceImpl extends SerializerServiceBase implements BusinessObjectSerializerService {

    private DocumentDictionaryService documentDictionaryService;

    /**
     * Serializes a document for routing
     *
     * @see DocumentSerializerService#serializeDocumentToXmlForRouting(Document)
     */
    public String serializeBusinessObjectToXml(Object businessObject) {
        final PropertySerializabilityEvaluator evaluator = getPropertySerizabilityEvaluator(businessObject);
        return doSerialization(evaluator, businessObject, businessObject1 -> {
            String xml;
            if (evaluator instanceof AlwaysTruePropertySerializibilityEvaluator) {
                xml = getXmlObjectSerializerService().toXml(businessObject1);
            }
            else {
                xml = xstream.toXML(businessObject1);
            }
            return xml;
        });
    }

    public PropertySerializabilityEvaluator getPropertySerizabilityEvaluator(Object businessObject) {
        PropertySerializabilityEvaluator evaluator = null;

        String docTypeName = getDocumentDictionaryService().getMaintenanceDocumentTypeName(businessObject.getClass());
        MaintenanceDocumentEntry maintenanceDocumentEntry =
            getDocumentDictionaryService().getMaintenanceDocumentEntry(docTypeName);

        // TODO: need to determine view properties to serialize
        evaluator = new AlwaysTruePropertySerializibilityEvaluator();

        return evaluator;
    }

    protected DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
