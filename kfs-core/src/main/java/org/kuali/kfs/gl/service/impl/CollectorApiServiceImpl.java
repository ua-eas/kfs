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
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.gl.service.CollectorApiService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class CollectorApiServiceImpl implements CollectorApiService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorApiServiceImpl.class);

    private ConfigurationService configurationService;
    private CollectorHelperService collectorHelperService;
    private BatchInputFileType collectorXmlInputFileType;
    private BatchInputFileType collectorFlatFileInputFileType;

    @Override
    public List<String> collectorApiLoad(InputStream inputStream, String contentType) {
        LOG.debug("collectorApiLoad() started");

        BatchInputFileType collectorInputFileType;
        if ( MediaType.TEXT_PLAIN.equals(contentType) ) {
            collectorInputFileType = collectorFlatFileInputFileType;
        } else {
            collectorInputFileType = collectorXmlInputFileType;
        }

        List<ErrorMessage> messages = collectorHelperService.loadCollectorApiData(inputStream,collectorInputFileType);
        return messages.stream().map(msg -> convertErrorMessage(msg)).collect(Collectors.toList());
    }

    private String convertErrorMessage(ErrorMessage message) {
        String msg = configurationService.getPropertyValueAsString(message.getErrorKey());
        MessageFormat mf = new MessageFormat(msg);
        return mf.format(message.getMessageParameters());
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setCollectorXmlInputFileType(BatchInputFileType collectorXmlInputFileType) {
        this.collectorXmlInputFileType = collectorXmlInputFileType;
    }

    public void setCollectorFlatFileInputFileType(BatchInputFileType collectorFlatFileInputFileType) {
        this.collectorFlatFileInputFileType = collectorFlatFileInputFileType;
    }

    public void setCollectorHelperService(CollectorHelperService collectorHelperService) {
        this.collectorHelperService = collectorHelperService;
    }
}
