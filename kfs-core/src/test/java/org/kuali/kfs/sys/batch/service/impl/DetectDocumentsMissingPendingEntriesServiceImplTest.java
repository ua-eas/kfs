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
package org.kuali.kfs.sys.batch.service.impl;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.MailService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(PowerMockRunner.class)
public class DetectDocumentsMissingPendingEntriesServiceImplTest {

    private Logger mockLogger;
    private ConfigurationService configurationService;
    private ParameterService parameterService;
    private MailService mailService;
    private DetectDocumentsMissingPendingEntriesServiceImpl detectDocumentsMissingPendingEntriesService;
    private MailMessage savedMessage;

    @Before
    public void setUp() {
        PowerMock.mockStatic(Logger.class);
        mockLogger = EasyMock.createMock(Logger.class);

        configurationService = EasyMock.createMock(ConfigurationService.class);
        parameterService = EasyMock.createMock(ParameterService.class);
        mailService = EasyMock.createMock(MailService.class);
    }

    @Test
    @PrepareForTest(Logger.class)
    public void testLogWithNoDocumentHeaders() throws Exception {
        mockLogger.debug("Running reportDocumentsWithoutPendingEntries");
        EasyMock.expect(parameterService.getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.MISSING_PLES_NOTIFICATION_EMAIL_ADDRESSES)).andReturn(new ArrayList<>());
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.FAILURE_HEADER)).andReturn("Could not find expected general ledger pending entries:");
        mockLogger.warn("\n" + "Could not find expected general ledger pending entries:\n");

        EasyMock.replay(mockLogger, configurationService, parameterService);

        initializeDetectDocumentsMissingPendingEntriesService();
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        detectDocumentsMissingPendingEntriesService.reportDocumentsWithoutPendingEntries(documentHeaderDataList);

        EasyMock.verify(mockLogger, parameterService, configurationService);
    }

    @Test
    @PrepareForTest(Logger.class)
    public void testLogWithDocumentHeaders() throws Exception {
        mockLogger.debug("Running reportDocumentsWithoutPendingEntries");
        EasyMock.expect(parameterService.getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.MISSING_PLES_NOTIFICATION_EMAIL_ADDRESSES)).andReturn(new ArrayList<>());
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.FAILURE_HEADER)).andReturn("Could not find expected general ledger pending entries:");
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.FAILURE_ENTRY)).andReturn("Document Number: {0} Document Type: {1}").times(2);

        mockLogger.warn("\nCould not find expected general ledger pending entries:\nDocument Number: AAAA Document Type: DT1\nDocument Number: BBBB Document Type: DT2");

        EasyMock.replay(mockLogger, configurationService, parameterService);

        initializeDetectDocumentsMissingPendingEntriesService();
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA", "DT1");
        DocumentHeaderData documentHeaderData2 = new DocumentHeaderData("BBBB", "DT2");
        documentHeaderDataList.add(documentHeaderData1);
        documentHeaderDataList.add(documentHeaderData2);
        detectDocumentsMissingPendingEntriesService.reportDocumentsWithoutPendingEntries(documentHeaderDataList);

        EasyMock.verify(mockLogger, parameterService, configurationService);
    }

    @Test
    @PrepareForTest(Logger.class)
    public void testEmailWithNoDocumentHeaders() throws Exception {
        mockLogger.debug("Running reportDocumentsWithoutPendingEntries");
        List<String> emailAddresses = new ArrayList<>();
        emailAddresses.add("to@kuali.co");
        EasyMock.expect(parameterService.getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.MISSING_PLES_NOTIFICATION_EMAIL_ADDRESSES)).andReturn(emailAddresses);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.FAILURE_HEADER)).andReturn("Could not find expected general ledger pending entries:");
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.EMAIL_SUBJECT)).andReturn("Email Header");
        EasyMock.expect(mailService.getBatchMailingList()).andReturn("from@kuali.co");

        MailMessage mailMessage = new MailMessage();
        mailMessage.setSubject("Email Header");
        Set<String> expectedEmailAddresses = new HashSet<>();
        expectedEmailAddresses.add("to@kuali.co");
        mailMessage.setToAddresses(expectedEmailAddresses);
        mailMessage.setFromAddress("from@kuali.co");
        mailMessage.setMessage("Could not find expected general ledger pending entries:\n");

        captureSendMessageResults();

        EasyMock.replay(mockLogger, configurationService, mailService, parameterService);

        initializeDetectDocumentsMissingPendingEntriesService();
        detectDocumentsMissingPendingEntriesService.setMailService(mailService);
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        detectDocumentsMissingPendingEntriesService.reportDocumentsWithoutPendingEntries(documentHeaderDataList);

        EasyMock.verify(mockLogger, parameterService, configurationService, mailService);

        assertMailMessage(mailMessage, savedMessage);
    }

    @Test
    @PrepareForTest(Logger.class)
    public void testEmailWithDocumentHeaders() throws Exception {
        List<String> emailAddresses = new ArrayList<>();
        emailAddresses.add("to@kuali.co");
        emailAddresses.add("to2@kuali.co");
        mockLogger.debug("Running reportDocumentsWithoutPendingEntries");
        EasyMock.expect(parameterService.getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.MISSING_PLES_NOTIFICATION_EMAIL_ADDRESSES)).andReturn(emailAddresses);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.FAILURE_HEADER)).andReturn("Could not find expected general ledger pending entries:");
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.EMAIL_SUBJECT)).andReturn("Email Header");
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.FAILURE_ENTRY)).andReturn("Document Number: {0} Document Type: {1}").times(3);
        EasyMock.expect(mailService.getBatchMailingList()).andReturn("from@kuali.co");

        MailMessage mailMessage = new MailMessage();
        mailMessage.setSubject("Email Header");
        Set<String> expectedEmailAddresses = new HashSet<>();
        expectedEmailAddresses.add("to@kuali.co");
        expectedEmailAddresses.add("to2@kuali.co");
        mailMessage.setToAddresses(expectedEmailAddresses);
        mailMessage.setFromAddress("from@kuali.co");
        mailMessage.setMessage("Could not find expected general ledger pending entries:\n" +
            "Document Number: AAAA Document Type: DT1\n" +
            "Document Number: BBBB Document Type: DT2\n" +
            "Document Number: CCCC Document Type: DT3");

        captureSendMessageResults();

        EasyMock.replay(mockLogger, configurationService, mailService, parameterService);

        initializeDetectDocumentsMissingPendingEntriesService();
        detectDocumentsMissingPendingEntriesService.setMailService(mailService);
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA", "DT1");
        DocumentHeaderData documentHeaderData2 = new DocumentHeaderData("BBBB", "DT2");
        DocumentHeaderData documentHeaderData3 = new DocumentHeaderData("CCCC", "DT3");
        documentHeaderDataList.add(documentHeaderData1);
        documentHeaderDataList.add(documentHeaderData2);
        documentHeaderDataList.add(documentHeaderData3);
        detectDocumentsMissingPendingEntriesService.reportDocumentsWithoutPendingEntries(documentHeaderDataList);

        EasyMock.verify(mockLogger, parameterService, configurationService, mailService);
        assertMailMessage(mailMessage, savedMessage);
    }

    private void assertMailMessage(MailMessage expectedMessage, MailMessage actualMessage) {
        Assert.assertEquals(expectedMessage.getSubject(), actualMessage.getSubject());
        Assert.assertEquals(expectedMessage.getFromAddress(), actualMessage.getFromAddress());
        Assert.assertEquals(expectedMessage.getMessage(), actualMessage.getMessage());
        Assert.assertEquals(expectedMessage.getToAddresses(), actualMessage.getToAddresses());
    }

    private void captureSendMessageResults() throws InvalidAddressException, MessagingException {
        mailService.sendMessage(EasyMock.isA(MailMessage.class));
        EasyMock.expectLastCall().andDelegateTo(new MailService() {
            @Override
            public void sendMessage(MailMessage message) throws InvalidAddressException, MessagingException {
                savedMessage = message;
            }

            @Override
            public void sendMessage(MailMessage message, boolean htmlMessage) throws InvalidAddressException, MessagingException {
            }

            @Override
            public String getBatchMailingList() {
                return null;
            }
        });
    }

    protected void initializeDetectDocumentsMissingPendingEntriesService() {
        EasyMock.expect(Logger.getLogger(DetectDocumentsMissingPendingEntriesServiceImpl.class)).andReturn(mockLogger);
        PowerMock.replay(Logger.class);
        detectDocumentsMissingPendingEntriesService = new DetectDocumentsMissingPendingEntriesServiceImpl();
        detectDocumentsMissingPendingEntriesService.setConfigurationService(configurationService);
        detectDocumentsMissingPendingEntriesService.setParameterService(parameterService);
        detectDocumentsMissingPendingEntriesService.setMailService(mailService);
    }
}
