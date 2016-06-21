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
package org.kuali.kfs.sys.datatools.service;

import org.kuali.kfs.sys.datatools.liquimongo.change.DocumentStoreChangeHandler;
import org.kuali.kfs.sys.datatools.liquimongo.service.impl.DocumentStoreSchemaUpdateServiceImpl;
import org.kuali.kfs.sys.datatools.mock.MockDocumentStoreChangeHandler;
import org.kuali.kfs.sys.datatools.mock.MockDocumentStoreUpdateProcessDao;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DocumentStoreSchemaUpdateServiceTest {
    private DocumentStoreSchemaUpdateServiceImpl documentStoreSchemaUpdateService;
    private MockDocumentStoreUpdateProcessDao mockMongoUpdateProcessDao;
    private MockDocumentStoreChangeHandler mongoChangeHandler;

    @Before
    public void setupMongoSchemaUpdateStep() {
        mongoChangeHandler = new MockDocumentStoreChangeHandler();
        List<DocumentStoreChangeHandler> handlers = new ArrayList<>();
        handlers.add(mongoChangeHandler);

        mockMongoUpdateProcessDao = new MockDocumentStoreUpdateProcessDao();
        documentStoreSchemaUpdateService = new DocumentStoreSchemaUpdateServiceImpl();
        documentStoreSchemaUpdateService.setUpdatesPath("classpath:org/kuali/kfs/sys/datatools/liquimongo/batch/fixture/documentstoreschemaupdate/");
        documentStoreSchemaUpdateService.setDocumentStoreUpdateProcessDao(mockMongoUpdateProcessDao);
        documentStoreSchemaUpdateService.setHandlers(handlers);
    }

    @Test
    public void testMissingUpdatesFile() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updatesXXX.json");
        try {
            documentStoreSchemaUpdateService.updateDocumentStoreSchema();
            Assert.fail("Job should not run successfully");
        } catch (IllegalArgumentException e) {
            // This is supposed to happen
        }
    }

    @Test
    public void testOneFile() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.json");
        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("3 change made",3,mongoChangeHandler.changesMade);
    }

    @Test
    public void testTwoFiles() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates2.json");
        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("6 change made",6,mongoChangeHandler.changesMade);
    }

    @Test
    public void testRunsOnTrueStartupProperty() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.json");
        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("3 change made",3,mongoChangeHandler.changesMade);
    }

    @Test
    public void testDoesNotRunsIfLocked() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.json");
        mockMongoUpdateProcessDao.lockSchemaChange();

        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("0 changes made",0,mongoChangeHandler.changesMade);
    }

    @Test
    public void testUnlocksAfterRun() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.json");
        mockMongoUpdateProcessDao.unlockSchemaChange();

        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("3 changes made",3,mongoChangeHandler.changesMade);
        Assert.assertEquals("Lock should be unset after run",false,mockMongoUpdateProcessDao.isSchemaChangeLocked());
    }

}
