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
package org.kuali.kfs.sys.web.struts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.web.struts.action.KualiAction;
import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.InstitutionPreferencesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SidebarMenuCacheAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SidebarMenuCacheAction.class);

    private InstitutionPreferencesService institutionPreferencesService;

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("start() started");

        request.setAttribute("KualiForm", new KualiForm());

        return mapping.findForward("basic");
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clear() started");

        getInstitutionPreferencesService().clearInstitutionPreferencesCache();

        return mapping.findForward("portal");
    }

    public InstitutionPreferencesService getInstitutionPreferencesService() {
        if (institutionPreferencesService == null) {
            institutionPreferencesService = SpringContext.getBean(InstitutionPreferencesService.class);
        }
        return institutionPreferencesService;
    }
}
