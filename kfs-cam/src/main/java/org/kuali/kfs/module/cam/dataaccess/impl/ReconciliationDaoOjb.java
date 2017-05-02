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
package org.kuali.kfs.module.cam.dataaccess.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cam.dataaccess.ReconciliationDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is the data access object for ReconciliationService
 * 
 * @see ReconciliationDaoOjb
 */
public class ReconciliationDaoOjb extends PlatformAwareDaoBaseOjb implements ReconciliationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReconciliationDaoOjb.class);

   
    public boolean isDuplicateEntry(Entry glEntry) {
        // find matching entry from CB_GL_ENTRY_T
        Map<String, Object> glKeys = new LinkedHashMap<String, Object>();
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_YEAR, glEntry.getUniversityFiscalYear());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.CHART_OF_ACCOUNTS_CODE, glEntry.getChartOfAccountsCode());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.ACCOUNT_NUMBER, glEntry.getAccountNumber());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.SUB_ACCOUNT_NUMBER, glEntry.getSubAccountNumber());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_CODE, glEntry.getFinancialObjectCode());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_SUB_OBJECT_CODE, glEntry.getFinancialSubObjectCode());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_BALANCE_TYPE_CODE, glEntry.getFinancialBalanceTypeCode());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_TYPE_CODE, glEntry.getFinancialObjectTypeCode());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_PERIOD_CODE, glEntry.getUniversityFiscalPeriodCode());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE, glEntry.getFinancialDocumentTypeCode());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_SYSTEM_ORIGINATION_CODE, glEntry.getFinancialSystemOriginationCode());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, glEntry.getDocumentNumber());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER, glEntry.getTransactionLedgerEntrySequenceNumber());
        glKeys.put(CamsPropertyConstants.GeneralLedgerEntry.PROJECT_CD, glEntry.getProjectCode());

        Criteria criteria = OJBUtility.buildCriteriaFromMap(glKeys, new GeneralLedgerEntry());
        // is equal to or is null criteria
        Criteria tempCriteria1 = new Criteria();
        Criteria tempCriteria2 = new Criteria();
        tempCriteria1.addEqualTo(CamsPropertyConstants.GeneralLedgerEntry.ORGNIZATION_REFERENCE_ID, glEntry.getOrganizationReferenceId());
        if (StringUtils.isEmpty(glEntry.getOrganizationReferenceId())) {
            tempCriteria2.addIsNull(CamsPropertyConstants.GeneralLedgerEntry.ORGNIZATION_REFERENCE_ID);
            tempCriteria1.addOrCriteria(tempCriteria2);
        }
        criteria.addAndCriteria(tempCriteria1);
        Query query = QueryFactory.newQuery(GeneralLedgerEntry.class, criteria);
        Collection<GeneralLedgerEntry> matchingEntries = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        // if not found, return false
        if (matchingEntries == null || matchingEntries.isEmpty()) {
            return false;
        }
        return true;
    }
}
