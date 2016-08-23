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
package org.kuali.kfs.module.ld.businessobject.inquiry;

import org.kuali.kfs.kns.inquiry.KualiInquirableImpl;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Properties;

public class ExpenseTransferAccountingLineInquirable extends KualiInquirableImpl {
    protected static final String FRINGE_BENEFIT_METHOD_TO_CALL = "calculateFringeBenefit";
    protected static final String FRINGE_BENEFIT_INQUIRY_PAGE_NAME = "/fringeBenefitInquiry.do";

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (businessObject instanceof ExpenseTransferSourceAccountingLine ||
            businessObject instanceof ExpenseTransferTargetAccountingLine) {
            if (attributeName.equalsIgnoreCase("fringeBenefitView")) {
                Object objFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeName);

                Properties parameters = new Properties();
                if (businessObject instanceof ExpenseTransferSourceAccountingLine) {
                    ExpenseTransferSourceAccountingLine sourceAccountingLine = (ExpenseTransferSourceAccountingLine) businessObject;
                    parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, sourceAccountingLine.getChartOfAccountsCode());
                    parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, sourceAccountingLine.getAccountNumber());
                    parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, ObjectUtils.isNotNull(sourceAccountingLine.getSubAccountNumber()) ? sourceAccountingLine.getSubAccountNumber() : "");
                    parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, sourceAccountingLine.getObjectCode().getFinancialObjectCode());
                    parameters.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, sourceAccountingLine.getPayrollEndDateFiscalYear().toString());
                    parameters.put(KFSPropertyConstants.AMOUNT, sourceAccountingLine.getAmount().toString());
                    parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, FRINGE_BENEFIT_METHOD_TO_CALL);
                } else if (businessObject instanceof ExpenseTransferTargetAccountingLine) {
                    ExpenseTransferTargetAccountingLine targetAccountingLine = (ExpenseTransferTargetAccountingLine) businessObject;
                    parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, targetAccountingLine.getChartOfAccountsCode());
                    parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, targetAccountingLine.getAccountNumber());
                    parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, ObjectUtils.isNotNull(targetAccountingLine.getSubAccountNumber()) ? targetAccountingLine.getSubAccountNumber() : "");
                    parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, targetAccountingLine.getObjectCode().getFinancialObjectCode());
                    parameters.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, targetAccountingLine.getPayrollEndDateFiscalYear().toString());
                    parameters.put(KFSPropertyConstants.AMOUNT, targetAccountingLine.getAmount().toString());
                    parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, FRINGE_BENEFIT_METHOD_TO_CALL);
                }

                String fieldValue = objFieldValue == null ? KFSConstants.EMPTY_STRING : objFieldValue.toString();
                // build out base path for return location, use config service
                String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

                final String url = UrlFactory.parameterizeUrl(basePath + FRINGE_BENEFIT_INQUIRY_PAGE_NAME, parameters);


                return new AnchorHtmlData(url, "");
            }

        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);

    }

}
