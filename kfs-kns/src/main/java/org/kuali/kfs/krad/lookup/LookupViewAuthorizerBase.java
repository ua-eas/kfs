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
package org.kuali.kfs.krad.lookup;

import org.kuali.kfs.krad.uif.view.LookupView;
import org.kuali.kfs.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.view.ViewAuthorizerBase;
import org.kuali.kfs.krad.uif.view.ViewModel;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.web.form.LookupForm;

import java.util.Map;

/**
 * Implementation of {@link ViewAuthorizer} for
 * {@link LookupView} instances
 *
 * 
 */
public class LookupViewAuthorizerBase extends ViewAuthorizerBase {
    private static final long serialVersionUID = 3755133641536256283L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupViewAuthorizerBase.class);

    /**
     * Override to check the for permissions of type 'Look Up Records' in addition to the open view check
     * done in super
     */
    @Override
    public boolean canOpenView(View view, ViewModel model, Person user) {
        boolean canOpen = super.canOpenView(view, model, user);

        if (canOpen) {
            LookupForm lookupForm = (LookupForm) model;

            Map<String, String> additionalPermissionDetails;
            try {
                additionalPermissionDetails = KRADUtils.getNamespaceAndComponentSimpleName(Class.forName(
                        lookupForm.getDataObjectClassName()));
            } catch (ClassNotFoundException e) {
                throw new RiceRuntimeException(
                        "Unable to create class for lookup class name: " + lookupForm.getDataObjectClassName());
            }

            if (permissionExistsByTemplate(model, KRADConstants.KNS_NAMESPACE,
                    KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS, additionalPermissionDetails)) {
                canOpen = isAuthorizedByTemplate(model, KRADConstants.KNS_NAMESPACE,
                        KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS, user.getPrincipalId(),
                        additionalPermissionDetails, null);
            }
        }

        return canOpen;
    }

    /**
     * Check if user is allowed to initiate the document
     *
     * @param lookupForm - The lookup form of the document
     * @param user - user we are authorizing the actions for
     * @return true if user is authorized to initiate the document, false otherwise
     */
    public boolean canInitiateDocument(LookupForm lookupForm, Person user) {
        boolean canInitiateDocument = false;

        try {
            Class<?> dataObjectClass = Class.forName(lookupForm.getDataObjectClassName());
            // check if creating documents is allowed
            String documentTypeName = KRADServiceLocatorWeb.getDocumentDictionaryService()
                    .getMaintenanceDocumentTypeName(dataObjectClass);
            if ((documentTypeName != null) &&
                    KRADServiceLocatorWeb.getDocumentDictionaryService().getDocumentAuthorizer(documentTypeName)
                            .canInitiate(documentTypeName, user)) {
                canInitiateDocument = true;
            }
        } catch (ClassNotFoundException e) {
            LOG.warn("Unable to load Data Object Class: " + lookupForm.getDataObjectClassName(), e);
        }

        return canInitiateDocument;
    }
}
