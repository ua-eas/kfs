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
package org.kuali.kfs.gl.batch.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.gl.batch.service.CollectorScrubberService;
import org.kuali.kfs.gl.batch.service.CollectorService;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.InitiateDirectoryBase;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
public class CollectorServiceImpl extends InitiateDirectoryBase implements CollectorService {
    private static Logger LOG = Logger.getLogger(CollectorServiceImpl.class);

    private CollectorHelperService collectorHelperService;
    private BatchInputFileService batchInputFileService;
    private List<BatchInputFileType> collectorInputFileTypes;
    private DateTimeService dateTimeService;
    private CollectorScrubberService collectorScrubberService;
    private String batchFileDirectoryName;

    @Override
    public CollectorReportData performCollection() {
        LOG.debug("performCollection() started");

        //add a step to check for directory paths
        prepareDirectories(getRequiredDirectoryNames());

        CollectorReportData collectorReportData = new CollectorReportData();
        List<CollectorScrubberStatus> collectorScrubberStatuses = new ArrayList<>();

        String collectorFinalOutputFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_OUTPUT + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        PrintStream collectorFinalOutputFilePs;
        try {
            collectorFinalOutputFilePs = new PrintStream(collectorFinalOutputFileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("writing all collector result files to output file process Stopped: " + e.getMessage(), e);
        }

        for (BatchInputFileType collectorInputFileType : collectorInputFileTypes) {
            List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(collectorInputFileType);
            for (String inputFileName : fileNamesToLoad) {
                LOG.info("performCollection() Collecting file: " + inputFileName);

                boolean processSuccess = collectorHelperService.loadCollectorFile(inputFileName, collectorReportData, collectorScrubberStatuses, collectorInputFileType, collectorFinalOutputFilePs);

                if (processSuccess) {
                    renameCollectorScrubberFiles();
                }
                collectorReportData.getLoadedfileNames().add(inputFileName);
            }
        }

        collectorScrubberService.removeTempGroups(collectorScrubberStatuses);
        collectorFinalOutputFilePs.close();

        return collectorReportData;
    }

    @Override
    public void finalizeCollector(CollectorReportData collectorReportData) {
        LOG.debug("finalizeCollector() started");

        // remove all done files for processed files
        removeDoneFiles(collectorReportData.getLoadedfileNames());

        // create a done file for collector gl output
        String collectorFinalOutputDoneFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_OUTPUT + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
        File doneFile = new File(collectorFinalOutputDoneFileName);
        if (!doneFile.exists()) {
            try {
                doneFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error creating collector done file", e);
            }
        }
    }

    @Override
    public List<String> getRequiredDirectoryNames() {
        LOG.debug("getRequiredDirectoryNames() started");

        List<String> requiredDirectoryList = new ArrayList<>();
        for (BatchInputFileType batchInputFile : collectorInputFileTypes) {
            requiredDirectoryList.add(batchInputFile.getDirectoryPath());
        }
        return requiredDirectoryList;
    }

    /**
     * Clears out associated .done files for the processed data files.
     *
     * @param dataFileNames the name of files with done files to remove
     */
    protected void removeDoneFiles(Collection<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    protected void renameCollectorScrubberFiles() {
        String filePath = batchFileDirectoryName + File.separator;
        List<String> fileNameList = new ArrayList<>();
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_BACKUP_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_INPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_VALID_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_ERROR_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_EXPIRED_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_ERROR_SORTED_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_VAILD_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_ERROR_OUTPUT_FILE);

        for (String fileName : fileNameList) {
            File file = new File(filePath + fileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            if (file.exists()) {
                String changedFileName = filePath + fileName + "." + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());
                file.renameTo(new File(changedFileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        }
    }

    public void setCollectorHelperService(CollectorHelperService collectorHelperService) {
        this.collectorHelperService = collectorHelperService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setCollectorInputFileTypes(List<BatchInputFileType> collectorInputFileTypes) {
        this.collectorInputFileTypes = collectorInputFileTypes;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setCollectorScrubberService(CollectorScrubberService collectorScrubberService) {
        this.collectorScrubberService = collectorScrubberService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
