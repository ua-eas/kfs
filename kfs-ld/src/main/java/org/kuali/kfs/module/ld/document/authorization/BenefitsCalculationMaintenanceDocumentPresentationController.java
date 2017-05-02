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
package org.kuali.kfs.module.ld.document.authorization;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.kns.inquiry.InquiryPresentationController;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.LaborEnterpriseFeedStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Set;

public class BenefitsCalculationMaintenanceDocumentPresentationController extends MaintenanceDocumentPresentationControllerBase implements InquiryPresentationController {

    private ParameterService parameterService;

    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyHiddenPropertyNames(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenPropertyNames(businessObject);

        final Boolean offsetParmValue = getParameterService().getParameterValueAsBoolean(LaborEnterpriseFeedStep.class, LaborConstants.BenefitCalculation.LABOR_BENEFIT_CALCULATION_OFFSET_IND);

        if (!offsetParmValue.booleanValue()) {
            fields.add(LaborConstants.BenefitCalculation.ACCOUNT_CODE_OFFSET_PROPERTY_NAME);
            fields.add(LaborConstants.BenefitCalculation.OBJECT_CODE_OFFSET_PROPERTY_NAME);
        } else {
            fields.remove(LaborConstants.BenefitCalculation.ACCOUNT_CODE_OFFSET_PROPERTY_NAME);
            fields.remove(LaborConstants.BenefitCalculation.OBJECT_CODE_OFFSET_PROPERTY_NAME);
        }

        return fields;
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

}
