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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;

/**
 * A when constraint is a child of a case constraint. It provides a specific additional constraint that should be processed when 
 * the condition itself is true. 
 * 
 * So a case constraint on country, might have a when constraint with value='USA', and another with value='Canada'. Each of these
 * when constraints would define a constraint of their own that would only be processed when the country was USA, or when the country 
 * was Canada. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WhenConstraint implements Constraint {
	protected List<Object> values;
	protected String valuePath;
	protected Constraint constraint;

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
        this.values = values;
    }

    public void setValue(Object value) {	    
	    values = new ArrayList<Object>();
	    values.add(value);
	}

	public String getValuePath() {
		return valuePath;
	}

	public void setValuePath(String valuePath) {
		this.valuePath = valuePath;
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
}
