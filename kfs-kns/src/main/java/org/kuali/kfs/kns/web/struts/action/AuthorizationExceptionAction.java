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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.RiceConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the struts action class for handling the exception for Kuali
 * applications.
 */
public class AuthorizationExceptionAction extends Action {

    private static final String MESSAGE_FIELD = "message";

    private static final Log LOG = LogFactory.getLog(AuthorizationExceptionAction.class);

    /**
     * Dispatches action to be taken during an AuthorizationException.
     *
     * @see org.apache.struts.action.Action#execute(
     *org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("ENTRY %s%n%s", form.getClass().getSimpleName(), request.getRequestURI()));
        }

        ActionForward forward = null;

        Throwable t = (Throwable) request.getAttribute(Globals.EXCEPTION_KEY);

        if (t == null) {
            forward = mapping.findForward(KRADConstants.MAPPING_CLOSE);
        } else {
            forward = processException(mapping, form, request, t);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("EXIT %s", (forward == null) ? "null" : forward.getPath()));
        }

        return forward;
    }

    private ActionForward processException(ActionMapping mapping, ActionForm form, HttpServletRequest request, Throwable t) throws Exception {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(MESSAGE_FIELD, t.getMessage());

        request.setAttribute(AuthorizationExceptionAction.class.getName(), properties);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

}
