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
package org.kuali.kfs.krad.datadictionary.impl;

import java.util.List;

import org.kuali.kfs.krad.datadictionary.FieldOverride;

/**
 * A Field Override used to insert elements into a Data Dictionary Bean.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class FieldOverrideForListElementInsertImpl extends FieldOverrideForListElementBase  implements FieldOverride{
	
	private Object insertBefore;
    private Object insertAfter;
    
    public Object getInsertBefore() {
        return insertBefore;
    }

    public void setInsertBefore(Object insertBefore) {
        this.insertBefore = insertBefore;
    }

    public Object getInsertAfter() {
        return insertAfter;
    }


    public void setInsertAfter(Object insertAfter) {
        this.insertAfter = insertAfter;
    }


    protected void varifyConfig()
    {
        if ( insertBefore != null && insertAfter != null )
        {
            throw new RuntimeException("Configuration Error, insertBefore and insertAfter can not be both NOT-NULL");
        }
        if ( insertBefore == null && insertAfter == null )
        {
            throw new RuntimeException("Configuration Error, Either insertBefore or insertAfter should be NOT-NULL");            
        }
    }
    
    private Object getObjectToInsert()
    {
        Object objToInsert = null;
        if ( insertBefore != null )
        {
            objToInsert = insertBefore;
        }
        if ( insertAfter != null )
        {
            if ( objToInsert != null )
            {
                throw new RuntimeException("Configuration Error, insertBefore and insertAfter can not be both NOT-NULL");
            }
            objToInsert = insertAfter;
        }
        if ( objToInsert == null )
        {
            throw new RuntimeException("Configuration Error, Either insertBefore or insertAfter must be NOT-NULL");                        
        }
        return objToInsert;
    }
    
    public Object performFieldOverride(Object bean, Object property) {
        Object objToInsert = getObjectToInsert();
        
        List oldList = (List)property;
        
        int insertPos = getElementPositionInList(getElement(), oldList);

        if ( insertPos == -1 )
        {
            insertPos = oldList.size();
        }
        else
        {
            if ( insertAfter != null )
            {
                insertPos = insertPos + 1;
            }
        }

        if ( objToInsert instanceof List )
        {
            oldList.addAll(insertPos, (List)objToInsert);
        }
        else
        {
            oldList.add(insertPos, objToInsert);
        }
        return oldList;
    }
}
