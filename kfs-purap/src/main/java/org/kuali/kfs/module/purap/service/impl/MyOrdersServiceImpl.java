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
package org.kuali.kfs.module.purap.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.service.MyOrdersService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kim.api.identity.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MyOrdersServiceImpl implements MyOrdersService {
    protected BusinessObjectService businessObjectService;

    protected static final List<String> DOCUMENT_STATUS_CODES = Arrays.asList(DocumentStatus.SAVED.getCode(),
        DocumentStatus.CANCELED.getCode(), DocumentStatus.DISAPPROVED.getCode(), DocumentStatus.ENROUTE.getCode(),
        DocumentStatus.EXCEPTION.getCode(), DocumentStatus.FINAL.getCode(), DocumentStatus.INITIATED.getCode(),
        DocumentStatus.RECALLED.getCode(), DocumentStatus.PROCESSED.getCode());


    @Override
    public List<Map<String, Object>> getLatestOrders(Person user, Integer count) {
        final List<FinancialSystemDocumentHeader> latestRequisitions = findLatestRequisitionsForUser(user, count);
        if (latestRequisitions.isEmpty()) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> myOrders = latestRequisitions.stream()
            .map((FinancialSystemDocumentHeader docHeader) -> retrieveRequisition(docHeader.getDocumentNumber()))
            .filter((RequisitionDocument req) -> !ObjectUtils.isNull(req))
            .map((RequisitionDocument req) -> convertDocumentInfoToMap(req))
            .collect(Collectors.toList());
        return myOrders;
    }

    protected Map<String, Object> convertDocumentInfoToMap(RequisitionDocument req) {
        Map<String, Object> requisitionRepresentation = new ConcurrentHashMap<>();
        final FinancialSystemDocumentHeader docHeader = (FinancialSystemDocumentHeader) req.getDocumentHeader();
        requisitionRepresentation.put(KFSPropertyConstants.DOCUMENT_NUMBER, docHeader.getDocumentNumber());
        requisitionRepresentation.put(PurapPropertyConstants.REQUISITION_IDENTIFIER, req.getPurapDocumentIdentifier());
        if (StringUtils.isNotBlank(req.getVendorName())) {
            requisitionRepresentation.put(KFSPropertyConstants.VENDOR_NAME, req.getVendorName());
        }
        requisitionRepresentation.put(KFSPropertyConstants.DOCUMENT_DESCRIPTION, docHeader.getDocumentDescription());
        requisitionRepresentation.put(KFSPropertyConstants.WORKFLOW_CREATE_DATE, docHeader.getWorkflowCreateDate());
        requisitionRepresentation.put(KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, DocumentStatus.fromCode(docHeader.getWorkflowDocumentStatusCode()).getLabel());
        return requisitionRepresentation;
    }

    protected RequisitionDocument retrieveRequisition(String documentNumber) {
        Map<String, String> fieldValues = new ConcurrentHashMap<>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        final Collection<RequisitionDocument> requistionDocuments = getBusinessObjectService().findMatching(RequisitionDocument.class, fieldValues);
        RequisitionDocument req = null;
        for (RequisitionDocument requisitionDocument : requistionDocuments) {
            req = requisitionDocument;
        }
        return req;
    }

    protected List<FinancialSystemDocumentHeader> findLatestRequisitionsForUser(Person user, Integer count) {
        Map<String, Object> fieldValues = new ConcurrentHashMap<>();
        fieldValues.put(KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, PurapConstants.REQUISITION_DOCUMENT_TYPE);
        fieldValues.put(KFSPropertyConstants.INITIATOR_PRINCIPAL_ID, user.getPrincipalId());
        fieldValues.put(KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, DOCUMENT_STATUS_CODES);
        Collection<FinancialSystemDocumentHeader> headers = getBusinessObjectService().findMatchingOrderBy(FinancialSystemDocumentHeader.class, fieldValues, KFSPropertyConstants.WORKFLOW_CREATE_DATE, false);
        List<FinancialSystemDocumentHeader> limitedHeaders = new ArrayList<>();
        int c = 0;
        for (FinancialSystemDocumentHeader header : headers) {
            if (c < count) {
                limitedHeaders.add(header);
            }
            c += 1;
        }
        return limitedHeaders;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
