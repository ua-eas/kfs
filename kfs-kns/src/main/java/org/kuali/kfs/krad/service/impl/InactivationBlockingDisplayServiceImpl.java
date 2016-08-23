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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.document.authorization.FieldRestriction;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatter;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - wliang don't forget to fill this in.
 */
public class InactivationBlockingDisplayServiceImpl implements org.kuali.kfs.krad.service.InactivationBlockingDisplayService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InactivationBlockingDetectionServiceImpl.class);

    private org.kuali.kfs.krad.service.PersistenceService persistenceService;
    private org.kuali.kfs.krad.service.DataDictionaryService dataDictionaryService;
    private org.kuali.kfs.krad.service.PersistenceStructureService persistenceStructureService;
    private BusinessObjectAuthorizationService businessObjectAuthorizationService;

    /**
     * This overridden method ...
     *
     * @see org.kuali.kfs.krad.service.InactivationBlockingDisplayService#listAllBlockerRecords(org.kuali.rice.krad.bo.BusinessObject, InactivationBlockingMetadata)
     */
    public List<String> listAllBlockerRecords(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        String inactivationBlockingDetectionServiceBeanName = inactivationBlockingMetadata.getInactivationBlockingDetectionServiceBeanName();
        if (StringUtils.isBlank(inactivationBlockingDetectionServiceBeanName)) {
            inactivationBlockingDetectionServiceBeanName = org.kuali.kfs.krad.service.KRADServiceLocatorWeb.DEFAULT_INACTIVATION_BLOCKING_DETECTION_SERVICE;
        }
        org.kuali.kfs.krad.service.InactivationBlockingDetectionService inactivationBlockingDetectionService = org.kuali.kfs.krad.service.KRADServiceLocatorWeb
            .getInactivationBlockingDetectionService(inactivationBlockingDetectionServiceBeanName);

        Collection<BusinessObject> collection = inactivationBlockingDetectionService.listAllBlockerRecords(blockedBo, inactivationBlockingMetadata);

        Map<String, Formatter> formatters = getFormattersForPrimaryKeyFields(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass());

        List<String> displayValues = new ArrayList<String>();
        List<String> pkFieldNames = persistenceStructureService.listPrimaryKeyFieldNames(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        for (BusinessObject element : collection) {
            StringBuilder buf = new StringBuilder();

            // the following method will return a restriction for all DD-defined attributes
            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);
            for (int i = 0; i < pkFieldNames.size(); i++) {
                String pkFieldName = pkFieldNames.get(i);
                Object value = ObjectUtils.getPropertyValue(element, pkFieldName);

                String displayValue = null;
                if (!businessObjectRestrictions.hasRestriction(pkFieldName)) {
                    Formatter formatter = formatters.get(pkFieldName);
                    if (formatter != null) {
                        displayValue = (String) formatter.format(value);
                    } else {
                        displayValue = String.valueOf(value);
                    }
                } else {
                    FieldRestriction fieldRestriction = businessObjectRestrictions.getFieldRestriction(pkFieldName);
                    if (fieldRestriction.isMasked() || fieldRestriction.isPartiallyMasked()) {
                        MaskFormatter maskFormatter = fieldRestriction.getMaskFormatter();
                        displayValue = maskFormatter.maskValue(value);
                    } else {
                        // there was a restriction, but we did not know how to obey it.
                        LOG.warn("Restriction was defined for class: " + element.getClass() + " field name: " + pkFieldName + ", but it was not honored by the inactivation blocking display framework");
                    }
                }

                buf.append(displayValue);
                if (i < pkFieldNames.size() - 1) {
                    buf.append(" - ");
                }
            }

            displayValues.add(buf.toString());
        }
        return displayValues;
    }

    protected Map<String, Formatter> getFormattersForPrimaryKeyFields(Class boClass) {
        List<String> keyNames = persistenceStructureService.listPrimaryKeyFieldNames(boClass);
        Map<String, Formatter> formattersForPrimaryKeyFields = new HashMap<String, Formatter>();

        for (String pkFieldName : keyNames) {
            Formatter formatter = null;

            Class<? extends Formatter> formatterClass = dataDictionaryService.getAttributeFormatter(boClass, pkFieldName);
            if (formatterClass != null) {
                try {
                    formatter = formatterClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return formattersForPrimaryKeyFields;
    }

    public void setPersistenceService(org.kuali.kfs.krad.service.PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setPersistenceStructureService(
        org.kuali.kfs.krad.service.PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public void setDataDictionaryService(org.kuali.kfs.krad.service.DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        if (businessObjectAuthorizationService == null) {
            businessObjectAuthorizationService = KNSServiceLocator.getBusinessObjectAuthorizationService();
        }
        return businessObjectAuthorizationService;
    }
}

