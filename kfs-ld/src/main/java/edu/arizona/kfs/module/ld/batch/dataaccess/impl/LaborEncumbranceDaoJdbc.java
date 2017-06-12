package edu.arizona.kfs.module.ld.batch.dataaccess.impl;

import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

import edu.arizona.kfs.module.ld.batch.dataaccess.LaborEncumbranceDao;

public class LaborEncumbranceDaoJdbc extends PlatformAwareDaoBaseJdbc implements LaborEncumbranceDao {

	protected static final String LABOR_LEDGER_ENTRY_SQL = "select empl_rcd from (" + 
            "select empl_rcd, max(TIMESTAMP) as lastUpdated " + 
            "from LD_LDGR_ENTR_T " + 
            "where emplid = ? " + 
            "and position_nbr = ? " + 
            "and FIN_BALANCE_TYP_CD = ? " + 
            "and account_nbr = ? " + 
            "and FIN_OBJECT_CD = ? " + 
            "and empl_rcd is not null " + 
            "group by empl_rcd " + 
            "union all " +
            "select -1, to_date('01-JAN-65') " + 
            "from dual " + 
            "order by lastUpdated desc " + 
            ") where rownum = 1";
	
     public Integer getEmployeeRecord(String emplid, String positionNumber, String financialBalanceTypeCode, String accountNumber, String financialObjectCode) {
        return new Integer(getSimpleJdbcTemplate().queryForInt(LABOR_LEDGER_ENTRY_SQL, emplid, positionNumber, financialBalanceTypeCode, accountNumber, financialObjectCode));
    }

}
