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
package org.kuali.kfs.krad.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.krad.dao.MaintenanceDocumentDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.krad.maintenance.MaintenanceLock;
import org.kuali.kfs.krad.util.KRADPropertyConstants;

/**
 * This class is the OJB implementation of the MaintenanceDocumentDao interface.
 */
public class MaintenanceDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements MaintenanceDocumentDao {

//    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceDocumentDaoOjb.class);

    /**
     * @see MaintenanceDocumentDao#getLockingDocumentNumber(java.lang.String, java.lang.String)
     */
    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber) {

        String lockingDocNumber = "";

        // build the query criteria
        Criteria criteria = new Criteria();
        criteria.addEqualTo("lockingRepresentation", lockingRepresentation);

        // if a docHeaderId is specified, then it will be excluded from the
        // locking representation test.
        if (StringUtils.isNotBlank(documentNumber)) {
            criteria.addNotEqualTo(KRADPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        }

        // attempt to retrieve a document based off this criteria
        MaintenanceLock maintenanceLock = (MaintenanceLock) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(MaintenanceLock.class, criteria));

        // if a document was found, then there's already one out there pending, and
        // we consider it 'locked' and we return the docnumber.
        if (maintenanceLock != null) {
            lockingDocNumber = maintenanceLock.getDocumentNumber();
        }
        return lockingDocNumber;
    }

//    /**
//     * Returns all pending maintenance documents locked by the given business object class.
//     */
//    public Collection getPendingDocumentsForClass(Class dataObjectClass) {
//        Criteria criteria = new Criteria();
//        criteria.addLike("lockingRepresentation", "%" + dataObjectClass.getName() + "%");
//
//        Collection maintenanceLocks = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(MaintenanceLock.class, criteria));
//
//        if (!maintenanceLocks.isEmpty()) {
//            criteria = new Criteria();
//            Collection<String> documentNumbers = new ArrayList();
//
//            for (Object maintenanceLock : maintenanceLocks) {
//                documentNumbers.add(((MaintenanceLock) maintenanceLock).getDocumentNumber());
//            }
//            criteria.addIn("documentNumber", documentNumbers);
//
//            MaintenanceDocumentEntry entry = KRADServiceLocatorInternal.getDataDictionaryService().getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(dataObjectClass);
//            return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(entry.getStandardDocumentBaseClass(), criteria));
//        } else {
//            return maintenanceLocks;
//        }
//    }

    /**
     * @see MaintenanceDocumentDao#deleteLocks(java.lang.String)
     */
    public void deleteLocks(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        QueryByCriteria query = new QueryByCriteria(MaintenanceLock.class, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(query);
    }

    /**
     * @see MaintenanceDocumentDao#storeLocks(java.util.List)
     */
    public void storeLocks(List<MaintenanceLock> maintenanceLocks) {
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            getPersistenceBrokerTemplate().store(maintenanceLock);
        }
    }

}
