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
package org.kuali.kfs.module.tem.document.service;

import org.kuali.kfs.kns.web.ui.Column;
import org.kuali.kfs.module.tem.document.TemCorrectionProcessDocument;
import org.kuali.kfs.module.tem.document.web.struts.TemCorrectionForm;

import java.util.List;

public interface TemCorrectionDocumentService {
    public final static String CORRECTION_TYPE_MANUAL = "M";
    public final static String CORRECTION_TYPE_CRITERIA = "C";
    public final static String CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING = "R";

    public final static String SYSTEM_DATABASE = "D";
    public final static String SYSTEM_UPLOAD = "U";

    String getBatchFileDirectoryName();

    public List<Column> getTableRenderColumnMetadata(String docId);

    void persistAgencyEntryGroupsForDocumentSave(TemCorrectionProcessDocument document, TemCorrectionForm correctionForm);

}
