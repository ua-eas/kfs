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
package org.kuali.kfs.coa.businessobject;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AccountTest {
    private static final String TEST_DATE_1_TODAY = "04/22/2002 19:48";

    private static final String TEST_DATE_2_TODAY = "04/22/2002 10:23";
    private static final String TEST_DATE_2_YESTERDAY = "04/21/2002 10:23";
    private static final String TEST_DATE_2_TOMORROW = "04/23/2002 10:23";

    private static final String TEST_DATE_3_TODAY = "04/22/2002 06:14";

    // pass this a name, and it returns a setup timestamp instance
    private Timestamp getTimestamp(String timestampString) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            return new Timestamp((sdf.parse(timestampString)).getTime());
        } catch (ParseException e) {
            Assert.assertNull("Timestamp String was not parseable", e);
        }
        return null;
    }

    private Date getDate(String dateString) {
        return new Date(getTimestamp(dateString).getTime());
    }

    // since all the tests are doing the same thing, this is centralized
    private void doTest(String expirationDateString, String testDateString, boolean expectedResult) {
        Date expirationDate = getDate(expirationDateString);
        Date testDate = getDate(testDateString);

        // setup the account, and set its expiration date
        Account account = new Account();
        account.setAccountExpirationDate(expirationDate);

        // test against isExpired, and get the result
        boolean actualResult = account.isExpired(testDate);

        // compare the result to what was expected
        Assert.assertEquals(expectedResult, actualResult);
    }

    // if date of expiration and date of today is the same date (time excluded)
    // then the account is not considered expired
    @Test
    public void testIsExpiredToday_ExpirationDateToday_ExpirationDateEarlierTime() {
        doTest(TEST_DATE_2_TODAY, TEST_DATE_1_TODAY, false);
    }

    // if date of expiration and date of today is the same date (time excluded)
    // then the account is not considered expired
    @Test
    public void testIsExpiredToday_ExpirationDateToday_ExpirationDateLaterTime() {
        doTest(TEST_DATE_2_TODAY, TEST_DATE_3_TODAY, false);
    }

    // if date of expiration is one day later than day of testDate, fail
    @Test
    public void testIsExpiredToday_ExpirationDateTomorrow() {
        doTest(TEST_DATE_2_TOMORROW, TEST_DATE_1_TODAY, false);
    }

    // if date of expiration is one day earlier than day of testDate, succeed
    @Test
    public void testIsExpiredToday_ExpirationDateYesterday() {
        doTest(TEST_DATE_2_YESTERDAY, TEST_DATE_1_TODAY, true);
    }
}
