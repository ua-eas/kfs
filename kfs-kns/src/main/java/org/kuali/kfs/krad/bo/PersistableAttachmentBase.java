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
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

/**
 * This is a description of what this class does - chitra07 don't forget to fill this in. 
 * 
 * 
 *
 */
@MappedSuperclass
public class PersistableAttachmentBase extends PersistableBusinessObjectBase implements PersistableAttachment {
	@Lob
	@Column(name="ATT_CNTNT")
    private byte[] attachmentContent;
	@Column(name="FILE_NM")
    private String fileName;
	@Column(name="CNTNT_TYP")
    private String contentType;

    /**
     * This overridden method ...
     * 
     * @see PersistableAttachment#getAttachmentContent()
     */
    public byte[] getAttachmentContent() {
        return this.attachmentContent;
    }

    /**
     * This overridden method ...
     * 
     * @see PersistableAttachment#setAttachmentContent(byte[])
     */
    public void setAttachmentContent(byte[] attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    /**
     * This overridden method ...
     * 
     * @see PersistableAttachment#getFileName()
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * This overridden method ...
     * 
     * @see PersistableAttachment#setFileName(java.lang.String)
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * This overridden method ...
     * 
     * @see PersistableAttachment#getContentType()
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * This overridden method ...
     * 
     * @see PersistableAttachment#setContentType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
