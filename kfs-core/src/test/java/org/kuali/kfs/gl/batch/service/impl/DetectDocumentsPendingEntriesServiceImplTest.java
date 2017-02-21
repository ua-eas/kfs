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

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.batch.DetectDocumentsMissingEntriesStep;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.mail.MailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(PowerMockRunner.class)
public class DetectDocumentsPendingEntriesServiceImplTest {

    private Logger mockLogger;
    private ConfigurationService configurationService;
    private ParameterService parameterService;
    private EmailService emailService;
    private DetectDocumentsMissingEntriesServiceImpl detectDocumentsMissingEntriesService;
    private MailMessage savedMessage;

    @Before
    public void setUp() {
        PowerMock.mockStatic(Logger.class);
        mockLogger = EasyMock.createMock(Logger.class);

        configurationService = EasyMock.createMock(ConfigurationService.class);
        parameterService = EasyMock.createMock(ParameterService.class);
        emailService = EasyMock.createMock(EmailService.class);
    }

    @Test
    @PrepareForTest(Logger.class)
    public void testLogWithNoDocumentHeaders() throws Exception {
        mockLogger.debug("reportDocumentsWithoutEntries() started");
        EasyMock.expect(parameterService.getParameterValuesAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.NOTIFICATION_EMAIL_ADDRESSES))
                .andReturn(new ArrayList<>());
        EasyMock.expect(configurationService
                .getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.FAILURE_HEADER))
                .andReturn("Could not find expected general ledger pending entries:");
        mockLogger.warn("\n" + "Could not find expected general ledger pending entries:\n");

        EasyMock.replay(mockLogger, configurationService, parameterService);

        initializeDetectDocumentsMissingPendingEntriesService();
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        detectDocumentsMissingEntriesService.reportDocumentsWithoutEntries(documentHeaderDataList);

        EasyMock.verify(mockLogger, parameterService, configurationService);
    }

    @Test
    @PrepareForTest(Logger.class)
    public void testLogWithDocumentHeaders() throws Exception {
        Timestamp now = new Timestamp(new Date().getTime());
        String nowString = new SimpleDateFormat("yyyy-MM-dd").format(now);
        mockLogger.debug("reportDocumentsWithoutEntries() started");
        EasyMock.expect(parameterService.getParameterValuesAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.NOTIFICATION_EMAIL_ADDRESSES))
                .andReturn(new ArrayList<>());
        EasyMock.expect(configurationService
                .getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.FAILURE_HEADER))
                .andReturn("Could not find expected general ledger pending entries:");
        EasyMock.expect(configurationService
                .getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.FAILURE_ENTRY))
                .andReturn("Document Number: {0} Document Type: {1} Processed Date: {2}").times(2);

        mockLogger.warn("\nCould not find expected general ledger pending entries:\n"
                + "Document Number: AAAA Document Type: DT1 Processed Date: " + nowString + "\n"
                + "Document Number: BBBB Document Type: DT2 Processed Date: " + nowString);

        EasyMock.replay(mockLogger, configurationService, parameterService);

        initializeDetectDocumentsMissingPendingEntriesService();
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA", "DT1", now);
        DocumentHeaderData documentHeaderData2 = new DocumentHeaderData("BBBB", "DT2", now);
        documentHeaderDataList.add(documentHeaderData1);
        documentHeaderDataList.add(documentHeaderData2);
        detectDocumentsMissingEntriesService.reportDocumentsWithoutEntries(documentHeaderDataList);

        EasyMock.verify(mockLogger, parameterService, configurationService);
    }

    @Test
    @PrepareForTest(Logger.class)
    public void testEmailWithNoDocumentHeaders() throws Exception {
        mockLogger.debug("reportDocumentsWithoutEntries() started");
        List<String> emailAddresses = new ArrayList<>();
        emailAddresses.add("to@kuali.co");
        EasyMock.expect(parameterService.getParameterValuesAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.NOTIFICATION_EMAIL_ADDRESSES))
                .andReturn(emailAddresses);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.FAILURE_HEADER))
                .andReturn("Could not find expected general ledger pending entries:");
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.EMAIL_SUBJECT))
                .andReturn("Email Header");
        EasyMock.expect(emailService.getFromAddress()).andReturn("from@kuali.co");

        BodyMailMessage mailMessage = new BodyMailMessage();
        mailMessage.setSubject("Email Header");
        Set<String> expectedEmailAddresses = new HashSet<>();
        expectedEmailAddresses.add("to@kuali.co");
        mailMessage.setToAddresses(expectedEmailAddresses);
        mailMessage.setFromAddress("from@kuali.co");
        mailMessage.setMessage("Could not find expected general ledger pending entries:\n");

        captureSendMessageResults();

        EasyMock.replay(mockLogger, configurationService, emailService, parameterService);

        initializeDetectDocumentsMissingPendingEntriesService();
        detectDocumentsMissingEntriesService.setEmailService(emailService);
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        detectDocumentsMissingEntriesService.reportDocumentsWithoutEntries(documentHeaderDataList);

        EasyMock.verify(mockLogger, parameterService, configurationService, emailService);

        assertMailMessage(mailMessage, savedMessage);
    }

    @Test
    @PrepareForTest(Logger.class)
    public void testEmailWithDocumentHeaders() throws Exception {
        Timestamp now = new Timestamp(new Date().getTime());
        String nowString = new SimpleDateFormat("yyyy-MM-dd").format(now);
        List<String> emailAddresses = new ArrayList<>();
        emailAddresses.add("to@kuali.co");
        emailAddresses.add("to2@kuali.co");
        mockLogger.debug("reportDocumentsWithoutEntries() started");
        EasyMock.expect(parameterService.getParameterValuesAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.NOTIFICATION_EMAIL_ADDRESSES))
                .andReturn(emailAddresses);
        EasyMock.expect(configurationService
                .getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.FAILURE_HEADER))
                .andReturn("Could not find expected general ledger pending entries:");
        EasyMock.expect(configurationService
                .getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.EMAIL_SUBJECT))
                .andReturn("Email Header");
        EasyMock.expect(configurationService
                .getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.FAILURE_ENTRY))
                .andReturn("Document Number: {0} Document Type: {1} Processed Date: {2}").times(3);
        EasyMock.expect(emailService.getFromAddress()).andReturn("from@kuali.co");

        BodyMailMessage mailMessage = new BodyMailMessage();
        mailMessage.setSubject("Email Header");
        Set<String> expectedEmailAddresses = new HashSet<>();
        expectedEmailAddresses.add("to@kuali.co");
        expectedEmailAddresses.add("to2@kuali.co");
        mailMessage.setToAddresses(expectedEmailAddresses);
        mailMessage.setFromAddress("from@kuali.co");
        mailMessage.setMessage("Could not find expected general ledger pending entries:\n"
                + "Document Number: AAAA Document Type: DT1 Processed Date: " + nowString + "\n"
                + "Document Number: BBBB Document Type: DT2 Processed Date: " + nowString + "\n"
                + "Document Number: CCCC Document Type: DT3 Processed Date: " + nowString);

        captureSendMessageResults();

        EasyMock.replay(mockLogger, configurationService, emailService, parameterService);

        initializeDetectDocumentsMissingPendingEntriesService();
        detectDocumentsMissingEntriesService.setEmailService(emailService);
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA", "DT1", now);
        DocumentHeaderData documentHeaderData2 = new DocumentHeaderData("BBBB", "DT2", now);
        DocumentHeaderData documentHeaderData3 = new DocumentHeaderData("CCCC", "DT3", now);
        documentHeaderDataList.add(documentHeaderData1);
        documentHeaderDataList.add(documentHeaderData2);
        documentHeaderDataList.add(documentHeaderData3);
        detectDocumentsMissingEntriesService.reportDocumentsWithoutEntries(documentHeaderDataList);

        EasyMock.verify(mockLogger, parameterService, configurationService, emailService);
        assertMailMessage(mailMessage, savedMessage);
    }

    private void assertMailMessage(MailMessage expectedMessage, MailMessage actualMessage) {
        Assert.assertEquals(expectedMessage.getSubject(), actualMessage.getSubject());
        Assert.assertEquals(expectedMessage.getFromAddress(), actualMessage.getFromAddress());
        Assert.assertEquals(expectedMessage.getMessage(), actualMessage.getMessage());
        Assert.assertEquals(expectedMessage.getToAddresses(), actualMessage.getToAddresses());
    }

    private void captureSendMessageResults() throws InvalidAddressException, MessagingException {
        emailService.sendMessage(EasyMock.isA(MailMessage.class),EasyMock.anyBoolean());
        EasyMock.expectLastCall().andDelegateTo(new EmailService() {
            @Override
            public String getFromAddress() {
                return null;
            }

            @Override
            public String getDefaultToAddress() {
                return null;
            }

            @Override
            public void sendMessage(MailMessage message,boolean htmlMessage) {
                savedMessage = message;
            }
        });
    }

    protected void initializeDetectDocumentsMissingPendingEntriesService() {
        EasyMock.expect(Logger.getLogger(DetectDocumentsMissingEntriesServiceImpl.class)).andReturn(mockLogger);
        PowerMock.replay(Logger.class);
        detectDocumentsMissingEntriesService = new DetectDocumentsMissingEntriesServiceImpl();
        detectDocumentsMissingEntriesService.setConfigurationService(configurationService);
        detectDocumentsMissingEntriesService.setParameterService(parameterService);
        detectDocumentsMissingEntriesService.setEmailService(emailService);
    }
}
