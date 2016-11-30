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

package org.kuali.kfs.krad.datadictionary;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;

/**
 * Class which generates names for api url's for business objects and documents
 */
public class ApiNamesGenerator {
    private KualiModuleService kualiModuleService;
    private DocumentTypeService documentTypeService;

    public ApiNamesGenerator() {
        this.kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        this.documentTypeService = KewApiServiceLocator.getDocumentTypeService();
    }

    public String convertBusinessObjectEntryToUrlBoName(BusinessObjectEntry boEntry) {
        final String beanName = boEntry.getJstlKey();
        return convertBusinessObjectClassNameToUrlBoName(beanName);
    }

    public String convertBusinessObjectClassNameToUrlBoName(String boClass) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, boClass) + "s";
    }

    public String convertBusinessObjectEntryToModuleName(BusinessObjectEntry boEntry) {
        return generateUrlModuleNameForClass(boEntry.getBusinessObjectClass());
    }

    public String generateUrlModuleNameForClass(Class<?> businessObjectClass) {
        final ModuleService moduleService = kualiModuleService.getResponsibleModuleService(businessObjectClass);
        if (moduleService == null) {
            return null;
        }
        return generateModuleName(moduleService);
    }

    private String generateModuleName(ModuleService moduleService) {
        String moduleServiceName = retrieveModuleName(moduleService);
        if (moduleServiceName.contains("-")) {
            moduleServiceName = StringUtils.substringAfter(moduleServiceName, "-");
        }
        return moduleServiceName;
    }

    private String retrieveModuleName(ModuleService moduleService) {
        return moduleService.getModuleConfiguration().getNamespaceCode().toLowerCase();
    }

    public String convertDocumentEntryToUrlDocumentName(DocumentEntry documentEntry) {
        final String documentTypeLabel = getDocumentLabelFromDocumentClass(documentEntry);
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, documentTypeLabel);
    }

    private String getDocumentLabelFromDocumentClass(DocumentEntry documentEntry) {
        return (isMaintenanceDocument(documentEntry))
                    ? ((org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry)documentEntry).getBusinessObjectClass().getSimpleName() + "sMaintenance"
                    : documentEntry.getDocumentClass().getSimpleName().replaceFirst("Document$","")+"s";
    }

    private String getDocumentLabelFromDocumentType(DocumentEntry documentEntry) {
        final DocumentType documentType = documentTypeService.getDocumentTypeByName(documentEntry.getDocumentTypeName());
        return documentType.getLabel().replaceAll("[^A-Za-z0-9]","");
    }

    private boolean isMaintenanceDocument(DocumentEntry documentEntry) {
        return documentEntry instanceof MaintenanceDocumentEntry && MaintenanceDocument.class.isAssignableFrom(documentEntry.getDocumentClass());
    }

    public String convertDocumentEntryToModuleName(DocumentEntry documentEntry) {
        final Class<?> documentClass = (isMaintenanceDocument(documentEntry))
                ? ((org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry)documentEntry).getBusinessObjectClass()
                : documentEntry.getDocumentClass();
        return generateUrlModuleNameForClass(documentClass);
    }
}
