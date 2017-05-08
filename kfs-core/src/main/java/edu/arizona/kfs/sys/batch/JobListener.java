package edu.arizona.kfs.sys.batch;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.log4j.PatternLayout;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.context.NDCFilter;
import org.quartz.JobExecutionContext;

import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class JobListener extends org.kuali.kfs.sys.batch.JobListener {
    private static final Logger LOG = Logger.getLogger(JobListener.class);
    private static final String STD_OUT = "StdOut";
    private static final String PATTERN = "%d [%t] %p %c %x - %m%n";
    
    @Override
    protected void initializeLogging(JobExecutionContext jobExecutionContext) {
        try {
            if(Logger.getRootLogger().getAppender(STD_OUT) == null) {
                Appender appender = new ConsoleAppender(new PatternLayout(PATTERN));
                appender.setName(STD_OUT);
                Logger.getRootLogger().addAppender(appender);
            }
            Calendar startTimeCalendar = dateTimeService.getCurrentCalendar();
            StringBuilder nestedDiagnosticContext = new StringBuilder(StringUtils.substringAfter(BatchSpringContext.getJobDescriptor(jobExecutionContext.getJobDetail().getName()).getNamespaceCode(), KFSParameterKeyConstants.HYPHEN).toLowerCase());
            nestedDiagnosticContext.append(File.separator);
            nestedDiagnosticContext.append(jobExecutionContext.getJobDetail().getName());
            nestedDiagnosticContext.append(KFSParameterKeyConstants.HYPHEN);
            nestedDiagnosticContext.append(dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()));
            
            ((Job) jobExecutionContext.getJobInstance()).setNdcAppender(new FileAppender(Logger.getRootLogger().getAppender(STD_OUT).getLayout(), getLogFileName(nestedDiagnosticContext.toString())));
            ((Job) jobExecutionContext.getJobInstance()).getNdcAppender().addFilter(new NDCFilter(nestedDiagnosticContext.toString()));
            Logger.getRootLogger().addAppender(((Job) jobExecutionContext.getJobInstance()).getNdcAppender());
            NDC.push(nestedDiagnosticContext.toString());
        }
        catch (IOException e) {
            LOG.warn("Could not initialize special custom logging for job: " + jobExecutionContext.getJobDetail().getName(), e);
        }
    }
}
