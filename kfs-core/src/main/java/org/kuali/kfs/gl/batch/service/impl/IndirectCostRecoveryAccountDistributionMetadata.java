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

import java.math.BigDecimal;
import java.text.MessageFormat;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

public class IndirectCostRecoveryAccountDistributionMetadata {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryAccountDistributionMetadata.class);

    private static final int MAXIMUM_CONTINUATION_ACCOUNT_ITERATIONS = 10;

    protected ConfigurationService configurationService;

    private String indirectCostRecoveryFinCoaCode;
    private String indirectCostRecoveryAccountNumber;
    private BigDecimal accountLinePercent;
    private boolean replacedIcrAccount;

    public IndirectCostRecoveryAccountDistributionMetadata(IndirectCostRecoveryAccount icrAccount) {
        indirectCostRecoveryFinCoaCode = icrAccount.getIndirectCostRecoveryFinCoaCode();
        indirectCostRecoveryAccountNumber = icrAccount.getIndirectCostRecoveryAccountNumber();
        accountLinePercent = icrAccount.getAccountLinePercent();

        processContinuationAccounts(icrAccount);

        if (LOG.isDebugEnabled()) {
            LOG.debug("IndirectCostRecoveryAccountDistributionMetadata For passed in icrAccount " + icrAccount.getAccountNumber() +
                    " calculated  IndirectCostRecoveryAccountNumber:" + getIndirectCostRecoveryAccountNumber() + " and IndirectCostRecoveryFinCoaCode:" + getIndirectCostRecoveryFinCoaCode());
        }
    }

    private void processContinuationAccounts(IndirectCostRecoveryAccount icrAccount) {

        if (ObjectUtils.isNotNull(icrAccount) && ObjectUtils.isNotNull(icrAccount.getIndirectCostRecoveryAccount()) && icrAccount.getIndirectCostRecoveryAccount().isClosed()) {
            Account replacementICRA = findReplacementICRAAccount(icrAccount);
            if (ObjectUtils.isNotNull(replacementICRA) && isReplacementDifferentThanIndirectCostRecoverAccount(icrAccount, replacementICRA)) {
                if (replacementICRA.isClosed()) {
                    LOG.error("IndirectCostRecoveryAccountDistributionMetadata: passed in icrAccount: " + icrAccount.getAccountNumber() + " and found replacementICRA:" + replacementICRA.getAccountNumber() + " but it is closed, throw an error");
                    throw new UnsupportedOperationException(buildErrorText(icrAccount));
                } else {
                    setReplacedIcrAccount(true);
                    setIndirectCostRecoveryFinCoaCode(replacementICRA.getChartOfAccountsCode());
                    setIndirectCostRecoveryAccountNumber(replacementICRA.getAccountNumber());
                }
            }
        }
    }

    private Account findReplacementICRAAccount(IndirectCostRecoveryAccount icrAccount) {
        Account replacementICRA = icrAccount.getIndirectCostRecoveryAccount();
        int i = 0;

        while ((i < MAXIMUM_CONTINUATION_ACCOUNT_ITERATIONS) && ObjectUtils.isNotNull(replacementICRA) && replacementICRA.isClosed()) {
            replacementICRA = replacementICRA.getContinuationAccount();
            i++;
        }
        return replacementICRA;
    }

    private String buildErrorText(IndirectCostRecoveryAccount icrAccount) {
        String errorText = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.ERROR_ICRACCOUNT_CONTINUATION_ACCOUNT_CLOSED);
        Object[] args = { icrAccount.getIndirectCostRecoveryFinCoaCode(), icrAccount.getIndirectCostRecoveryAccountNumber(), MAXIMUM_CONTINUATION_ACCOUNT_ITERATIONS };
        errorText = MessageFormat.format(errorText, args);
        LOG.debug("buildErrorText() generated error: " + errorText);
        return errorText;
    }

    private boolean isReplacementDifferentThanIndirectCostRecoverAccount(IndirectCostRecoveryAccount icrAccount, Account replacementICRA) {
        return !replacementICRA.equals(icrAccount.getIndirectCostRecoveryAccount());
    }

    public String getIndirectCostRecoveryFinCoaCode() {
        return indirectCostRecoveryFinCoaCode;
    }

    public String getIndirectCostRecoveryAccountNumber() {
        return indirectCostRecoveryAccountNumber;
    }

    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    public void setIndirectCostRecoveryFinCoaCode(String indirectCostRecoveryFinCoaCode) {
        this.indirectCostRecoveryFinCoaCode = indirectCostRecoveryFinCoaCode;
    }

    public void setIndirectCostRecoveryAccountNumber(String indirectCostRecoveryAccountNumber) {
        this.indirectCostRecoveryAccountNumber = indirectCostRecoveryAccountNumber;
    }

    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    public ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public boolean isReplacedIcrAccount() {
        return replacedIcrAccount;
    }

    public void setReplacedIcrAccount(boolean replacedIcrAccount) {
        this.replacedIcrAccount = replacedIcrAccount;
    }
}
