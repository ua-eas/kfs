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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.log4j.PatternLayout;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.NDCFilter;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;

public class JobListener implements org.quartz.JobListener {
    private static final Logger LOG = Logger.getLogger(JobListener.class);
    protected static final String NAME = "jobListener";
    public static final String REQUESTOR_EMAIL_ADDRESS_KEY = "requestorEmailAdress";
    protected SchedulerService schedulerService;
    protected ConfigurationService configurationService;
    protected EmailService emailService;
    protected DateTimeService dateTimeService;

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException jobExecutionException) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            try {
                if (!((Job) jobExecutionContext.getJobInstance()).isNotRunnable()) {
                    notify(jobExecutionContext, schedulerService.getStatus(jobExecutionContext.getJobDetail()));
                }
            } finally {
                completeLogging(jobExecutionContext);
            }
        }
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            schedulerService.initializeJob(jobExecutionContext.getJobDetail().getName(), (Job) jobExecutionContext.getJobInstance());
            initializeLogging(jobExecutionContext);
            // We only want to auto-cancel executions if they are part of a master scheduling job
            // Otherwise, this is a standalone job and should fire, regardless of prior status
            if (jobExecutionContext.getMergedJobDataMap().containsKey(Job.MASTER_JOB_NAME)) {
                if (schedulerService.shouldNotRun(jobExecutionContext.getJobDetail())) {
                    ((Job) jobExecutionContext.getJobInstance()).setNotRunnable(true);
                }
            } else {
                ((Job) jobExecutionContext.getJobInstance()).setNotRunnable(false);
            }
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        if (jobExecutionContext.getJobInstance() instanceof Job) {
            throw new UnsupportedOperationException("JobListener does not implement jobExecutionVetoed(JobExecutionContext jobExecutionContext)");
        }
    }

    protected void initializeLogging(JobExecutionContext jobExecutionContext) {
        try {
        	if(Logger.getRootLogger().getAppender("StdOut") == null) {
        		Appender appender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
            	appender.setName("StdOut");
            	Logger.getRootLogger().addAppender(appender);
        	}
            Calendar startTimeCalendar = dateTimeService.getCurrentCalendar();
            StringBuilder nestedDiagnosticContext = new StringBuilder(StringUtils.substringAfter(BatchSpringContext.getJobDescriptor(jobExecutionContext.getJobDetail().getName()).getNamespaceCode(), "-").toLowerCase()).append(File.separator).append(jobExecutionContext.getJobDetail().getName()).append("-").append(dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()));
            ((Job) jobExecutionContext.getJobInstance()).setNdcAppender(new FileAppender(Logger.getRootLogger().getAppender("StdOut").getLayout(), getLogFileName(nestedDiagnosticContext.toString())));
            ((Job) jobExecutionContext.getJobInstance()).getNdcAppender().addFilter(new NDCFilter(nestedDiagnosticContext.toString()));
            Logger.getRootLogger().addAppender(((Job) jobExecutionContext.getJobInstance()).getNdcAppender());
            NDC.push(nestedDiagnosticContext.toString());
        } catch (IOException e) {
            LOG.warn("Could not initialize special custom logging for job: " + jobExecutionContext.getJobDetail().getName(), e);
        }
    }

    private void completeLogging(JobExecutionContext jobExecutionContext) {
        ((Job) jobExecutionContext.getJobInstance()).getNdcAppender().close();
        Logger.getRootLogger().removeAppender(((Job) jobExecutionContext.getJobInstance()).getNdcAppender());
        NDC.pop();
    }

    protected String getLogFileName(String nestedDiagnosticContext) {
        return new StringBuilder(configurationService.getPropertyValueAsString(KFSConstants.REPORTS_DIRECTORY_KEY)).append(File.separator).append(nestedDiagnosticContext.toString()).append(".log").toString();
    }

    protected void notify(JobExecutionContext jobExecutionContext, String jobStatus) {
        try {
            StringBuilder mailMessageSubject = new StringBuilder(jobExecutionContext.getJobDetail().getGroup()).append(": ").append(jobExecutionContext.getJobDetail().getName());
            BodyMailMessage mailMessage = new BodyMailMessage();
            mailMessage.setFromAddress(emailService.getDefaultFromAddress());
            if (jobExecutionContext.getMergedJobDataMap().containsKey(REQUESTOR_EMAIL_ADDRESS_KEY) && !StringUtils.isBlank(jobExecutionContext.getMergedJobDataMap().getString(REQUESTOR_EMAIL_ADDRESS_KEY))) {
                mailMessage.addToAddress(jobExecutionContext.getMergedJobDataMap().getString(REQUESTOR_EMAIL_ADDRESS_KEY));
            }
            if (SchedulerService.FAILED_JOB_STATUS_CODE.equals(jobStatus) || SchedulerService.CANCELLED_JOB_STATUS_CODE.equals(jobStatus)) {
                mailMessage.addToAddress(emailService.getDefaultFromAddress());
            }
            mailMessageSubject.append(": ").append(jobStatus);
            String messageText = MessageFormat.format(configurationService.getPropertyValueAsString(KFSKeyConstants.MESSAGE_BATCH_FILE_LOG_EMAIL_BODY), getLogFileName(NDC.peek()));
            mailMessage.setMessage(messageText);
            if (mailMessage.getToAddresses().size() > 0) {
                mailMessage.setSubject(mailMessageSubject.toString());
                emailService.sendMessage(mailMessage,false);
            }
        } catch (Exception iae) {
            LOG.error("Caught exception while trying to send job completion notification e-mail for " + jobExecutionContext.getJobDetail().getName(), iae);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
