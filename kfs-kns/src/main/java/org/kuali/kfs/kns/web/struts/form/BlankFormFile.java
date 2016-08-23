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
package org.kuali.kfs.kns.web.struts.form;

import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Empty FormFile instance, used to clear out FormFile attributes of Struts forms.
 */
public class BlankFormFile implements FormFile, Serializable {
    public void destroy() {
    }

    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    public byte[] getFileData() throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException();
    }

    public String getFileName() {
        return "";
    }

    public int getFileSize() {
        return 0;
    }

    public InputStream getInputStream() throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException();
    }

    public void setContentType(String contentType) {
        throw new UnsupportedOperationException();
    }

    public void setFileName(String fileName) {
        throw new UnsupportedOperationException();
    }

    public void setFileSize(int fileSize) {
        throw new UnsupportedOperationException();
    }
}
