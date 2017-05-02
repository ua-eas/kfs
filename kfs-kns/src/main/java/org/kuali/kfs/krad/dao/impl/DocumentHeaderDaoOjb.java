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
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.dao.DocumentHeaderDao;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class is the OJB implementation of the DocumentHeaderDao interface.
 */
public class DocumentHeaderDaoOjb extends PlatformAwareDaoBaseOjb implements DocumentHeaderDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentHeaderDaoOjb.class);

    private Class documentHeaderBaseClass = DocumentHeader.class;

    /**
     * Default constructor
     */
    public DocumentHeaderDaoOjb() {
        super();
    }

    /**
     * @see DocumentHeaderDao#getByDocumentHeaderId(java.lang.String)
     */
    public DocumentHeader getByDocumentHeaderId(String id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KRADPropertyConstants.DOCUMENT_NUMBER, id);

        return (DocumentHeader) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(getDocumentHeaderBaseClass(), criteria));
    }

    /**
     * Method used to define the {@link DocumentHeader} object to use in case clients need to override the class.  Default value is {@link DocumentHeader}.
     *
     * @see DocumentHeaderDao#getDocumentHeaderBaseClass()
     */
    public Class getDocumentHeaderBaseClass() {
        return this.documentHeaderBaseClass;
    }

    /**
     * @param documentHeaderBaseClass the documentHeaderBaseClass to set
     */
    public void setDocumentHeaderBaseClass(Class documentHeaderBaseClass) {
        this.documentHeaderBaseClass = documentHeaderBaseClass;
    }

}
