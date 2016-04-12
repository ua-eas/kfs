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
package org.kuali.kfs.krad.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * A proxy invocationhandler that implements around advice which pushes a new GlobalVariables frame before
 * invocation and pops it after invocation.
 * @see GlobalVariables#doInNewGlobalVariables
 */
public class GlobalVariablesContextInvocationHandler implements InvocationHandler {
    private Object proxied;
    public GlobalVariablesContextInvocationHandler(Object proxied) {
        this.proxied = proxied;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return GlobalVariables.doInNewGlobalVariables(new Callable() {
            @Override
            public Object call() throws Exception {
                return method.invoke(proxied, args);
            }
        });
    }
}
