/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.kns.web.struts.action;

import org.apache.log4j.Logger;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.CommonsMultipartRequestHandler;

import java.util.List;

/**
 * Subclass of the MultipartRequestHandler used by Struts.  This one allows the maximum upload size to be set
 * by the application rather than by an init parameter. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiMultipartRequestHandler extends CommonsMultipartRequestHandler {
    private static final Logger LOG = Logger.getLogger(KualiMultipartRequestHandler.class);

    private String sizeMax;
    
    /**
     * Returns the maximum allowable size, in bytes, of an uploaded file. The
     * value is obtained from the current module's controller configuration.
     *
     * @param mc The current module's configuration.
     *
     * @return The maximum allowable file size, in bytes.
     */
    public long getSizeMax(ModuleConfig mc) {
        return convertSizeToBytes( sizeMax, super.getSizeMax(mc) );
    }

    public String getSizeMaxString() {
        return sizeMax;
    }    

    public void setSizeMax( String sizeString ) {
    	this.sizeMax = sizeString;
    }
    
//    public long convertSizeToBytes(String sizeString, long defaultSize) {
//	return super.convertSizeToBytes(sizeString, defaultSize);
//    }
    
    /**
     * Sets the max size string to the item in the list that represents the largest size.
     */
    public void setMaxUploadSizeToMaxOfList( List<String> sizes ) {
	long maxSize = 0L;
	for ( String size : sizes ) {
	    long currSize = convertSizeToBytes(size, 0L);
	    if ( currSize == 0L ) {
		LOG.warn( "Unable to parse max size (" + size + ").  Ignoring." );
	    }
	    if ( currSize > maxSize ) {
		maxSize = currSize;
		sizeMax = size;
	    }
	}
    }
    
    public long calculateMaxUploadSizeToMaxOfList( List<String> sizes ) {
    	long maxSize = 0L;
    	for ( String size : sizes ) {
    	    long currSize = convertSizeToBytes(size, 0L);
    	    if ( currSize == 0L ) {
    		LOG.warn( "Unable to parse max size (" + size + ").  Ignoring." );
    	    }
    	    if ( currSize > maxSize ) {
    		maxSize = currSize;    		
    	    }
    	}
    	return maxSize;
    }
    
}
