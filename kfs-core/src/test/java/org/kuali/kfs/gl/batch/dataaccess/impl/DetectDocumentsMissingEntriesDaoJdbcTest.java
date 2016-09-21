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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DetectDocumentsMissingEntriesDaoJdbcTest {

    @Test
    public void testGeneralLedgerSqlCorrectness() {
        DetectDocumentsMissingEntriesDaoJdbc detectDocumentsMissingPendingEntriesDaoJdbc = new DetectDocumentsMissingEntriesDaoJdbc();
        List<String> mockDocumentTypes = new ArrayList<>();
        mockDocumentTypes.add("FakeDocA");
        mockDocumentTypes.add("FakeDocB");
        final String sql = detectDocumentsMissingPendingEntriesDaoJdbc.buildQuery(mockDocumentTypes);

        final String expectedSql = "select distinct fs_doc_header_t.fdoc_nbr as doc_hdr_nbr, fs_doc_header_t.fdoc_typ_nm, fs_doc_header_t.fdoc_prcssd_dt"
                + " from fs_doc_header_t left join gl_entry_t" + " on fs_doc_header_t.fdoc_nbr = gl_entry_t.fdoc_nbr"
                + " where fs_doc_header_t.fdoc_prcssd_dt is not null" + " and fs_doc_header_t.fdoc_prcssd_dt > ?"
                + " and fs_doc_header_t.FDOC_TYP_NM in (?, ?)" + " and gl_entry_t.fdoc_nbr is null";
        final String scrubbedWhitespaceSql = sql.replaceAll("\\s\\s*", " ");
        Assert.assertEquals(expectedSql.toLowerCase(), scrubbedWhitespaceSql.toLowerCase());
    }

    @Test
    public void testGeneralLedgerSqlCorrectnessWithMoreDocumentTypes() {
        DetectDocumentsMissingEntriesDaoJdbc detectDocumentsMissingPendingEntriesDaoJdbc = new DetectDocumentsMissingEntriesDaoJdbc();
        List<String> mockDocumentTypes = new ArrayList<>();
        mockDocumentTypes.add("FakeDocA");
        mockDocumentTypes.add("FakeDocB");
        mockDocumentTypes.add("FakeDocC");
        mockDocumentTypes.add("FakeDocD");
        final String sql = detectDocumentsMissingPendingEntriesDaoJdbc.buildQuery(mockDocumentTypes);

        final String expectedSql = "select distinct fs_doc_header_t.fdoc_nbr as doc_hdr_nbr, fs_doc_header_t.fdoc_typ_nm, fs_doc_header_t.fdoc_prcssd_dt"
                + " from fs_doc_header_t left join gl_entry_t" + " on fs_doc_header_t.fdoc_nbr = gl_entry_t.fdoc_nbr"
                + " where fs_doc_header_t.fdoc_prcssd_dt is not null" + " and fs_doc_header_t.fdoc_prcssd_dt > ?"
                + " and fs_doc_header_t.FDOC_TYP_NM in (?, ?, ?, ?)" + " and gl_entry_t.fdoc_nbr is null";
        final String scrubbedWhitespaceSql = sql.replaceAll("\\s\\s*", " ");
        Assert.assertEquals(expectedSql.toLowerCase(), scrubbedWhitespaceSql.toLowerCase());
    }
}
