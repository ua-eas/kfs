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
package org.kuali.kfs.kns.web.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.web.struts.form.KualiFeedbackHandlerForm;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.exception.AuthorizationException;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiFeedbackService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class handles when the feedback form is submitted.
 * It invokes the KualiFeedbackService to send the feedback email
 */

public class KualiFeedbackHandlerAction extends KualiAction {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (findMethodToCall(form, request) == null || findMethodToCall(form, request).equals("docHandler")) {
            return executeFeedback(mapping, form, request, response);
        } else {
            return super.execute(mapping, form, request, response);
        }
    }

    public ActionForward executeFeedback(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward returnForward = null;
        KualiFeedbackHandlerForm formObject = (KualiFeedbackHandlerForm) form;
        if (!formObject.isCancel()) {
            populateRequest(form, request);
            returnForward = mapping.findForward(RiceConstants.MAPPING_BASIC);
        } else {
            returnForward = mapping.findForward(KRADConstants.MAPPING_CANCEL);
        }

        return returnForward;
    }

    public ActionForward submitFeedback(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (form instanceof KualiFeedbackHandlerForm) {
            KualiFeedbackHandlerForm feedbackForm = (KualiFeedbackHandlerForm) form;
            KualiFeedbackService reporterService = KRADServiceLocatorWeb.getKualiFeedbackService();
            reporterService.sendFeedback(feedbackForm.getDocumentId(), feedbackForm.getComponentName(), feedbackForm.getDescription());
        }
        return mapping.findForward(KRADConstants.MAPPING_CLOSE);
    }

    private void populateRequest(ActionForm form, HttpServletRequest request) {
        UserSession userSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
        Person sessionUser = null;
        if (userSession != null) {
            sessionUser = userSession.getPerson();
        }
        if (sessionUser != null) {
            request.setAttribute("principalName", sessionUser.getPrincipalName());
        }
    }

    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
    }
}
