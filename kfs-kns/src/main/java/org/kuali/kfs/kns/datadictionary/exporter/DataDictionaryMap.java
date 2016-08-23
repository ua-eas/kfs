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
package org.kuali.kfs.kns.datadictionary.exporter;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntry;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class DataDictionaryMap extends DataDictionaryMapBase {

    private DataDictionaryService dataDictionaryService;

    BusinessObjectEntryMapper boMapper = new BusinessObjectEntryMapper();
    MaintenanceDocumentEntryMapper maintDocMapper = new MaintenanceDocumentEntryMapper();
    TransactionalDocumentEntryMapper transDocMapper = new TransactionalDocumentEntryMapper();

    Map<String,Map> ddMap = new HashMap<String,Map>();

    public DataDictionaryMap(DataDictionaryService dataDictionaryService) {
        super();
        this.dataDictionaryService = dataDictionaryService;
    }

    public Object get(Object key) {
        Map subMap = ddMap.get( key );
        if ( subMap == null ) { // need to load from DD
            synchronized( this ) { // ensure only one update access happening at a time
                subMap = ddMap.get( key );
                if ( subMap == null ) { // recheck in case it was loaded by another thread while this one was blocked
                    DataDictionaryEntry entry = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry( key.toString() );
                    // if that fails try just using the simple name if a full class name was passed
                    if ( entry == null && key.toString().contains(".")) {
                    	entry = dataDictionaryService.getDataDictionary().getDictionaryObjectEntry( StringUtils.substringAfterLast( key.toString(), "." ) );
                    }
                    if ( entry != null ) {
                        if ( entry instanceof BusinessObjectEntry ) {
                            subMap = boMapper.mapEntry( (BusinessObjectEntry)entry ).getExportData();
                        } else if ( entry instanceof MaintenanceDocumentEntry ) {
                            subMap = maintDocMapper.mapEntry( (MaintenanceDocumentEntry)entry ).getExportData();
                        } else if ( entry instanceof TransactionalDocumentEntry ) {
                            subMap = transDocMapper.mapEntry( (TransactionalDocumentEntry)entry ).getExportData();
                        }
                    }
                    if ( subMap != null ) {
                        ddMap.put( key.toString(), subMap );
                    }
                }
            }
        }
        return subMap;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
