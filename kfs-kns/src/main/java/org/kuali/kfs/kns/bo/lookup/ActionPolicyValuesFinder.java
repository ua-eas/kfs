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
package org.kuali.kfs.kns.bo.lookup;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.util.CodeTranslator;

import java.util.ArrayList;
import java.util.List;


public class ActionPolicyValuesFinder extends KeyValuesBase {
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));
        for (Object key : CodeTranslator.approvePolicyLabels.keySet()) {
            labels.add(new ConcreteKeyValue((String)key,(String) CodeTranslator.approvePolicyLabels.get(key)));
        }
        return labels;
    }
}
