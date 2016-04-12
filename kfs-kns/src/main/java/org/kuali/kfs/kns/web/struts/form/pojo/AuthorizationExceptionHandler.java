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
package org.kuali.kfs.kns.web.struts.form.pojo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles any AuthorizationException by logging it first and then passing it forward to an explanation page.
 */
public class AuthorizationExceptionHandler extends ExceptionHandler {
    
    private static final String AUTHORIZATION_EXCEPTION_HANDLER = "authorizationExceptionHandler";

    private static final Log LOG = LogFactory.getLog(AuthorizationExceptionHandler.class);
    
    /**
     * Logs the AuthorizationException before forwarding the user to the explanation page.
     * 
     * @see org.apache.struts.action.ExceptionHandler#execute(
     *      java.lang.Exception, org.apache.struts.config.ExceptionConfig, org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, 
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(Exception exception, ExceptionConfig exceptionConfig, ActionMapping mapping, ActionForm form, HttpServletRequest request, 
            HttpServletResponse response) {
        
        if (LOG.isTraceEnabled()) {
            String message = String.format("ENTRY %s", exception.getMessage());
            LOG.trace(message);
        }
        exception.printStackTrace();
        request.setAttribute(Globals.EXCEPTION_KEY, exception);
        
        ActionForward forward = mapping.findForward(AUTHORIZATION_EXCEPTION_HANDLER);
        
        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("EXIT %s", exception.getMessage()));
        }
        
        return forward;
    }

}
