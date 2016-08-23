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
package org.kuali.kfs.kns.document.authorization;

import org.kuali.kfs.krad.document.Document;

import java.util.Set;

/**
 * The DocumentPresentationController class is used for non-user related lock down
 *
 *
 */
public interface TransactionalDocumentPresentationController extends DocumentPresentationController {
	 /**
     * @param document
     * @return Set of operations that allow to take on that document.
     */
    public Set<String> getEditModes(Document document);


}

