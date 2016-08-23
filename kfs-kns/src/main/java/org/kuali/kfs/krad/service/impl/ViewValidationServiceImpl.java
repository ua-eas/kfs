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
package org.kuali.kfs.krad.service.impl;

import org.kuali.kfs.krad.datadictionary.validation.ViewAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.service.DictionaryValidationService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.ViewValidationService;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.view.ViewModel;

/**
 * Implementation of Validation service for views, uses the same validation mechanisms as DictionaryValidationService
 * but uses a different AttributeValueReader to get the correct information from InputFields - which
 * include any AttributeDefinition defined attributes, if defined and not overriden
 *
 * @see ViewValidationService
 */
public class ViewValidationServiceImpl implements ViewValidationService {

    protected DictionaryValidationService dictionaryValidationService;

    @Override
    public DictionaryValidationResult validateView(ViewModel model) {
        return validateView(model.getPostedView(), model);
    }

    @Override
    public DictionaryValidationResult validateView(View view, ViewModel model) {
        return getDictionaryValidationService().validate(new ViewAttributeValueReader(view, model), true);
    }

    public DictionaryValidationService getDictionaryValidationService() {
        if (dictionaryValidationService == null) {
            this.dictionaryValidationService = KRADServiceLocatorWeb.getDictionaryValidationService();
        }
        return dictionaryValidationService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }
}
