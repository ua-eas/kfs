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
package org.kuali.kfs.coreservice.impl.parameter;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.api.parameter.Parameter;
import org.kuali.kfs.coreservice.api.parameter.ParameterKey;
import org.kuali.kfs.coreservice.api.parameter.ParameterRepositoryService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.util.KRADConstants;

import java.util.Collection;

public class ParameterServiceImpl implements ParameterService {
    private KualiModuleService kualiModuleService;
    private ParameterRepositoryService parameterRepositoryService;
    private String applicationId = KRADConstants.DEFAULT_PARAMETER_APPLICATION_ID;

    @Override
    public Parameter createParameter(Parameter parameter) {
        return parameterRepositoryService.createParameter(parameter);
    }

    @Override
    public Parameter updateParameter(Parameter parameter) {
        return parameterRepositoryService.updateParameter(parameter);
    }

    @Override
    public Parameter getParameter(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<Parameter>() {
            @Override public Parameter f(ParameterKey key) {
                return parameterRepositoryService.getParameter(key);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Parameter getParameter(Class<?> componentClass, String parameterName) {
        return exec(new Fun<Parameter>() {
            @Override public Parameter f(ParameterKey key) {
                return parameterRepositoryService.getParameter(key);
            }
        }, componentClass, parameterName);
    }

    @Override
    public Boolean parameterExists(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<Boolean>() {
            @Override
            public Boolean f(ParameterKey key) {
                return Boolean.valueOf(parameterRepositoryService.getParameter(key) != null);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Boolean parameterExists(Class<?> componentClass, String parameterName) {
        return exec(new Fun<Boolean>() {
            @Override
            public Boolean f(ParameterKey key) {
                return Boolean.valueOf(parameterRepositoryService.getParameter(key) != null);
            }
        }, componentClass, parameterName);
    }

    @Override
    public Boolean getParameterValueAsBoolean(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<Boolean>() {
            @Override
            public Boolean f(ParameterKey key) {
                return parameterRepositoryService.getParameterValueAsBoolean(key);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Boolean getParameterValueAsBoolean(String namespaceCode, String componentCode, String parameterName, Boolean defaultValue) {
        final Boolean value = getParameterValueAsBoolean(namespaceCode, componentCode, parameterName);
        return (value != null) ? value : defaultValue;
    }

    @Override
    public Boolean getParameterValueAsBoolean(Class<?> componentClass, String parameterName) {
        return exec(new Fun<Boolean>() {
            @Override
            public Boolean f(ParameterKey key) {
                return parameterRepositoryService.getParameterValueAsBoolean(key);
            }
        }, componentClass, parameterName);
    }

    @Override
    public Boolean getParameterValueAsBoolean(Class<?> componentClass, String parameterName, Boolean defaultValue) {
        final Boolean value = getParameterValueAsBoolean(componentClass, parameterName);
        return (value != null) ? value : defaultValue;
    }

    @Override
    public String getParameterValueAsString(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<String>() {
            @Override
            public String f(ParameterKey key) {
                return parameterRepositoryService.getParameterValueAsString(key);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public String getParameterValueAsString(String namespaceCode, String componentCode, String parameterName, String defaultValue) {
        final String value = getParameterValueAsString(namespaceCode, componentCode, parameterName);
        return (value != null) ? value : defaultValue;
    }

    @Override
    public String getParameterValueAsString(Class<?> componentClass, String parameterName) {
        return exec(new Fun<String>() {
            @Override public String f(ParameterKey key) {
                return parameterRepositoryService.getParameterValueAsString(key);
            }
        }, componentClass, parameterName);
    }

    @Override
    public String getParameterValueAsString(Class<?> componentClass, String parameterName, String defaultValue) {
        final String value = getParameterValueAsString(componentClass, parameterName);
        return (value != null) ? value : defaultValue;
    }

    @Override
    public Collection<String> getParameterValuesAsString(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<Collection<String>>() {
            @Override public Collection<String> f(ParameterKey key) {
                return parameterRepositoryService.getParameterValuesAsString(key);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Collection<String> getParameterValuesAsString(Class<?> componentClass, String parameterName) {
        return exec(new Fun<Collection<String>>() {
            @Override public Collection<String> f(ParameterKey key) {
                return parameterRepositoryService.getParameterValuesAsString(key);
            }
        }, componentClass, parameterName);
    }

    @Override
    public Collection<String> getSubParameterValuesAsString(String namespaceCode, String componentCode, String parameterName, final String constrainingValue) {
        return exec(new Fun<Collection<String>>() {
            @Override public Collection<String> f(ParameterKey key) {
                return parameterRepositoryService.getSubParameterValuesAsString(key, constrainingValue);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Collection<String> getSubParameterValuesAsString(Class<?> componentClass, String parameterName, final String constrainingValue) {
        return exec(new Fun<Collection<String>>() {
            @Override public Collection<String> f(ParameterKey key) {
                return parameterRepositoryService.getSubParameterValuesAsString(key, constrainingValue);
            }
        }, componentClass, parameterName);
    }

    @Override
    public String getSubParameterValueAsString(String namespaceCode, String componentCode, String parameterName, final String constrainingValue) {
        return exec(new Fun<String>() {
            @Override public String f(ParameterKey key) {
               return parameterRepositoryService.getSubParameterValueAsString(key, constrainingValue);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public String getSubParameterValueAsString(Class<?> componentClass, String parameterName, final String constrainingValue) {
        return exec(new Fun<String>() {
            @Override public String f(ParameterKey key) {
               return parameterRepositoryService.getSubParameterValueAsString(key, constrainingValue);
            }
        }, componentClass, parameterName);
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public void setParameterRepositoryService(ParameterRepositoryService parameterRepositoryService) {
        this.parameterRepositoryService = parameterRepositoryService;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    //utilities that act as a poor-man's closure & higher order functions - these help consolidate validation & construction of parameter keys
    private <R> R exec(Fun<R> fun, String namespaceCode, String componentCode, String parameterName) {
        if (StringUtils.isBlank(applicationId)) {
            throw new IllegalStateException("applicationId is blank - this service is not configured correctly");
        }

        return fun.f(ParameterKey.create(applicationId, namespaceCode, componentCode, parameterName));
    }

    private <R> R exec(Fun<R> fun, Class<?> componentClass, String parameterName) {
        return exec(fun, kualiModuleService.getNamespaceCode(componentClass), kualiModuleService.getComponentCode(componentClass), parameterName);
    }

    private interface Fun<R> {
        R f(ParameterKey key);
    }
}
