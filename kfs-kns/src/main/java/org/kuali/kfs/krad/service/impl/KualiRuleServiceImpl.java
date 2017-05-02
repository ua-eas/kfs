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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.bo.AdHocRoutePerson;
import org.kuali.kfs.krad.bo.AdHocRouteWorkgroup;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.TransactionalDocument;
import org.kuali.kfs.krad.exception.InfrastructureException;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.krad.rules.TransactionalDocumentRuleBase;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.kfs.krad.rules.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiRuleService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.MessageMap;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a rule evaluator for Kuali. This class is to be used for evaluating business rule checks. The class defines
 * one method right now - applyRules() which takes in a Document and a DocumentEvent and does the proper business rule checks based
 * on the context of the event and the document type.
 */
public class KualiRuleServiceImpl implements KualiRuleService {
    private static final Logger LOG = Logger.getLogger(KualiRuleServiceImpl.class);

    private DocumentDictionaryService documentDictionaryService;
    private DataDictionaryService dataDictionaryService;

    /**
     * @see KualiRuleService#applyRules(KualiDocumentEvent)
     */
    public boolean applyRules(KualiDocumentEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("invalid (null) event");
        }

        event.validate();
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling applyRules for event " + event);
        }

        BusinessRule rule = getBusinessRulesInstance(event.getDocument(), event.getRuleInterfaceClass());

        boolean success = true;
        if (rule != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("processing " + event.getName() + " with rule " + rule.getClass().getName());
            }
            increaseErrorPath(event.getErrorPathPrefix());

            // get any child events and apply rules
            List<KualiDocumentEvent> events = event.generateEvents();
            for (KualiDocumentEvent generatedEvent : events) {
                success &= applyRules(generatedEvent);
            }

            // now call the event rule method
            success &= event.invokeRuleMethod(rule);

            decreaseErrorPath(event.getErrorPathPrefix());

            // report failures
            if (!success) {
                if (LOG.isDebugEnabled()) { // NO, this is not a type - only log if in debug mode - this is not an error in production
                    LOG.debug(event.getName() + " businessRule " + rule.getClass().getName() + " failed");
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("processed " + event.getName() + " for rule " + rule.getClass().getName());
                }
            }

        }
        return success;
    }

    /**
     * Builds a list containing AddAdHocRoutePersonEvents since the validation done for an AdHocRouteRecipient is the same for all
     * events.
     *
     * @see KualiRuleService#generateAdHocRoutePersonEvents(Document)
     */
    public List<AddAdHocRoutePersonEvent> generateAdHocRoutePersonEvents(Document document) {
        List<AdHocRoutePerson> adHocRoutePersons = document.getAdHocRoutePersons();

        List<AddAdHocRoutePersonEvent> events = new ArrayList<AddAdHocRoutePersonEvent>();

        for (int i = 0; i < adHocRoutePersons.size(); i++) {
            events.add(new AddAdHocRoutePersonEvent(
                KRADConstants.EXISTING_AD_HOC_ROUTE_PERSON_PROPERTY_NAME + "[" + i + "]", document, adHocRoutePersons.get(i)));
        }

        return events;
    }

    /**
     * Builds a list containing AddAdHocRoutePersonEvents since the validation done for an AdHocRouteRecipient is the same for all
     * events.
     *
     * @see KualiRuleService#generateAdHocRouteWorkgroupEvents(Document)
     */
    public List<AddAdHocRouteWorkgroupEvent> generateAdHocRouteWorkgroupEvents(Document document) {
        List<AdHocRouteWorkgroup> adHocRouteWorkgroups = document.getAdHocRouteWorkgroups();

        List<AddAdHocRouteWorkgroupEvent> events = new ArrayList<AddAdHocRouteWorkgroupEvent>();

        for (int i = 0; i < adHocRouteWorkgroups.size(); i++) {
            events.add(new AddAdHocRouteWorkgroupEvent(
                KRADConstants.EXISTING_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME + "[" + i + "]", document, adHocRouteWorkgroups.get(i)));
        }

        return events;
    }


    /**
     * @param document
     * @param ruleInterface
     * @return instance of the businessRulesClass for the given document's type, if that businessRulesClass implements the given
     * ruleInterface
     */
    public BusinessRule getBusinessRulesInstance(Document document, Class<? extends BusinessRule> ruleInterface) {
        // get the businessRulesClass
        Class<? extends BusinessRule> businessRulesClass = null;
        if (document instanceof TransactionalDocument) {
            TransactionalDocument transactionalDocument = (TransactionalDocument) document;

            businessRulesClass = getDocumentDictionaryService().getBusinessRulesClass(transactionalDocument);
            if (businessRulesClass == null) {
                return new TransactionalDocumentRuleBase(); // default to a generic rule that will enforce Required fields
            }
        } else if (document instanceof MaintenanceDocument) {
            MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

            businessRulesClass = getDocumentDictionaryService().getBusinessRulesClass(maintenanceDocument);
            if (businessRulesClass == null) {
                return new MaintenanceDocumentRuleBase(); // default to a generic rule that will enforce Required fields
            }
        } else {
            LOG.error("unable to get businessRulesClass for unknown document type '" + document.getClass().getName() + "'");
        }

        // instantiate and return it if it implements the given ruleInterface
        BusinessRule rule = null;
        if (businessRulesClass != null) {
            try {
                if (ruleInterface.isAssignableFrom(businessRulesClass)) {
                    rule = businessRulesClass.newInstance();
                }
            } catch (IllegalAccessException e) {
                throw new InfrastructureException("error processing business rules", e);
            } catch (InstantiationException e) {
                throw new InfrastructureException("error processing business rules", e);
            }
        }

        return rule;
    }

    /**
     * This method increases the registered error path, so that field highlighting can occur on the appropriate object attribute.
     *
     * @param errorPathPrefix
     */
    private void increaseErrorPath(String errorPathPrefix) {
        MessageMap errorMap = GlobalVariables.getMessageMap();

        if (!StringUtils.isBlank(errorPathPrefix)) {
            errorMap.addToErrorPath(errorPathPrefix);
        }
    }

    /**
     * This method decreases the registered error path, so that field highlighting can occur on the appropriate object attribute.
     *
     * @param errorPathPrefix
     */
    private void decreaseErrorPath(String errorPathPrefix) {
        MessageMap errorMap = GlobalVariables.getMessageMap();

        if (!StringUtils.isBlank(errorPathPrefix)) {
            errorMap.removeFromErrorPath(errorPathPrefix);
        }
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
