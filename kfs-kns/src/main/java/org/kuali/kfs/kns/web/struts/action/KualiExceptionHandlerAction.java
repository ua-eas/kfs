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
package org.kuali.kfs.kns.web.struts.action;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.util.IncidentReportUtils;
import org.kuali.kfs.kns.web.struts.form.KualiExceptionIncidentForm;
import org.kuali.kfs.krad.exception.ExceptionIncident;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.kfs.krad.exception.KualiExceptionIncident;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiExceptionIncidentService;
import org.kuali.kfs.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the struts action class for handling the exception for Kuali
 * applications.
 *
 */
public class KualiExceptionHandlerAction extends Action {
	private static final Logger LOG = Logger
			.getLogger(KualiExceptionHandlerAction.class);

    private static final String EXCEPTION_TIME_STAMP = "exception-timeStamp";
    private static final String EXCEPTION_DOCUMENT_ID = "exception-" + ExceptionIncident.DOCUMENT_ID;
    private static final String EXCEPTION_USER_EMAIL = "exception-" + ExceptionIncident.USER_EMAIL;
    private static final String EXCEPTION_USER_NAME = "exception-" + ExceptionIncident.USER_NAME;
    private static final String EXCEPTION_UUID = "exception-" + ExceptionIncident.UUID;
    private static final String EXCEPTION_COMPONENT_NAME = "exception-" + ExceptionIncident.COMPONENT_NAME;
    private static final String EXCEPTION_EXCEPTION_REPORT_SUBJECT = "exception-" + ExceptionIncident.EXCEPTION_REPORT_SUBJECT;
    private static final String EXCEPTION_EXCEPTION_MESSAGE = "exception-" + ExceptionIncident.EXCEPTION_MESSAGE;
    private static final String EXCEPTION_STACK_TRACE = "exception-" + ExceptionIncident.STACK_TRACE;

    /**
	 * This overridden method dispatches action to be taken based on
	 * "methodToCall" parameter. The exception is processed when there is no
	 * "methodToCall" specified.
	 *
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return executeException(mapping, form, request, response);
	}

	/**
	 * This overridden method processes the exception and post exception (when
	 * user either submit/cancel the exception JSP page).
	 * <ul>
	 * <li>ProcessDefinition application Exception - Exception is stored in Http Request</li>
	 * <li>ProcessDefinition exception incident reporting - No exception, only form data</li>
	 * </ul>
	 *
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeException(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (LOG.isDebugEnabled()) {
			String lm = String.format("ENTRY %s%n%s", form.getClass()
					.getSimpleName(), request.getRequestURI());
			LOG.debug(lm);
		}

		// Get exception thrown
		Exception e = (Exception) request.getAttribute(Globals.EXCEPTION_KEY);

		// Initialize defined action mapping from struts-config
		ActionForward returnForward = null;

		// In case there is no exception, either a post back after page was
		// filled in
		// or just an error from directly accessing this struts action
		if (e == null) {
			if (form instanceof KualiExceptionIncidentForm) {
				KualiExceptionIncidentForm formObject = (KualiExceptionIncidentForm) form;
				// Manage conditions: submit or cancel
				if (!formObject.isCancel()) {
					// Locate the post exception handler service. The service id
					// is
					// defined in the application properties
					// Only process the post exception handling when the
					// service
					// is specified
					KualiExceptionIncidentService reporterService = KRADServiceLocatorWeb
							.getKualiExceptionIncidentService();
					// An instance of the ExceptionIncident is created by
					// the
					// ExceptionIncidentService
					Map reducedMap = new HashMap();
					Enumeration<String> names = request.getParameterNames();
					while (names.hasMoreElements()) {
						String name = names.nextElement();
						reducedMap.put(name, request.getParameter(name));
					}

                    // Sensitive data stored in user session
                    Map<String, Object> userSessionMap = GlobalVariables.getUserSession().getObjectMap();

                    // Only display if this is the right exception
                    if(userSessionMap.get("EXCEPTION_TIME_STAMP").toString().equals(reducedMap.get(ExceptionIncident.STACK_TRACE))) {
                        reducedMap.put(ExceptionIncident.DOCUMENT_ID, userSessionMap.get("EXCEPTION_DOCUMENT_ID").toString());
                        reducedMap.put(ExceptionIncident.USER_EMAIL, userSessionMap.get("EXCEPTION_USER_EMAIL").toString());
						reducedMap.put(ExceptionIncident.USER_NAME, userSessionMap.get("EXCEPTION_USER_NAME").toString());
                        reducedMap.put(ExceptionIncident.UUID, userSessionMap.get("EXCEPTION_UUID").toString());
						reducedMap.put(ExceptionIncident.COMPONENT_NAME, userSessionMap.get("EXCEPTION_COMPONENT_NAME").toString());
						reducedMap.put(ExceptionIncident.EXCEPTION_REPORT_SUBJECT, userSessionMap.get("EXCEPTION_EXCEPTION_REPORT_SUBJECT").toString());
                        reducedMap.put(ExceptionIncident.EXCEPTION_MESSAGE, userSessionMap.get("EXCEPTION_EXCEPTION_MESSAGE").toString());
						reducedMap.put(ExceptionIncident.STACK_TRACE, userSessionMap.get("EXCEPTION_STACK_TRACE").toString());

					} else {
						reducedMap.put(ExceptionIncident.STACK_TRACE,"Not available.");
					}

					KualiExceptionIncident exceptionIncident = reporterService
							.getExceptionIncident(reducedMap);

					// Report the incident
					reporterService.report(exceptionIncident);
				} else {
					// Set return after canceling
					ActionForward cancelForward = mapping
							.findForward(KRADConstants.MAPPING_CANCEL);
					if (cancelForward == null) {
						cancelForward = returnForward;
					} else {
						returnForward = cancelForward;
					}
				}
			}
		} else {
			// ProcessDefinition the received exception from HTTP request
			returnForward = processException(mapping, form, request, e);
		}

		// Not specified, return
		if (returnForward == null) {
			returnForward = mapping.findForward(KRADConstants.MAPPING_CLOSE);
		}

		if (LOG.isDebugEnabled()) {
			String lm = String.format("EXIT %s",
					(returnForward == null) ? "null" : returnForward.getPath());
			LOG.debug(lm);
		}

		return returnForward;
	}

	/**
	 * This method process the caught exception by creating an exception
	 * information properties list and forward these properties to the exception
	 * incident handler JSP.
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param exception
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected ActionForward processException(ActionMapping mapping,
			ActionForm form, HttpServletRequest request, Exception exception)
			throws Exception {
		// Only process the exception handling when the service
		// is specified
		KualiExceptionIncidentService reporterService = KRADServiceLocatorWeb
				.getKualiExceptionIncidentService();
		// Get exception properties from the Http Request
		Map<String, String> properties = (Map<String, String>) request
				.getAttribute(IncidentReportUtils.EXCEPTION_PROPERTIES);
		// Construct the exception incident object
		KualiExceptionIncident ei = reporterService.getExceptionIncident(
				exception, properties);

        // Add sensitive data to user session
		String exceptionTimeStamp = String.valueOf(System.currentTimeMillis());
		GlobalVariables.getUserSession().addObject("EXCEPTION_TIME_STAMP", exceptionTimeStamp);
        GlobalVariables.getUserSession().addObject("EXCEPTION_DOCUMENT_ID", ei.getProperty(ExceptionIncident.DOCUMENT_ID));
        GlobalVariables.getUserSession().addObject("EXCEPTION_USER_EMAIL", ei.getProperty(ExceptionIncident.USER_EMAIL));
		GlobalVariables.getUserSession().addObject("EXCEPTION_USER_NAME", ei.getProperty(ExceptionIncident.USER_NAME));
		GlobalVariables.getUserSession().addObject("EXCEPTION_UUID", ei.getProperty(ExceptionIncident.UUID));
		GlobalVariables.getUserSession().addObject("EXCEPTION_COMPONENT_NAME", ei.getProperty(ExceptionIncident.COMPONENT_NAME));
		GlobalVariables.getUserSession().addObject("EXCEPTION_EXCEPTION_REPORT_SUBJECT", ei.getProperty(ExceptionIncident.EXCEPTION_REPORT_SUBJECT));
		GlobalVariables.getUserSession().addObject("EXCEPTION_EXCEPTION_MESSAGE", ei.getProperty(ExceptionIncident.EXCEPTION_MESSAGE));
		GlobalVariables.getUserSession().addObject("EXCEPTION_STACK_TRACE", ei.getProperty(ExceptionIncident.STACK_TRACE));

        // Hide sensitive data from form in production only
        if(ConfigContext.getCurrentContextConfig().isProductionEnvironment()) {
            Map<String, String> prodProperties = ei.toProperties();
            prodProperties.put(ExceptionIncident.DOCUMENT_ID, "");
            prodProperties.put(ExceptionIncident.USER_EMAIL, "");
			prodProperties.put(ExceptionIncident.USER_NAME, "");
            prodProperties.put(ExceptionIncident.UUID, "");
			prodProperties.put(ExceptionIncident.COMPONENT_NAME, "");
			prodProperties.put(ExceptionIncident.EXCEPTION_REPORT_SUBJECT, "");
            prodProperties.put(ExceptionIncident.EXCEPTION_MESSAGE, "");
			prodProperties.put(ExceptionIncident.STACK_TRACE, exceptionTimeStamp);
            ei = reporterService.getExceptionIncident(
                    null, prodProperties);
        }

		// Set full exception properties in Http Request and forward to JSP
		request.setAttribute(KualiExceptionHandlerAction.class
				.getSimpleName(), ei.toProperties());
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}
}
