package org.kuali.kfs.sys.batch.dataaccess.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.dataaccess.DetectDocumentsMissingPendingEntriesDao;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Abstract JDBC DAO implementation, to make it easier to get both GL and LL pending entries
 */
public abstract class DetectDocumentsMissingPendingEntriesDaoJdbc extends PlatformAwareDaoBaseJdbc implements DetectDocumentsMissingPendingEntriesDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetectDocumentsMissingPendingEntriesDaoJdbc.class);

    private static final String DETECTION_SQL_PREFIX = "select distinct fs_doc_header_t.fdoc_nbr as doc_hdr_nbr,\n" +
            "       fs_doc_header_t.fdoc_typ_nm\n" +
            "from fs_doc_header_t left join %1$s\n" +
            "  on fs_doc_header_t.fdoc_nbr = %1$s.fdoc_nbr\n" +
            "where fs_doc_header_t.fdoc_prcssd_dt is not null\n" +
            "  and fs_doc_header_t.fdoc_prcssd_dt > ?\n" +
            "  and fs_doc_header_t.FDOC_TYP_NM in (";
    private static final String DETECTION_SQL_SUFFIX = ")\n" +
            "  and %1$s.fdoc_nbr is null";

    @Override
    public List<DocumentHeaderData> discoverLedgerDocumentsWithoutPendingEntries(Date startTime, List<String> documentTypesToSearch) {
        LOG.debug("Entered discoverLedgerDocumentsWithoutPendingEntries");
        final String sql = buildQuery(documentTypesToSearch);
        LOG.debug("SQL statement = "+sql);
        List<Object> sqlParameters = new ArrayList<>();
        sqlParameters.add(new java.sql.Timestamp(startTime.getTime()));
        sqlParameters.addAll(documentTypesToSearch);
        return getJdbcTemplate().query(sql,
                sqlParameters.toArray(),
                (resultSet, i) -> {
                    final String documentNumber = resultSet.getString("doc_hdr_nbr");
                    final String documentTypeName = resultSet.getString("fdoc_typ_nm");
                    return new DocumentHeaderData(documentNumber, documentTypeName);
                });
    }

    public Optional<String> getCustomerPaymentMediumCodeFromCashControlDocument(String documentNumber) {
        List<String> results = getJdbcTemplate().query("select CUST_PMT_MEDIUM_CD from AR_CSH_CTRL_T where FDOC_NBR = ?",
                new Object[] { documentNumber },
                (resultSet, i) -> resultSet.getString("CUST_PMT_MEDIUM_CD"));

        return results.stream().findFirst();
    }

    protected String buildQuery(List<String> documentTypesToSearch) {
        final String[] docTypeQuestionMarks = new String[documentTypesToSearch.size()];
        Arrays.fill(docTypeQuestionMarks, "?");
        return String.format(DETECTION_SQL_PREFIX + StringUtils.join(docTypeQuestionMarks, ", ") + DETECTION_SQL_SUFFIX, getPendingEntryTableName());
    }

    protected abstract String getPendingEntryTableName();
}
