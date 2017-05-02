/*
 * Copyright 2012 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kfs.module.ld.batch.dataaccess.impl;

import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

import edu.arizona.kfs.module.ld.batch.dataaccess.LaborEncumbranceDao;

public class LaborEncumbranceDaoJdbc extends PlatformAwareDaoBaseJdbc implements LaborEncumbranceDao {

    protected static final String LABOR_LEDGER_ENTRY_SQL = "select empl_rcd from (\n" + 
                                                            "select empl_rcd, max(TIMESTAMP) as lastUpdated \n" + 
                                                            "from LD_LDGR_ENTR_T \n" + 
                                                            "where emplid = ? \n" + 
                                                            "and position_nbr = ?\n" + 
                                                            "and FIN_BALANCE_TYP_CD = ? \n" + 
                                                            "and account_nbr = ? \n" + 
                                                            "and FIN_OBJECT_CD = ? \n" + 
                                                            "and empl_rcd is not null \n" + 
                                                            "group by empl_rcd \n" + 
                                                            "union all \n" +
                                                            "select -1, to_date('01-JAN-65') \n" + 
                                                            "from dual \n" + 
                                                            "order by lastUpdated desc \n" + 
                                                            ") where rownum = 1";

    // **START AZ** KATTS-21 KevinMcO
    public Integer getEmployeeRecord(String emplid, String positionNumber, String financialBalanceTypeCode, String accountNumber, String financialObjectCode) {
        return new Integer(getSimpleJdbcTemplate().queryForInt(LABOR_LEDGER_ENTRY_SQL, emplid, positionNumber, financialBalanceTypeCode, accountNumber, financialObjectCode));
    }

}
