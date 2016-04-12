/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.dao.impl;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.core.IdentityFactoryImpl;
import org.apache.ojb.broker.core.proxy.IndirectionHandlerCGLIBImpl;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.krad.dao.PersistenceDao;

public class PersistenceDaoOjb extends PlatformAwareDaoBaseOjb implements PersistenceDao {

    /**
     * @see PersistenceDao#clearCache()
     */
    public void clearCache() {
        getPersistenceBroker(true).clearCache();
    }

    /**
     * @see PersistenceDao#resolveProxy(java.lang.Object)
     */
    public Object resolveProxy(Object o) {
        Identity ident = new IdentityFactoryImpl(getPersistenceBroker(true)).buildIdentity(o);
        IndirectionHandlerCGLIBImpl ih = new IndirectionHandlerCGLIBImpl(getPersistenceBroker(true).getPBKey(), ident);
        return ih.getRealSubject();
    }

    /**
     * @see PersistenceDao#retrieveAllReferences(java.lang.Object)
     */
    public void retrieveAllReferences(Object o) {
        getPersistenceBroker(true).retrieveAllReferences(o);
    }

    /**
     * @see PersistenceDao#retrieveReference(java.lang.Object, java.lang.String)
     */
    public void retrieveReference(Object o, String referenceName) {
        getPersistenceBroker(true).retrieveReference(o, referenceName);
    }

	/**
	 * Asks ProxyHelper if the object is proxied
	 * 
	 * @see PersistenceDao#isProxied(java.lang.Object)
	 */
	public boolean isProxied(Object object) {
		return ProxyHelper.isProxy(object);
	}
 
}
