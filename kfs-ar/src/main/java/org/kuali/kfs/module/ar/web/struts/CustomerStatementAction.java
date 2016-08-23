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
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.util.WebUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.report.util.CustomerStatementResultHolder;
import org.kuali.kfs.module.ar.service.AccountsReceivablePdfHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsWebUtils;
import org.kuali.kfs.kns.web.struts.action.KualiAction;
import org.kuali.kfs.krad.util.UrlFactory;

/**
 * This class handles Actions for lookup flow
 */
public class CustomerStatementAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerStatementAction.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerStatementForm csForm = (CustomerStatementForm)form;
        csForm.clear();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = getApplicationBaseUrl();
        CustomerStatementForm csForm = (CustomerStatementForm) form;
        String chartCode = csForm.getChartCode();
        chartCode = chartCode==null?"":chartCode;
        String orgCode = csForm.getOrgCode();
        orgCode = orgCode==null?"":orgCode;
        String customerNumber = csForm.getCustomerNumber();
        customerNumber = customerNumber==null?"":customerNumber;
        String accountNumber = csForm.getAccountNumber();
        accountNumber = accountNumber==null?"":accountNumber;

        String statementFormat = csForm.getStatementFormat();
        if (StringUtils.isBlank(statementFormat)) {
            statementFormat = ArConstants.STATEMENT_FORMAT_SUMMARY;
        }
        String includeZeroBalanceCustomers = csForm.getIncludeZeroBalanceCustomers();
        if (StringUtils.isBlank(includeZeroBalanceCustomers)) {
            includeZeroBalanceCustomers = ArConstants.INCLUDE_ZERO_BALANCE_NO;
        }


        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        List<CustomerStatementResultHolder> reports = new ArrayList<>();

        StringBuilder fileName = new StringBuilder();

        if ( StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
            reports = reportService.generateStatementByBillingOrg(chartCode, orgCode, statementFormat, includeZeroBalanceCustomers);
            fileName.append(chartCode);
            fileName.append(orgCode);
        } else if (StringUtils.isNotBlank(customerNumber)) {
            reports = reportService.generateStatementByCustomer(customerNumber.toUpperCase(), statementFormat, includeZeroBalanceCustomers);
            fileName.append(customerNumber);
        } else if (StringUtils.isNotBlank(accountNumber)) {
            reports = reportService.generateStatementByAccount(accountNumber, statementFormat, includeZeroBalanceCustomers);
            fileName.append(accountNumber);
        }
        fileName.append("-StatementBatchPDFs.pdf");

        if (reports.size() != 0) {
            List<byte[]> contents = new ArrayList<>();
            for (CustomerStatementResultHolder customerStatementResultHolder : reports) {
                File file = customerStatementResultHolder.getFile();
                byte[] data = Files.readAllBytes(file.toPath());
                contents.add(data);
            }

            ByteArrayOutputStream baos = SpringContext.getBean(AccountsReceivablePdfHelperService.class).buildPdfOutputStream(contents);
            WebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, baos, fileName.toString());

            // update reported data for the detailed statement
            if (statementFormat.equalsIgnoreCase(ArConstants.STATEMENT_FORMAT_DETAIL)) {
                CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
                for (CustomerStatementResultHolder data : reports) {
                    // update reported invoice info
                    if (data.getInvoiceNumbers() != null) {
                        List<String> invoiceNumbers = data.getInvoiceNumbers();
                        for (String number : invoiceNumbers) {
                            customerInvoiceDocumentService.updateReportedDate(number);
                        }
                    }
                    // update reported customer info
                    customerInvoiceDocumentService.updateReportedInvoiceInfo(data);
                }
            }

            return null;
        }
        csForm.setMessage("No Reports Generated");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
