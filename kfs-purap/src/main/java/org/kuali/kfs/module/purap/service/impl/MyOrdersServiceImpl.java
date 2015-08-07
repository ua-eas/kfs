package org.kuali.kfs.module.purap.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.bouncycastle.ocsp.Req;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.service.MyOrdersService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.FinancialSystemDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MyOrdersServiceImpl implements MyOrdersService {
    protected BusinessObjectService businessObjectService;

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
        final FinancialSystemDocumentHeader docHeader = (FinancialSystemDocumentHeader)req.getDocumentHeader();
        requisitionRepresentation.put(KFSPropertyConstants.DOCUMENT_NUMBER, docHeader.getDocumentNumber());
        requisitionRepresentation.put(PurapPropertyConstants.REQUISITION_IDENTIFIER, req.getPurapDocumentIdentifier());
        requisitionRepresentation.put(KFSPropertyConstants.VENDOR_NAME, req.getVendorName());
        requisitionRepresentation.put(KFSPropertyConstants.DOCUMENT_DESCRIPTION, docHeader.getDocumentDescription());
        requisitionRepresentation.put(KFSPropertyConstants.WORKFLOW_CREATE_DATE, docHeader.getWorkflowCreateDate());
        requisitionRepresentation.put(KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, docHeader.getWorkflowDocumentStatusCode());
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
