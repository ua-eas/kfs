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
package org.kuali.kfs.coreservice.web.parameter;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.coreservice.impl.parameter.ParameterTypeBo;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KeyValuesService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParameterTypeValuesFinder extends KeyValuesBase {

    @Override
    public List<KeyValue> getKeyValues() {

        // get a list of all ParameterTypes
        KeyValuesService boService = KRADServiceLocator.getKeyValuesService();
        List<ParameterTypeBo> bos = (List<ParameterTypeBo>) boService.findAll(ParameterTypeBo.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if (bos == null) {
            return Collections.emptyList();
        }
        final List<ParameterTypeBo> toReturn = new ArrayList<ParameterTypeBo>(bos);

        // sort using comparator.
        Collections.sort(bos, ParameterTypeComparator.INSTANCE);

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>(bos.size());

        for (ParameterTypeBo bo : bos) {
            labels.add(new ConcreteKeyValue(bo.getCode(), bo.getName()));
        }

        return labels;
    }

    private static class ParameterTypeComparator implements Comparator<ParameterTypeBo> {
        public static final Comparator<ParameterTypeBo> INSTANCE = new ParameterTypeComparator();

        @Override
        public int compare(ParameterTypeBo o1, ParameterTypeBo o2) {
            return o1.getCode().compareTo(o2.getCode());
        }

    }
}
