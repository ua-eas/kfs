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
package org.kuali.kfs.kns.web.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a row of fields on the ui.
 */
@Deprecated
public class Row implements java.io.Serializable {

    private static final long serialVersionUID = 5920833652172097098L;
    private List<Field> fields;
    private boolean hidden;

    public Row() {
        fields = new ArrayList<Field>();
        hidden = false;
    }

    public Row(List<Field> fields) {
        this.fields = fields;
        hidden = false;
    }

    public Row(Field field) {
        this.fields = new ArrayList<Field>();
        fields.add(field);
        hidden = false;
    }

    /**
     * @return the fields contained in the row
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to be displayed in the row.
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * @return the hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Field getField(int index) {
        while (fields.size() <= index) {
            Field field = new Field();
            fields.add(field);
        }
        return (Field) fields.get(index);
    }

    public String toString(){
    	StringBuffer sRet = new StringBuffer();
    	sRet.append("[");

    	if(fields != null){
    		for(Field f: fields){
    			sRet.append(f.getPropertyName() + ", ");
    		}

    		sRet.delete(sRet.length()-2, sRet.length());
    	}
    	sRet.append("]");

    	return sRet.toString();

    }
}
