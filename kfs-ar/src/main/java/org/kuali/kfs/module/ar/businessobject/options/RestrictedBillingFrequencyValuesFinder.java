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
package org.kuali.kfs.module.ar.businessobject.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.service.KeyValuesService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.BillingFrequency;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the link for Billing Frequency excluding LOC Billing type.
 */
public class RestrictedBillingFrequencyValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {

        List<BillingFrequency> boList = (List) SpringContext.getBean(KeyValuesService.class).findAll(BillingFrequency.class);
        List<KeyValue> keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("", ""));
        for (BillingFrequency element : boList) {
            if (element.isActive()) {
                if (!ArConstants.BillingFrequencyValues.LETTER_OF_CREDIT.equals(ArConstants.BillingFrequencyValues.fromCode(element.getFrequency()))) {
                    String displayValue = element.getFrequencyDescription();
                    keyValues.add(new ConcreteKeyValue(element.getFrequency(), displayValue));
                }
            }
        }
        return keyValues;

    }

}
