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

package org.kuali.kfs.fp.businessobject;

import org.kuali.kfs.krad.bo.KualiCodeBase;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * This class is used to represent a travel expense type code business object.
 */
public class TravelExpenseTypeCode extends KualiCodeBase implements MutableInactivatable {
    boolean prepaidExpense;

    private String financialObjectCode;

    /**
     * Default no-arg constructor.
     */
    public TravelExpenseTypeCode() {

    }

    /**
     * @return Returns the prepaidExpense.
     */
    public boolean isPrepaidExpense() {
        return prepaidExpense;
    }

    /**
     * @param prepaidExpense The prepaidExpense to set.
     */
    public void setPrepaidExpense(boolean prepaidExpense) {
        this.prepaidExpense = prepaidExpense;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }
}
