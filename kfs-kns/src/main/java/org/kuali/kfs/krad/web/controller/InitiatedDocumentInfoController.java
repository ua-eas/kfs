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

import org.apache.log4j.Logger;
import org.kuali.kfs.krad.web.form.InitiatedDocumentInfoForm;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for <code>MaintenanceView</code> screens which operate on
 * <code>MaintenanceDocument</code> instances
 *
 * 
 */
@Controller
@RequestMapping(value="/initdocinfo")

public class InitiatedDocumentInfoController extends UifControllerBase {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(InitiatedDocumentInfoController.class);

    @Override
    public InitiatedDocumentInfoForm createInitialForm(HttpServletRequest request) {

        InitiatedDocumentInfoForm initiatedDocumentInfoForm = new InitiatedDocumentInfoForm();

        return initiatedDocumentInfoForm;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=start")
    public ModelAndView start(@ModelAttribute("KualiForm")UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        InitiatedDocumentInfoForm initiatedDocumentInfoForm = (InitiatedDocumentInfoForm) form;
        return super.start(form, result, request, response);
    }
}
