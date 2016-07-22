/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.FileStorageFile;
import org.kuali.kfs.sys.businessobject.FileSystemFileStorageFile;
import org.kuali.kfs.sys.exception.FileStorageException;
import org.kuali.kfs.sys.service.FileStorageService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class FileSystemFileStorageServiceImpl implements FileStorageService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileSystemFileStorageServiceImpl.class);

    private String pathPrefix;

    private String getFullPathname(String filename) {
        if ( ! pathPrefix.endsWith(separator()) ) {
            return pathPrefix + separator() + filename;
        } else {
            return pathPrefix + filename;
        }
    }

    @Override
    public String separator() {
        return File.separator;
    }

    @Override
    public void open(String filename,Consumer<FileStorageFile> action) {
        LOG.debug("open() started");

        FileStorageFile fsf = null;
        try {
            fsf = new FileSystemFileStorageFile(getFullPathname(filename));
            action.accept(fsf);
        } finally {
            fsf.close();
        }
    }

    @Override
    public boolean fileExists(String filename) {
        LOG.debug("fileExists() started");

        File f = new File(getFullPathname(filename));

        return f.exists() && f.isFile();
    }

    @Override
    public InputStream getFileStream(String filename) {
        LOG.debug("getFileStream() started");

        try {
            return new FileInputStream(getFullPathname(filename));
        } catch (FileNotFoundException e) {
            LOG.error("getFileStream() Unable to get file",e);
            throw new FileStorageException("Unable to get file",e);
        }
    }

    @Override
    public List<String> getFileContents(String filename) {
        LOG.debug("getFileContents() started");

        try {
            return FileUtils.readLines(new File(getFullPathname(filename)),"UTF-8");
        } catch (IOException e) {
            LOG.error("getFileContents() Unable to get file",e);
            throw new FileStorageException("Unable to get file",e);
        }
    }

	@Override
	public long getFileLength(String filename) {
		LOG.debug("getFileLength() started");

		return new File(getFullPathname(filename)).length();
	}

    @Override
    public void delete(String filename) {
        LOG.debug("delete() started");

        if ( fileExists(filename) ) {
            File f = new File(getFullPathname(filename));

            if (! f.delete()) {
                LOG.error("delete() Unable to delete file");
                throw new FileStorageException("Unable to delete file");
            }
        } else {
            LOG.error("delete() Unable to delete file - does not exist");
            throw new FileStorageException("Unable to delete file - does not exist");
        }
    }

    @Override
    public List<String> getFilesMatching(String prefix) {
        LOG.debug("getFilesMatching() started");

        return getFilesMatching(prefix,null);
    }

    @Override
    public List<String> getFilesMatching(String prefix, String extension) {
        LOG.debug("getFilesMatching() started");

        List<String> matches = new ArrayList<>();
        File dir = new File(getFullPathname(prefix));

        for (File f : dir.listFiles()) {
            if ( (extension == null) || (f.getName().toLowerCase().endsWith(extension.toLowerCase())) ) {
                matches.add(f.getName());
            }
        }
        return matches;
    }

    @Override
    public void mkdir(String dirname) {
        LOG.debug("mkdir() started");

        File f = new File(getFullPathname(dirname));
        if ( ! f.mkdir() ) {
            LOG.error("mkdir() Unable to make directory");
            throw new FileStorageException("Unable to make directory");
        }
    }

    @Override
    public void emptyDirectory(String dirname) {
        LOG.debug("emptyDirectory() started");

        if ( ! directoryExists(dirname) ) {
            LOG.error("emptyDirectory() Unable to empty directory, it does not exist");
            throw new FileStorageException("Unable to empty directory");
        }

        File dir = new File(getFullPathname(dirname));
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                emptyDirectory(dirname + separator() + f.getName());
            } else {
                delete(dirname + separator() + f.getName());
            }
        }
    }

    @Override
    public void rmdir(String dirname) {
        LOG.debug("rmdir() started");

        if ( directoryExists(dirname) ) {
            File f = new File(getFullPathname(dirname));
            if ( ! f.delete() ) {
                LOG.error("rmdir() Unable to remove directory");
                throw new FileStorageException("Unable to remove directory");
            }
        } else {
            LOG.error("rmdir() Unable to remove directory, does not exist");
            throw new FileStorageException("Unable to remove directory, does not exist");
        }
    }

    @Override
    public boolean directoryExists(String dirname) {
        LOG.debug("directoryExists() started");

        File f = new File(getFullPathname(dirname));

        return f.exists() && f.isDirectory();
    }

    @Override
    public void createDoneFile(String filename) {
        LOG.debug("createDoneFile() started");

        String fullPath = getFullPathname(filename);
        fullPath = StringUtils.substringBeforeLast(fullPath,".") + ".done";

        File f = new File(fullPath);
        try {
            if ( (! f.exists()) ) {
                if ( ! f.createNewFile() ) {
                    LOG.error("createDoneFile() Unable to create done file");
                    throw new FileStorageException("Unable to create done file");
                }
            } else {
                if ( f.isDirectory() ) {
                    LOG.error("createDoneFile() Unable to create done file, it is a directory");
                    throw new FileStorageException("Unable to create done file, it is a directory");
                }
            }
        } catch (IOException e) {
            LOG.error("createDoneFile() Unable to create done file",e);
            throw new FileStorageException("Unable to create done file",e);
        }
    }

    @Override
    public void removeDoneFiles(List<String> files) {
        LOG.debug("removeDoneFiles() started");

        for (String filename : files) {
            File f = new File(StringUtils.substringBeforeLast(getFullPathname(filename),".") + ".done");
            if ( f.exists() ) {
                if ( ! f.delete() ) {
                    LOG.error("removeDoneFiles() Unable to delete done file " + f.getAbsolutePath());
                    throw new FileStorageException("Unable to delete done file");
                }
            }
        }
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

}
