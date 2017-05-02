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
package org.kuali.kfs.coa.document.validation.impl;

import org.kuali.kfs.coa.businessobject.ObjectCodeGlobal;
import org.kuali.kfs.coa.businessobject.ObjectCodeGlobalDetail;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.Collection;

/**
 * PreRules checks for the {@link ObjectCodeGlobal} that needs to occur while still in the Struts processing. This includes defaults
 */
public class ObjectCodeGlobalPreRules extends MaintenancePreRulesBase {


    /**
     * Updates the university fiscal year when it is not already set.
     *
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {
        ObjectCodeGlobal bo = (ObjectCodeGlobal) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        Collection<ObjectCodeGlobalDetail> details = bo.getObjectCodeGlobalDetails();

        for (ObjectCodeGlobalDetail detail : details) {
            if (detail.getUniversityFiscalYear() == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("setting fiscal year on ObjectCodeGlobalDetail: " + detail);
                }
                detail.setUniversityFiscalYear(new Integer(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getCollectionFieldDefaultValue(maintenanceDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName(), "objectCodeGlobalDetails", "universityFiscalYear")));
            }
        }

        return true;
    }
}
