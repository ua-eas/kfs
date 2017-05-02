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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.dao.NoteDao;
import org.kuali.kfs.krad.service.AttachmentService;
import org.kuali.kfs.krad.service.NoteService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This class is the service implementation for the Note structure.
 */
@Transactional
public class NoteServiceImpl implements NoteService {

    private NoteDao noteDao;

    private AttachmentService attachmentService;

    public NoteServiceImpl() {
        super();
    }

    /**
     * @see NoteService#saveNoteValueList(java.util.List)
     */
    public void saveNoteList(List<Note> notes) {
        if (notes != null) {
            for (Note note : notes) {
                if (StringUtils.isBlank(note.getRemoteObjectIdentifier())) {
                    throw new IllegalStateException("The remote object identifier must be established on a Note before it can be saved.  The following note in the given list had a null or empty remote object identifier: " + note);
                }
                save(note);
            }
        }
    }

    /**
     * @see NoteService#save(Note)
     */
    public Note save(Note note) {
        validateNoteNotNull(note);
        if (StringUtils.isBlank(note.getRemoteObjectIdentifier())) {
            throw new IllegalStateException("The remote object identifier must be established on a Note before it can be saved.  Given note had a null or empty remote object identifier.");
        }
        noteDao.save(note);
        // move attachment from pending directory
        if (note.getAttachment() != null) {
            attachmentService.moveAttachmentWherePending(note);
        }
        return note;
    }

    /**
     * @see NoteService#getByRemoteObjectId(java.lang.String)
     */
    public List<Note> getByRemoteObjectId(String remoteObjectId) {
        if (StringUtils.isBlank(remoteObjectId)) {
            throw new IllegalArgumentException("The remoteObjectId must not be null or blank.");
        }
        return noteDao.findByremoteObjectId(remoteObjectId);
    }

    /**
     * @see NoteService#getNoteByNoteId(java.lang.Long)
     */
    public Note getNoteByNoteId(Long noteId) {
        if (noteId == null) {
            throw new IllegalArgumentException("The noteId must not be null.");
        }
        return noteDao.getNoteByNoteId(noteId);
    }

    /**
     * @see NoteService#deleteNote(Note)
     */
    public void deleteNote(Note note) {
        validateNoteNotNull(note);
        noteDao.deleteNote(note);
    }

    /**
     * TODO this method seems awfully out of place in this service
     *
     * @see NoteService#createNote(Note, PersistableBusinessObject)
     */
    public Note createNote(Note noteToCopy, PersistableBusinessObject bo, String authorPrincipalId) {
        validateNoteNotNull(noteToCopy);
        if (bo == null) {
            throw new IllegalArgumentException("The bo must not be null.");
        }
        if (StringUtils.isBlank(authorPrincipalId)) {
            throw new IllegalArgumentException("The authorPrincipalId must not be null.");
        }
        // TODO: Why is a deep copy being done?  Nowhere that this is called uses the given note argument
        // again after calling this method.
        Note tmpNote = (Note) ObjectUtils.deepCopy(noteToCopy);
        tmpNote.setRemoteObjectIdentifier(bo.getObjectId());
        tmpNote.setAuthorUniversalIdentifier(authorPrincipalId);
        return tmpNote;
    }

    /**
     * Sets the data access object
     *
     * @param d
     */
    public void setNoteDao(NoteDao d) {
        this.noteDao = d;
    }

    /**
     * Retrieves a data access object
     */
    protected NoteDao getNoteDao() {
        return noteDao;
    }

    public void setAttachmentService(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    protected AttachmentService getAttachmentService() {
        return this.attachmentService;
    }

    private void validateNoteNotNull(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note must not be null.");
        }
    }

}
