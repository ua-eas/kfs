/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.config.property.ConfigurationService;

import edu.arizona.kfs.sys.KFSConstants;

/**
 * Adding history BOs for shipping feed data
 */
public class ShippingInvoiceTrackingHistory extends ShippingInvoiceTracking {
    private static final long serialVersionUID = -3818656113202673722L;
    protected Timestamp loadDate;
    protected String fileName;
    protected ConfigurationService configurationService;    
        
    protected LinkedHashMap<String, String> toStringMapper() {
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
    	map.put("loadDate", loadDate.toString());
    	map.put("fileName", fileName);
    	map.putAll( super.toStringMapper() );
    	return map;
    }

    public Timestamp getLoadDate() {
    	return loadDate;
    }

    public void setLoadDate(Timestamp loadDate) {
    	this.loadDate = loadDate;
    }

    public String getFileName() {
    	return fileName;
    }

    public void setFileName(String fileName) {
    	this.fileName = fileName;
    }    

    public String getFileNameWithoutStagingDirectoryPrefix() {
    	return fileName.replace(configurationService.getPropertyValueAsString(KFSConstants.STAGING_DIRECTORY_KEY), "" );                       
    }
            
}
