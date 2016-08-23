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
package org.kuali.kfs.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.service.ViewService;
import org.kuali.kfs.krad.uif.view.History;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.web.controller.UifControllerBase;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Provides helper methods that will be used during the request lifecycle
 * <p>
 * <p>
 * Created to avoid duplication of the methods used by the UifHandlerExceptionResolver
 * </p>
 */
public class UifWebUtils {
    private static final Logger LOG = Logger.getLogger(UifWebUtils.class);

    /**
     * Configures the <code>ModelAndView</code> instance containing the form
     * data and pointing to the UIF generic spring view
     *
     * @param form   - Form instance containing the model data
     * @param pageId - Id of the page within the view that should be rendered, can
     *               be left blank in which the current or default page is rendered
     * @return ModelAndView object with the contained form
     */
    public static ModelAndView getUIFModelAndView(UifFormBase form, String pageId) {
        if (StringUtils.isNotBlank(pageId)) {
            form.setPageId(pageId);
        }

        // create the spring return object pointing to View.jsp
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(UifConstants.DEFAULT_MODEL_NAME, form);
        modelAndView.setViewName(UifConstants.SPRING_VIEW_ID);

        return modelAndView;
    }

    public static ModelAndView getComponentModelAndView(Component component, Object model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(UifConstants.DEFAULT_MODEL_NAME, model);
        modelAndView.addObject("Component", component);
        modelAndView.setViewName("ComponentUpdate");

        return modelAndView;
    }

    /**
     * After the controller logic is executed, the form is placed into session
     * and the corresponding view is prepared for rendering
     */
    public static void postControllerHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                            ModelAndView modelAndView) throws Exception {
        if (handler instanceof UifControllerBase && (modelAndView != null)) {
            UifControllerBase controller = (UifControllerBase) handler;
            UifFormBase form = null;

            // check to see if this is a full view request
            if (modelAndView.getViewName().equals(UifConstants.SPRING_VIEW_ID)) {
                Object model = modelAndView.getModelMap().get(UifConstants.DEFAULT_MODEL_NAME);
                if (model instanceof UifFormBase) {
                    form = (UifFormBase) model;

                    // prepare view instance
                    prepareViewForRendering(request, form);

                    // update history for view
                    prepareHistory(request, form);
                }
            }
        }
    }

    /**
     * Updates the history object (or constructs a new History) for the view we are getting ready
     * to render
     *
     * @param request - Http request object containing the request parameters
     * @param form    - object containing the view data
     */
    public static void prepareHistory(HttpServletRequest request, UifFormBase form) {
        View view = form.getView();

        // main history/breadcrumb tracking support
        History history = form.getFormHistory();
        if (history == null || request.getMethod().equals("GET")) {
            history = new History();
            history.setHomewardPath(view.getBreadcrumbs().getHomewardPathList());
            history.setAppendHomewardPath(view.getBreadcrumbs().isDisplayHomewardPath());
            history.setAppendPassedHistory(view.getBreadcrumbs().isDisplayPassedHistory());

            // passed settings ALWAYS override the defaults
            if (StringUtils.isNotBlank(request.getParameter(UifConstants.UrlParams.SHOW_HOME))) {
                history.setAppendHomewardPath(Boolean.parseBoolean(request.getParameter(
                    UifConstants.UrlParams.SHOW_HOME)));
            }

            if (StringUtils.isNotBlank(request.getParameter(UifConstants.UrlParams.SHOW_HISTORY))) {
                history.setAppendPassedHistory(Boolean.parseBoolean(request.getParameter(
                    UifConstants.UrlParams.SHOW_HISTORY)));
            }

            history.setCurrent(form, request);
            history.buildHistoryFromParameterString(request.getParameter(UifConstants.UrlParams.HISTORY));
            form.setFormHistory(history);
        }
    }

    /**
     * Prepares the <code>View</code> instance contained on the form for
     * rendering
     *
     * @param request - request object
     * @param form    - form instance containing the data and view instance
     */
    public static void prepareViewForRendering(HttpServletRequest request, UifFormBase form) {
        View view = form.getView();

        // set view page to page requested on form
        if (StringUtils.isNotBlank(form.getPageId())) {
            view.setCurrentPageId(form.getPageId());
        }

        Map<String, String> parameterMap = KRADUtils.translateRequestParameterMap(request.getParameterMap());
        parameterMap.putAll(form.getViewRequestParameters());

        // build view which will prepare for rendering
        getViewService().buildView(view, form, parameterMap);

        // set dirty flag
        form.setValidateDirty(view.isValidateDirty());
    }

    protected static ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }
}
