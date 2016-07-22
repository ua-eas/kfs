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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.krad.bo.Attachment;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.dao.NoteDao;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the NoteDao interface.
 *
 * 
 */
public class NoteDaoOjb extends PlatformAwareDaoBaseOjb implements NoteDao {
    private static Logger LOG = Logger.getLogger(NoteDaoOjb.class);

    /**
     * Default constructor.
     */
    public NoteDaoOjb() {
        super();
    }

    /**
     * Saves a note to the DB using OJB.
     *
     * @param line
     */
    public void save(Note note) throws DataAccessException {
        // Add this check for KRAD to avoid saving the empty Attachments
        // TODO : look into avoiding the default empty attachments being added to the note
        if (note.getAttachment() != null && note.getAttachment().getAttachmentFileName() == null) {
            note.setAttachment(null);
        }
        //workaround in case sequence is empty  I shouldn't need this but ojb seems to work weird with this case
        if (note != null && note.getNoteIdentifier() == null && note.getAttachment() != null) {
            Attachment attachment = note.getAttachment();
            note.setAttachment(null);
            //store without attachment
            getPersistenceBrokerTemplate().store(note);
            attachment.setNoteIdentifier(note.getNoteIdentifier());
            //put attachment back
            note.setAttachment(attachment);
        }
        getPersistenceBrokerTemplate().store(note);
    }

    /**
     * Deletes a note from the DB using OJB.
     */
    public void deleteNote(Note note) throws DataAccessException {
        getPersistenceBrokerTemplate().delete(note.getAttachment());
        note.setAttachment(null);
        getPersistenceBrokerTemplate().delete(note);
        
    }

    /**
     * Retrieves document associated with a given object using OJB.
     *
     * @param id
     * @return
     */
    public List<Note> findByremoteObjectId(String remoteObjectId) {
        Criteria criteria = new Criteria();
        //TODO: Notes - Chris move remoteObjectId string to constants
        criteria.addEqualTo("RMT_OBJ_ID", remoteObjectId);

        QueryByCriteria query = QueryFactory.newQuery(Note.class, criteria);
        //while this is currently called every time these methods could be changed to allow
        //custom sorting by BO see discussion on Notes confluence page
        defaultOrderBy(query);
        Collection<Note> notes = findCollection(query);

        return new ArrayList<Note>(notes);
    }
    
    public Note getNoteByNoteId(Long noteId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("noteIdentifier", noteId);
        return (Note) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Note.class, crit));          
    }

    /**
     * This method defines the default sort for notes
     * @param query
     */
    private void defaultOrderBy(QueryByCriteria query) {
        //TODO: Notes - Chris move remoteObjectId string to constants
        query.addOrderBy("notePostedTimestamp", true);
    }


    /**
     * Retrieve a Collection of note instances found by a query.
     *
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
    private Collection<Note> findCollection(Query query) throws DataAccessException {
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
