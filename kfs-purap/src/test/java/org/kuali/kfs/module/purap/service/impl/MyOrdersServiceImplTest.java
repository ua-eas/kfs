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
package org.kuali.kfs.module.purap.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MyOrdersServiceImplTest {
    @Test
    public void testGetLatestOrders_NoOrders() {
        final MyOrdersServiceImpl myOrdersService = new MyOrdersServiceImpl();
        myOrdersService.setBusinessObjectService(new StubBusinessObjectService(new NoOrdersJoeRequisitionRetriever()));

        final Person noOrdersUser = new StubPerson(new NoOrdersJoeRequisitionRetriever());
        final List<Map<String, Object>> orders = myOrdersService.getLatestOrders(noOrdersUser, 4);
        Assert.assertTrue("Orders for ineff should be empty", orders.isEmpty());
    }

    interface RequisitionRetriever {
        RequisitionDocument getRequisitionDocument(String docNumber);

        List<FinancialSystemDocumentHeader> getLatestRequisitionDocumentHeaders();

        String getPrincipalId();

        String getPrincipalName();
    }

    class NoOrdersJoeRequisitionRetriever implements RequisitionRetriever {
        @Override
        public RequisitionDocument getRequisitionDocument(String docNumber) {
            return null; // we know we'll never get here
        }

        @Override
        public List<FinancialSystemDocumentHeader> getLatestRequisitionDocumentHeaders() {
            return new ArrayList<>();
        }

        @Override
        public String getPrincipalId() {
            return "12345678";
        }

        @Override
        public String getPrincipalName() {
            return "noordersjoe";
        }
    }


    class StubPerson implements Person {
        private RequisitionRetriever requisitionRetriever;

        public StubPerson(RequisitionRetriever requisitionRetriever) {
            this.requisitionRetriever = requisitionRetriever;
        }

        @Override
        public String getPrincipalId() {
            return requisitionRetriever.getPrincipalId();
        }

        @Override
        public String getPrincipalName() {
            return requisitionRetriever.getPrincipalName();
        }

        @Override
        public String getEntityId() {
            return null;
        }

        @Override
        public String getEntityTypeCode() {
            return null;
        }

        @Override
        public String getFirstName() {
            return null;
        }

        @Override
        public String getFirstNameUnmasked() {
            return null;
        }

        @Override
        public String getMiddleName() {
            return null;
        }

        @Override
        public String getMiddleNameUnmasked() {
            return null;
        }

        @Override
        public String getLastName() {
            return null;
        }

        @Override
        public String getLastNameUnmasked() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getNameUnmasked() {
            return null;
        }

        @Override
        public String getEmailAddress() {
            return null;
        }

        @Override
        public String getEmailAddressUnmasked() {
            return null;
        }

        @Override
        public String getAddressLine1() {
            return null;
        }

        @Override
        public String getAddressLine1Unmasked() {
            return null;
        }

        @Override
        public String getAddressLine2() {
            return null;
        }

        @Override
        public String getAddressLine2Unmasked() {
            return null;
        }

        @Override
        public String getAddressLine3() {
            return null;
        }

        @Override
        public String getAddressLine3Unmasked() {
            return null;
        }

        @Override
        public String getAddressCity() {
            return null;
        }

        @Override
        public String getAddressCityUnmasked() {
            return null;
        }

        @Override
        public String getAddressStateProvinceCode() {
            return null;
        }

        @Override
        public String getAddressStateProvinceCodeUnmasked() {
            return null;
        }

        @Override
        public String getAddressPostalCode() {
            return null;
        }

        @Override
        public String getAddressPostalCodeUnmasked() {
            return null;
        }

        @Override
        public String getAddressCountryCode() {
            return null;
        }

        @Override
        public String getAddressCountryCodeUnmasked() {
            return null;
        }

        @Override
        public String getPhoneNumber() {
            return null;
        }

        @Override
        public String getPhoneNumberUnmasked() {
            return null;
        }

        @Override
        public String getCampusCode() {
            return null;
        }

        @Override
        public Map<String, String> getExternalIdentifiers() {
            return null;
        }

        @Override
        public boolean hasAffiliationOfType(String affiliationTypeCode) {
            return false;
        }

        @Override
        public List<String> getCampusCodesForAffiliationOfType(String affiliationTypeCode) {
            return null;
        }

        @Override
        public String getEmployeeStatusCode() {
            return null;
        }

        @Override
        public String getEmployeeTypeCode() {
            return null;
        }

        @Override
        public KualiDecimal getBaseSalaryAmount() {
            return null;
        }

        @Override
        public String getExternalId(String externalIdentifierTypeCode) {
            return null;
        }

        @Override
        public String getPrimaryDepartmentCode() {
            return null;
        }

        @Override
        public String getEmployeeId() {
            return null;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void refresh() {

        }
    }

    class StubBusinessObjectService implements BusinessObjectService {
        private RequisitionRetriever requisitionRetriever;

        public StubBusinessObjectService(RequisitionRetriever requisitionRetriever) {
            this.requisitionRetriever = requisitionRetriever;
        }

        @Override
        public <T extends PersistableBusinessObject> T save(T bo) {
            return null;
        }

        @Override
        public List<? extends PersistableBusinessObject> save(List<? extends PersistableBusinessObject> businessObjects) {
            return null;
        }

        @Override
        public PersistableBusinessObject linkAndSave(PersistableBusinessObject bo) {
            return null;
        }

        @Override
        public List<? extends PersistableBusinessObject> linkAndSave(List<? extends PersistableBusinessObject> businessObjects) {
            return null;
        }

        @Override
        public <T extends BusinessObject> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey) {
            return null;
        }

        @Override
        public <T extends BusinessObject> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
            return null;
        }

        @Override
        public PersistableBusinessObject retrieve(PersistableBusinessObject object) {
            return null;
        }

        @Override
        public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends BusinessObject> Collection<T> findAllOrderBy(Class<T> clazz, String sortField, boolean sortAscending) {
            return null;
        }

        @Override
        public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
            if (RequisitionDocument.class.isAssignableFrom(clazz)) {
                List<RequisitionDocument> reqs = new ArrayList<>();
                reqs.add(requisitionRetriever.getRequisitionDocument((String) fieldValues.get(KFSPropertyConstants.DOCUMENT_NUMBER)));
                return (List<T>) reqs;
            }
            return new ArrayList<>();
        }

        @Override
        public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues, int skip, int limit, String[] orderBy) {
            if (RequisitionDocument.class.isAssignableFrom(clazz)) {
                List<RequisitionDocument> reqs = new ArrayList<>();
                reqs.add(requisitionRetriever.getRequisitionDocument((String) fieldValues.get(KFSPropertyConstants.DOCUMENT_NUMBER)));
                return (List<T>) reqs;
            }
            return new ArrayList<>();
        }

        @Override
        public int countMatching(Class clazz, Map<String, ?> fieldValues) {
            return 0;
        }

        @Override
        public int countMatching(Class clazz, Map<String, ?> positiveFieldValues, Map<String, ?> negativeFieldValues) {
            return 0;
        }

        @Override
        public <T extends BusinessObject> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField, boolean sortAscending) {
            if (FinancialSystemDocumentHeader.class.isAssignableFrom(clazz)) {
                return (List<T>) requisitionRetriever.getLatestRequisitionDocumentHeaders();
            }
            return new ArrayList<>();
        }

        @Override
        public void delete(PersistableBusinessObject bo) {

        }

        @Override
        public void delete(List<? extends PersistableBusinessObject> boList) {

        }

        @Override
        public void deleteMatching(Class clazz, Map<String, ?> fieldValues) {

        }

        @Override
        public BusinessObject getReferenceIfExists(BusinessObject bo, String referenceName) {
            return null;
        }

        @Override
        public void linkUserFields(PersistableBusinessObject bo) {

        }

        @Override
        public void linkUserFields(List<PersistableBusinessObject> bos) {

        }

        @Override
        public PersistableBusinessObject manageReadOnly(PersistableBusinessObject bo) {
            return null;
        }
    }
}
