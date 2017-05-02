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
package org.kuali.kfs.integration.cg;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.rice.kim.api.identity.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ContractsAndGrantsModuleServiceNoOp implements ContractsAndGrantsModuleService {

    private Logger LOG = Logger.getLogger(getClass());

    public List<Integer> getAllAccountReponsiblityIds() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList<Integer>(0);
    }

    public Person getProjectDirectorForAccount(String chartOfAccountsCode, String accountNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    public Person getProjectDirectorForAccount(Account account) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    public boolean hasValidAccountReponsiblityIdIfNotNull(Account account) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    public boolean isAwardedByFederalAgency(String chartOfAccountsCode, String accountNumber, Collection<String> federalAgencyTypeCodes) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    //@Override
    public List<String> getParentUnits(String unitNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public String getProposalNumberForAccountAndProjectDirector(String chartOfAccountsCode, String accountNumber, String projectDirectorId) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }
}
