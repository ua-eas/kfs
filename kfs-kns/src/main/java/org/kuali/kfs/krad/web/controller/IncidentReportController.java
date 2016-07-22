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
package org.kuali.kfs.krad.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiExceptionIncidentService;
import org.kuali.kfs.krad.web.form.IncidentReportForm;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handler for incident reports
 * 
 * 
 */
@Controller
@RequestMapping(value = "/incidentReport")
public class IncidentReportController extends UifControllerBase {

    /**
     * @see UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected IncidentReportForm createInitialForm(HttpServletRequest request) {
        return new IncidentReportForm();
    }

    /**
     * Emails the report and closes the incident report screen
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=submitReport")
    public ModelAndView submitReport(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get the exception incident service and use it to mail the report
        KualiExceptionIncidentService reporterService = KRADServiceLocatorWeb.getKualiExceptionIncidentService();
        reporterService.emailReport(((IncidentReportForm) uifForm).createEmailSubject(),
                ((IncidentReportForm) uifForm).createEmailMessage());

        // return the close redirect
        return close(uifForm, result, request, response);
    }

}
