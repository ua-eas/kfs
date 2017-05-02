/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;

import java.util.List;

/**
 * This service provides various operations related to {@link Note} objects.
 */
public interface NoteService {

    /**
     * Retrieves a list of notes that are associated with the given object id.
     * This object id will generally be the object id of the {@link PersistableBusinessObject}
     * that the note was attached to when it was created.
     *
     * @param remoteObjectId the object id that the notes being searched for are associated with
     * @return the list of notes which are associated with the given object id.  If no such notes are found, an empty list will be returned.
     */
    public List<Note> getByRemoteObjectId(String remoteObjectId);

    /**
     * Retrieves the note with the given id.
     *
     * @param noteId the note id to search by
     * @return the note with the given note id, or null if no note is found
     * @throws IllegalArgumentException if the specified id is null
     */
    public Note getNoteByNoteId(Long noteId);

    /**
     * Saves the given lists of notes.  If the given list is null or empty,
     * this method will do nothing.
     *
     * @param notes the list of notes to save
     * @throws IllegalStateException if any of the notes in the list have an invalid remoteObjectId
     */
    public void saveNoteList(List<Note> notes);

    /**
     * Saves the specified note.  This method returns a reference to the note that was
     * saved.  Callers of this method should reassign their reference to the note
     * passed in with the one that is returned.
     *
     * @param note the note to save
     * @return the saved note
     * @throws IllegalArgumentException if the specified note is null
     * @throws IllegalStateException    if the given note's remoteObjectId is not valid
     */
    public Note save(Note note);

    /**
     * Deletes the specified note.
     *
     * @param note the note to delete
     * @throws IllegalArgumentException if the given note is null
     */
    public void deleteNote(Note note);

    /**
     * Creates a new note which is a copy of the given note and is associated with
     * the specified PersistableBusinessObject and Person.
     *
     * @param noteToCopy the note to copy
     * @param bo         the business object to associate the Note with
     * @return a copy of the given note which
     */
    public Note createNote(Note noteToCopy, PersistableBusinessObject bo, String authorPrincipalId);

}
