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
package org.kuali.kfs.kns.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.util.documentserlializer.MaintenanceDocumentPropertySerializibilityEvaluator;
import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.kfs.krad.util.documentserializer.PropertySerializabilityEvaluator;

import java.util.List;


public class BusinessObjectSerializerServiceImpl extends org.kuali.kfs.krad.service.impl.BusinessObjectSerializerServiceImpl {

    @Override
    public PropertySerializabilityEvaluator getPropertySerizabilityEvaluator(Object businessObject) {
        PropertySerializabilityEvaluator evaluator = null;

        String docTypeName = getDocumentDictionaryService().getMaintenanceDocumentTypeName(businessObject.getClass());
        MaintenanceDocumentEntry maintenanceDocumentEntry =
            getDocumentDictionaryService().getMaintenanceDocumentEntry(docTypeName);

        if (maintenanceDocumentEntry instanceof org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry) {
            List<MaintainableSectionDefinition> maintainableSectionDefinitions =
                ((org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry) maintenanceDocumentEntry).getMaintainableSections();
            if (CollectionUtils.isEmpty(maintainableSectionDefinitions)) {
                evaluator = new AlwaysTruePropertySerializibilityEvaluator();
            } else {
                evaluator = new MaintenanceDocumentPropertySerializibilityEvaluator();
                evaluator.initializeEvaluatorForDataObject(businessObject);
            }
        } else {
            evaluator = new AlwaysTruePropertySerializibilityEvaluator();
        }

        return evaluator;
    }
}
