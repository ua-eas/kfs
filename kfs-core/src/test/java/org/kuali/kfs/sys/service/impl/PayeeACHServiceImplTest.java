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
package org.kuali.kfs.sys.service.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.rice.krad.bo.BusinessObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PayeeACHServiceImplTest {

    private static PayeeACHServiceImpl payeeACHServiceImpl;

    @BeforeClass
    public static void setUp() throws Exception {
        payeeACHServiceImpl = new PayeeACHServiceImpl();
        payeeACHServiceImpl
            .setBusinessObjectService(new StubBusinessObjectService());
    }

    public void tearDown() throws Exception {
    }

    @Test
    public void noPayeeExists() throws Exception {
        assertFalse(payeeACHServiceImpl.isPayeeSignedUpForACH("test", "0987654321"));
    }

    @Test
    public void payeeExist() throws Exception {
        assertTrue(payeeACHServiceImpl.isPayeeSignedUpForACH("test", "1234567890"));
    }

    @Test
    public void payeeIsNull() throws Exception {
        assertFalse(payeeACHServiceImpl.isPayeeSignedUpForACH(null, null));
    }

    protected static class StubBusinessObjectService implements BusinessObjectService {

        @Override
        public <T extends PersistableBusinessObject> T save(T bo) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<? extends PersistableBusinessObject> save(
            List<? extends PersistableBusinessObject> businessObjects) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PersistableBusinessObject linkAndSave(
            PersistableBusinessObject bo) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<? extends PersistableBusinessObject> linkAndSave(
            List<? extends PersistableBusinessObject> businessObjects) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends BusinessObject> T findBySinglePrimaryKey(
            Class<T> clazz, Object primaryKey) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends BusinessObject> T findByPrimaryKey(Class<T> clazz,
                                                             Map<String, ?> primaryKeys) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public PersistableBusinessObject retrieve(
            PersistableBusinessObject object) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends BusinessObject> Collection<T> findAllOrderBy(
            Class<T> clazz, String sortField, boolean sortAscending) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends BusinessObject> Collection<T> findMatching(
            Class<T> clazz, Map<String, ?> fieldValues) {

            if (fieldValues.get(PdpPropertyConstants.PAYEE_ID_NUMBER) == null)
                return null;
            else if (((String) fieldValues.get(PdpPropertyConstants.PAYEE_ID_NUMBER)).equalsIgnoreCase("1234567890")) {
                List list = new ArrayList();
                list.add(new PayeeACHAccount());
                return list;
            }

            // looks up send empty lists if nothing found.
            return new ArrayList<T>();
        }

        @Override
        public <T extends BusinessObject> Collection<T> findMatching(
                Class<T> clazz, Map<String, ?> fieldValues, int skip, int limit, Instant updatedBefore, Instant updatedAfter, String[] orderBy) {

            if (fieldValues.get(PdpPropertyConstants.PAYEE_ID_NUMBER) == null)
                return null;
            else if (((String) fieldValues.get(PdpPropertyConstants.PAYEE_ID_NUMBER)).equalsIgnoreCase("1234567890")) {
                List list = new ArrayList();
                list.add(new PayeeACHAccount());
                return list;
            }

            // looks up send empty lists if nothing found.
            return new ArrayList<T>();
        }

        @Override
        public int countMatching(Class clazz, Map<String, ?> fieldValues) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int countMatching(Class clazz, Map<String, ?> fieldValues, Instant updatedBefore, Instant updatedAfter) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int countMatching(Class clazz,
                                 Map<String, ?> positiveFieldValues,
                                 Map<String, ?> negativeFieldValues) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public <T extends BusinessObject> Collection<T> findMatchingOrderBy(
            Class<T> clazz, Map<String, ?> fieldValues, String sortField,
            boolean sortAscending) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void delete(PersistableBusinessObject bo) {
            // TODO Auto-generated method stub

        }

        @Override
        public void delete(List<? extends PersistableBusinessObject> boList) {
            // TODO Auto-generated method stub

        }

        @Override
        public void deleteMatching(Class clazz, Map<String, ?> fieldValues) {
            // TODO Auto-generated method stub

        }

        @Override
        public BusinessObject getReferenceIfExists(BusinessObject bo,
                                                   String referenceName) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void linkUserFields(PersistableBusinessObject bo) {
            // TODO Auto-generated method stub

        }

        @Override
        public void linkUserFields(List<PersistableBusinessObject> bos) {
            // TODO Auto-generated method stub

        }

        @Override
        public PersistableBusinessObject manageReadOnly(
            PersistableBusinessObject bo) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
