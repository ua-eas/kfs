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

import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all MaintenanceDocumentPresentationControllers.
 */
public class MaintenanceDocumentPresentationControllerBase extends DocumentPresentationControllerBase
    implements MaintenanceDocumentPresentationController {

    public boolean canCreate(Class boClass) {
        return KRADServiceLocatorWeb.getDocumentDictionaryService().getAllowsNewOrCopy(
            KRADServiceLocatorWeb.getDocumentDictionaryService().getMaintenanceDocumentTypeName(boClass));
    }

    public boolean canMaintain(Object dataObject) {
        return true;
    }

    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        return new HashSet<String>();
    }

    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getConditionallyReadOnlySectionIds(MaintenanceDocument document) {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getConditionallyRequiredPropertyNames(MaintenanceDocument document) {
        return new HashSet<String>();
    }
}
