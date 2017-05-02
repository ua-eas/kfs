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
package org.kuali.rice.core.framework.persistence.ojb.dao;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.persistence.dao.PlatformAwareDao;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

//import org.kuali.rice.krad.service.KRADServiceLocatorInternal;

public abstract class PlatformAwareDaoBaseOjb extends PersistenceBrokerDaoSupport implements PlatformAwareDao {
    private DatabasePlatform dbPlatform;

    public synchronized DatabasePlatform getDbPlatform() {
        if (this.dbPlatform == null) {
            //this.dbPlatform = KRADServiceLocatorInternal.getDatabasePlatform();
            this.dbPlatform = GlobalResourceLoader.<DatabasePlatform>getService("dbPlatform");
        }
        return dbPlatform;
    }

    public synchronized void setDbPlatform(DatabasePlatform dbPlatform) {
        this.dbPlatform = dbPlatform;
    }
}
