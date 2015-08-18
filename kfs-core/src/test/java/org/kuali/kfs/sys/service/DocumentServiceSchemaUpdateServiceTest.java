/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.batch.mock.MockDocumentStoreChangeHandler;
import org.kuali.kfs.sys.batch.mock.MockDocumentStoreUpdateProcessDao;
import org.kuali.kfs.sys.change.DocumentStoreChangeHandler;
import org.kuali.kfs.sys.service.impl.DocumentStoreSchemaUpdateServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DocumentServiceSchemaUpdateServiceTest {
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
        documentStoreSchemaUpdateService.setUpdatesPath("/org/kuali/kfs/sys/batch/fixture/documentstoreschemaupdate/");
        documentStoreSchemaUpdateService.setDocumentStoreUpdateProcessDao(mockMongoUpdateProcessDao);
        documentStoreSchemaUpdateService.setHandlers(handlers);
        documentStoreSchemaUpdateService.setRunOnAppStartup(true);
    }

    @Test
    public void testMissingUpdatesFile() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updatesXXX.xml");
        try {
            documentStoreSchemaUpdateService.updateDocumentStoreSchema();
            Assert.fail("Job should not run successfully");
        } catch (IllegalArgumentException e) {
            // This is supposed to happen
        }
    }

    @Test
    public void testOneFile() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.xml");
        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("1 change made",1,mongoChangeHandler.changesMade);
    }

    @Test
    public void testTwoFiles() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates2.xml");
        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("2 change made",2,mongoChangeHandler.changesMade);
    }

    @Test
    public void testDoesNotRunOnFalseStartupProperty() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.xml");
        documentStoreSchemaUpdateService.setRunOnAppStartup(false);

        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("0 change made",0,mongoChangeHandler.changesMade);
    }

    @Test
    public void testRunsOnTrueStartupProperty() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.xml");
        documentStoreSchemaUpdateService.setRunOnAppStartup(true);

        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("1 change made",1,mongoChangeHandler.changesMade);
    }

    @Test
    public void testDoesNotRunsIfLocked() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.xml");
        mockMongoUpdateProcessDao.lockSchemaChange();

        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("0 changes made",0,mongoChangeHandler.changesMade);
    }

    @Test
    public void testUnlocksAfterRun() throws Exception {
        documentStoreSchemaUpdateService.setUpdatesList("updates1.xml");
        mockMongoUpdateProcessDao.unlockSchemaChange();

        documentStoreSchemaUpdateService.updateDocumentStoreSchema();
        Assert.assertEquals("1 changes made",1,mongoChangeHandler.changesMade);
        Assert.assertEquals("Lock should be unset after run",false,mockMongoUpdateProcessDao.isSchemaChangeLocked());
    }
}
