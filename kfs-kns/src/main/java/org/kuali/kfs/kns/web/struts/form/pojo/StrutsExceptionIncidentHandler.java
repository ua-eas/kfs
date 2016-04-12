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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;
import org.kuali.kfs.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.kns.util.IncidentReportUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This class is the exception handler for the base exception class java.lang.Throwable
 * and is defined as global exception in the struts-config.xml. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StrutsExceptionIncidentHandler extends ExceptionHandler {
    private static final Logger LOG=
        Logger.getLogger(StrutsExceptionIncidentHandler.class);
    
    /**
     * This is defined in struts-config.xml for forwarding this exception to a specified
     * exception handler.
     * <p>Value is exceptionIncidentHandler
     */
    public static final String EXCEPTION_INCIDENT_HANDLER="exceptionIncidentHandler";
    
    /**
     * This overridden method extract exception information such as component name,
     * user name and email, etc.
     * 
     * @see org.apache.struts.action.ExceptionHandler#execute(
     * java.lang.Exception,
     *  org.apache.struts.config.ExceptionConfig,
     *   org.apache.struts.action.ActionMapping,
     *    org.apache.struts.action.ActionForm,
     *     javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(Exception exception,
            ExceptionConfig exceptionConfig,
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s", exception.getMessage());
            LOG.trace(message);
        }
        
        LOG.error("Exception being handled by Exception Handler", exception);

        String documentId="";
        if (form instanceof KualiDocumentFormBase) {
            KualiDocumentFormBase docForm=(KualiDocumentFormBase)form;
            if (docForm.getDocument() != null) {
            	documentId=docForm.getDocument().getDocumentNumber();
            }
        }
        
        Map<String, String> properties = IncidentReportUtils.populateRequestForIncidentReport(exception, documentId, form.getClass().getSimpleName(), request);
        
        ActionForward forward=mapping.findForward(EXCEPTION_INCIDENT_HANDLER);
        
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s%n%s%n%s",
                    exception.getMessage(),
                    properties.toString(),
                    (forward==null)?"null":forward.getPath());
            LOG.trace(message);
        }

        return forward;
    }

}

