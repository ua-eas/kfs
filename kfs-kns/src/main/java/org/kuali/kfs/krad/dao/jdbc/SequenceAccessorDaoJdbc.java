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
package org.kuali.kfs.krad.dao.jdbc;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.dao.SequenceAccessorDao;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.springmodules.orm.ojb.OjbFactoryUtils;

import javax.persistence.EntityManager;

/**
 * This class uses the KualiDBPlatform to get the next number from a given sequence.
 */
public class SequenceAccessorDaoJdbc extends PlatformAwareDaoBaseJdbc implements SequenceAccessorDao {
	private KualiModuleService kualiModuleService;
	
	private Long nextAvailableSequenceNumber(String sequenceName, 
			Class<? extends BusinessObject> clazz) {
		
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if ( moduleService == null )
        	throw new ConfigurationException("moduleService is null");
        	        	
        ModuleConfiguration moduleConfig = moduleService.getModuleConfiguration();
        if ( moduleConfig == null )
        	throw new ConfigurationException("moduleConfiguration is null");

		String dataSourceName = moduleConfig.getDataSourceName();
		if ( StringUtils.isEmpty(dataSourceName) )
			throw new ConfigurationException("dataSourceName is not set");

		PBKey key = new PBKey(dataSourceName);
		PersistenceBroker broker = OjbFactoryUtils.getPersistenceBroker(key, false);
		if ( broker != null )
			return getDbPlatform().getNextValSQL(sequenceName, broker);
		else
			throw new ConfigurationException("PersistenceBroker is null");
	}
	
	public Long getNextAvailableSequenceNumber(String sequenceName, 
			Class<? extends BusinessObject> clazz) {
		
		// There are situations where a module hasn't been configured with
		// a dataSource.  In these cases, this method would have previously
		// thrown an error.  Instead, we've opted to factor out the code,
		// catch any configuration-related exceptions, and if one occurs,
		// attempt to use the dataSource associated with KNS. -- tbradford
		
		try {
			return nextAvailableSequenceNumber(sequenceName, clazz);
		}
		catch ( ConfigurationException e  ) {
	    	// Use DocumentHeader to get the dataSourceName associated with KNS			
			return nextAvailableSequenceNumber(sequenceName, DocumentHeader.class);			
		}
	}
	
    /**
     * @see SequenceAccessorDao#getNextAvailableSequenceNumber(java.lang.String)
     */
    public Long getNextAvailableSequenceNumber(String sequenceName) {
    	// Use DocumentHeader to get the dataSourceName associated with KNS
    	return nextAvailableSequenceNumber(sequenceName, DocumentHeader.class);
    }
    
    private KualiModuleService getKualiModuleService() {
        if ( kualiModuleService == null ) 
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        return kualiModuleService;
    }
}
