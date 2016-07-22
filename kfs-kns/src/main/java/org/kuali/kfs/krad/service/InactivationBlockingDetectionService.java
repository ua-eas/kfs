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
package org.kuali.kfs.krad.service;

import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.datadictionary.InactivationBlockingMetadata;

import java.util.Collection;

/**
 * This service detects whether there are any records that block the inactivation of a particular record 
 * 
 * 
 */
public interface InactivationBlockingDetectionService {
    /**
     * Determines whether there is ANY record in the relationship defined by the inactivationBlockingMetadata that prevents inactivation of blockedBo
     * 
     * @param blockedBo a BO that is potentially inactivation blocked
     * @param inactivationBlockingMetadata
     * @return true iff there was a record that blocks the blockedBo using the metadata in inactivationBlockingMetadata
     */
    public boolean hasABlockingRecord(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata);

    /**
     * Lists all records in the relationship defined by the inactivationBlockingMetadata that prevents inactivation of blockedBo
     * 
     * @param blockedBo a BO that is potentially inactivation blocked
     * @param inactivationBlockingMetadata
     * @return true iff there was a record that blocks the blockedBo using the metadata in inactivationBlockingMetadata
     */
    public Collection<BusinessObject> listAllBlockerRecords(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata);
}
