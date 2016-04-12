/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.krad.dao.SessionDocumentDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.krad.bo.SessionDocument;
import org.kuali.kfs.krad.dao.BusinessObjectDao;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the DocumentDao interface.
 */
public class SessionDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements SessionDocumentDao {
    private static Logger LOG = Logger.getLogger(SessionDocumentDaoOjb.class);
    private BusinessObjectDao businessObjectDao;
    

    public SessionDocumentDaoOjb() {
        super();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see SessionDocumentDao#purgeAllSessionDocuments(java.sql.Timestamp)
     */
    public void purgeAllSessionDocuments(Timestamp expirationDate)throws DataAccessException {
    	Criteria criteria = new Criteria();
		criteria.addLessThan(KRADPropertyConstants.LAST_UPDATED_DATE, expirationDate);
		 getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(SessionDocument.class, criteria));
		 //getPersistenceBrokerTemplate().clearCache();
	        
    }

 
}
