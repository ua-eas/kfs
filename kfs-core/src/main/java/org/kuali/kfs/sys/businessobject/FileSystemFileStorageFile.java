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
package org.kuali.kfs.sys.businessobject;

import org.kuali.kfs.sys.exception.FileStorageException;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileSystemFileStorageFile implements FileStorageFile, Closeable, AutoCloseable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileSystemFileStorageFile.class);

    private String filename;
    private OutputStream outputStream;

    public FileSystemFileStorageFile(String filename) {
        this.filename = filename;
    }

    @Override
    public OutputStream getOutputStream() {
        LOG.debug("getOutputStream() started");
        try {
            outputStream = new FileOutputStream(filename);
            return outputStream;
        } catch (IOException e) {
            LOG.error("getOutputStream() Unable to get output stream", e);
            throw new FileStorageException("Unable to get output stream: " + e.getMessage());
        }
    }

    public void close() {
        LOG.debug("close() started");
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                LOG.error("close() Unable to close output stream", e);
                throw new FileStorageException("Unable to close output stream: " + e.getMessage());
            }
        }
    }
}
