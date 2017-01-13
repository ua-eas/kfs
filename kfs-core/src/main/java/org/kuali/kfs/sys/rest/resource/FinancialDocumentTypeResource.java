/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 The Kuali Foundation
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

package org.kuali.kfs.sys.rest.resource;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("financial-document-types/{documentTypeName}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FinancialDocumentTypeResource {
    private static volatile FinancialSystemDocumentTypeService financialSystemDocumentTypeService;
    private static volatile DocumentTypeService documentTypeService;

    @GET
    public Response getFinancialDocumentType(@PathParam("documentTypeName") final String documentTypeName) {
        final String upperCasedDocumentTypeName = StringUtils.isAllLowerCase(documentTypeName)
                ? documentTypeName.toUpperCase()
                : documentTypeName;
        final DocumentType documentType = getDocumentTypeService().getDocumentTypeByName(upperCasedDocumentTypeName);
        if (documentType == null) {
            return Response.status(404).build();
        }

        Map<String, Object> documentTypeInformation = new HashMap<>();
        documentTypeInformation.put(KFSPropertyConstants.DOCUMENT_TYPE_NAME, upperCasedDocumentTypeName);
        documentTypeInformation.put("currentActiveAccountingDocumentType", getFinancialSystemDocumentTypeService().isCurrentActiveAccountingDocumentType(upperCasedDocumentTypeName));
        documentTypeInformation.put("financialSystemDocumentType", getFinancialSystemDocumentTypeService().isFinancialSystemDocumentType(upperCasedDocumentTypeName));
        documentTypeInformation.put("documentTypeLabel", documentType.getLabel());
        final DocumentType parentDocumentType = getDocumentTypeService().getDocumentTypeById(documentType.getParentId());
        documentTypeInformation.put("parentDocumentTypeName", parentDocumentType.getName());
        return Response.ok(documentTypeInformation).build();
    }

    public static FinancialSystemDocumentTypeService getFinancialSystemDocumentTypeService() {
        if (financialSystemDocumentTypeService == null) {
            financialSystemDocumentTypeService = SpringContext.getBean(FinancialSystemDocumentTypeService.class);
        }
        return financialSystemDocumentTypeService;
    }

    public static void setFinancialSystemDocumentTypeService(FinancialSystemDocumentTypeService financialSystemDocumentTypeService) {
        FinancialDocumentTypeResource.financialSystemDocumentTypeService = financialSystemDocumentTypeService;
    }

    public static DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        }
        return documentTypeService;
    }

    public static void setDocumentTypeService(DocumentTypeService documentTypeService) {
        FinancialDocumentTypeResource.documentTypeService = documentTypeService;
    }
}
