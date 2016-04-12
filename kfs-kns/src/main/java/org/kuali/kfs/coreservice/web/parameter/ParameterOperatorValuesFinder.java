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
package org.kuali.kfs.coreservice.web.parameter;

import org.kuali.kfs.coreservice.api.parameter.EvaluationOperator;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class returns list of parameter operator value pairs.
 */
public class ParameterOperatorValuesFinder extends KeyValuesBase {

	private static final List<KeyValue> KEY_VALUES;
	
	static {
		final List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(EvaluationOperator.ALLOW.getCode(), "Allowed"));
        keyValues.add(new ConcreteKeyValue(EvaluationOperator.DISALLOW.getCode(), "Denied"));
		KEY_VALUES = Collections.unmodifiableList(keyValues);
	}
	
    @Override
	public List<KeyValue> getKeyValues() {
        return KEY_VALUES;
    }
}
