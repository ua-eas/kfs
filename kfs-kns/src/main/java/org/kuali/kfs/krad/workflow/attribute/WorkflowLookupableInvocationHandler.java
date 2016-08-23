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
package org.kuali.kfs.krad.workflow.attribute;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.krad.bo.BusinessObject;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * This class provides access to the properties of business objects returned as search results by the WorkflowLookupableImpl.
 *
 * @see org.kuali.rice.kew.attribute.WorkflowLookupableImpl
 * @deprecated This will go away once workflow supports simple url integration for custom attribute lookups.
 */
public class WorkflowLookupableInvocationHandler implements InvocationHandler {
    private BusinessObject proxiedBusinessObject;
    private ClassLoader classLoader;

    private String returnUrl;

    /**
     * Constructs a WorkflowLookupableInvocationHandler.java.
     *
     * @param proxiedBusinessObject The BusinessObject that this instance is providing access to.
     */
    public WorkflowLookupableInvocationHandler(BusinessObject proxiedBusinessObject, ClassLoader classLoader) {
        this.proxiedBusinessObject = proxiedBusinessObject;
        this.classLoader = classLoader;
    }

    /**
     * Constructs a WorkflowLookupableInvocationHandler.java.
     *
     * @param proxiedBusinessObject The BusinessObject that this instance is providing access to.
     * @param returnUrl             The returnUrl String for selection of a result from the UI
     */
    public WorkflowLookupableInvocationHandler(BusinessObject proxiedBusinessObject, String returnUrl, ClassLoader classLoader) {
        this.proxiedBusinessObject = proxiedBusinessObject;
        this.returnUrl = returnUrl;
        this.classLoader = classLoader;
    }

    /**
     * This method intercepts "getReturnUrl" and returns this objects returnUrl attribute. It proxies access to nested
     * BusinessObjects to ensure that the application plugin classloader is used to resolve OJB proxies. And, it translates booleans
     * for the UI, using the BooleanFormatter.
     *
     * @see net.sf.cglib.proxy.InvocationHandler#invoke(java.lang.Object proxy, java.lang.reflect.Method method, java.lang.Object[]
     * args)
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            if ("getReturnUrl".equals(method.getName())) {
                return returnUrl;
            } else if ("getWorkflowLookupableResult".equals(method.getName())) {
                return Enhancer.create(HashMap.class, new Class[]{WorkflowLookupableResult.class}, this);
            } else if ("get".equals(method.getName())) {
                Object propertyValue = ObjectUtils.getNestedValue(proxiedBusinessObject, args[0].toString());
                if (propertyValue instanceof BusinessObject) {
                    return Enhancer.create(propertyValue.getClass(), new WorkflowLookupableInvocationHandler((BusinessObject) propertyValue, classLoader));
                } else {
                    if (propertyValue instanceof Boolean) {
                        return new BooleanFormatter().format(propertyValue);
                    }
                    return propertyValue;
                }
            } else {
                return method.invoke(proxiedBusinessObject, args);
            }
        } catch (Exception e) {
            throw (e.getCause() != null ? e.getCause() : e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }
}
