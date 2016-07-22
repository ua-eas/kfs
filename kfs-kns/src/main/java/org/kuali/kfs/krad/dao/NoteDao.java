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
package org.kuali.kfs.krad.dao;

import java.util.List;

import org.kuali.kfs.krad.bo.Note;


/**
 * The data access interface for NOte objects.
 * 
 * 
 */
public interface NoteDao {
    /**
     * Saves a note to the DB.
     * 
     * @param line
     */
    void save(Note note);

    /**
     * Deletes a note from the DB.
     * 
     * @param line
     */
    void deleteNote(Note note);

    /**
     * Retrieves a list of notes (by class type) associated with a given object.
     * 
     * @param clazz
     * @param id
     * @return
     */
    public List<Note> findByremoteObjectId(String id);
    
    /**
     * Retrieve note by a given noteIdentifier
     * 
     * @param noteId
     * @return
     */
    public Note getNoteByNoteId(Long noteId);
}
