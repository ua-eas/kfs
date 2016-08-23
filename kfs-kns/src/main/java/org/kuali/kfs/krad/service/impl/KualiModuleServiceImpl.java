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

import org.apache.commons.beanutils.BeanUtils;
import org.kuali.kfs.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.kfs.coreservice.api.namespace.Namespace;
import org.kuali.kfs.kns.bo.Step;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.kfs.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.kfs.krad.document.TransactionalDocument;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.ModuleServiceNotFoundException;
import org.kuali.kfs.krad.util.KRADConstants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class KualiModuleServiceImpl implements KualiModuleService, InitializingBean, ApplicationContextAware {

    private List<ModuleService> installedModuleServices = new ArrayList<ModuleService>();
    private boolean loadRiceInstalledModuleServices;
    private ApplicationContext applicationContext;
    private ConcurrentHashMap<Class, ModuleService> responsibleModuleServices = new ConcurrentHashMap<Class, ModuleService>();

    /**
	 * @param applicationContext the applicationContext to set
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public List<ModuleService> getInstalledModuleServices() {
        return installedModuleServices;
    }

    @Override
	public ModuleService getModuleService(final String moduleId) {
        for (ModuleService moduleService : installedModuleServices) {
            if ( moduleService.getModuleConfiguration().getNamespaceCode().equals( moduleId ) ) {
                return moduleService;
            }
        }
        ModuleService allBranTranslatedModuleService = getQuinoaModuleService(new Function<org.kuali.rice.krad.service.KualiModuleService, org.kuali.rice.krad.service.ModuleService>() {
            @Override
            public org.kuali.rice.krad.service.ModuleService apply(org.kuali.rice.krad.service.KualiModuleService kualiModuleService) {
                return kualiModuleService.getModuleService(moduleId);
            }
        });
        return allBranTranslatedModuleService;
    }

    @Override
	public ModuleService getModuleServiceByNamespaceCode(final String namespaceCode) {
        for (ModuleService moduleService : installedModuleServices) {
            if ( moduleService.getModuleConfiguration().getNamespaceCode().equals( namespaceCode ) ) {
                return moduleService;
            }
        }
        ModuleService allBranTranslatedModuleService = getQuinoaModuleService(new Function<org.kuali.rice.krad.service.KualiModuleService, org.kuali.rice.krad.service.ModuleService>() {
            @Override
            public org.kuali.rice.krad.service.ModuleService apply(org.kuali.rice.krad.service.KualiModuleService kualiModuleService) {
                return kualiModuleService.getModuleServiceByNamespaceCode(namespaceCode);
            }
        });
        return allBranTranslatedModuleService;
    }

    @Override
	public boolean isModuleServiceInstalled(String namespaceCode) {
        for (ModuleService moduleService : installedModuleServices) {
            if ( moduleService.getModuleConfiguration().getNamespaceCode().equals( namespaceCode ) ) {
                return true;
            }
        }
        org.kuali.rice.krad.service.KualiModuleService quinoaKualiModuleService = org.kuali.rice.krad.service.KRADServiceLocatorWeb.getKualiModuleService();
        return quinoaKualiModuleService.isModuleServiceInstalled(namespaceCode);
    }

    @Override
	public ModuleService getResponsibleModuleService(final Class boClass) {
    	if(boClass==null) {
			return null;
		}
    	if (responsibleModuleServices.containsKey(boClass)) {
    		return responsibleModuleServices.get(boClass);
    	}
    	for (ModuleService moduleService : installedModuleServices) {
    	    if ( moduleService.isResponsibleFor( boClass ) ) {
    	    	responsibleModuleServices.put(boClass, moduleService);
    	        return moduleService;
    	    }
    	}

        ModuleService allBranTranslatedModuleService = getQuinoaModuleService(new Function<org.kuali.rice.krad.service.KualiModuleService, org.kuali.rice.krad.service.ModuleService>() {
            @Override
            public org.kuali.rice.krad.service.ModuleService apply(org.kuali.rice.krad.service.KualiModuleService kualiModuleService) {
                org.kuali.rice.krad.service.ModuleService moduleService = null;
                try {
                    moduleService = kualiModuleService.getResponsibleModuleService(boClass);
                } catch (Exception e) {
                    //nothing to see here folks, move it along.
                }
                return moduleService;
            }
        });
        if (!ObjectUtils.isNull(allBranTranslatedModuleService)) {
            return allBranTranslatedModuleService;
        }

        //Throwing exception only for externalizable business objects
    	if(ExternalizableBusinessObject.class.isAssignableFrom(boClass)){
    	    String message;
    		if(!boClass.isInterface()) {
				message = "There is no responsible module for the externalized business object class: "+boClass;
			} else {
				message = "There is no responsible module for the externalized business object interface: "+boClass;
			}
    		throw new ModuleServiceNotFoundException(message);
    	}
    	//Returning null for business objects other than externalizable to keep the framework backward compatible
    	return null;
    }

    protected ModuleService getQuinoaModuleService(Function<org.kuali.rice.krad.service.KualiModuleService, org.kuali.rice.krad.service.ModuleService> action) {
        org.kuali.rice.krad.service.KualiModuleService quinoaKualiModuleService = org.kuali.rice.krad.service.KRADServiceLocatorWeb.getKualiModuleService();
        org.kuali.rice.krad.service.ModuleService quinoaModuleService = action.apply(quinoaKualiModuleService);
        if (!ObjectUtils.isNull(quinoaModuleService)) {
            return new QuinoaModuleServiceAdapter(quinoaModuleService);
        }

        return null;
    }

    @Override
	public ModuleService getResponsibleModuleServiceForJob(String jobName){
        for(ModuleService moduleService : installedModuleServices){
            if(moduleService.isResponsibleForJob(jobName)){
                return moduleService;
            }
        }
        return null;
    }

    @Override
	public void setInstalledModuleServices(List<ModuleService> installedModuleServices) {
        this.installedModuleServices = installedModuleServices;
    }

    @Override
	public List<String> getDataDictionaryPackages() {
        List<String> packages  = new ArrayList<String>();
        for ( ModuleService moduleService : installedModuleServices ) {
            if ( moduleService.getModuleConfiguration().getDataDictionaryPackages() != null ) {
                packages.addAll( moduleService.getModuleConfiguration().getDataDictionaryPackages() );
            }
        }
        return packages;
    }

    @Override
	public String getNamespaceName(final String namespaceCode){
    	Namespace parameterNamespace = CoreServiceApiServiceLocator.getNamespaceService().getNamespace(namespaceCode);
    	return parameterNamespace==null ? "" : parameterNamespace.getName();
    }

	/**
	 * @param loadRiceInstalledModuleServices the loadRiceInstalledModuleServices to set
	 */
	public void setLoadRiceInstalledModuleServices(
			boolean loadRiceInstalledModuleServices) {
		this.loadRiceInstalledModuleServices = loadRiceInstalledModuleServices;
	}

	/***
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if(loadRiceInstalledModuleServices){
			try {
				installedModuleServices.addAll(
						GlobalResourceLoader.<KualiModuleService>getService(KualiModuleService.class.getSimpleName().substring(0, 1).toLowerCase() + KualiModuleService.class.getSimpleName().substring(1)).getInstalledModuleServices());
			} catch ( NoSuchBeanDefinitionException ex ) {
				installedModuleServices.addAll( ((KualiModuleService)applicationContext.getBean( KRADServiceLocatorWeb.KUALI_MODULE_SERVICE )).getInstalledModuleServices() );
			}
		}
	}

    @Override
    public String getNamespaceCode(Class<?> documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("documentClass must not be null");
        }

        if (documentClass.isAnnotationPresent(ParameterConstants.NAMESPACE.class)) {
            return (documentClass.getAnnotation(ParameterConstants.NAMESPACE.class)).namespace();
        }
        ModuleService moduleService = getResponsibleModuleService(documentClass);
        if (moduleService != null) {
            return moduleService.getModuleConfiguration().getNamespaceCode();
        }
        if (documentClass.getName().startsWith("org.kuali.rice.krad")) {
            return KRADConstants.KNS_NAMESPACE;
        }
        if (documentClass.getName().startsWith("org.kuali.rice.edl")) {
            return "KR-EDL";
        }
        if (documentClass.getName().startsWith("org.kuali.rice.kew")) {
            return "KR-WKFLW";
        }
        if (documentClass.getName().startsWith("org.kuali.rice.edl")) {
        	return "KR-WKFLW";
    	}
        if (documentClass.getName().startsWith("org.kuali.rice.kim")) {
            return "KR-IDM";
        }
        if (documentClass.getName().startsWith("org.kuali.rice.core")) {
            return "KR-CORE";
        }
        throw new IllegalArgumentException("Unable to determine the namespace code for documentClass " + documentClass.getName());
    }

    @Override
    public String getComponentCode(Class<?> documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("documentClass must not be null");
        }

        if (documentClass.isAnnotationPresent(ParameterConstants.COMPONENT.class)) {
            return documentClass.getAnnotation(ParameterConstants.COMPONENT.class).component();
        } else if (TransactionalDocument.class.isAssignableFrom(documentClass)) {
            return documentClass.getSimpleName().replace("Document", "");
        } else if (BusinessObject.class.isAssignableFrom(documentClass)) {
            return documentClass.getSimpleName();
        } else if (Step.class.isAssignableFrom(documentClass)) {
                return documentClass.getSimpleName();
        }
        throw new IllegalArgumentException("Unable to determine the component code for documentClass " + documentClass.getName());
    }

    @Override
    public boolean isBusinessObjectExternal(String boClassName) {
    	if (boClassName == null) {
    		return false;
    	}
    	if (boClassName.startsWith("org.kuali.rice")) {
    		return true;
    	}
    	try {
    		Class boClass = Class.forName(boClassName);
    		return getResponsibleModuleService(boClass).isExternal(boClass);
    	} catch (ClassNotFoundException e) {
    		// It's nothing, let alone an external business object.
    		return false;
    	}
    }
}

