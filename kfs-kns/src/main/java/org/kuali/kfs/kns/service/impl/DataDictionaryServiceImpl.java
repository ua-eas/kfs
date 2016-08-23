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
package org.kuali.kfs.kns.service.impl;

import org.kuali.kfs.kns.datadictionary.KNSDocumentEntry;
import org.kuali.kfs.kns.datadictionary.exporter.DataDictionaryMap;
import org.kuali.kfs.kns.rule.PromptBeforeValidation;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.Map;


public class DataDictionaryServiceImpl extends org.kuali.kfs.krad.service.impl.DataDictionaryServiceImpl implements DataDictionaryService, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private DataDictionaryMap dataDictionaryMap = new DataDictionaryMap(this);

    public DataDictionaryServiceImpl() {
        super();
    }

    public DataDictionaryServiceImpl(DataDictionary dataDictionary) {
        super(dataDictionary);
    }

    /**
     * @see org.kuali.kfs.krad.service.DataDictionaryService#getPromptBeforeValidationClass(java.lang.String)
     */
    public Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass(String docTypeName) {
        Class preRulesCheckClass = null;

        KNSDocumentEntry documentEntry = (KNSDocumentEntry) getDataDictionary().getDocumentEntry(docTypeName);
        preRulesCheckClass = documentEntry.getPromptBeforeValidationClass();

        return preRulesCheckClass;
    }

    public Map getDataDictionaryMap() {
        return dataDictionaryMap;
    }

    @Override
    public void addDataDictionaryLocation(String location) throws IOException {
        if (!ObjectUtils.isNull(applicationContext)) {
            getDataDictionary().addConfigFileLocation(location, applicationContext);
        } else {
            getDataDictionary().addConfigFileLocation(location);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
