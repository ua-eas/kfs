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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.dao.SequenceAccessorDao;
import org.kuali.kfs.krad.service.SequenceAccessorService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SequenceAccessorServiceImpl implements SequenceAccessorService {
    private SequenceAccessorDao sequenceAccessorDao;

	public Long getNextAvailableSequenceNumber(String sequenceName,
			Class<? extends BusinessObject> clazz) {
    	if (StringUtils.isBlank(sequenceName)) {
    		throw new RuntimeException("Sequence name cannot be blank.");
    	}
    	return sequenceAccessorDao.getNextAvailableSequenceNumber(sequenceName, clazz);
	}

    /**
     * @see SequenceAccessorService#getNextAvailableSequenceNumber(java.lang.String)
     */
    public Long getNextAvailableSequenceNumber(String sequenceName) {
    	if (StringUtils.isBlank(sequenceName)) {
    		throw new RuntimeException("Sequence name cannot be blank.");
    	}
    	return sequenceAccessorDao.getNextAvailableSequenceNumber(sequenceName);
    }

    public void setSequenceAccessorDao(SequenceAccessorDao sequenceAccessorDao) {
    	this.sequenceAccessorDao = sequenceAccessorDao;
    }
}
