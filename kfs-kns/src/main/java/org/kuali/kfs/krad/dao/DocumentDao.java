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
package org.kuali.kfs.krad.dao;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentAdHocService;

import java.util.List;

/**
 * This is the data access interface for Document objects.
 */
public interface DocumentDao {

    public <T extends Document> T save(T document);

    public <T extends Document> T findByDocumentHeaderId(Class<T> clazz, String id);

    public <T extends Document> List<T> findByDocumentHeaderIds(Class<T> clazz, List<String> idList);

    public BusinessObjectDao getBusinessObjectDao();

    public DocumentAdHocService getDocumentAdHocService();

}
