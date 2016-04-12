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
package org.kuali.kfs.kns.web.servlet.dwr;

import org.directwebremoting.spring.SpringCreator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A {@link SpringCreator} that checks the {@link GlobalResourceLoader} for the
 * bean name in question if the default {@link BeanFactory} (the applications)
 * does not have the bean in question.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class GlobalResourceDelegatingSpringCreator extends SpringCreator {

    public static final String KEW_RUN_MODE_PROPERTY = "kew.mode";
    public static final String DOCUMENT_TYPE_SERVICE = "enDocumentTypeService";

	@Override
	public Object getInstance() throws InstantiationException {

        //KULRICE-7770 enDocumentTypeService isn't supported in remote mode
        if(ConfigContext.getCurrentContextConfig().getProperty(KEW_RUN_MODE_PROPERTY).equals("REMOTE") &&
                this.getBeanName().equals(DOCUMENT_TYPE_SERVICE))
        {   
            return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{DocumentTypeService.class},
                // trivial invocationHandler
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return null;
                    }
                }
            );
        }

        Object bean = GlobalResourceLoader.getService(this.getBeanName());
    
        if (bean == null) {
            throw new InstantiationException("Unable to find bean " + this.getBeanName() + " in Rice Global Resource Loader");
        }

        return bean;
	}

}
