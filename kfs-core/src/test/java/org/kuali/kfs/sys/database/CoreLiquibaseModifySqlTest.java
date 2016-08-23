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
package org.kuali.kfs.sys.database;

import org.junit.Test;

import java.util.List;

public class CoreLiquibaseModifySqlTest extends LiquibaseTestBase {
    @Test
    public void testForDateColumnRice() throws Exception {
        testForDateColumn("/org/kuali/kfs/core/db/phase1/kfs-core-rice_createTable.xml");
    }

    @Test
    public void testForDateColumnKfs() throws Exception {
        testForDateColumn("/org/kuali/kfs/core/db/phase1/kfs-core_createTable.xml");
    }

    @Test
    public void testForDateColumnRiceServer() throws Exception {
        testForDateColumn("/org/kuali/rice/db/phase1/rice-server_createTable.xml");
    }

    @Test
    public void testForModifySqlRice() throws Exception {
        testForMissingModifySql("/org/kuali/kfs/core/db/phase1/kfs-core-rice_createTable.xml");
    }

    @Test
    public void testForModifySqlKfs() throws Exception {
        testForMissingModifySql("/org/kuali/kfs/core/db/phase1/kfs-core_createTable.xml");
    }

    @Test
    public void testForModifySqlRiceServer() throws Exception {
        testForMissingModifySql("/org/kuali/rice/db/phase1/rice-server_createTable.xml");
    }

    @Test
    public void testPhase5_modifySql() throws Exception {
        List<String> phase5Files = findLiquibaseFiles("org/kuali/kfs/core/db/phase5/");
        for (String fileName : phase5Files) {
            System.out.println(fileName);
            testForMissingModifySql("/" + fileName);
        }
    }

    @Test
    public void testPhase5_dateColumn() throws Exception {
        List<String> phase5Files = findLiquibaseFiles("org/kuali/kfs/core/db/phase5/");
        for (String fileName : phase5Files) {
            System.out.println(fileName);
            testForDateColumn("/" + fileName);
        }
    }
}
