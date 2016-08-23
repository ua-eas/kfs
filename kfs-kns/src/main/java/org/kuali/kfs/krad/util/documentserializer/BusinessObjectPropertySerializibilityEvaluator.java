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
package org.kuali.kfs.krad.util.documentserializer;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.WorkflowProperties;
import org.kuali.kfs.krad.datadictionary.WorkflowProperty;
import org.kuali.kfs.krad.datadictionary.WorkflowPropertyGroup;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

import java.util.List;

/**
 * This implementation of {@link PropertySerializabilityEvaluator} uses the &lt;workflowProperties&gt; defined within the data dictionary
 * for a document.  If the property being serialized corresponds to one of the properties in the data dictionary, then it will be serialized.
 * If a property specified in the data dictionary corresponds to a business object, then all primitives will be serialized of the business object.
 * All primitives of a primitive that has already been serialized will be serialized as well.   If a property specified in the data dictionary corresponds
 * to a collection, then all primitives of all collection elements will be serialized.
 *
 */
public class BusinessObjectPropertySerializibilityEvaluator extends PropertySerializabilityEvaluatorBase implements PropertySerializabilityEvaluator {

    /**
     * Reads the data dictionary to determine which properties of the document should be serialized.
     *
     * @see PropertySerializabilityEvaluator#initializeEvaluator(Document)
     */
	@Override
    public void initializeEvaluatorForDocument(Document document) {
        DataDictionary dictionary = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary();
        DocumentEntry docEntry = dictionary.getDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        WorkflowProperties workflowProperties = docEntry.getWorkflowProperties();
        List<WorkflowPropertyGroup> groups = workflowProperties.getWorkflowPropertyGroups();

        serializableProperties = new PropertySerializerTrie();

        for (WorkflowPropertyGroup group : groups) {
            // the basepath of each workflow property group is serializable
            if (StringUtils.isEmpty(group.getBasePath())) {
                // automatically serialize all primitives of document when the base path is null or empty string
                serializableProperties.addSerializablePropertyName(document.getBasePathToDocumentDuringSerialization(), false);
            }
            else {
               serializableProperties.addSerializablePropertyName(group.getBasePath(), false);
            }

            for (WorkflowProperty property : group.getWorkflowProperties()) {
                String fullPath;
                if (StringUtils.isEmpty(group.getBasePath())) {
                    fullPath = document.getBasePathToDocumentDuringSerialization() + "." + property.getPath();
                }
                else {
                    fullPath = group.getBasePath() + "." + property.getPath();
                }
                serializableProperties.addSerializablePropertyName(fullPath, false);
            }
        }
    }

}
