/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.sys.datatools.mock;


import org.kuali.kfs.sys.datatools.liquimongo.businessobject.DocumentStoreChange;
import org.kuali.kfs.sys.datatools.liquimongo.dataaccess.DocumentStoreUpdateProcessDao;

public class MockDocumentStoreUpdateProcessDao implements DocumentStoreUpdateProcessDao {
    private boolean locked = false;
    private boolean schemaChangeHappened = false;

    public void setSchemaChangeHappened(boolean schemaChangeHappenend) {
        this.schemaChangeHappened = schemaChangeHappenend;
    }

    @Override
    public boolean isSchemaChangeLocked() {
        return locked;
    }

    @Override
    public void lockSchemaChange() {
        locked = true;
    }

    @Override
    public void unlockSchemaChange() {
        locked = false;
    }

    @Override
    public boolean hasSchemaChangeHappened(DocumentStoreChange change) {
        return this.schemaChangeHappened;
    }

    @Override
    public void saveSchemaChange(DocumentStoreChange change) {

    }

    @Override
    public void removeSchemaChange(DocumentStoreChange change) {

    }
}
