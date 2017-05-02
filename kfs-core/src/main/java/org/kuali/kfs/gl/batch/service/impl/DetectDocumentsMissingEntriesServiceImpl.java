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

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.batch.DetectDocumentsMissingEntriesStep;
import org.kuali.kfs.gl.batch.dataaccess.DetectDocumentsMissingEntriesDao;
import org.kuali.kfs.gl.batch.service.DetectDocumentsMissingEntriesService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class DetectDocumentsMissingEntriesServiceImpl implements DetectDocumentsMissingEntriesService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetectDocumentsMissingEntriesServiceImpl.class);

    public static final String PAYMENT_MEDIUM_CODE_CHECK = "CK";

    protected ParameterService parameterService;
    protected EmailService emailService;
    protected ConfigurationService configurationService;
    protected DetectDocumentsMissingEntriesDao detectDocumentsMissingEntriesDao;

    @Override
    public List<DocumentHeaderData> discoverGeneralLedgerDocumentsWithoutEntries() {
        LOG.debug("discoverGeneralLedgerDocumentsWithoutEntries() started");

        List<DocumentHeaderData> docs = detectDocumentsMissingEntriesDao.discoverLedgerDocumentsWithoutEntries(calculateEarliestProcessingDate(), getSearchByDocumentTypes());

        // Deal with exceptions before returning
        return docs.stream().filter(this::includeCtrlDocument).collect(Collectors.toList());
    }

    @Override
    public void reportDocumentsWithoutEntries(List<DocumentHeaderData> documentHeaders) {
        LOG.debug("reportDocumentsWithoutEntries() started");

        final Collection<String> notificationEmailAddresses = parameterService.getParameterValuesAsString(
                DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.NOTIFICATION_EMAIL_ADDRESSES);
        if (CollectionUtils.isEmpty(notificationEmailAddresses)) {
            logDocumentHeaders(documentHeaders);
        } else {
            emailDocumentHeaders(documentHeaders, notificationEmailAddresses);
        }
    }

    protected Date calculateEarliestProcessingDate() {
        String lookbackDaysParameter = parameterService.getParameterValueAsString(
                DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.LOOK_BACK_DAYS, "7");
        int lookbackDays = Integer.valueOf(lookbackDaysParameter);
        java.util.Calendar currentMoment = Calendar.getInstance();
        currentMoment.add(Calendar.DATE, lookbackDays * -1);
        return currentMoment.getTime();
    }

    /**
     * AR Cash Control documents do not create GLPEs unless the payment medium
     * code is check.
     *
     * @param doc
     * @return
     */
    protected boolean includeCtrlDocument(DocumentHeaderData doc) {
        if (!"CTRL".equals(doc.getWorkflowDocumentTypeName())) {
            return true;
        }

        return detectDocumentsMissingEntriesDao
                .getCustomerPaymentMediumCodeFromCashControlDocument(doc.getDocumentNumber())
                .map(code -> PAYMENT_MEDIUM_CODE_CHECK.equals(code)).orElseThrow(() -> {
                    LOG.error("includeCtrlDocument() " + doc.getDocumentNumber() + " is not a CTRL document");
                    return new RuntimeException(doc.getDocumentNumber() + " is not a CTRL document");
                });
    }

    protected List<String> getSearchByDocumentTypes() {
        List<String> searchByDocumentTypes = new ArrayList<>();
        final Collection<String> parameterDocumentTypesValues = parameterService.getParameterValuesAsString(
                DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.ENTRY_GENERATING_DOCUMENT_TYPES);
        if (!CollectionUtils.isEmpty(parameterDocumentTypesValues)) {
            searchByDocumentTypes.addAll(parameterDocumentTypesValues);
        }
        return searchByDocumentTypes;
    }

    protected void logDocumentHeaders(List<DocumentHeaderData> documentHeaders) {
        LOG.warn("\n" + buildReportMessage(documentHeaders));
    }

    protected void emailDocumentHeaders(List<DocumentHeaderData> documentHeaders, Collection<String> notificationEmailAddresses) {
        emailService.sendMessage(buildNotificationMessage(documentHeaders, notificationEmailAddresses),false);
    }

    protected BodyMailMessage buildNotificationMessage(List<DocumentHeaderData> documentHeaders, Collection<String> notificationEmailAddresses) {
        BodyMailMessage message = new BodyMailMessage();
        message.setSubject(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.EMAIL_SUBJECT));
        message.setToAddresses(buildNotificationEmailAddressesSet(notificationEmailAddresses));
        message.setFromAddress(emailService.getDefaultFromAddress());
        message.setMessage(buildReportMessage(documentHeaders));
        return message;
    }

    protected Set<String> buildNotificationEmailAddressesSet(Collection<String> notificationEmailAddresses) {
        Set<String> emailAddressesSet = new HashSet<>();
        emailAddressesSet.addAll(notificationEmailAddresses);
        return emailAddressesSet;
    }

    protected String buildReportMessage(List<DocumentHeaderData> documentHeaders) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder reportMessage = new StringBuilder();
        reportMessage.append(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.FAILURE_HEADER));
        reportMessage.append("\n");
        final String documentHeadersReport = documentHeaders.stream()
                .map(documentHeader -> MessageFormat.format(
                        configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingEntriesMessages.FAILURE_ENTRY),
                        documentHeader.getDocumentNumber(), documentHeader.getWorkflowDocumentTypeName(),
                        dateFormatter.format(documentHeader.getProcessedDate())))
                .collect(Collectors.joining("\n"));
        reportMessage.append(documentHeadersReport);
        return reportMessage.toString();
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setDetectDocumentsMissingEntriesDao(DetectDocumentsMissingEntriesDao detectDocumentsMissingEntriesDao) {
        this.detectDocumentsMissingEntriesDao = detectDocumentsMissingEntriesDao;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
