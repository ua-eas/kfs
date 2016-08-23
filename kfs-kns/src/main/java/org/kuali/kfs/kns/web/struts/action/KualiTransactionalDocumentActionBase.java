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
package org.kuali.kfs.kns.web.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.kfs.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.kns.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.krad.document.Copyable;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class handles UI actions for all shared methods of transactional documents.
 */
public class KualiTransactionalDocumentActionBase extends KualiDocumentActionBase {
//    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiTransactionalDocumentActionBase.class);

    /**
     * Method that will take the current document and call its copy method if Copyable.
     */
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiTransactionalDocumentFormBase tmpForm = (KualiTransactionalDocumentFormBase) form;

        Document document = tmpForm.getDocument();

        if (!tmpForm.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_COPY)) {
            throw buildAuthorizationException("copy", document);
        }

        ((Copyable) tmpForm.getTransactionalDocument()).toCopy();

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    @SuppressWarnings("unchecked")
	protected void populateAuthorizationFields(KualiDocumentFormBase formBase){
    	super.populateAuthorizationFields(formBase);
    	Document document = formBase.getDocument();
    	Map editMode = new HashMap();

    	if (formBase.isFormDocumentInitialized()) {
        	Person user = GlobalVariables.getUserSession().getPerson();

        	TransactionalDocumentPresentationController documentPresentationController = (TransactionalDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(document);
            TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) KNSServiceLocator
                    .getDocumentHelperService().getDocumentAuthorizer(document);
            Set<String> editModes = documentAuthorizer.getEditModes(document, user, documentPresentationController.getEditModes(document));
            editMode = this.convertSetToMap(editModes);
    	}
    	formBase.setEditingMode(editMode);
    }
}
