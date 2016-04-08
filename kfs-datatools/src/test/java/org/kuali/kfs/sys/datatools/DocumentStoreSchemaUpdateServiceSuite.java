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
package org.kuali.kfs.sys.datatools;

import org.kuali.kfs.sys.datatools.businessobject.DocumentStoreChangeTest;
import org.kuali.kfs.sys.datatools.handler.AddDocumentHandlerTest;
import org.kuali.kfs.sys.datatools.handler.DeleteDocumentsHandlerTest;
import org.kuali.kfs.sys.datatools.handler.UpdateDocumentHandlerTest;
import org.kuali.kfs.sys.datatools.service.DocumentStoreSchemaUpdateServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all tests related to the Document Service Schema Update Service
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    DocumentStoreSchemaUpdateServiceTest.class,
    AddDocumentHandlerTest.class,
    DeleteDocumentsHandlerTest.class,
    UpdateDocumentHandlerTest.class,
    DocumentStoreChangeTest.class
})
public class DocumentStoreSchemaUpdateServiceSuite {
}
