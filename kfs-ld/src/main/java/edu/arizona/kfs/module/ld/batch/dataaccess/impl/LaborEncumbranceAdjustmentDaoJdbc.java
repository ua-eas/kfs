package edu.arizona.kfs.module.ld.batch.dataaccess.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.jdbc.support.JdbcUtils;

import edu.arizona.kfs.module.ld.batch.dataaccess.LaborEncumbranceAdjustmentDao;

public class LaborEncumbranceAdjustmentDaoJdbc extends PlatformAwareDaoBaseJdbc implements LaborEncumbranceAdjustmentDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborEncumbranceAdjustmentDaoJdbc.class);

    protected String additionalWhereClause = "";
    protected static final String ENCUMBRANCE_BALANCE_SQL = "select l.FIN_BALANCE_TYP_CD, l.EMPLID, l.position_nbr, l.fin_coa_cd, l.account_nbr, l.sub_acct_nbr, l.fin_obj_typ_cd, " + 
            "l.FIN_OBJECT_CD, l.FIN_SUB_OBJ_CD, SUM(l.ACLN_ANNL_BAL_AMT) AS encumbrance_balance" + 
            "    from LD_LDGR_BAL_T l, ld_labor_obj_t lo " + 
            "    WHERE l.UNIV_FISCAL_YR = ?" + 
            "      AND l.FIN_BALANCE_TYP_CD in (?,?) " + 
            "      AND lo.univ_fiscal_yr = l.UNIV_FISCAL_YR" + 
            "      AND lo.FIN_COA_CD = l.FIN_COA_CD" + 
            "      AND lo.FIN_OBJECT_CD = l.FIN_OBJECT_CD" + 
            "      AND lo.FINOBJ_FRNGSLRY_CD = '" + LaborConstants.LABOR_OBJECT_SALARY_CODE + "'";
    protected static final String ENCUMBRANCE_BALANCE_SQL2 = "    GROUP BY l.FIN_BALANCE_TYP_CD, l.EMPLID, l.position_nbr, l.fin_coa_cd, l.account_nbr, l.sub_acct_nbr, l.fin_obj_typ_cd, l.FIN_OBJECT_CD, l.FIN_SUB_OBJ_CD" + 
              "    HAVING SUM(l.ACLN_ANNL_BAL_AMT) != 0" + 
              "    ORDER BY FIN_BALANCE_TYP_CD, EMPLID, position_nbr, fin_coa_cd, account_nbr, sub_acct_nbr," + 
              "             FIN_OBJECT_CD, FIN_SUB_OBJ_CD, fin_obj_typ_cd";
    
    public int buildFileForEncumbranceBalances(Integer fiscalYear, File fileName) {
        // get the connection and use resultsets directly
        PreparedStatement stmt = null;
        ResultSet rs = null;
        PrintWriter writer = null;
        int outputLines = 0;
        Map<String, Integer> lMap = new LaborOriginEntryFieldUtil().getFieldLengthMap();
        // this is a generated tmp file, so if one exists, remove it
        if (fileName.exists()) {
            fileName.delete();
        }
        // get the fiscal year's internal encumbrance value
        String internalEncumbranceBalanceTypeCode = SpringContext.getBean(OptionsService.class).getOptions(fiscalYear).getIntrnlEncumFinBalanceTypCd();

        String preencumbranceFinBalTypeCd;
        // **START AZ** KFSI-5984 KITT-3023 KevinMcO
        preencumbranceFinBalTypeCd = SpringContext.getBean(OptionsService.class).getOptions(fiscalYear).getPreencumbranceFinBalTypeCd();
        // **END AZ**

        try {
            // create an empty file, so that if no records are found, a file is still created (makes later processing easier)
            fileName.createNewFile();
            try {
                stmt = getConnection().prepareStatement(ENCUMBRANCE_BALANCE_SQL + additionalWhereClause + ENCUMBRANCE_BALANCE_SQL2);
                stmt.setInt(1, fiscalYear);
                stmt.setString(2, internalEncumbranceBalanceTypeCode);
                // **START AZ** KFSI-5984 KITT-3023 KevinMcO
                stmt.setString(3, preencumbranceFinBalTypeCd);

                rs = stmt.executeQuery();
            }
            catch (SQLException ex) {
                LOG.error("Unable to create and execute statement to retrieve labor encumbrance balances", ex);
                throw new RuntimeException("Unable to create and execute statement to retrieve labor encumbrance balances", ex);
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("Opening " + fileName.getAbsolutePath() + " for output of balance records.");
                if (!StringUtils.isBlank(additionalWhereClause)) {
                    LOG.info("Additional WHERE clause in use: " + additionalWhereClause);
                }
            }
            writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
            try {
                StringBuilder sb = new StringBuilder(100);
                while (rs.next()) {
                    sb.setLength(0); // reset the existing string builder
                    // write data to outputStream
                    sb.append(StringUtils.rightPad(rs.getString(1), lMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), ' '));
                    sb.append(StringUtils.rightPad(rs.getString(2), lMap.get(KFSPropertyConstants.EMPLID), ' '));
                    sb.append(StringUtils.rightPad(rs.getString(3), lMap.get(KFSPropertyConstants.POSITION_NUMBER), ' '));
                    sb.append(StringUtils.rightPad(rs.getString(4), lMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), ' '));
                    sb.append(StringUtils.rightPad(rs.getString(5), lMap.get(KFSPropertyConstants.ACCOUNT_NUMBER), ' '));
                    sb.append(StringUtils.rightPad(rs.getString(6), lMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER), ' '));
                    sb.append(StringUtils.rightPad(rs.getString(7), lMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE), ' '));
                    sb.append(StringUtils.rightPad(rs.getString(8), lMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE), ' '));
                    sb.append(StringUtils.rightPad(rs.getString(9), lMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE), ' '));
                    BigDecimal bal = rs.getBigDecimal(10);
                    sb.append((bal.signum() == -1) ? '-' : '+');
                    sb.append(StringUtils.rightPad(bal.abs().toString(), lMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT), ' '));
                    writer.println(sb.toString());
                    outputLines++;
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info(outputLines + " records written to output file.");
                }
            }
            catch (SQLException ex) {
                LOG.error("Unable to iterate over encumbrance balance records", ex);
                throw new RuntimeException("Unable to iterate over encumbrance balance records", ex);
            }
        }
        catch (FileNotFoundException ex) {
            LOG.error("Unable to open output file for writing: " + fileName.getAbsolutePath(), ex);
            throw new RuntimeException("Unable to open output file for writing: " + fileName.getAbsolutePath(), ex);
        }
        catch (IOException ex) {
            LOG.error("Unable to create output file for balance records: " + fileName.getAbsolutePath(), ex);
            throw new RuntimeException("Unable to create output file for balance records: " + fileName.getAbsolutePath(), ex);
        }
        finally {
            IOUtils.closeQuietly(writer);
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(stmt);
        }
        return outputLines;
    }

    public String getAdditionalWhereClause() {
        return additionalWhereClause;
    }

    /**
     * This is used for testing to blank out the file.
     */
    public void setAdditionalWhereClause(String additionalWhereClause) {
        if (additionalWhereClause == null) {
            this.additionalWhereClause = "";
        }
        else {
            this.additionalWhereClause = additionalWhereClause;
        }
    }
}
