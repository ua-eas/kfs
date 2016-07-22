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
package org.kuali.kfs.krad.uif.service.impl;

import org.kuali.kfs.krad.bo.AdHocRoutePerson;
import org.kuali.kfs.krad.bo.AdHocRouteWorkgroup;
import org.kuali.kfs.krad.rules.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.kfs.krad.rules.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiRuleService;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.web.form.DocumentFormBase;


public class DocumentViewHelperServiceImpl extends ViewHelperServiceImpl {

    private KualiRuleService kualiRuleService;

    /**
     * Performs validation on the new collection line before it is added to the
     * corresponding collection
     *
     * @param view - view instance that the action was taken on
     * @param collectionGroup - collection group component for the collection
     * @param addLine - new line instance to validate
     * @param model - object instance that contains the views data
     * @return boolean true if the line is valid and it should be added to the
     *         collection, false if it was not valid and should not be added to
     *         the collection
     */
    @Override
    protected boolean performAddLineValidation(View view, CollectionGroup collectionGroup, Object model,
            Object addLine) {
        boolean isValidLine = true;

        if (model instanceof DocumentFormBase && addLine instanceof AdHocRoutePerson) {
            DocumentFormBase form = (DocumentFormBase) model;
            isValidLine = getKualiRuleService()
                    .applyRules(new AddAdHocRoutePersonEvent(form.getDocument(), (AdHocRoutePerson) addLine));
        } else if (model instanceof DocumentFormBase && addLine instanceof AdHocRouteWorkgroup) {
            DocumentFormBase form = (DocumentFormBase) model;
            isValidLine = getKualiRuleService()
                    .applyRules(new AddAdHocRouteWorkgroupEvent(form.getDocument(), (AdHocRouteWorkgroup) addLine));
        }

        return isValidLine;
    }

    protected KualiRuleService getKualiRuleService() {
        if (kualiRuleService == null) {
            kualiRuleService = KRADServiceLocatorWeb.getKualiRuleService();
        }
        return this.kualiRuleService;
    }
}
