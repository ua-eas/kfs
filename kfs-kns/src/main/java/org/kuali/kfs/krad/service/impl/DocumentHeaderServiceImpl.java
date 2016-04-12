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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.dao.DocumentHeaderDao;
import org.kuali.kfs.krad.service.DocumentHeaderService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is an implementation of {@link DocumentHeaderService} that facilitates
 * document header management and customization
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@Transactional
public class DocumentHeaderServiceImpl implements DocumentHeaderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentHeaderServiceImpl.class);
    private DocumentHeaderDao documentHeaderDao;

    /**
     * @see DocumentHeaderService#getDocumentHeaderBaseClass()
     */
    public Class<? extends DocumentHeader> getDocumentHeaderBaseClass() {
        Class documentHeaderClass = documentHeaderDao.getDocumentHeaderBaseClass();
        if ( (documentHeaderClass == null) || (!DocumentHeader.class.isAssignableFrom(documentHeaderClass)) ) {
            throw new RuntimeException("invalid document header base class '" + documentHeaderClass + "' returned by dao '" + documentHeaderDao.getClass().getName() + "'");
        }
        return documentHeaderClass;
    }

    /**
     * @see DocumentHeaderService#getDocumentHeaderById(java.lang.String)
     */
    public DocumentHeader getDocumentHeaderById(String documentHeaderId) {
        if (StringUtils.isBlank(documentHeaderId)) {
            throw new IllegalArgumentException("document header id given is blank");
        }
        return documentHeaderDao.getByDocumentHeaderId(documentHeaderId);
    }

    /**
     * @see DocumentHeaderService#saveDocumentHeader(DocumentHeader)
     */
    public void saveDocumentHeader(DocumentHeader documentHeader) {
        KRADServiceLocator.getBusinessObjectService().save(documentHeader);
    }
    
    /**
     * @see DocumentHeaderService#deleteDocumentHeader(DocumentHeader)
     */
    public void deleteDocumentHeader(DocumentHeader documentHeader) {
        KRADServiceLocator.getBusinessObjectService().delete(documentHeader);
    }

    /**
     * dao injected by spring
     * 
     * @param documentHeaderDao
     */
    public void setDocumentHeaderDao(DocumentHeaderDao documentHeaderDao) {
        this.documentHeaderDao = documentHeaderDao;
    }

}
