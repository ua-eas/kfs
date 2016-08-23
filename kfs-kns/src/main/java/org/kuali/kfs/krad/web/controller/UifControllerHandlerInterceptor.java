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
package org.kuali.kfs.krad.web.controller;

import org.apache.log4j.Logger;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.uif.util.UifFormManager;
import org.kuali.kfs.krad.uif.util.UifWebUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.util.CsrfValidator;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring controller intercepter for KRAD controllers
 *
 * <p>
 * Provides infrastructure for preparing the form and view before and after the controller is invoked.
 * Included in this is form session management and preparation of the view for rendering
 * </p>
 *
 *
 */
public class UifControllerHandlerInterceptor implements HandlerInterceptor {
    private static final Logger LOG = Logger.getLogger(UifControllerHandlerInterceptor.class);

    /**
     * Before the controller executes the user session is set on GlobalVariables
     * and messages are cleared
     *
     * TODO: do we need to clear the messages before this so that formatting and
     * validation errors done during the binding are not cleared out?
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        if (!CsrfValidator.validateCsrf(request, response)) {
            return false;
        }

        final UserSession session = KRADUtils.getUserSessionFromRequest(request);

        if (session == null) {
            throw new IllegalStateException("the user session has not been established");
        }

        GlobalVariables.setUserSession(session);
        GlobalVariables.clear();

        return true;
    }

    /**
     * After the controller logic is executed, the form is placed into session
     * and the corresponding view is prepared for rendering
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object,
     *      org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        UifWebUtils.postControllerHandle(request, response, handler, modelAndView);
    }

    /**
     * After the view is rendered we can do some cleaning to reduce the size of the form storage in memory
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        UifFormManager uifFormManager = (UifFormManager) request.getSession().getAttribute(UifParameters.FORM_MANAGER);

        UifFormBase uifForm = uifFormManager.getCurrentForm();
        if (uifForm != null) {
            if (uifForm.isSkipViewInit()) {
                // partial refresh or query
                View postedView = uifForm.getPostedView();
                if (postedView != null) {
                    postedView.getViewHelperService().cleanViewAfterRender(postedView);
                }
            } else {
                // full view render
                View view = uifForm.getView();
                if (view != null) {
                    view.getViewHelperService().cleanViewAfterRender(view);
                }

                uifForm.setPostedView(view);
                uifForm.setView(null);
            }
        }
    }

}
