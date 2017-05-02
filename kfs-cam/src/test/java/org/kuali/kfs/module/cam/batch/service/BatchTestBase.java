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
package org.kuali.kfs.module.cam.batch.service;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.kfs.coreservice.api.parameter.Parameter;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.document.service.impl.PurApInfoServiceImpl;
import org.kuali.kfs.module.cam.fixture.CreditMemoAccountFixture;
import org.kuali.kfs.module.cam.fixture.CreditMemoAccountRevisionFixture;
import org.kuali.kfs.module.cam.fixture.CreditMemoDocumentFixture;
import org.kuali.kfs.module.cam.fixture.CreditMemoItemFixture;
import org.kuali.kfs.module.cam.fixture.EntryFixture;
import org.kuali.kfs.module.cam.fixture.FinancialSystemDocumentHeaderFixture;
import org.kuali.kfs.module.cam.fixture.PaymentRequestAccountFixture;
import org.kuali.kfs.module.cam.fixture.PaymentRequestAccountRevisionFixture;
import org.kuali.kfs.module.cam.fixture.PaymentRequestDocumentFixture;
import org.kuali.kfs.module.cam.fixture.PaymentRequestItemFixture;
import org.kuali.kfs.module.cam.fixture.PurchaseOrderAccountFixture;
import org.kuali.kfs.module.cam.fixture.PurchaseOrderCapitalAssetItemFixture;
import org.kuali.kfs.module.cam.fixture.PurchaseOrderCapitalAssetLocationFixture;
import org.kuali.kfs.module.cam.fixture.PurchaseOrderCapitalAssetSystemFixture;
import org.kuali.kfs.module.cam.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.cam.fixture.PurchaseOrderItemFixture;
import org.kuali.kfs.module.cam.fixture.RequisitionAccountFixture;
import org.kuali.kfs.module.cam.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.cam.fixture.RequisitionItemFixture;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This abstract test class will provide the SQL inserts required to perform the testing CAB batch extract related services
 */
public abstract class BatchTestBase extends KualiTestBase {
    protected DateTimeService dateTimeService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        updateLastExtractTime();
        prepareTestDataRecords();
    }

    protected void updateLastExtractTime() {
        updateLastCabExtractTime();
        updateLastPreTagExtractTime();
    }

    protected void updateLastCabExtractTime() {
        Parameter lastExtractTime = findCabExtractTimeParam();
        if (ObjectUtils.isNotNull(lastExtractTime)) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


            Parameter.Builder updatedParm = Parameter.Builder.create(lastExtractTime);
            updatedParm.setValue(fmt.format(DateUtils.addDays(dateTimeService.getCurrentDate(), -1)));
            SpringContext.getBean(ParameterService.class).updateParameter(updatedParm.build());
        } else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    protected Parameter findCabExtractTimeParam() {
        Parameter lastExtractTime = SpringContext.getBean(ParameterService.class).getParameter(CamsConstants.Parameters.NAMESPACE, CamsConstants.Parameters.DETAIL_TYPE_BATCH, CamsConstants.Parameters.LAST_EXTRACT_TIME);
        return lastExtractTime;
    }

    protected void updateLastPreTagExtractTime() {
        Parameter lastExtractTime = findPretagExtractDateParam();
        if (lastExtractTime != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");


            Parameter.Builder updatedParm = Parameter.Builder.create(lastExtractTime);
            updatedParm.setValue(fmt.format(DateUtils.addDays(dateTimeService.getCurrentDate(), -1)));
            SpringContext.getBean(ParameterService.class).updateParameter(updatedParm.build());
        } else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    protected Parameter findPretagExtractDateParam() {

        Parameter lastExtractTime = SpringContext.getBean(ParameterService.class).getParameter(CamsConstants.Parameters.NAMESPACE, CamsConstants.Parameters.DETAIL_TYPE_PRE_ASSET_TAGGING_STEP, CamsConstants.Parameters.LAST_EXTRACT_DATE);
        return lastExtractTime;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void prepareTestDataRecords() throws SQLException {
        // clean first
        FinancialSystemDocumentHeaderFixture.setUpData();
        RequisitionDocumentFixture.setUpData();
        RequisitionItemFixture.setUpData();
        RequisitionAccountFixture.setUpData();
        PurchaseOrderDocumentFixture.setUpData();
        PurchaseOrderItemFixture.setUpData();
        PurchaseOrderAccountFixture.setUpData();
        PurchaseOrderCapitalAssetSystemFixture.setUpData();
        PurchaseOrderCapitalAssetItemFixture.setUpData();
        PurchaseOrderCapitalAssetLocationFixture.setUpData();
        PaymentRequestDocumentFixture.setUpData();
        PaymentRequestItemFixture.setUpData();
        PaymentRequestAccountFixture.setUpData();
        PaymentRequestAccountRevisionFixture.setUpData();
        CreditMemoDocumentFixture.setUpData();
        CreditMemoItemFixture.setUpData();
        CreditMemoAccountFixture.setUpData();
        CreditMemoAccountRevisionFixture.setUpData();
        EntryFixture.setUpData();
    }

    protected class BatchTestBaseFinancialSystemDocumentService implements FinancialSystemDocumentService {
        @Override
        public <T extends Document> Collection<T> findByDocumentHeaderStatusCode(Class<T> clazz, String statusCode) throws WorkflowException {
            return null;
        }

        @Override
        public <T extends Document> Collection<T> findByWorkflowStatusCode(Class<T> clazz, DocumentStatus docStatus) throws WorkflowException {
            return null;
        }

        @Override
        public <T extends Document> Collection<T> findByApplicationDocumentStatus(Class<T> clazz, String applicationDocumentStatus) throws WorkflowException {
            if (clazz == PurchaseOrderDocument.class && StringUtils.equals(applicationDocumentStatus, CamsConstants.PO_STATUS_CODE_OPEN)) {
                List<PurchaseOrderDocument> purchaseOrderDocuments = new ArrayList<>();
                purchaseOrderDocuments.add(PurchaseOrderDocumentFixture.REC1.newRecord());
                purchaseOrderDocuments.add(PurchaseOrderDocumentFixture.REC2.newRecord());
                purchaseOrderDocuments.add(PurchaseOrderDocumentFixture.REC3.newRecord());
                return (Collection<T>)purchaseOrderDocuments;
            }
            return null;
        }

        @Override
        public Collection<FinancialSystemDocumentHeader> findByApplicationDocumentStatus(String applicationDocumentStatus) {
            return null;
        }

        @Override
        public void prepareToCopy(FinancialSystemDocumentHeader oldDocumentHeader, FinancialSystemTransactionalDocument document) {
        }

        @Override
        public Collection<FinancialSystemDocumentHeader> findByWorkflowStatusCode(DocumentStatus docStatus) {
            return null;
        }

        @Override
        public FinancialSystemDocumentHeader findByDocumentNumber(String documentNumber) {
            return null;
        }

        @Override
        public Set<String> getPendingDocumentStatuses() {
            return null;
        }

        @Override
        public Set<String> getSuccessfulDocumentStatuses() {
            return null;
        }

        @Override
        public Set<String> getUnsuccessfulDocumentStatuses() {
            return null;
        }

        @Override
        public DocumentHeader getCorrectingDocumentHeader(String documentId) {
            return null;
        }

        @Override
        public int getFetchMoreIterationLimit() {
            return 0;
        }

        @Override
        public int getMaxResultCap(DocumentSearchCriteria criteria) {
            return 0;
        }
    }

    protected class BatchTestBasePurApInfoService extends PurApInfoServiceImpl {
        @Override
        protected CapitalAssetSystem getCapitalAssetSystemForIndividual(Integer poId, PurApItem purApItem) {
            if (purApItem.getItemIdentifier() == 211) {
                return PurchaseOrderCapitalAssetSystemFixture.REC1.newRecord();
            } else if (purApItem.getItemIdentifier() == 212) {
                return PurchaseOrderCapitalAssetSystemFixture.REC2.newRecord();
            } else if (purApItem.getItemIdentifier() == 221) {
                return PurchaseOrderCapitalAssetSystemFixture.REC3.newRecord();
            } else if (purApItem.getItemIdentifier() == 222) {
                return PurchaseOrderCapitalAssetSystemFixture.REC4.newRecord();
            }
            return null;
        }
    }
}
