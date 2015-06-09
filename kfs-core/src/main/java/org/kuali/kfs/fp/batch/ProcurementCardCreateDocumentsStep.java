/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.fp.batch;

import java.util.Date;

import org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step will call a service method to create the procurement card documents from the loaded transaction table.
 */
public class ProcurementCardCreateDocumentsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentsStep.class);
    private ProcurementCardCreateDocumentService procurementCardDocumentService;
    
    /**
     * Name of the parameter to use to check if ProcurementCardDefault accounting defaults are turned on
     */
    public static final String USE_ACCOUNTING_DEFAULT_PARAMETER_NAME = "PROCUREMENT_CARD_ACCOUNTING_DEFAULT_IND";
    /**
     * Name of the parameter to use to check if ProcurementCardDefault card holder defaults are turned on
     */
    public static final String USE_CARD_HOLDER_DEFAULT_PARAMETER_NAME = "PROCUREMENT_CARD_HOLDER_DEFAULT_IND";

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        return procurementCardDocumentService.createProcurementCardDocuments();
    }

    /**
     * @param procurementCardDocumentService The procurementCardDocumentService to set.
     */
    public void setProcurementCardCreateDocumentService(ProcurementCardCreateDocumentService procurementCardDocumentService) {
        this.procurementCardDocumentService = procurementCardDocumentService;
    }
}