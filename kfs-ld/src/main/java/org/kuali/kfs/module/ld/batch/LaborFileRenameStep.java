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
package org.kuali.kfs.module.ld.batch;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A step to run the scrubber process.
 */
public class LaborFileRenameStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborFileRenameStep.class);
    private String batchFileDirectoryName;

    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);
        String filePath = batchFileDirectoryName + File.separator;
        List<String> fileNameList = new ArrayList<String>();
        fileNameList.add(LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.BACKUP_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.PRE_SCRUBBER_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.POSTER_INPUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.POSTER_VALID_OUTPUT_FILE);
        fileNameList.add(LaborConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE);

        for (String fileName : fileNameList) {
            File file = new File(filePath + fileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            if (file.exists()) {
                String changedFileName = filePath + fileName + "." + getDateTimeService().toDateTimeStringForFilename(jobRunDate);
                file.renameTo(new File(changedFileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        }


        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("LaborFileRenameStep of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
