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
package org.kuali.kfs.coreservice.impl.component;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.coreservice.impl.component.ComponentSetBo;
import org.kuali.rice.core.framework.persistence.ojb.DataAccessUtils;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * JDBC-based implementation of the {@code ComponentSetDao}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentSetDaoOjbImpl extends PersistenceBrokerDaoSupport implements ComponentSetDao {

    @Override
    public ComponentSetBo getComponentSet(String componentSetId) {
        Criteria criteria = new Criteria();
	    criteria.addEqualTo("componentSetId", componentSetId);
	    return (ComponentSetBo) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ComponentSetBo.class, criteria));
    }

    @Override
    public boolean saveIgnoreLockingFailure(ComponentSetBo componentSetBo) {
        try {
            getPersistenceBrokerTemplate().store(componentSetBo);
        } catch (RuntimeException e) {
            if (DataAccessUtils.isOptimisticLockFailure(e)) {
                return false;
            }
            throw e;
        }
        return true;
    }
}
