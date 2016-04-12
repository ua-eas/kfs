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
package org.kuali.kfs.krad.document;

import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

import javax.persistence.MappedSuperclass;

/**
 * Controller that handles requests coming from a <code>TransactionalDocumentView</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
public abstract class TransactionalDocumentBase extends DocumentBase implements TransactionalDocument, SessionDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionalDocumentBase.class);

    /**
     * Default constructor.
     */
    public TransactionalDocumentBase() {
        super();
    }

    /**
     * @see TransactionalDocument#getAllowsCopy()
     *      Checks if copy is set to true in data dictionary and the document instance implements
     *      Copyable.
     */
    public boolean getAllowsCopy() {
        return KRADServiceLocatorWeb.getDocumentDictionaryService().getAllowsCopy(this).booleanValue() &&
                this instanceof Copyable;
    }

    /**
     * This method to check whether the document class implements SessionDocument
     *
     * @return
     */
    public boolean isSessionDocument() {
        return SessionDocument.class.isAssignableFrom(this.getClass());
    }
}
