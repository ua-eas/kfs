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
package org.kuali.kfs.module.ld.dataaccess;

import org.kuali.kfs.module.ld.businessobject.EmployeeFunding;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceSummary;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerBalanceForYearEndBalanceForward;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the data access object for ledger balance.
 *
 * @see org.kuali.kfs.module.ld.businessobject.LedgerBalance
 */
public interface LaborLedgerBalanceDao {

    /**
     * @param fieldValues    the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return the records of balance entries
     * @deprecated use {@link findBalance(Map fieldValues, boolean isConsolidated, List<String> encumbranceBalanceTypes, boolean noZeroAmounts)} instead.
     * <p>
     * NOTE: unused
     * <p>
     * This method finds the records of balance entries according to input fields and values
     */
    @Deprecated
    public Iterator findBalance(Map fieldValues, boolean isConsolidated, List<String> encumbranceBalanceTypes);

    /**
     * This method finds the records of balance entries according to input fields and values
     *
     * @param fieldValues    the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @param noZeroAmounts  makes sure at least one of the 13 monthly buckets has an amount not equals to zero
     * @return the records of balance entries
     */
    public Iterator<LedgerBalance> findBalance(Map fieldValues, boolean isConsolidated, List<String> encumbranceBalanceTypes, boolean noZeroAmounts);

    /**
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @param fieldValues             the input fields and values
     * @return the size collection of balance entry groups
     * @deprecated use {@link getConsolidatedBalanceRecordCount(Map fieldValues, List<String> encumbranceBalanceTypes, boolean noZeroAmounts)} instead.
     * <p>
     * NOTE: unused
     * <p>
     * This method gets the size collection of balance entry groups according to input fields and values if the entries are required
     * to be consolidated
     */
    @Deprecated
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues, List<String> encumbranceBalanceTypes);

    /**
     * This method gets the size collection of balance entry groups according to input fields and values if the entries are required
     * to be consolidated
     *
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @param fieldValues             the input fields and values
     * @param noZeroAmounts           makes sure at least one of the 13 monthly buckets has an amount not equals to zero
     * @return the size collection of balance entry groups
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues, List<String> encumbranceBalanceTypes, boolean noZeroAmounts);

    /**
     * @param fiscalYear the given fiscal year
     * @return an iterator over all balances for a given fiscal year
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear);

    /**
     * retrieve the current funds according to the given field values
     *
     * @param fieldValues the given field values
     * @return the current funds according to the given field values
     */
    public List<LedgerBalance> findCurrentFunds(Map fieldValues);

    /**
     * retrieve the encumbrance funds according to the given field values
     *
     * @param fieldValues the given field values
     * @return the encumbrance funds according to the given field values
     */
    public List<LedgerBalance> findEncumbranceFunds(Map fieldValues);

    /**
     * retrieve the current funds according to the given field values
     *
     * @param fieldValues the given field values
     * @return the current funds according to the given field values
     */
    public List<EmployeeFunding> findCurrentEmployeeFunds(Map fieldValues);

    /**
     * retrieve the encumbrance funds according to the given field values
     *
     * @param fieldValues the given field values
     * @return the encumbrance funds according to the given field values
     */
    public List<EmployeeFunding> findEncumbranceEmployeeFunds(Map fieldValues);

    /**
     * find the summary of the ledger balances for the given fiscal year and balance types
     *
     * @param fiscalYear   the given fiscal year
     * @param balanceTypes the given balance type codes
     * @return the ledger balances for the given fiscal year and balance types
     */
    public List<LaborBalanceSummary> findBalanceSummary(Integer fiscalYear, Collection<String> balanceTypes);

    /**
     * @param fiscalYear              the given fiscal year
     * @param fieldValues             the given field values
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @return an iterator over all balances for a given fiscal year
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues, List<String> encumbranceBalanceTypes);

    /**
     * @param fiscalYear        the given fiscal year
     * @param fieldValues       the input fields and values
     * @param subFundGroupCodes the given list of qualified sub fund group codes
     * @param fundGroupCodes    the given list of qualified group codes
     * @return an Iterator over all balances for a given year and search criteria that include the accounts of balances must belong
     * to the given sub fund group or fund group
     */
    public Iterator<LedgerBalanceForYearEndBalanceForward> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues, List<String> subFundGroupCodes, List<String> fundGroupCodes);

    /**
     * find the accounts (chart of accounts code + account number) in the given fund groups
     *
     * @param fiscalYear        the given fiscal year
     * @param fieldValues       the input fields and values
     * @param subFundGroupCodes the given list of qualified sub fund group codes
     * @param fundGroupCodes    the given list of qualified group codes
     * @return the accounts (chart of accounts code + account number) in the given fund groups
     */
    public List<List<String>> findAccountsInFundGroups(Integer fiscalYear, Map<String, String> fieldValues, List<String> subFundGroupCodes, List<String> fundGroupCodes);

    /**
     * find all ledger balances matching the given criteria within the given fiscal years
     *
     * @param fieldValues              the given field values
     * @param excludedFieldValues      the given field values that must not be matched
     * @param fiscalYears              the given fiscal years
     * @param balanceTypeList          the given balance type code list
     * @param positionObjectGroupCodes the specified position obejct group codes
     * @return all ledger balances matching the given criteria within the given fiscal years
     */
    public Collection<LedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypeList, List<String> positionObjectGroupCodes);

    /**
     * delete the ledger balance records that were posted prior to the given fiscal year
     *
     * @param fiscalYear          the given fiscal year
     * @param chartOfAccountsCode the given chart of account code
     */
    public void deleteLedgerBalancesPriorToYear(Integer fiscalYear, String chartOfAccountsCode);
}
