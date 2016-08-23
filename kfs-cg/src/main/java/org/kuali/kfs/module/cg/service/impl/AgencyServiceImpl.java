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
package org.kuali.kfs.module.cg.service.impl;

import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.NoteService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.service.AgencyService;
import org.kuali.kfs.sys.KFSPropertyConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Agency service.
 */
public class AgencyServiceImpl implements AgencyService {

    protected BusinessObjectService businessObjectService;
    protected NoteService noteService;

    /**
     * @see org.kuali.kfs.module.cg.service.AgencyService#getByPrimaryId(String)
     */
    @Override
    public Agency getByPrimaryId(String agencyNumber) {
        return businessObjectService.findByPrimaryKey(Agency.class, mapPrimaryKeys(agencyNumber));
    }

    protected Map<String, Object> mapPrimaryKeys(String agencyNumber) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber.trim());
        return primaryKeys;
    }

    public List<Note> getAgencyNotes(String agencyNumber) {
        Agency agency = getByPrimaryId(agencyNumber);
        List<Note> notes = new ArrayList<Note>();
        if (ObjectUtils.isNotNull(agency)) {
            notes = noteService.getByRemoteObjectId(agency.getObjectId());
        }
        return notes;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
