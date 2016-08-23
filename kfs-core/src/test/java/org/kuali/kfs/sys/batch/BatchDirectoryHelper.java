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
package org.kuali.kfs.sys.batch;

import org.apache.commons.io.FileUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.io.File;
import java.io.IOException;

/**
 * Helper class to build up a batch directory and then tear it down when it is no longer needed.  To create a directory, call setUp; to remove
 * the directory, just call tearDown.
 */
public class BatchDirectoryHelper {
    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BatchDirectoryHelper.class);

    private String module;
    private String directoryName;
    private boolean batchFileDirectoryCreated = false;

    private volatile static ConfigurationService configurationService;

    public BatchDirectoryHelper(String module, String directoryName) {
        this.module = module;
        this.directoryName = directoryName;
    }

    public void createBatchDirectory() {
        File batchFileDirectory = new File(getBatchFileDirectoryName());
        if (!batchFileDirectory.exists()) {
            batchFileDirectory.mkdir();
            batchFileDirectoryCreated = true;
        }
    }

    public void removeBatchDirectory() {
        if (batchFileDirectoryCreated) {
            try {
                File batchDirectoryFile = new File(getBatchFileDirectoryName());
                FileUtils.deleteDirectory(batchDirectoryFile);
                batchFileDirectoryCreated = false;
            } catch (IOException e) {
                LOG.error("Could not remove batch directory for test");
                throw new RuntimeException(e);
            }
        }
    }

    public String getBatchFileDirectoryName() {
        String stagingDirectory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory");
        return stagingDirectory + File.separator + this.module + File.separator + this.directoryName;
    }

    private ConfigurationService getConfigurationService() {
        if (this.configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }
}
