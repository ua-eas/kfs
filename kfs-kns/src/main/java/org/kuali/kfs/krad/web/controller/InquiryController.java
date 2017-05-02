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
package org.kuali.kfs.krad.web.controller;

import org.kuali.kfs.krad.bo.Exporter;
import org.kuali.kfs.krad.datadictionary.DataObjectEntry;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.web.form.InquiryForm;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Properties;

/**
 * Controller for <code>InquiryView</code> screens which handle initial requests for the inquiry and
 * actions coming from the inquiry view such as export
 */
@Controller
@RequestMapping(value = "/inquiry")
public class InquiryController extends UifControllerBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquiryController.class);

    /**
     * @see UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected InquiryForm createInitialForm(HttpServletRequest request) {
        return new InquiryForm();
    }

    /**
     * Invoked to request an inquiry view for a data object class
     * <p>
     * <p>
     * Checks if the data object is externalizable and we need to redirect to the appropriate inquiry URL, else
     * continues with the inquiry view display
     * </p>
     * <p>
     * <p>
     * Data object class name and values for a primary or alternate key set must
     * be sent in the request
     * </p>
     * <p>
     * <p>
     * Invokes the inquirable to perform the query for the data object record, if not found
     * an exception will be thrown. If found the object is set on the form and then the view
     * is rendered
     * </p>
     */
    @RequestMapping(params = "methodToCall=start")
    @Override
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
                              HttpServletRequest request, HttpServletResponse response) {
        InquiryForm inquiryForm = (InquiryForm) form;

        // if request is not a redirect, determine if we need to redirect for an externalizable object inquiry
        if (!inquiryForm.isRedirectedInquiry()) {
            Class inquiryObjectClass = null;
            try {
                inquiryObjectClass = Class.forName(inquiryForm.getDataObjectClassName());
            } catch (ClassNotFoundException e) {
                throw new RiceRuntimeException("Unable to get class for name: " + inquiryForm.getDataObjectClassName());
            }

            ModuleService responsibleModuleService =
                KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(inquiryObjectClass);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(inquiryObjectClass)) {
                String inquiryUrl = responsibleModuleService.getExternalizableDataObjectInquiryUrl(inquiryObjectClass,
                    KRADUtils.convertRequestMapToProperties(request.getParameterMap()));

                Properties redirectUrlProps = new Properties();
                redirectUrlProps.put(UifParameters.REDIRECTED_INQUIRY, "true");

                // clear current form from session
                GlobalVariables.getUifFormManager().removeForm(form);

                return performRedirect(form, inquiryUrl, redirectUrlProps);
            }
        }

        // initialize data object class in inquirable
        try {
            inquiryForm.getInquirable().setDataObjectClass(Class.forName(inquiryForm.getDataObjectClassName()));
        } catch (ClassNotFoundException e) {
            LOG.error("Unable to get new instance for object class: " + inquiryForm.getDataObjectClassName(), e);
            throw new RuntimeException(
                "Unable to get new instance for object class: " + inquiryForm.getDataObjectClassName(), e);
        }

        // invoke inquirable to retrieve inquiry data object
        Object dataObject = inquiryForm.getInquirable().retrieveDataObject(KRADUtils.translateRequestParameterMap(
            request.getParameterMap()));

        inquiryForm.setDataObject(dataObject);

        return super.start(form, result, request, response);
    }

    /**
     * Handles exporting the BusinessObject for this Inquiry to XML if it has a custom XML exporter available.
     */
    @RequestMapping(params = "methodToCall=export")
    public ModelAndView export(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        InquiryForm inquiryForm = (InquiryForm) form;

        Object dataObject = inquiryForm.getDataObject();
        if (dataObject != null) {
            DataObjectEntry dataObjectEntry =
                KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDataObjectEntry(
                    inquiryForm.getDataObjectClassName());

            Class<? extends Exporter> exporterClass = dataObjectEntry.getExporterClass();
            if (exporterClass != null) {
                Exporter exporter = exporterClass.newInstance();

                response.setContentType(KRADConstants.XML_MIME_TYPE);
                response.setHeader("Content-disposition", "attachment; filename=export.xml");
                exporter.export(dataObjectEntry.getDataObjectClass(), Collections.singletonList(dataObject),
                    KRADConstants.XML_FORMAT, response.getOutputStream());
            }
        }

        return null;
    }
}
