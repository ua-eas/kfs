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
package org.kuali.kfs.coa.service.impl;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.sys.util.KfsDateUtils;

import java.sql.Date;
import java.util.Calendar;

public class MockAccountingPeriodService extends AccountingPeriodServiceImpl {
    @Override
    public AccountingPeriod getByDate(final Date currentDate) {
        return new AccountingPeriod() {
            @Override
            public Date getUniversityFiscalPeriodEndDate() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                final int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);

                return new Date(cal.getTimeInMillis());
            }

            @Override
            public Integer getUniversityFiscalYear() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                if (KfsDateUtils.isSameDayOrEarlier(currentDate, Date.valueOf(cal.get(Calendar.YEAR) + "-06-30"))) {
                    return cal.get(Calendar.YEAR);
                }
                return cal.get(Calendar.YEAR)+1;
            }

            @Override
            public String getUniversityFiscalPeriodCode() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                final int month = (cal.get(Calendar.MONTH) + 7) % 12;
                if (month == 0) {
                    return "12";
                } else if (month < 10) {
                    return "0"+month;
                } else {
                    return ""+month;
                }
            }

        };
    }

    @Override
    public AccountingPeriod getByPeriod(final String periodCode, final Integer fiscalYear) {
        return new AccountingPeriod() {
            @Override
            public Date getUniversityFiscalPeriodEndDate() {
                int periodCodeMonth = Integer.parseInt(periodCode) - 7;
                if (periodCodeMonth < 0) {
                    periodCodeMonth += 12;
                }
                int year = (periodCodeMonth >= 7) ? fiscalYear - 1 : fiscalYear;
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH, periodCodeMonth);
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.DAY_OF_MONTH,1);
                final int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
                return KfsDateUtils.clearTimeFields(new Date(cal.getTimeInMillis()));
            }

            @Override
            public Integer getUniversityFiscalYear() {
                return fiscalYear;
            }

            @Override
            public String getUniversityFiscalPeriodCode() {
                return periodCode;
            }
        };
    }
}
