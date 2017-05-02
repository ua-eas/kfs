/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.module.ld.businessobject;

import org.kuali.kfs.integration.ld.SegmentedBusinessObject;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.Arrays;
import java.util.Collection;

/**
 * Labor business object specifically for SalaryExpenseTransferDocument ledger balance import functionality.
 */
public class LedgerBalanceForSalaryExpenseTransfer extends LedgerBalance implements SegmentedBusinessObject {

    /**
     * Constructs a LedgerBalanceForSalaryExpenseTransfer
     */
    public LedgerBalanceForSalaryExpenseTransfer() {
        super();
    }

    /**
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#isLookupResultsSegmented()
     */
    public boolean isLookupResultsSegmented() {
        return true;
    }

    /**
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#getSegmentedPropertyNames()
     */
    public Collection<String> getSegmentedPropertyNames() {
        return (Collection<String>) Arrays.asList(AccountingPeriodProperties.namesToArray());
    }

    /**
     * Adds the period amount to the return string. Since the return string cannot have string, multiplies the amount by 100 so the
     * decimal places are not lost.
     *
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#getAdditionalReturnData(java.lang.String)
     */
    public String getAdditionalReturnData(String segmentedPropertyName) {
        String periodCode = LaborConstants.periodCodeMapping.get(segmentedPropertyName);
        KualiDecimal periodAmount = getAmount(periodCode);

        return KRADUtils.convertDecimalIntoInteger(periodAmount);
    }
}
