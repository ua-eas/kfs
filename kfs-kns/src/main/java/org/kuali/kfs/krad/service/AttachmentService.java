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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.bo.Attachment;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Defines the methods common to all AttachmentService implementations
 */
public interface AttachmentService {
    /**
     * Stores the given fileContents and returns referring Attachment object whieh acts as a momento to the archived object.
     *
     * @param document TODO
     * @param foo
     * @return Attachment
     * @throws IOException
     */
    public Attachment createAttachment(PersistableBusinessObject parent, String uploadedFileName, String mimeType, int fileSize, InputStream fileContents, String attachmentType) throws IOException;

    /**
     * Retrieves a given Attachments contents from the corresponding Attachment object
     *
     * @param documentAttachment
     * @return OutputStream
     * @throws IOException
     */
    public InputStream retrieveAttachmentContents(Attachment attachment) throws IOException;

    /**
     * Deletes a given DocumentAttachment contents from the corresponding Attachment object
     *
     * @param documentAttachment
     */
    public void deleteAttachmentContents(Attachment attachment);

    /**
     * Moves attachments on notes from the pending directory to the real one
     *
     * @param note the Note from which to move attachments.  If this Note does not
     *             have an attachment then this method does nothing.
     * @throws IllegalArgumentException if the given Note is null
     * @throws IllegalArgumentException if the Note does not have a valid object id
     */
    public void moveAttachmentWherePending(Note note);

    /**
     * Deletes pending attachments that were last modified before the given time.  Java does not have easy access to a file's creation
     * time, so we use modification time instead.
     *
     * @param modificationTime the number of milliseconds since "the epoch" (i.e.January 1, 1970, 00:00:00 GMT).  java.util.Date and java.util.Calendar's
     *                         methods return time in this format.  If a pending attachment was modified before this time, then it will be deleted (unless an error occurs)
     */
    public void deletePendingAttachmentsModifiedBefore(long modificationTime);

    public Attachment getAttachmentByNoteId(Long noteId);
}
