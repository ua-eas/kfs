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
package org.kuali.kfs.kns.service;

import org.kuali.kfs.kns.document.authorization.DocumentAuthorizer;
import org.kuali.kfs.kns.document.authorization.DocumentPresentationController;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentDictionaryService;

/**
 * This class is a utility service intended to help retrieve objects related to particular documents.
 *
 * @deprecated use {@link DocumentDictionaryService#getDocumentAuthorizer(java.lang.String)}
 */
@Deprecated
public interface DocumentHelperService {

    @Deprecated
    public DocumentPresentationController getDocumentPresentationController(String documentType);

    @Deprecated
    public DocumentPresentationController getDocumentPresentationController(Document document);

    @Deprecated
    public DocumentAuthorizer getDocumentAuthorizer(String documentType);

    @Deprecated
    public DocumentAuthorizer getDocumentAuthorizer(Document document);

}
