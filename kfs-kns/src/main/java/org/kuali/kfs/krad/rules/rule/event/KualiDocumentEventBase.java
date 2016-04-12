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
package org.kuali.kfs.krad.rules.rule.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.KRADPropertyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass for document-related events.
 */
abstract public class KualiDocumentEventBase implements KualiDocumentEvent {
    private static final Logger LOG = Logger.getLogger(KualiDocumentEventBase.class);

    private final String description;
    private final String errorPathPrefix;
    protected Document document;

    /**
     * 
     * As a general rule, business rule classes should not change the original object. This constructor was created so that
     * PreRulesCheckEvent, a UI level rule checker, can make changes.
     * 
     * @param description
     * @param errorPathPrefix
     */
    protected KualiDocumentEventBase(String description, String errorPathPrefix) {
        this.description = description;
        this.errorPathPrefix = errorPathPrefix;
    }

    /**
     * Constructs a KualiEvent with the given description and errorPathPrefix for the given document.
     * 
     * @param errorPathPrefix
     * @param document
     * @param description
     */
    public KualiDocumentEventBase(String description, String errorPathPrefix, Document document) {
        this.description = description;
        this.errorPathPrefix = errorPathPrefix;
        this.document = document;

        LOG.debug(description);
    }


    /**
     * @see KualiDocumentEvent#getDocument()
     */
    public final Document getDocument() {
        return document;
    }

    /**
     * @see KualiDocumentEvent#getName()
     */
    public final String getName() {
        return this.getClass().getName();
    }

    /**
     * @return a description of this event
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @see KualiDocumentEvent#getErrorPathPrefix()
     */
    public String getErrorPathPrefix() {
        return errorPathPrefix;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * @see KualiDocumentEvent#validate()
     */
    public void validate() {
        if (getDocument() == null) {
            throw new IllegalArgumentException("invalid (null) event document");
        }
    }

    /**
     * @see KualiDocumentEvent#generateEvents()
     */
    public List<KualiDocumentEvent> generateEvents() {
        return new ArrayList<KualiDocumentEvent>();
    }

    /**
     * Provides null-safe access to the documentNumber of the given document.
     * 
     * @param document
     * @return String containing the documentNumber of the given document, or some indication of why the documentNumber isn't
     *         accessible
     */
    protected static String getDocumentId(Document document) {
        String docId = "(null document)";

        if (document != null) {
            String documentNumber = document.getDocumentNumber();
            if (StringUtils.isBlank(documentNumber)) {
                docId = "(blank " + KRADPropertyConstants.DOCUMENT_NUMBER + ")";
            }
            else {
                docId = documentNumber;
            }
        }

        return docId;
    }
}
