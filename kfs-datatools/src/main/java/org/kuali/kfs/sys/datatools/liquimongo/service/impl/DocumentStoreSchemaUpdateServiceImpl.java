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
package org.kuali.kfs.sys.datatools.liquimongo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kuali.kfs.sys.datatools.liquimongo.businessobject.DocumentStoreChange;
import org.kuali.kfs.sys.datatools.liquimongo.change.DocumentStoreChangeHandler;
import org.kuali.kfs.sys.datatools.liquimongo.dataaccess.DocumentStoreUpdateProcessDao;
import org.kuali.kfs.sys.datatools.liquimongo.service.DocumentStoreSchemaUpdateService;
import org.kuali.kfs.sys.datatools.util.ResourceLoaderUtil;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Update the schema of the document store database on system start up.
 *
 * This must be set transactional because the non-transactional annotation
 * is in the kfs-core package.  kfs-core depends on kfs-datatools so kfs-datatools
 * cannot depend on kfs-core (cyclic reference).  The class must have either the transactional or
 * nonTransactional annotation or unit tests fail.
 */
@Transactional
public class DocumentStoreSchemaUpdateServiceImpl implements DocumentStoreSchemaUpdateService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentStoreSchemaUpdateServiceImpl.class);

    private DocumentStoreUpdateProcessDao documentStoreUpdateProcessDao;
    private List<DocumentStoreChangeHandler> handlers;

    private String updatesPath;
    private String updatesList;
    private String revertPath;
    private String revertList;

    @Override
    public void updateDocumentStoreSchema() {
        LOG.debug("updateDocumentStoreSchema() started");

        if ( documentStoreUpdateProcessDao.isSchemaChangeLocked() ) {
            LOG.debug("updateDocumentStoreSchema() not running because schema is locked");
            return;
        }

        try {
            documentStoreUpdateProcessDao.lockSchemaChange();

            List<String> updateFiles = getUpdateFiles(updatesPath + updatesList);
            for (String updateFile : updateFiles) {
                LOG.debug("updateDocumentStoreSchema() " + updateFile);
                process(updateFile);
            }
        } catch (IOException ex) {
            LOG.error("updateDocumentStoreSchema() Unable to read and process updates", ex);
            throw new IllegalArgumentException("Unable to open update file " + updatesPath + updatesList, ex);
        } finally {
            documentStoreUpdateProcessDao.unlockSchemaChange();
        }
    }
    
    @Override
    public void revertDocumentStoreSchema() {
        LOG.debug("revertDocumentStoreSchema() started");

        if ( documentStoreUpdateProcessDao.isSchemaChangeLocked() ) {
            LOG.debug("revertDocumentStoreSchema() not running because schema is locked");
            return;
        }

        try {
            documentStoreUpdateProcessDao.lockSchemaChange();

            List<String> revertFiles = getUpdateFiles(revertPath + revertList);
            Collections.reverse(revertFiles);
            for (String revertFile : revertFiles) {
                LOG.debug("revertDocumentStoreSchema() " + revertFile);
                revert(revertFile);
            }
        } catch (IOException ex) {
            LOG.error("revertDocumentStoreSchema() Unable to read and process reversions", ex);
            throw new IllegalArgumentException("Unable to open revert file " + revertPath + revertList, ex);
        } finally {
            documentStoreUpdateProcessDao.unlockSchemaChange();
        }
    }

    @Override
    public void updateDocumentStoreSchemaForLocation(String location) {
        if (location.startsWith("/")) {
            location = "file:" + location;
        }
        FileInfo fileInfo = parseFilePath(location);
        setUpdatesPath(fileInfo.filePath);
        setUpdatesList(fileInfo.fileName);
        updateDocumentStoreSchema();
    }
    
    @Override
    public void revertDocumentStoreSchemaForLocation(String location) {
        if (location.startsWith("/")) {
            location = "file:" + location;
        }
        FileInfo fileInfo = parseFilePath(location);
        setRevertPath(fileInfo.filePath);
        setRevertList(fileInfo.fileName);
        revertDocumentStoreSchema();
    }

    /**
     * Read and process all the changes an update file.
     *
     * @param updateFile The file name to process
     * @throws IOException
     */
    public void process(String updateFile) throws IOException {
        LOG.debug("process() started");

        Resource resource = ResourceLoaderUtil.getFileResource(updatesPath + updateFile);
        if (resource == null) {
            throw new FileNotFoundException("Invalid resource specified: " + updatesPath + updateFile);
        }
        InputStream is = resource.getInputStream();

        if (is == null) {
            throw new IOException("process() Unable to read file: " + updatesPath + updateFile);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(is, JsonNode.class);

        JsonNode arrayOfChanges = rootNode.get("changeLog");

        Iterator<JsonNode> items = arrayOfChanges.elements();
        while ( items.hasNext() ) {
            JsonNode item = items.next();
            DocumentStoreChange change = new DocumentStoreChange(updateFile, item);
            applyChangeSetIfNecessary(change);
        }
    }
    
    /**
     * Read and reverts all the changes in an update file.
     *
     * @param revertFile The file name to revert
     * @throws IOException
     */
    public void revert(String revertFile) throws IOException {
        LOG.debug("revert() started");

        Resource resource = ResourceLoaderUtil.getFileResource(revertPath + revertFile);
        if (resource == null) {
            throw new FileNotFoundException("Invalid resource specified: " + revertPath + revertFile);
        }
        InputStream is = resource.getInputStream();

        if (is == null) {
            throw new IOException("revert() Unable to read file: " + revertPath + revertFile);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(is, JsonNode.class);

        JsonNode arrayOfChanges = rootNode.get("changeLog");

        Iterator<JsonNode> items = arrayOfChanges.elements();
        
        // Reverse the iterator, so we revert in the reverse order of the original processing
        List<JsonNode> itemsReversed = new ArrayList<JsonNode>();
        while (items.hasNext()) {
            itemsReversed.add(0, items.next());
        }
        
        for (JsonNode item : itemsReversed) {
            DocumentStoreChange change = new DocumentStoreChange(revertFile, item);
            revertChangeSetIfNecessary(change);
        }
    }

    /**
     * Apply the change set requested if it hasn't been made previously.
     *
     * @param changeSet The change to be made
     */
    private void applyChangeSetIfNecessary(DocumentStoreChange changeSet) {
        LOG.debug("applyChangeSetIfNecessary() started");

        if ( ! documentStoreUpdateProcessDao.hasSchemaChangeHappened(changeSet) ) {
            LOG.debug("applyChangeSetIfNecessary() Making change: " + changeSet);

            changeSet.getAllChanges().forEach((change) -> applyChange(change));
            documentStoreUpdateProcessDao.saveSchemaChange(changeSet);
        }
    }
    
    /**
     * Revert the change set requested if it has been made.
     *
     * @param changeSet The change to be made
     */
    private void revertChangeSetIfNecessary(DocumentStoreChange changeSet) {
        LOG.debug("revertChangeSetIfNecessary() started");

        if ( documentStoreUpdateProcessDao.hasSchemaChangeHappened(changeSet) ) {
            LOG.debug("revertChangeSetIfNecessary() Reverting change: " + changeSet);

            List<JsonNode> changes = changeSet.getAllChanges();
            Collections.reverse(changes);
            changes.forEach((change) -> revertChange(change));
            documentStoreUpdateProcessDao.removeSchemaChange(changeSet);
        }
    }

    /**
     * Apply one change from the set
     * @param change change to apply
     */
    private void applyChange(JsonNode change) {
        boolean changeMade = false;

        for (DocumentStoreChangeHandler handler : handlers) {
            if (handler.handlesChange(change)) {
                handler.makeChange(change);
                changeMade = true;
                break;
            }
        }

        if ( ! changeMade ) {
            LOG.error("applyChange() No document handler found for this change: " + change);
            throw new IllegalArgumentException("No handler registered to handle this change");
        }
    }
    
    /**
     * Revert one change from the set
     * @param change change to apply
     */
    private void revertChange(JsonNode change) {
        boolean changeMade = false;

        for (DocumentStoreChangeHandler handler : handlers) {
            if (handler.handlesChange(change)) {
                handler.revertChange(change);
                changeMade = true;
                break;
            }
        }

        if ( ! changeMade ) {
            LOG.error("applyChange() No document handler found for this change: " + change);
            throw new IllegalArgumentException("No handler registered to handle this change");
        }
    }

    /**
     * Parse the file containing a list of update files and return the list of file names.
     *
     * @param listFile Filename to parse
     * @return List of change files to process
     * @throws IOException
     */
    private List<String> getUpdateFiles(String listFile) throws IOException {
        LOG.debug("getUpdateFiles() started");

        List<String> files = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        Resource resource = ResourceLoaderUtil.getFileResource(listFile);
        if (resource == null) {
            LOG.warn("Invalid resource specified: " + updatesPath + listFile);
        }

        InputStream is = resource.getInputStream();
        if (is == null) {
            LOG.warn("getUpdateFiles() Unable to read file: " + updatesPath + listFile);
        }

        JsonNode rootNode = mapper.readValue(is, JsonNode.class);
        JsonNode arrayOfChanges = rootNode.get("updateFiles");

        Iterator<JsonNode> items = arrayOfChanges.elements();
        while (items.hasNext()) {
            JsonNode item = items.next();
            String filename = item.asText();
            files.add(filename);
        }
        return files;
    }

    private static FileInfo parseFilePath(String fullPath) {
        FileInfo fileInfo = new FileInfo();

        int splitIndex = fullPath.lastIndexOf("/");
        fileInfo.filePath = fullPath.substring(0, splitIndex + 1);
        fileInfo.fileName = fullPath.substring(splitIndex + 1);

        return fileInfo;
    }

    private static class FileInfo {
        public String filePath;
        public String fileName;
    }

    public void setUpdatesPath(String updatesPath) {
        this.updatesPath = updatesPath;
    }

    public void setUpdatesList(String updatesList) {
        this.updatesList = updatesList;
    }
    
    public void setRevertPath(String revertPath) {
        this.revertPath = revertPath;
    }

    public void setRevertList(String revertList) {
        this.revertList = revertList;
    }

    public void setDocumentStoreUpdateProcessDao(DocumentStoreUpdateProcessDao documentStoreUpdateProcessDao) {
        this.documentStoreUpdateProcessDao = documentStoreUpdateProcessDao;
    }

    public void setHandlers(List<DocumentStoreChangeHandler> handlers) {
        this.handlers = handlers;
    }
}
