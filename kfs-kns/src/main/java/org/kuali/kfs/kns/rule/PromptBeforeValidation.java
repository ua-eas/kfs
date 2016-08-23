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
package org.kuali.kfs.kns.rule;

import org.apache.struts.action.ActionForm;
import org.kuali.kfs.kns.rule.event.PromptBeforeValidationEvent;
import org.kuali.kfs.krad.rules.rule.BusinessRule;

import javax.servlet.http.HttpServletRequest;

/**
 * An interface for a class that provides the ability to prompt the user with a question prior to running a document action.
 * An implementation class of this interface may be specified in the document data dictionary file.
 * <p>
 * By default, unless KualiDocumentActionBase is overridden, the sole method will be invoked upon using the "approve", "blanketApprove",
 * "performRouteReport", and "route" methodToCalls.
 */
public interface PromptBeforeValidation extends BusinessRule {

    /**
     * Callback method from Maintenance action that allows checks to be done and response redirected via the PreRulesCheckEvent
     *
     * @param form
     * @param request
     * @param event   stores various information necessary to render the question prompt
     * @return boolean indicating whether the validation (and if validation successful, the action) should continue.  If false, the
     * values within the event parameter will determine how the struts action handler should proceed
     */
    public boolean processPrompts(ActionForm form, HttpServletRequest request, PromptBeforeValidationEvent event);
}
