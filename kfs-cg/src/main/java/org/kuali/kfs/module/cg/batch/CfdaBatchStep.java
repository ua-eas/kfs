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
package org.kuali.kfs.module.cg.batch;

import org.apache.log4j.Logger;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.module.cg.businessobject.CfdaUpdateResults;
import org.kuali.kfs.module.cg.service.CfdaService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * Parses data from a government web page listing the valid CFDA codes. The codes are then compared with what's in the CFDA table in
 * Kuali. Codes set to be managed automatically are reconciled with what's on the web page. Codes managed manually are left alone.
 * Finally an email containing a summary of what was done by the step execution is sent to the member of the CG_CFDA_BATCH_NOTIFY workgroup.
 */
public class CfdaBatchStep extends AbstractStep {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(CfdaBatchStep.class);

    protected CfdaService cfdaService;
    protected EmailService emailService;
    protected ParameterService parameterService;
    protected ConfigurationService configurationService;

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");

        BodyMailMessage message = new BodyMailMessage();

        try {
            CfdaUpdateResults results = cfdaService.update();

            StringBuilder builder = new StringBuilder();
            builder.append("The CFDA batch script is complete.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
            builder.append(" records were deactivated because they are no longer on the web site.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsInKfsDatabase());
            builder.append(" records were in the KFS database.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsNewlyAddedFromWebSite());
            builder.append(" records were newly added from the web site.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsNotUpdatedBecauseManual());
            builder.append(" records were not updated because they are manual.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsReActivated());
            builder.append(" records were re-activated.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsRetrievedFromWebSite());
            builder.append(" records were retrieved from the web site.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecordsUpdatedBecauseAutomatic());
            builder.append(" records were updated because they are automatic.\n");
            builder.append(" - ");
            builder.append(results.getNumberOfRecrodsNotUpdatedForHistoricalPurposes());
            builder.append(" records were not updated for historical reasons.\n");
            builder.append(" - Message\n");
            builder.append(null != results.getMessage() ? results.getMessage() : "");

            LOG.info(message.toString());

            Collection<String> listservAddresses = parameterService.getParameterValuesAsString(CfdaBatchStep.class, KFSConstants.RESULT_SUMMARY_TO_EMAIL_ADDRESSES);
            if (listservAddresses.isEmpty()) {
                LOG.fatal("execute() No addresses for notification to in " + KFSConstants.RESULT_SUMMARY_TO_EMAIL_ADDRESSES + " parameter.  Aborting Email.");
                return true;
            }

            for (String listserv : listservAddresses) {
                LOG.info("execute() Mailing to: " + listserv);

                message.addToAddress(listserv);
            }

            message.setFromAddress(listservAddresses.iterator().next());

            message.setSubject(configurationService.getPropertyValueAsString(KFSKeyConstants.CFDA_UPDATE_EMAIL_SUBJECT_LINE));
            message.setMessage(builder.toString());
            emailService.sendMessage(message,false);
        } catch (IOException ioe) {
            LOG.warn("Exception while updating CFDA codes.", ioe);
            return false;
        }
        return true;
    }

    public void setCfdaService(CfdaService cfdaService) {
        this.cfdaService = cfdaService;
    }

    @Override
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}

