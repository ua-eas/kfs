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
package org.kuali.kfs.sys.service.impl;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.exception.FileStorageException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Test the File system implementation of the File Storage Service
 */
public class FileSystemFileStorageServiceImplTest {
    private final static String FILE_DATA = "line 1\nline 2\n";

    private FileSystemFileStorageServiceImpl service;
    private String tempFolder;

    private boolean fileExists(String filename) {
        File f = new File(tempFolder + filename);
        return f.exists();
    }

    private String getUniqueFilename(String extension) {
        String filename;
        do {
            filename = RandomStringUtils.randomAlphanumeric(8);
        } while ((new File(tempFolder + filename + (extension != null ? "." + extension : ""))).exists());
        return filename;
    }

    private String getUniqueFilename() {
        return getUniqueFilename(null);
    }

    private void createFile(String filename) throws IOException {
        File f = new File(tempFolder + filename);
        FileUtils.writeStringToFile(f, FILE_DATA);
    }

    private void deleteFile(String filename) {
        File f = new File(tempFolder + filename);
        f.delete();
    }

    private void makeDirectory(String directory) {
        File f = new File(tempFolder + directory);
        if (!f.mkdir()) {
            System.out.println("WTF?");
        }
    }

    @Before
    public void setup() {
        tempFolder = System.getProperty("java.io.tmpdir");

        if (!StringUtils.endsWith(tempFolder, File.separator)) {
            tempFolder = tempFolder + File.separator;
        }

        service = new FileSystemFileStorageServiceImpl();
        service.setPathPrefix(tempFolder);
    }

    @Test
    public void testSeparatorReturnsOperatingSystemSeparator() {
        FileSystemFileStorageServiceImpl service = new FileSystemFileStorageServiceImpl();

        Assert.assertEquals("Should return operating system separator", File.separator, service.separator());
    }

    @Test
    public void testSavesToFile() throws IOException {
        String filename = getUniqueFilename("txt");

        service.open(filename, (outputFile) -> {
            PrintWriter pw = new PrintWriter(outputFile.getOutputStream());
            pw.print("test");
            pw.flush();
        });

        String data = FileUtils.readFileToString(new File(tempFolder + filename));
        (new File(tempFolder + filename)).delete();

        Assert.assertEquals("File should have been written", "test", data);
    }

    @Test
    public void testSavesFail() {
        String filename = getUniqueFilename("txt");

        makeDirectory(filename);

        try {
            service.open(filename, (outputFile) -> {
                PrintWriter pw = new PrintWriter(outputFile.getOutputStream());
                pw.print("test");
                pw.flush();
            });
            Assert.fail("Should have thrown exception");
        } catch (FileStorageException e) {
            // This is expected
        }

        deleteFile(filename);
    }

    @Test
    public void testFileExists() throws IOException {
        String filename = getUniqueFilename("txt");

        createFile(filename);

        Assert.assertEquals("File should exist", true, service.fileExists(filename));

        deleteFile(filename);
    }

    @Test
    public void testFileExistsDirectory() {
        String filename = getUniqueFilename("txt");

        makeDirectory(filename);

        Assert.assertEquals("File should not exist", false, service.fileExists(filename));

        deleteFile(filename);
    }

    @Test
    public void testFileExistsNoFile() {
        String filename = getUniqueFilename("txt");

        Assert.assertEquals("File should not exist", false, service.fileExists(filename));
    }

    @Test
    public void testGetFileGoodFile() throws IOException {
        String filename = getUniqueFilename("txt");

        createFile(filename);

        InputStream is = service.getFileStream(filename);
        InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        Assert.assertEquals("Should have read data from file", "line 1", line);

        deleteFile(filename);
    }

    @Test
    public void testGetFileNotGoodFile() {
        String filename = getUniqueFilename("txt");

        try {
            service.getFileStream(filename);
            Assert.fail("Method should have thrown exception");
        } catch (FileStorageException e) {
            // This is expected
        }
    }

    @Test
    public void testReadFileContents() throws IOException {
        String filename = getUniqueFilename("txt");

        createFile(filename);

        List<String> lines = service.getFileContents(filename);
        Assert.assertEquals("Line 1 should be read", "line 1", lines.get(0));
        Assert.assertEquals("Line 2 should be read", "line 2", lines.get(1));

        deleteFile(filename);
    }

    @Test
    public void testReadFileContentsFileNotExist() {
        String filename = getUniqueFilename("txt");

        try {
            service.getFileContents(filename);
            Assert.fail("Method should have thrown exception");
        } catch (FileStorageException e) {
            // This is expected
        }
    }

    @Test
    public void testDeleteFile() throws IOException {
        String filename = getUniqueFilename("txt");

        createFile(filename);

        service.delete(filename);

        File f = new File(tempFolder + filename);
        Assert.assertEquals("File should not exist", false, f.exists());
    }

    @Test
    public void testDeleteFileDirectory() {
        String filename = getUniqueFilename("txt");

        makeDirectory(filename);

        try {
            service.delete(filename);
            Assert.fail("Method should have thrown exception");
        } catch (Exception e) {
            // This is expected
        }

        deleteFile(filename);
    }

    @Test
    public void testDeleteFileNotExist() {
        String filename = getUniqueFilename("txt");

        try {
            service.delete(filename);
            Assert.fail("Method should have thrown exception");
        } catch (Exception e) {
            // This is expected
        }
    }

    @Test
    public void testGetMatchingFiles() throws IOException {

        String dir1 = getUniqueFilename();
        String dir2 = getUniqueFilename();

        makeDirectory(dir1);
        makeDirectory(dir2);

        createFile(dir1 + File.separator + "data1.data");
        createFile(dir1 + File.separator + "data2.data");
        createFile(dir1 + File.separator + "data3.done");

        createFile(dir2 + File.separator + "data3.done");

        // Some results
        List<String> matches = service.getFilesMatching(dir1, "data");
        Assert.assertEquals("Should have found 2 files", 2, matches.size());

        // No results
        matches = service.getFilesMatching(dir2, "data");
        Assert.assertEquals("Should have found 0 files", 0, matches.size());

        // No extensions specified some results
        matches = service.getFilesMatching(dir1);
        Assert.assertEquals("Should have found 3 files", 3, matches.size());

        // No extensions specified no results
        matches = service.getFilesMatching(dir2);
        Assert.assertEquals("Should have found 1 file", 1, matches.size());

        deleteFile(dir1 + File.separator + "data1.data");
        deleteFile(dir1 + File.separator + "data2.data");
        deleteFile(dir1 + File.separator + "data3.done");
        deleteFile(dir2 + File.separator + "data3.done");
        deleteFile(dir1);
        deleteFile(dir2);
    }

    @Test
    public void testMakeDirectorySuccess() {
        String dirname = getUniqueFilename();
        service.mkdir(dirname);

        File d = new File(tempFolder + dirname);
        Assert.assertEquals("Directory should exist", true, d.exists());
        Assert.assertEquals("Should be a directory", true, d.isDirectory());

        deleteFile(dirname);
    }

    @Test
    public void testMakeDirectoryFail() {
        String dirname = getUniqueFilename();
        makeDirectory(dirname);

        try {
            service.mkdir(dirname);
            Assert.fail("Should have thrown exception");
        } catch (FileStorageException e) {
            // This is expected
        }

        deleteFile(dirname);
    }

    @Test
    public void testEmptyDirectory() throws IOException {

        String dir1 = getUniqueFilename();
        makeDirectory(dir1);

        createFile(dir1 + File.separator + "data1.data");
        createFile(dir1 + File.separator + "data2.data");
        createFile(dir1 + File.separator + "data3.done");

        service.emptyDirectory(dir1);

        File f = new File(tempFolder + dir1);
        Assert.assertEquals("Folder not empty", true, f.delete());
    }

    @Test
    public void testEmptyDirectoryError() throws IOException {
        String filename = getUniqueFilename();
        createFile(filename);

        try {
            service.emptyDirectory(filename);
            Assert.fail("The method should have thrown an exception");
        } catch (FileStorageException e) {
            // This is expected
        }
        deleteFile(filename);
    }

    @Test
    public void testRmdir() {
        String dirname = getUniqueFilename();
        makeDirectory(dirname);

        service.rmdir(dirname);

        File f = new File(tempFolder + dirname);
        Assert.assertEquals("Directory should not exist", false, f.exists());
    }

    @Test
    public void testRmdirFail() throws IOException {
        String filename = getUniqueFilename();
        createFile(filename);

        try {
            service.rmdir(filename);
            Assert.fail("The method should have thrown an exception");
        } catch (FileStorageException e) {
            // This is expected
        }
        deleteFile(filename);
    }

    @Test
    public void testDirectoryExists() throws IOException {
        String dirname = getUniqueFilename();
        makeDirectory(dirname);

        String dirname2 = getUniqueFilename();

        String filename = getUniqueFilename();
        createFile(filename);

        Assert.assertEquals("Directory exists", true, service.directoryExists(dirname));

        Assert.assertEquals("Directory should not exist", false, service.directoryExists(dirname2));

        Assert.assertEquals("File is not a directory", false, service.directoryExists(filename));

        deleteFile(dirname);
        deleteFile(filename);
    }

    @Test
    public void testCreateDoneFile() {
        String filename = getUniqueFilename();

        service.createDoneFile(filename + ".data");

        File f = new File(tempFolder + filename + ".done");
        Assert.assertEquals("Done file should exist", true, f.exists());

        deleteFile(filename + ".done");
    }

    @Test
    public void testCreateDoneFileFail() {
        String filename = getUniqueFilename();
        makeDirectory(filename + ".done");

        try {
            service.createDoneFile(filename + ".data");
            Assert.fail("method should have thrown exception");
        } catch (FileStorageException e) {
            // This is expected
        }

        deleteFile(filename + ".done");
    }

    @Test
    public void testRemoveDoneFiles() throws IOException {
        String filename1 = getUniqueFilename();
        String filename2 = getUniqueFilename();
        String filename3 = getUniqueFilename();

        createFile(filename1 + ".done");
        createFile(filename2 + ".done");

        List<String> dataFiles = new ArrayList<>();
        dataFiles.add(filename1 + ".data");
        dataFiles.add(filename2 + ".data");
        dataFiles.add(filename3 + ".data");

        service.removeDoneFiles(dataFiles);

        Assert.assertEquals("file 1 should not exist", false, fileExists(filename1 + ".done"));
        Assert.assertEquals("file 2 should not exist", false, fileExists(filename2 + ".done"));
    }
}
