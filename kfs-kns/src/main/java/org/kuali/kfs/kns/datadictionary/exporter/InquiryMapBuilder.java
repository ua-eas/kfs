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
package org.kuali.kfs.kns.datadictionary.exporter;

import java.util.Iterator;

import org.kuali.kfs.kns.datadictionary.FieldDefinition;
import org.kuali.kfs.kns.datadictionary.InquiryDefinition;
import org.kuali.kfs.kns.datadictionary.InquirySectionDefinition;
import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

/**
 * InquiryMapBuilder
 * 
 * 
 */
@Deprecated
public class InquiryMapBuilder {

    /**
     * Default constructor
     */
    public InquiryMapBuilder() {
    }


    /**
     * @param inquiry
     * @return ExportMap containing the standard entries for the entry's InquiryDefinition, or null if the given entry has no
     *         inquiryDefinition
     */
    public ExportMap buildInquiryMap(BusinessObjectEntry entry) {
    	try {
	        ExportMap inquiryMap = null;
	
	        if (entry.hasInquiryDefinition()) {
	            InquiryDefinition inquiryDefinition = entry.getInquiryDefinition();
	            inquiryMap = new ExportMap("inquiry");
	
	            inquiryMap.set("title", inquiryDefinition.getTitle());
	
	            inquiryMap.set(buildInquiryFieldsMap(inquiryDefinition));
	        }
	
	        return inquiryMap;
    	} catch ( Exception ex ) {
    		throw new RuntimeException( "Unable to build inquiry Map for " + entry, ex );
    	}
    }

    private ExportMap buildInquiryFieldsMap(InquiryDefinition inquiryDefinition) {
        ExportMap inquiryFieldsMap = new ExportMap("inquiryFields");

        for (Iterator i = inquiryDefinition.getInquirySections().iterator(); i.hasNext();) {
            InquirySectionDefinition inquirySection = (InquirySectionDefinition) i.next();
            for (Iterator iter = inquirySection.getInquiryFields().iterator(); iter.hasNext();) {
                FieldDefinition FieldDefinition = (FieldDefinition) iter.next();
                inquiryFieldsMap.set(MapperUtils.buildFieldMap(FieldDefinition));
            }
        }

        return inquiryFieldsMap;
    }
}
