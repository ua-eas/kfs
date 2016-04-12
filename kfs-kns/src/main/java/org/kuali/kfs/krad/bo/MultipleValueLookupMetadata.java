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
package org.kuali.kfs.krad.bo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class MultipleValueLookupMetadata extends PersistableBusinessObjectBase {
    @Id
    @Column(name="LOOKUP_RSLT_ID")
    private String lookupResultsSequenceNumber;
    @Column(name="PRNCPL_ID")
    private String lookupPersonId;
    /**
     * the time the lookup data was persisted, used by a batch purge job
     */
    //@Transient
    @Column(name="LOOKUP_DT")
    private Timestamp lookupDate;
    
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public String getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(String lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    /**
     * @return the time the lookup data was persisted, used by a batch purge job
     */
    public Timestamp getLookupDate() {
        return lookupDate;
    }

    /**
     * @param lookupDate the time the lookup data was persisted, used by a batch purge job
     */
    public void setLookupDate(Timestamp lookupDate) {
        this.lookupDate = lookupDate;
    }
}

