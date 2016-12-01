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
package org.kuali.kfs.gl.batch.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterBalancingStep;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.AccountBalanceHistory;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.EncumbranceHistory;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.dataaccess.AccountBalanceDao;
import org.kuali.kfs.gl.dataaccess.BalancingDao;
import org.kuali.kfs.gl.dataaccess.EncumbranceDao;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Transactional
public class BalancingServiceImpl extends BalancingServiceBaseImpl<EntryHistory, BalanceHistory> implements BalancingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceImpl.class);

    protected BalancingDao balancingDao;
    protected AccountBalanceDao accountBalanceDao;
    protected EncumbranceDao encumbranceDao;

    @Override
    public boolean runBalancing() {
        LOG.debug("runBalancing() started");

        return super.runBalancing();
    }

    @Override
    public File getPosterInputFile() {
        return getFile(GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    @Override
    public File getPosterErrorOutputFile() {
        return getFile(GeneralLedgerConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    @Override
    public File getReversalInputFile() {
        return getFile(GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_VALID_OUTPUT_FILE, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    @Override
    public File getReversalErrorOutputFile() {
        return getFile(GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_ERROR_OUTPUT_FILE, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    @Override
    public File getICRInputFile() {
        return getFile(GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_INPUT_FILE, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    @Override
    public File getICRErrorOutputFile() {
        return getFile(GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_ERROR_OUTPUT_FILE, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    @Override
    public File getICREncumbranceInputFile() {
        return getFile(GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_POSTER_OUTPUT_FILE, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    @Override
    public File getICREncumbranceErrorOutputFile() {
        return getFile(GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_POSTER_ERROR_OUTPUT_FILE, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }

    protected File getFile(final String fileName, final String fileExtension) {
        FilenameFilter filenameFilter = (File dir, String name) -> (name.startsWith(fileName) && name.endsWith(fileExtension));

        return FileUtil.getNewestFile(new File(batchFileDirectoryName), filenameFilter);
    }

    @Override
    public int getPastFiscalYearsToConsider() {
        return Integer.parseInt(parameterService.getParameterValueAsString(PosterBalancingStep.class, GeneralLedgerConstants.Balancing.NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE));
    }

    @Override
    public int getComparisonFailuresToPrintPerReport() {
        return Integer.parseInt(parameterService.getParameterValueAsString(PosterBalancingStep.class, GeneralLedgerConstants.Balancing.NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT));
    }

    @Override
    public String getShortTableLabel(String businessObjectName) {
        Map<String, String> names = new HashMap<>();
        names.put((Entry.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((EntryHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((Balance.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((BalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((AccountBalance.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_LABEL));
        names.put((AccountBalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_LABEL));
        names.put((Encumbrance.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_LABEL));
        names.put((EncumbranceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_LABEL));

        return names.get(businessObjectName) == null ? kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_UNKNOWN_LABEL) : names.get(businessObjectName);
    }

    @Override
    public OriginEntryInformation getOriginEntry(String inputLine, int lineNumber) {
        // We need a OriginEntryFull because that's what updateBalanceHistory is looking for
        OriginEntryFull originEntry = new OriginEntryFull();
        originEntry.setFromTextFileForBatch(inputLine, lineNumber);

        return originEntry;
    }

    @Override
    public void updateEntryHistory(Integer postMode, OriginEntryInformation originEntry) {
        EntryHistory entryHistory = new EntryHistory(originEntry);

        EntryHistory retrievedEntryHistory = (EntryHistory) businessObjectService.retrieve(entryHistory);
        if (ObjectUtils.isNotNull(retrievedEntryHistory)) {
            entryHistory = retrievedEntryHistory;
        }

        entryHistory.addAmount(originEntry.getTransactionLedgerEntryAmount());

        businessObjectService.save(entryHistory);
    }

    @Override
    public void updateBalanceHistory(Integer postMode, OriginEntryInformation originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;
        BalanceHistory balanceHistory = new BalanceHistory(originEntryFull);

        BalanceHistory retrievedBalanceHistory = (BalanceHistory) businessObjectService.retrieve(balanceHistory);
        if (ObjectUtils.isNotNull(retrievedBalanceHistory)) {
            balanceHistory = retrievedBalanceHistory;
        }

        KualiDecimal amount = originEntryFull.getTransactionLedgerEntryAmount();

        // Make sure the amount update properly recognized debit / credit logic. This is modeled after PostBalance#post
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.BALANCE_TYPE);
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OBJECT_TYPE);
        if (originEntryFull.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if (!originEntryFull.getTransactionDebitCreditCode().equals(originEntryFull.getObjectType().getFinObjectTypeDebitcreditCd())) {
                amount = amount.negated();
            }
        }

        balanceHistory.addAmount(originEntryFull.getUniversityFiscalPeriodCode(), amount);

        businessObjectService.save(balanceHistory);
    }

    @Override
    public Balance getBalance(LedgerBalanceHistory ledgerBalanceHistory) {
        Balance balance = new Balance((BalanceHistory) ledgerBalanceHistory);
        return (Balance) businessObjectService.retrieve(balance);
    }

    @Override
    public void customPopulateHistoryTables(Integer fiscalYear) {
        balancingDao.populateAccountBalancesHistory(fiscalYear);
        balancingDao.populateEncumbranceHistory(fiscalYear);
    }

    @Override
    protected boolean doesCustomHistoryExist(Integer fiscalYear) {
        return (this.getHistoryCount(fiscalYear, AccountBalanceHistory.class) > 0 &&
            this.getHistoryCount(fiscalYear, EncumbranceHistory.class) > 0);
    }

    @Override
    protected void deleteCustomHistory(Integer fiscalYear) {
        deleteHistory(fiscalYear, AccountBalanceHistory.class);
        deleteHistory(fiscalYear, EncumbranceHistory.class);

        reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_OBSOLETE_FISCAL_YEAR_DATA_DELETED), (AccountBalanceHistory.class).getSimpleName(), (EncumbranceHistory.class).getSimpleName(), fiscalYear);
        reportWriterService.writeNewLines(1);
    }

    @Override
    protected void updateCustomHistory(Integer postMode, OriginEntryInformation originEntry) {
        this.updateAccountBalanceHistory(originEntry);
        this.updateEncumbranceHistory(originEntry);
    }

    /**
     * Update the account balance history table
     *
     * @param originEntry representing the update details
     */
    protected void updateAccountBalanceHistory(OriginEntryInformation originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;

        // As taken from PostAccountBalance#post: only post transactions where: balance type code is AC or CB or where object type
        // isn't FB and
        // balance type code is EX, IE, PE and CE
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OPTION);
        if ((originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getActualFinancialBalanceTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getBudgetCheckingBalanceTypeCd())) || (originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getExtrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getIntrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getPreencumbranceFinBalTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getCostShareEncumbranceBalanceTypeCd())) && (!originEntryFull.getFinancialObjectTypeCode().equals(originEntryFull.getOption().getFinObjectTypeFundBalanceCd()))) {
            AccountBalanceHistory accountBalanceHistory = new AccountBalanceHistory(originEntry);

            AccountBalanceHistory retrievedAccountBalanceHistory = (AccountBalanceHistory) businessObjectService.retrieve(accountBalanceHistory);
            if (ObjectUtils.isNotNull(retrievedAccountBalanceHistory)) {
                accountBalanceHistory = retrievedAccountBalanceHistory;
            }

            // Following is a copy of PostAccountBalance.updateAccountBalanceReturn since the balancing process is to do this independently
            if (accountBalanceHistory.addAmount(originEntryFull)) {
                businessObjectService.save(accountBalanceHistory);
            }
        }
    }

    @Override
    public void clearHistories() {
        Map<String, Object> fieldValues = new HashMap<>();
        businessObjectService.deleteMatching(EntryHistory.class, fieldValues);
        businessObjectService.deleteMatching(BalanceHistory.class, fieldValues);
        businessObjectService.deleteMatching(EncumbranceHistory.class, fieldValues);
        businessObjectService.deleteMatching(AccountBalanceHistory.class, fieldValues);

        reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_HISTORY_PURGED));
    }

    @Override
    public String getFilenames() {
        return getName(getPosterInputFile())
            + getName(getPosterErrorOutputFile())
            + getName(getReversalInputFile())
            + getName(getReversalErrorOutputFile())
            + getName(getICRInputFile())
            + getName(getICRErrorOutputFile())
            + getName(getICREncumbranceInputFile())
            + getName(getICREncumbranceErrorOutputFile());
    }

    /**
     * Compares entries in the Balance and BalanceHistory tables to ensure the amounts match.
     *
     * @return count is compare failures
     */
    @Override
    protected Integer compareBalanceHistory() {
        Integer countComparisionFailures = 0;

        String balanceTable = persistenceStructureService.getTableName(Balance.class);
        String historyTable = persistenceStructureService.getTableName(balanceHistoryPersistentClass);

        List<Balance> data = ledgerEntryBalanceCachingDao.compareBalanceHistory(balanceTable, historyTable, getFiscalYear());

        if (!data.isEmpty()) {
            for (Iterator<Balance> itr = data.iterator(); itr.hasNext(); ) {
                BalanceHistory balance = createBalanceFromMap((Map<String, Object>) itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(balance, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, balance.getClass().getSimpleName()));
                }
            }
        }

        return countComparisionFailures;
    }

    /**
     * Compares entries in the Entry and EntryHistory tables to ensure the amounts match.
     *
     * @return count is compare failures
     */
    @Override
    protected Integer compareEntryHistory() {
        Integer countComparisionFailures = 0;

        String entryTable = persistenceStructureService.getTableName(Entry.class);
        String historyTable = persistenceStructureService.getTableName(entryHistoryPersistentClass);

        List<Entry> data = ledgerEntryBalanceCachingDao.compareEntryHistory(entryTable, historyTable, getFiscalYear());

        if (!data.isEmpty()) {
            for (Iterator<Entry> itr = data.iterator(); itr.hasNext(); ) {
                EntryHistory entry = createEntryHistoryFromMap((Map<String, Object>) itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(entry, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, entry.getClass().getSimpleName()));
                }
            }
        }

        return countComparisionFailures;
    }

    /**
     * Update the encumbrance history table
     *
     * @param originEntry representing the update details
     */
    protected void updateEncumbranceHistory(OriginEntryInformation originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;

        // PostEncumbrance.verifyTransaction is not run because entries that fail that verification will be in the error poster file
        // which entries
        // are already ignored before being passed to this method.

        // As taken from PostEncumbrance#post: If the encumbrance update code is space or N, or the object type code is FB we don't
        // need to post an encumbrance
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OPTION);
        if ((StringUtils.isBlank(originEntryFull.getTransactionEncumbranceUpdateCode())) || " ".equals(originEntryFull.getTransactionEncumbranceUpdateCode()) || KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(originEntryFull.getTransactionEncumbranceUpdateCode()) || originEntryFull.getOption().getFinObjectTypeFundBalanceCd().equals(originEntryFull.getFinancialObjectTypeCode())) {
            return;
        }

        EncumbranceHistory encumbranceHistory = new EncumbranceHistory(originEntryFull);
        if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntryFull.getTransactionEncumbranceUpdateCode())) {
            encumbranceHistory.setDocumentNumber(originEntryFull.getReferenceFinancialDocumentNumber());
            encumbranceHistory.setOriginCode(originEntryFull.getReferenceFinancialSystemOriginationCode());
            encumbranceHistory.setDocumentTypeCode(originEntryFull.getReferenceFinancialDocumentTypeCode());
        }

        EncumbranceHistory retrievedEncumbranceHistory = (EncumbranceHistory) businessObjectService.retrieve(encumbranceHistory);

        if (ObjectUtils.isNotNull(retrievedEncumbranceHistory)) {
            encumbranceHistory = retrievedEncumbranceHistory;
        }

        // Following is a copy & paste of PostEncumbrance.updateEncumbrance since the balancing process is to do this independently
        encumbranceHistory.addAmount(originEntryFull);

        businessObjectService.save(encumbranceHistory);
    }


    @Override
    protected Map<String, Integer> customCompareHistory() {
        Integer countAccountBalanceComparisionFailure = this.accountBalanceCompareHistory();
        Integer countEncumbranceComparisionFailure = this.encumbranceCompareHistory();

        // Using LinkedHashMap because we want it ordered
        Map<String, Integer> countMap = new LinkedHashMap<>();
        countMap.put((AccountBalanceHistory.class).getSimpleName(), countAccountBalanceComparisionFailure);
        countMap.put((EncumbranceHistory.class).getSimpleName(), countEncumbranceComparisionFailure);

        return countMap;
    }

    /**
     * Does comparision, error printing and returns failure count for account balances
     *
     * @return failure count
     */
    protected Integer accountBalanceCompareHistory() {
        Integer countComparisionFailures = 0;

        String accountBalanceTable = persistenceStructureService.getTableName(AccountBalance.class);
        String historyTable = persistenceStructureService.getTableName(AccountBalanceHistory.class);

        List<AccountBalance> data = ledgerEntryBalanceCachingDao.accountBalanceCompareHistory(accountBalanceTable, historyTable, getFiscalYear());

        if (!data.isEmpty()) {
            for (Iterator<AccountBalance> itr = data.iterator(); itr.hasNext(); ) {
                AccountBalanceHistory accountBalanceHistory = createAccountBalanceHistoryFromMap((Map<String, Object>) itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(accountBalanceHistory, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, accountBalanceHistory.getClass().getSimpleName()));
                }
            }
        } else {
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), (AccountBalanceHistory.class).getSimpleName(), countComparisionFailures, this.getComparisonFailuresToPrintPerReport());
        }
        return countComparisionFailures;
    }

    /**
     * Does comparision, error printing and returns failure count for encumbrances
     *
     * @return failure count
     */
    protected Integer encumbranceCompareHistory() {
        Integer countComparisionFailures = 0;

        String encumbranceTable = persistenceStructureService.getTableName(Encumbrance.class);
        String historyTable = persistenceStructureService.getTableName(EncumbranceHistory.class);

        List<Encumbrance> data = ledgerEntryBalanceCachingDao.encumbranceCompareHistory(encumbranceTable, historyTable, getFiscalYear());

        if (!data.isEmpty()) {
            for (Iterator<Encumbrance> itr = data.iterator(); itr.hasNext(); ) {
                EncumbranceHistory encumbranceHistory = createEncumbranceHistoryFromMap((Map<String, Object>) itr.next());
                countComparisionFailures++;
                if (countComparisionFailures <= this.getComparisonFailuresToPrintPerReport()) {
                    reportWriterService.writeError(encumbranceHistory, new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING), Message.TYPE_WARNING, encumbranceHistory.getClass().getSimpleName()));
                }
            }
        } else {
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.MESSAGE_BATCH_BALANCING_FAILURE_COUNT), (EncumbranceHistory.class).getSimpleName(), countComparisionFailures, this.getComparisonFailuresToPrintPerReport());
        }

        countComparisionFailures = data.size();

        return countComparisionFailures;
    }

    @Override
    protected void customPrintRowCountHistory(Integer fiscalYear) {
        // Note that fiscal year is passed as null for the History tables because for those we shouldn't have data prior to the
        // fiscal year anyway (and
        // if we do it's a bug that should be discovered)
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_ROW_COUNT_HISTORY), this.getShortTableLabel((AccountBalanceHistory.class).getSimpleName()), "(" + (AccountBalanceHistory.class).getSimpleName() + ")", this.getHistoryCount(null, AccountBalanceHistory.class));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ACCOUNT_BALANCE_ROW_COUNT_PRODUCTION), this.getShortTableLabel((AccountBalance.class).getSimpleName()), accountBalanceDao.findCountGreaterOrEqualThan(fiscalYear));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_ROW_COUNT_HISTORY), this.getShortTableLabel((EncumbranceHistory.class).getSimpleName()), "(" + (EncumbranceHistory.class).getSimpleName() + ")", this.getHistoryCount(null, EncumbranceHistory.class));
        reportWriterService.writeStatisticLine(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Balancing.REPORT_ENCUMBRANCE_ROW_COUNT_PRODUCTION), this.getShortTableLabel((Encumbrance.class).getSimpleName()), encumbranceDao.findCountGreaterOrEqualThan(fiscalYear));
    }

    protected BalanceHistory createBalanceFromMap(Map<String, Object> map) {
        BalanceHistory balance = new BalanceHistory();
        balance.setUniversityFiscalYear(((BigDecimal) (map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        balance.setChartOfAccountsCode((String) map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        balance.setAccountNumber((String) map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER));
        balance.setSubAccountNumber((String) map.get(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER));
        balance.setObjectCode((String) map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        balance.setSubObjectCode((String) map.get(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE));
        balance.setBalanceTypeCode((String) map.get(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE));
        balance.setObjectTypeCode((String) map.get(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE));

        balance.setAccountLineAnnualBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.ACCOUNTING_LINE_ACTUALS_BALANCE_AMOUNT)));
        balance.setContractsGrantsBeginningBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.CONTRACT_AND_GRANTS_BEGINNING_BALANCE)));
        balance.setBeginningBalanceLineAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.BEGINNING_BALANCE)));
        balance.setMonth1Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_1_ACCT_AMT)));
        balance.setMonth2Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_2_ACCT_AMT)));
        balance.setMonth3Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_3_ACCT_AMT)));
        balance.setMonth4Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_4_ACCT_AMT)));
        balance.setMonth5Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_5_ACCT_AMT)));
        balance.setMonth6Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_6_ACCT_AMT)));
        balance.setMonth7Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_7_ACCT_AMT)));
        balance.setMonth8Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_8_ACCT_AMT)));
        balance.setMonth9Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_9_ACCT_AMT)));
        balance.setMonth10Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_10_ACCT_AMT)));
        balance.setMonth11Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_11_ACCT_AMT)));
        balance.setMonth12Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_12_ACCT_AMT)));
        balance.setMonth13Amount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.MONTH_13_ACCT_AMT)));

        return balance;
    }

    protected EntryHistory createEntryHistoryFromMap(Map<String, Object> map) {
        EntryHistory entry = new EntryHistory();
        entry.setUniversityFiscalYear(((BigDecimal) (map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        entry.setChartOfAccountsCode((String) map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        entry.setFinancialObjectCode((String) map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        entry.setFinancialBalanceTypeCode((String) map.get(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE));
        entry.setUniversityFiscalPeriodCode((String) map.get(GeneralLedgerConstants.ColumnNames.FISCAL_PERIOD_CODE));
        entry.setTransactionDebitCreditCode((String) map.get(GeneralLedgerConstants.ColumnNames.TRANSACTION_DEBIT_CREDIT_CD));
        entry.setTransactionLedgerEntryAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT)));
        return entry;
    }

    protected AccountBalanceHistory createAccountBalanceHistoryFromMap(Map<String, Object> map) {
        AccountBalanceHistory accountBalanceHistory = new AccountBalanceHistory();
        accountBalanceHistory.setUniversityFiscalYear(((BigDecimal) (map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        accountBalanceHistory.setChartOfAccountsCode((String) map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        accountBalanceHistory.setAccountNumber((String) map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER));
        accountBalanceHistory.setSubAccountNumber((String) map.get(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER));
        accountBalanceHistory.setObjectCode((String) map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        accountBalanceHistory.setSubObjectCode((String) map.get(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE));
        accountBalanceHistory.setCurrentBudgetLineBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.CURRENT_BUDGET_LINE_BALANCE_AMOUNT)));
        accountBalanceHistory.setAccountLineActualsBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ACTUALS_BALANCE_AMOUNT)));
        accountBalanceHistory.setAccountLineEncumbranceBalanceAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_BALANCE_AMOUNT)));
        return accountBalanceHistory;
    }

    protected EncumbranceHistory createEncumbranceHistoryFromMap(Map<String, Object> map) {
        EncumbranceHistory encumbranceHistory = new EncumbranceHistory();
        encumbranceHistory.setUniversityFiscalYear(((BigDecimal) (map.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR))).intValue());
        encumbranceHistory.setChartOfAccountsCode((String) map.get(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE));
        encumbranceHistory.setAccountNumber((String) map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER));
        encumbranceHistory.setSubAccountNumber((String) map.get(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER));
        encumbranceHistory.setObjectCode((String) map.get(GeneralLedgerConstants.ColumnNames.OBJECT_CODE));
        encumbranceHistory.setSubObjectCode((String) map.get(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE));
        encumbranceHistory.setBalanceTypeCode((String) map.get(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE));
        encumbranceHistory.setDocumentTypeCode((String) map.get(GeneralLedgerConstants.ColumnNames.FINANCIAL_DOCUMENT_TYPE_CODE));
        encumbranceHistory.setOriginCode((String) map.get(GeneralLedgerConstants.ColumnNames.ORIGINATION_CODE));
        encumbranceHistory.setDocumentNumber((String) map.get(GeneralLedgerConstants.ColumnNames.DOCUMENT_NUMBER));
        encumbranceHistory.setAccountLineEncumbranceAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_AMOUNT)));
        encumbranceHistory.setAccountLineEncumbranceClosedAmount(convertBigDecimalToKualiDecimal((BigDecimal) map.get(GeneralLedgerConstants.ColumnNames.ACCOUNT_LINE_ENCUMBRANCE_CLOSED_AMOUNT)));
        return encumbranceHistory;
    }

    protected KualiDecimal convertBigDecimalToKualiDecimal(BigDecimal biggy) {
        if (ObjectUtils.isNull(biggy)) {
            return new KualiDecimal(0);
        } else {
            return new KualiDecimal(biggy);
        }
    }

    public void setBalancingDao(BalancingDao balancingDao) {
        this.balancingDao = balancingDao;
    }

    public void setAccountBalanceDao(AccountBalanceDao accountBalanceDao) {
        this.accountBalanceDao = accountBalanceDao;
    }

    public void setEncumbranceDao(EncumbranceDao encumbranceDao) {
        this.encumbranceDao = encumbranceDao;
    }
}
