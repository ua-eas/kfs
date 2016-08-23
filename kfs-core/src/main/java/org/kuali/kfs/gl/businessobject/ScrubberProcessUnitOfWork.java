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
package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.util.Calendar;

/**
 * A class to hold the current unit of work the scrubber is using
 */
public class ScrubberProcessUnitOfWork {
    protected Integer univFiscalYr = 0;
    protected String finCoaCd = "";
    protected String accountNbr = "";
    protected String subAcctNbr = "";
    protected String finBalanceTypCd = "";
    protected String fdocTypCd = "";
    protected String fsOriginCd = "";
    protected String fdocNbr = "";
    protected Date fdocReversalDt = new Date(Calendar.getInstance().getTime().getTime());
    protected String univFiscalPrdCd = "";

    protected boolean errorsFound = false;
    protected KualiDecimal offsetAmount = KualiDecimal.ZERO;

    public ScrubberProcessUnitOfWork() {
    }

    public ScrubberProcessUnitOfWork(OriginEntryInformation e) {
        univFiscalYr = e.getUniversityFiscalYear();
        finCoaCd = e.getChartOfAccountsCode();
        accountNbr = e.getAccountNumber();
        subAcctNbr = e.getSubAccountNumber();
        finBalanceTypCd = e.getFinancialBalanceTypeCode();
        fdocTypCd = e.getFinancialDocumentTypeCode();
        fsOriginCd = e.getFinancialSystemOriginationCode();
        fdocNbr = e.getDocumentNumber();
        fdocReversalDt = e.getFinancialDocumentReversalDate();
        univFiscalPrdCd = e.getUniversityFiscalPeriodCode();
    }

    /**
     * Determines if an entry belongs to this unit of work
     *
     * @param e the entry to check
     * @return true if it belongs to this unit of work, false otherwise
     */
    public boolean isSameUnitOfWork(OriginEntryInformation e) {
        return univFiscalYr.equals(e.getUniversityFiscalYear()) && finCoaCd.equals(e.getChartOfAccountsCode()) && accountNbr.equals(e.getAccountNumber()) && subAcctNbr.equals(e.getSubAccountNumber()) && finBalanceTypCd.equals(e.getFinancialBalanceTypeCode()) && fdocTypCd.equals(e.getFinancialDocumentTypeCode()) && fsOriginCd.equals(e.getFinancialSystemOriginationCode()) && fdocNbr.equals(e.getDocumentNumber()) && ObjectHelper.isEqual(fdocReversalDt, e.getFinancialDocumentReversalDate()) && univFiscalPrdCd.equals(e.getUniversityFiscalPeriodCode());
    }

    /**
     * Converts this unit of work info to a String
     *
     * @return a String representation of this UnitOfWorkInfo
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return univFiscalYr + finCoaCd + accountNbr + subAcctNbr + finBalanceTypCd + fdocTypCd + fsOriginCd + fdocNbr + fdocReversalDt + univFiscalPrdCd;
    }

    public boolean isErrorsFound() {
        return errorsFound;
    }

    public void setErrorsFound(boolean errorsFound) {
        this.errorsFound = errorsFound;
    }

    public KualiDecimal getOffsetAmount() {
        return offsetAmount;
    }

    public void setOffsetAmount(KualiDecimal offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

}
