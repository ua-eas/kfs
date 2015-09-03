/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.event;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.krad.document.Document;

/**
 * Defines methods to CapitalAccountingLinesSameObjectCodeSubTypeEvent.
 */
public class CapitalAccountingLinesSameObjectCodeSubTypeEvent extends AttributedDocumentEventBase {

   public CapitalAccountingLinesSameObjectCodeSubTypeEvent(Document document) {
       this(KFSConstants.EMPTY_STRING, document);
   }
    
    public CapitalAccountingLinesSameObjectCodeSubTypeEvent(String errorPathPrefix, Document document) {
        super("Continue capital assets object subtype checks " + getDocumentId(document), errorPathPrefix, document);        
    }
}
