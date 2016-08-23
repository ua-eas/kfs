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
package org.kuali.kfs.module.kc.database;

import org.junit.Test;
import org.kuali.kfs.sys.database.LiquibaseTestBase;

import java.util.List;

public class KcLiquibaseModifySqlTest extends LiquibaseTestBase {
    @Test
    public void testForDateColumn() throws Exception {
        testForDateColumn("/org/kuali/kfs/module/kc/db/phase1/kfs-kc_createTable.xml");
    }

    @Test
    public void testForMissingModifySql() throws Exception {
        testForMissingModifySql("/org/kuali/kfs/module/kc/db/phase1/kfs-kc_createTable.xml");
    }

    @Test
    public void testPhase5_modifySql() throws Exception {
        List<String> phase5Files = findLiquibaseFiles("org/kuali/kfs/module/kc/db/phase5/");
        for (String fileName : phase5Files) {
            System.out.println(fileName);
            testForMissingModifySql("/" + fileName);
        }
    }

    @Test
    public void testPhase5_dateColumn() throws Exception {
        List<String> phase5Files = findLiquibaseFiles("org/kuali/kfs/module/kc/db/phase5/");
        for (String fileName : phase5Files) {
            System.out.println(fileName);
            testForDateColumn("/" + fileName);
        }
    }
}
