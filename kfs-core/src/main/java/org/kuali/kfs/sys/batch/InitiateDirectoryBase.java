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
package org.kuali.kfs.sys.batch;

import org.kuali.kfs.sys.FileUtil;

import java.util.List;

/**
 * Base class for InitiateDirectory implementations
 */
public abstract class InitiateDirectoryBase implements InitiateDirectory {
    private boolean skipDirectoryInitiation;

    /**
     * Create the directories needed
     *
     * @see org.kuali.kfs.sys.batch.service.InitiateDirectory#prepareDirectories(java.util.List)
     */
    public void prepareDirectories(List<String> directoryPaths) {
        if (skipDirectoryInitiation) {
            FileUtil.createDirectories(directoryPaths);
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.InitiateDirectory#getRequiredDirectoryNames()
     */
    public abstract List<String> getRequiredDirectoryNames();

    public void setSkipDirectoryInitiation(boolean skipDirectoryInitiation) {
        this.skipDirectoryInitiation = skipDirectoryInitiation;
    }


}
