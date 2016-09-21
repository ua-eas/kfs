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
package org.kuali.kfs.gl.batch.dataaccess.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.dataaccess.DetectDocumentsMissingEntriesDao;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

public class DetectDocumentsMissingEntriesDaoJdbc extends PlatformAwareDaoBaseJdbc
        implements DetectDocumentsMissingEntriesDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(DetectDocumentsMissingEntriesDaoJdbc.class);

    private static final String DETECTION_SQL_PREFIX = "select distinct fs_doc_header_t.fdoc_nbr as doc_hdr_nbr,\n"
            + "       fs_doc_header_t.fdoc_typ_nm,\n" + "       fs_doc_header_t.fdoc_prcssd_dt\n"
            + " from fs_doc_header_t left join gl_entry_t\n" + "  on fs_doc_header_t.fdoc_nbr = gl_entry_t.fdoc_nbr\n"
            + " where fs_doc_header_t.fdoc_prcssd_dt is not null\n" + "  and fs_doc_header_t.fdoc_prcssd_dt > ?\n"
            + "  and fs_doc_header_t.FDOC_TYP_NM in (";
    private static final String DETECTION_SQL_SUFFIX = ")\n" + "  and gl_entry_t.fdoc_nbr is null";

    @Override
    public List<DocumentHeaderData> discoverLedgerDocumentsWithoutEntries(Date startTime,
            List<String> documentTypesToSearch) {
        LOG.debug("Entered discoverLedgerDocumentsWithoutEntries");
        final String sql = buildQuery(documentTypesToSearch);
        LOG.debug("SQL statement = " + sql);
        List<Object> sqlParameters = new ArrayList<>();
        sqlParameters.add(new java.sql.Timestamp(startTime.getTime()));
        sqlParameters.addAll(documentTypesToSearch);
        return getJdbcTemplate().query(sql, sqlParameters.toArray(), (resultSet, i) -> {
            final String documentNumber = resultSet.getString("doc_hdr_nbr");
            final String documentTypeName = resultSet.getString("fdoc_typ_nm");
            final Timestamp processedDate = resultSet.getTimestamp("fdoc_prcssd_dt");
            return new DocumentHeaderData(documentNumber, documentTypeName, processedDate);
        });
    }

    @Override
    public Optional<String> getCustomerPaymentMediumCodeFromCashControlDocument(String documentNumber) {
        List<String> results = getJdbcTemplate().query(
                "select CUST_PMT_MEDIUM_CD from AR_CSH_CTRL_T where FDOC_NBR = ?", new Object[] { documentNumber },
                (resultSet, i) -> resultSet.getString("CUST_PMT_MEDIUM_CD"));

        return results.stream().findFirst();
    }

    protected String buildQuery(List<String> documentTypesToSearch) {
        final String[] docTypeQuestionMarks = new String[documentTypesToSearch.size()];
        Arrays.fill(docTypeQuestionMarks, "?");
        return DETECTION_SQL_PREFIX + StringUtils.join(docTypeQuestionMarks, ", ") + DETECTION_SQL_SUFFIX;
    }

}
