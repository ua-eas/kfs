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
package org.kuali.kfs.coreservice.impl.component;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import java.io.Serializable;

public class ComponentId implements Serializable {

    private static final long serialVersionUID = 1L;
    
	@Column(name="NMSPC_CD")
    private final String namespaceCode;

    @Column(name="CMPNT_CD")
    private final String componentCode;

    /** this ctor should never be called.  It is only present for hibernate */
    private ComponentId() {
        namespaceCode = null;
        componentCode = null;
    }
    
    public ComponentId(String namespaceCode, String componentCode) {
    	this.namespaceCode = namespaceCode;
    	this.componentCode = componentCode;
    }
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public String getComponentCode() {
        return componentCode;
    }
}

