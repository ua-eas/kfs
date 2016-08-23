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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.FileStorageFile;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interface to the file storage provider.
 */
public interface FileStorageService {
    /**
     * Return the OS separator between directories.
     *
     * @return The correct path separator for the environment.
     */
    String separator();

    /**
     * Create a file with a handle to write data.
     *
     * @param filename filename to create
     * @param action   code to create data for file
     */
    void open(String filename, Consumer<FileStorageFile> action);

    /**
     * Determine if a file exists.
     *
     * @param filename File name to test
     * @return true if file exists
     */
    boolean fileExists(String filename);

    /**
     * Get file contents into a stream.
     *
     * @param filename File name to read
     * @return Stream containing file data
     */
    InputStream getFileStream(String filename);

    /**
     * Returns a List<String> containing each line in the file
     *
     * @param filename the file whose contents we want to retrieve
     * @return a String array containing each line in the file
     */
    List<String> getFileContents(String filename);

    /**
     * Gets the length of the file.
     *
     * @param filename the file whose length we want to check
     * @return The length, in bytes, of the file, or 0L if the file does not exist.
     */
    long getFileLength(String filename);

    /**
     * Delete a file.
     *
     * @param filename File name to delete
     */
    void delete(String filename);

    /**
     * Get a list of all files at a prefix
     *
     * @param prefix Prefix to check
     * @return List of filenames matching prefix
     */
    List<String> getFilesMatching(String prefix);

    /**
     * Get a list of all files at a prefix with a specific extension.
     *
     * @param prefix    Prefix to check
     * @param extension File name extension to match
     * @return List of filenames matching prefix with correct extension
     */
    List<String> getFilesMatching(String prefix, String extension);

    /**
     * Make a folder.
     *
     * @param dirname Folder name to create
     */
    void mkdir(String dirname);

    /**
     * Delete the contents of a folder.
     *
     * @param dirname Folder name to empty
     */
    void emptyDirectory(String dirname);

    /**
     * Delete a folder if empty.
     *
     * @param dirname Folder name to delete
     */
    void rmdir(String dirname);

    /**
     * Determine if a directory exists.
     *
     * @param dirname Directory name to test
     * @return True if exists
     */
    boolean directoryExists(String dirname);

    /**
     * Create a done file.
     *
     * @param filename File name, with extension of "done" to create
     */
    void createDoneFile(String filename);

    /**
     * Remove all the done files given a list of data files.
     *
     * @param files List of file names, to change extensions to "done" and remove
     */
    void removeDoneFiles(List<String> files);
}
