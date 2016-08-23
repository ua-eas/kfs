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
package org.kuali.kfs.krad.service.impl;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentSerializerService;
import org.kuali.kfs.krad.service.XmlObjectSerializerService;
import org.kuali.kfs.krad.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.kfs.krad.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.kfs.krad.util.documentserializer.SerializationState;

/**
 * Default implementation of the {@link DocumentSerializerService}.  If no &lt;workflowProperties&gt; have been defined in the
 * data dictionary for a document type (i.e. {@link Document#getDocumentPropertySerizabilityEvaluator()} returns an instance of
 * {@link AlwaysTruePropertySerializibilityEvaluator}), then this service will revert to using the {@link XmlObjectSerializerService}
 * bean, which was the old way of serializing a document for routing.  If workflowProperties are defined, then this implementation
 * will selectively serialize items.
 */
public class DocumentSerializerServiceImpl extends SerializerServiceBase implements DocumentSerializerService {

    /**
     * Serializes a document for routing
     *
     * @see DocumentSerializerService#serializeDocumentToXmlForRouting(Document)
     */
    public String serializeDocumentToXmlForRouting(Document document) {
        PropertySerializabilityEvaluator propertySerizabilityEvaluator = document.getDocumentPropertySerizabilityEvaluator();
        evaluators.set(propertySerizabilityEvaluator);
        SerializationState state = createNewDocumentSerializationState(document);
        serializationStates.set(state);

        Object xmlWrapper = wrapDocumentWithMetadata(document);
        String xml;
        if (propertySerizabilityEvaluator instanceof AlwaysTruePropertySerializibilityEvaluator) {
            xml = getXmlObjectSerializerService().toXml(xmlWrapper);
        } else {
            xml = xstream.toXML(xmlWrapper);
        }

        evaluators.set(null);
        serializationStates.set(null);
        return xml;
    }

    /**
     * Wraps the document before it is routed.  This implementation defers to {@link Document#wrapDocumentWithMetadataForXmlSerialization()}.
     *
     * @param document
     * @return may return the document, or may return another object that wraps around the document to provide additional metadata
     */
    protected Object wrapDocumentWithMetadata(Document document) {
        return document.wrapDocumentWithMetadataForXmlSerialization();
    }

}
