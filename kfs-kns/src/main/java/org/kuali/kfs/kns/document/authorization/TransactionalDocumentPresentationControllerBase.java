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
package org.kuali.kfs.kns.document.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.krad.document.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all TransactionalDocumentPresentationControllers.
 */
public class TransactionalDocumentPresentationControllerBase extends DocumentPresentationControllerBase implements TransactionalDocumentPresentationController {
    private static Log LOG = LogFactory.getLog(TransactionalDocumentPresentationControllerBase.class);

    /**
     * @see DocumentPresentationController#getEditMode(Document)
     */
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = new HashSet();
        return editModes;
    }
}
