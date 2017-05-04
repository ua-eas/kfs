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
package org.kuali.kfs.module.cam.document.service.impl;

import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.module.cam.document.service.GlAndPurApHelperService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;

public class GlAndPurApHelperServiceImpl implements GlAndPurApHelperService {

    @Override
    public String getDocHandlerUrl(String documentNumber, String docTypeName) {
        DocumentTypeService documentTypeService = (DocumentTypeService) KewApiServiceLocator.getDocumentTypeService();
        DocumentType docType = documentTypeService.getDocumentTypeByName(docTypeName);
        String docHandlerUrl = docType.getResolvedDocumentHandlerUrl();
        if (docHandlerUrl.indexOf("?") == -1) {
            docHandlerUrl += "?";
        } else {
            docHandlerUrl += "&";
        }

        docHandlerUrl += KRADConstants.PARAMETER_DOC_ID + "=" + documentNumber + "&" + KRADConstants.PARAMETER_COMMAND + "=" + KewApiConstants.DOCSEARCH_COMMAND;
        return docHandlerUrl;
    }
}
