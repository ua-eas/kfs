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

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.krad.dao.BusinessObjectDao;
import org.kuali.kfs.krad.dao.DocumentDao;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentAdHocService;
import org.kuali.kfs.krad.service.KRADServiceLocatorInternal;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.OjbCollectionAware;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the OJB implementation of the DocumentDao interface.
 */
public class DocumentDaoOjb extends PlatformAwareDaoBaseOjb implements DocumentDao, OjbCollectionAware {
    private static final Logger LOG = Logger.getLogger(DocumentDaoOjb.class);
    protected BusinessObjectDao businessObjectDao;
    protected DocumentAdHocService documentAdHocService;


    public DocumentDaoOjb(BusinessObjectDao businessObjectDao, DocumentAdHocService documentAdHocService) {
        super();
        this.businessObjectDao = businessObjectDao;
        this.documentAdHocService = documentAdHocService;
    }

    /**
     * @see org.kuali.dao.DocumentDao#save(null)
     */
    @Override
    public <T extends Document> T save(T document) throws DataAccessException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("About to store document: " + document, new Throwable());
        }
        Document retrievedDocument = findByDocumentHeaderId(document.getClass(), document.getDocumentNumber());
        KRADServiceLocatorInternal.getOjbCollectionHelper().processCollections(this, document, retrievedDocument);
        this.getPersistenceBrokerTemplate().store(document);
        return document;
    }

    /**
     * Retrieve a Document of a specific type with a given document header ID.
     *
     * @param clazz
     * @param id
     * @return Document with given id
     */
    @Override
    public <T extends Document> T findByDocumentHeaderId(Class<T> clazz, String id) {
        List<String> idList = new ArrayList<String>();
        idList.add(id);

        List<T> documentList = findByDocumentHeaderIds(clazz, idList);

        T document = null;
        if ((null != documentList) && (documentList.size() > 0)) {
            document = documentList.get(0);
        }

        return document;
    }

    /**
     * Retrieve a List of Document instances with the given ids
     *
     * @param clazz
     * @param idList
     * @return List
     */
    @Override
    public <T extends Document> List<T> findByDocumentHeaderIds(Class<T> clazz, List<String> idList) {
        Criteria criteria = new Criteria();
        criteria.addIn(KRADPropertyConstants.DOCUMENT_NUMBER, idList);

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);

        // this cast is correct because OJB produces a collection which contains elements of the class defined on the query
        @SuppressWarnings("unchecked")
        List<T> tempList = new ArrayList<T>(this.getPersistenceBrokerTemplate().getCollectionByQuery(query));

        for (T doc : tempList) {
            documentAdHocService.addAdHocs(doc);
        }
        return tempList;
    }

    /**
     * Returns the {@link BusinessObjectDao}
     *
     * @return the {@link BusinessObjectDao}
     * @see DocumentDao#getBusinessObjectDao()
     */
    @Override
    public BusinessObjectDao getBusinessObjectDao() {
        return businessObjectDao;
    }

    /**
     * Sets the {@link BusinessObjectDao}
     *
     * @param businessObjectDao ths {@link BusinessObjectDao}
     */
    public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * @return the documentAdHocService
     */
    @Override
    public DocumentAdHocService getDocumentAdHocService() {
        return this.documentAdHocService;
    }

    /**
     * Setter for injecting the DocumentAdHocService
     *
     * @param dahs
     */
    public void setDocumentAdHocService(DocumentAdHocService dahs) {
        this.documentAdHocService = dahs;
    }
}
