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
package org.kuali.kfs.krad.web.bind;

import org.apache.log4j.Logger;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.service.ViewService;
import org.kuali.kfs.krad.uif.util.UifWebUtils;
import org.kuali.kfs.krad.uif.view.History;
import org.kuali.kfs.krad.uif.view.HistoryEntry;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.web.form.DocumentFormBase;
import org.kuali.kfs.krad.web.form.IncidentReportForm;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring Exception intercepter
 * <p>
 * <p>
 * Gets the data needed for the incident report from the request and builds the
 * model and view for the incident report. This resolver intercepts any unhandled
 * exception.
 * </p>
 */
public class UifHandlerExceptionResolver implements org.springframework.web.servlet.HandlerExceptionResolver {
    private static final Logger LOG = Logger.getLogger(UifHandlerExceptionResolver.class);

    /**
     * Builds the incident report model and view from the request that threw the exception
     *
     * @param request  -
     *                 the request
     * @param response -
     *                 the response
     * @param handler  -
     *                 the current handler when the exception occurred
     * @param ex       -
     *                 the exception
     * @return the incident report model and view
     * @see org.springframework.web.servlet.HandlerExceptionResolver#resolveException(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object,
     * java.lang.Exception)
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        LOG.error("The following error was caught by the UifHandlerExceptionResolver : ", ex);

        // log exception
        LOG.error(ex.getMessage(), ex);

        String incidentDocId = request.getParameter(KRADConstants.DOCUMENT_DOCUMENT_NUMBER);
        String incidentViewId = "";

        UifFormBase form = GlobalVariables.getUifFormManager().getCurrentForm();
        if (form instanceof DocumentFormBase) {
            if (((DocumentFormBase) form).getDocument() != null) {
                incidentDocId = ((DocumentFormBase) form).getDocument().getDocumentNumber();
            }
            incidentViewId = ((DocumentFormBase) form).getViewId();
        }
        GlobalVariables.getUifFormManager().removeForm(form);

        UserSession userSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
        IncidentReportForm incidentReportForm = new IncidentReportForm();

        // Set the post url map to the incident report controller and not
        // the one the exception occurred on
        String postUrl = request.getRequestURL().toString();
        postUrl = postUrl.substring(0, postUrl.lastIndexOf("/")) + "/incidentReport";
        incidentReportForm.setFormPostUrl(postUrl);

        incidentReportForm.setException(ex);
        incidentReportForm.setIncidentDocId(incidentDocId);
        incidentReportForm.setIncidentViewId(incidentViewId);
        incidentReportForm.setController(handler.getClass().toString());
        incidentReportForm.setUserId(userSession.getPrincipalId());
        incidentReportForm.setUserName(userSession.getPrincipalName());
        incidentReportForm.setUserEmail(userSession.getPerson().getEmailAddress());
        incidentReportForm.setDevMode(!KRADUtils.isProductionEnvironment());
        incidentReportForm.setViewId("Uif-IncidentReportView");

        // Set the view object
        incidentReportForm.setView(getViewService().getViewById("Uif-IncidentReportView"));

        // Add a new History entry to avoid errors in the postHandle
        History history = new History();
        HistoryEntry entry = new HistoryEntry("", "", "Incident Report", "", "");
        history.setCurrent(entry);
        incidentReportForm.setFormHistory(history);

        // Set render full view to force full render
        incidentReportForm.setRenderFullView(true);

        ModelAndView modelAndView = UifWebUtils.getUIFModelAndView(incidentReportForm, "");
        try {
            UifWebUtils.postControllerHandle(request, response, handler, modelAndView);
        } catch (Exception e) {
            LOG.error("An error stopped the incident form from loading", e);
        }

        GlobalVariables.getUifFormManager().setCurrentForm(incidentReportForm);

        return modelAndView;
    }

    protected ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }

}
