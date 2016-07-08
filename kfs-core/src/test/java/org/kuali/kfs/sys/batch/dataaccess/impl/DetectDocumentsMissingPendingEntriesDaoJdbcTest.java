package org.kuali.kfs.sys.batch.dataaccess.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DetectDocumentsMissingPendingEntriesDaoJdbcTest {

    @Test
    public void testGeneralLedgerSqlCorrectness() {
        DetectDocumentsMissingPendingEntriesDaoJdbc detectDocumentsMissingPendingEntriesDaoJdbc = new DetectDocumentsMissingGeneralLedgerPendingEntriesDaoJdbc();
        List<String> mockDocumentTypes = new ArrayList<>();
        mockDocumentTypes.add("FakeDocA");
        mockDocumentTypes.add("FakeDocB");
        final String sql = detectDocumentsMissingPendingEntriesDaoJdbc.buildQuery(mockDocumentTypes);

        final String expectedSql = "select distinct fs_doc_header_t.fdoc_nbr as doc_hdr_nbr, fs_doc_header_t.fdoc_typ_nm" +
                " from fs_doc_header_t left join gl_pending_entry_t" +
                " on fs_doc_header_t.fdoc_nbr = gl_pending_entry_t.fdoc_nbr" +
                " where fs_doc_header_t.fdoc_prcssd_dt is not null" +
                " and fs_doc_header_t.fdoc_prcssd_dt > ?" +
                " and fs_doc_header_t.FDOC_TYP_NM in (?, ?)" +
                " and gl_pending_entry_t.fdoc_nbr is null";
        final String scrubbedWhitespaceSql = sql.replaceAll("\\s\\s*", " ");
        Assert.assertEquals(expectedSql.toLowerCase(), scrubbedWhitespaceSql.toLowerCase());
    }

    @Test
    public void testGeneralLedgerSqlCorrectnessWithMoreDocumentTypes() {
        DetectDocumentsMissingPendingEntriesDaoJdbc detectDocumentsMissingPendingEntriesDaoJdbc = new DetectDocumentsMissingGeneralLedgerPendingEntriesDaoJdbc();
        List<String> mockDocumentTypes = new ArrayList<>();
        mockDocumentTypes.add("FakeDocA");
        mockDocumentTypes.add("FakeDocB");
        mockDocumentTypes.add("FakeDocC");
        mockDocumentTypes.add("FakeDocD");
        final String sql = detectDocumentsMissingPendingEntriesDaoJdbc.buildQuery(mockDocumentTypes);

        final String expectedSql = "select distinct fs_doc_header_t.fdoc_nbr as doc_hdr_nbr, fs_doc_header_t.fdoc_typ_nm" +
                " from fs_doc_header_t left join gl_pending_entry_t" +
                " on fs_doc_header_t.fdoc_nbr = gl_pending_entry_t.fdoc_nbr" +
                " where fs_doc_header_t.fdoc_prcssd_dt is not null" +
                " and fs_doc_header_t.fdoc_prcssd_dt > ?" +
                " and fs_doc_header_t.FDOC_TYP_NM in (?, ?, ?, ?)" +
                " and gl_pending_entry_t.fdoc_nbr is null";
        final String scrubbedWhitespaceSql = sql.replaceAll("\\s\\s*", " ");
        Assert.assertEquals(expectedSql.toLowerCase(), scrubbedWhitespaceSql.toLowerCase());
    }
}