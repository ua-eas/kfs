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
package org.kuali.kfs.krad.dao.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.krad.bo.LookupResults;
import org.kuali.kfs.krad.bo.SelectedObjectIds;
import org.kuali.kfs.krad.dao.PersistedLookupMetadataDao;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.sql.Timestamp;

public class PersistedLookupMetadataDaoOjb extends PlatformAwareDaoBaseOjb implements PersistedLookupMetadataDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersistedLookupMetadataDaoOjb.class);

    /**
     * @see PersistedLookupMetadataDao#deleteOldLookupResults(java.sql.Timestamp)
     */
    public void deleteOldLookupResults(Timestamp expirationDate) {
        Criteria criteria = new Criteria();
        criteria.addLessThan(KRADPropertyConstants.LOOKUP_DATE, expirationDate);
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(LookupResults.class, criteria));
    }

    /**
     * @see PersistedLookupMetadataDao#deleteOldSelectedObjectIds(java.sql.Timestamp)
     */
    public void deleteOldSelectedObjectIds(Timestamp expirationDate) {
        Criteria criteria = new Criteria();
        criteria.addLessThan(KRADPropertyConstants.LOOKUP_DATE, expirationDate);
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(SelectedObjectIds.class, criteria));
    }
}
