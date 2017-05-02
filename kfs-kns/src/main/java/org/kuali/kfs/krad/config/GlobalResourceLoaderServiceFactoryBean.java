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
package org.kuali.kfs.krad.config;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.util.ApplicationThreadLocal;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.namespace.QName;

/**
 * Exports services in the {@link org.kuali.rice.core.api.resourceloader.GlobalResourceLoader} as beans available to Spring.
 */
public class GlobalResourceLoaderServiceFactoryBean implements FactoryBean<Object>, InitializingBean {

    private String serviceNamespace;
    private String serviceName;
    private boolean singleton;
    private boolean mustExist;

    // used to prevent a stack overflow when trying to get the service
    private ThreadLocal<Boolean> isFetchingService = new ApplicationThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public GlobalResourceLoaderServiceFactoryBean() {
        this.mustExist = true;
    }

    public Object getObject() throws Exception {
        if (isFetchingService.get()) return null; // we already have been invoked, don't recurse, just return null.
        isFetchingService.set(true);
        try {
            Object service = null;
            if (StringUtils.isBlank(getServiceNamespace())) {
                service = GlobalResourceLoader.getService(this.getServiceName());
            } else {
                service = GlobalResourceLoader.getService(new QName(getServiceNamespace(), getServiceName()));
            }
            if (mustExist && service == null) {
                throw new IllegalStateException("Service must exist and no service could be located with serviceNamespace='" + getServiceNamespace() + "' and name='" + this.getServiceName() + "'");
            }
            return service;
        } finally {
            isFetchingService.remove();
        }
    }

    public Class<?> getObjectType() {
        return Object.class;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public String getServiceNamespace() {
        return serviceNamespace;
    }

    public void setServiceNamespace(String serviceNamespace) {
        this.serviceNamespace = serviceNamespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isMustExist() {
        return mustExist;
    }

    public void setMustExist(boolean mustExist) {
        this.mustExist = mustExist;
    }


    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(this.getServiceName())) {
            throw new ConfigurationException("No serviceName given.");
        }
    }

}
