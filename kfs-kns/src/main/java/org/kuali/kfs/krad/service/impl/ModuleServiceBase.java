/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.kfs.kns.service.BusinessObjectDictionaryService;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.UrlFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class implements ModuleService interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ModuleServiceBase extends RemoteModuleServiceBase implements ModuleService {

    protected static final Logger LOG = Logger.getLogger(ModuleServiceBase.class);

    protected BusinessObjectService businessObjectService;
    protected BusinessObjectDictionaryService businessObjectDictionaryService;


    /**
     * @see ModuleService#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
     */
    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass,
            Map<String, Object> fieldValues) {
        Class<? extends ExternalizableBusinessObject> implementationClass =
                getExternalizableBusinessObjectImplementation(businessObjectClass);
        ExternalizableBusinessObject businessObject =
                (ExternalizableBusinessObject) getBusinessObjectService().findByPrimaryKey(implementationClass,
                        fieldValues);
        return (T) businessObject;
    }

    /**
     * @see ModuleService#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
     */
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
            Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues) {
        Class<? extends ExternalizableBusinessObject> implementationClass =
                getExternalizableBusinessObjectImplementation(externalizableBusinessObjectClass);
        return (List<T>) getBusinessObjectService().findMatching(implementationClass, fieldValues);
    }



    @Deprecated
    public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass,
            Map<String, String[]> parameters) {
        if (!isExternalizable(inquiryBusinessObjectClass)) {
            return KRADConstants.EMPTY_STRING;
        }
        String businessObjectClassAttribute;

        Class implementationClass = getExternalizableBusinessObjectImplementation(inquiryBusinessObjectClass);
        if (implementationClass == null) {
            LOG.error("Can't find ExternalizableBusinessObject implementation class for " + inquiryBusinessObjectClass
                    .getName());
            throw new RuntimeException("Can't find ExternalizableBusinessObject implementation class for interface "
                    + inquiryBusinessObjectClass.getName());
        }
        businessObjectClassAttribute = implementationClass.getName();
        return UrlFactory.parameterizeUrl(getInquiryUrl(inquiryBusinessObjectClass), getUrlParameters(
                businessObjectClassAttribute, parameters));
    }

    @Deprecated
    @Override
    protected String getInquiryUrl(Class inquiryBusinessObjectClass) {
        
        String riceBaseUrl = "";
        String potentialUrlAddition = "";

        if (goToCentralRiceForInquiry()) {
            riceBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(KRADConstants.KUALI_RICE_URL_KEY); 
        } else {
            riceBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
            potentialUrlAddition = "kr/";
        }
        
        String inquiryUrl = riceBaseUrl;
        if (!inquiryUrl.endsWith("/")) {
            inquiryUrl = inquiryUrl + "/";
        }
        return inquiryUrl + potentialUrlAddition + KRADConstants.INQUIRY_ACTION;
    }

    @Override
    public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
        return getBusinessObjectDictionaryService().isLookupable(boClass);
    }

    @Override
    public boolean isExternalizableBusinessObjectInquirable(Class boClass) {
        return getBusinessObjectDictionaryService().isInquirable(boClass);
    }

    /**
     * This overridden method ...
     *
     * @see ModuleService#getExternalizableBusinessObjectLookupUrl(java.lang.Class,
     *      java.util.Map)
     */
    @Deprecated
    @Override
    public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass,
            Map<String, String> parameters) {
        Properties urlParameters = new Properties();

        String riceBaseUrl = "";
        String potentialUrlAddition = "";

        if (goToCentralRiceForInquiry()) {
            riceBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(KRADConstants.KUALI_RICE_URL_KEY);
        } else {
            riceBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
            potentialUrlAddition = "kr/";
        }
        
        String lookupUrl = riceBaseUrl;
        if (!lookupUrl.endsWith("/")) {
            lookupUrl = lookupUrl + "/";
        }
        
        if (parameters.containsKey(KRADConstants.MULTIPLE_VALUE)) {
            lookupUrl = lookupUrl + potentialUrlAddition + KRADConstants.MULTIPLE_VALUE_LOOKUP_ACTION;
        }
        else {
            lookupUrl = lookupUrl + potentialUrlAddition + KRADConstants.LOOKUP_ACTION;
        }
           
        for (String paramName : parameters.keySet()) {
            urlParameters.put(paramName, parameters.get(paramName));
        }

        /*Class clazz;
        if (inquiryBusinessObjectClass.getClass().isInterface()) {*/
        Class clazz = getExternalizableBusinessObjectImplementation(inquiryBusinessObjectClass);
        /*} else {
            clazz = inquiryBusinessObjectClass;
        }*/
        urlParameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, clazz == null ? "" : clazz.getName());

        return UrlFactory.parameterizeUrl(lookupUrl, urlParameters);
    }

    protected BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        if (businessObjectDictionaryService == null) {
            businessObjectDictionaryService = KNSServiceLocator.getBusinessObjectDictionaryService();
        }
        return businessObjectDictionaryService;
    }

    /**
     * @return the businessObjectService
     */
    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    public boolean goToCentralRiceForInquiry() { 
        return false;
    }

    /**
     * Returns the base URL to use for lookup requests to objects within the module
     *
     * @return String base lookup URL
     */
    @Override
    protected String getBaseLookupUrl() {
        if (goToCentralRiceForInquiry()) {
            return BaseLookupUrlsHolder.remoteKradBaseLookupUrl;
        } else {
            return BaseLookupUrlsHolder.localKradBaseLookupUrl;
        }
    }

    protected RunMode getRunMode(String module) {
        String propertyName = module + ".mode";
        String runMode = ConfigContext.getCurrentContextConfig().getProperty(propertyName);
        if (StringUtils.isBlank(runMode)) {
            throw new ConfigurationException("Failed to determine run mode for module '" + module + "'.  Please be sure to set configuration parameter '" + propertyName + "'");
        }
        return RunMode.valueOf(runMode.toUpperCase());
    }

}

