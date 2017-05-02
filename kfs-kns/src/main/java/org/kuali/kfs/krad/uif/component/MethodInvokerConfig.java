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
package org.kuali.kfs.krad.uif.component;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Extends <code>MethodInvoker</code> to add properties for specifying
 * a method for invocation within the UIF
 */
public class MethodInvokerConfig extends MethodInvoker implements Serializable {

    private String staticMethod;
    private Class[] argumentTypes;

    /**
     * Override to catch a set staticMethod since super does
     * not contain a getter
     *
     * @param staticMethod - static method to invoke
     */
    @Override
    public void setStaticMethod(String staticMethod) {
        super.setStaticMethod(staticMethod);
    }

    /**
     * Declared argument types for the method to be invoked, if not set the types will
     * be retrieved based on the target class and target name
     *
     * @return Class[] method argument types
     */
    public Class[] getArgumentTypes() {
        if (argumentTypes == null) {
            return getMethodArgumentTypes();
        }

        return argumentTypes;
    }

    /**
     * Setter for the method argument types that should be invoked
     *
     * @param argumentTypes
     */
    public void setArgumentTypes(Class[] argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    /**
     * Finds the method on the target class that matches the target name and
     * returns the declared parameter types
     *
     * @return Class[] method parameter types
     */
    protected Class[] getMethodArgumentTypes() {
        if (StringUtils.isNotBlank(staticMethod)) {
            int lastDotIndex = this.staticMethod.lastIndexOf('.');
            if (lastDotIndex == -1 || lastDotIndex == this.staticMethod.length()) {
                throw new IllegalArgumentException("staticMethod must be a fully qualified class plus method name: " +
                    "e.g. 'example.MyExampleClass.myExampleMethod'");
            }
            String className = this.staticMethod.substring(0, lastDotIndex);
            String methodName = this.staticMethod.substring(lastDotIndex + 1);
            try {
                setTargetClass(resolveClassName(className));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to get class for name: " + className);
            }
            setTargetMethod(methodName);
        }

        Method[] candidates = ReflectionUtils.getAllDeclaredMethods(getTargetClass());
        for (Method candidate : candidates) {
            if (candidate.getName().equals(getTargetMethod())) {
                return candidate.getParameterTypes();
            }
        }

        return null;
    }
}
