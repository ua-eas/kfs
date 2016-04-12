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
package org.kuali.kfs.krad.maintenance;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.krad.service.KRADServiceLocator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/**
 * List of business objects that this maintenance document is locking (prevents two documents from being routed trying to update the same object)
 * Most maintenance documents have only one lock, but globals have many 
 */
public class MaintenanceLock extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 7766326835852387301L;
	@Id
    @Column(name="MAINT_LOCK_ID")
    private String lockId;
	@Column(name="MAINT_LOCK_REP_TXT")
	private String lockingRepresentation;
    @Column(name="DOC_HDR_ID")
	private String documentNumber;

    public String getLockId() {
		return this.lockId;
	}

	public void setLockId(String lockId) {
		this.lockId = lockId;
	}

	public String getLockingRepresentation() {
        return lockingRepresentation;
    }

    public void setLockingRepresentation(String lockingRepresentation) {
        this.lockingRepresentation = lockingRepresentation;
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
}

