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
package org.kuali.kfs.krad.uif.view;

import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.DocumentViewAuthorizerBase;
import org.kuali.kfs.krad.document.DocumentViewPresentationControllerBase;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinder;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.container.ContainerBase;
import org.kuali.kfs.krad.uif.UifConstants;

/**
 * View type for KRAD documents
 *
 * <p>
 * Provides commons configuration and default behavior applicable to documents
 * in the KRAD module.
 * </p>
 *
 *
 */
public class DocumentView extends FormView {
	private static final long serialVersionUID = 2251983409572774175L;

	private Class<? extends Document> documentClass;

	private boolean allowsNoteAttachments = true;
	private boolean allowsNoteFYI = false;
	private boolean displayTopicFieldInNotes = false;

	private Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass;

	public DocumentView() {
		super();
	}

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Retrieve the document entry</li>
     * <li>Set up the document view authorizer and presentation controller</li>
     * </ul>
     *
     * @see ContainerBase#performInitialization(View,
     *      java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        // get document entry
        DocumentEntry documentEntry = getDocumentEntryForView();
        pushObjectToContext(UifConstants.ContextVariableNames.DOCUMENT_ENTRY, documentEntry);

        // setup authorizer and presentation controller using the configured authorizer and pc for document
        if (getAuthorizer() == null) {
            setAuthorizer(new DocumentViewAuthorizerBase());
        }

        if (getAuthorizer() instanceof DocumentViewAuthorizerBase) {
            DocumentViewAuthorizerBase documentViewAuthorizerBase = (DocumentViewAuthorizerBase) getAuthorizer();
            if (documentViewAuthorizerBase.getDocumentAuthorizer() == null) {
                documentViewAuthorizerBase.setDocumentAuthorizerClass(documentEntry.getDocumentAuthorizerClass());
            }
        }

        if (getPresentationController() == null) {
            setPresentationController(new DocumentViewPresentationControllerBase());
        }

        if (getPresentationController() instanceof DocumentViewPresentationControllerBase) {
            DocumentViewPresentationControllerBase documentViewPresentationControllerBase =
                    (DocumentViewPresentationControllerBase) getPresentationController();
            if (documentViewPresentationControllerBase.getDocumentPresentationController() == null) {
                documentViewPresentationControllerBase.setDocumentPresentationControllerClass(
                        documentEntry.getDocumentPresentationControllerClass());
            }
        }
    }

    /**
     * Retrieves the associated {@link DocumentEntry} for the document view
     *
     * @return DocumentEntry entry (exception thrown if one is not found)
     */
    protected DocumentEntry getDocumentEntryForView() {
        DocumentEntry documentEntry = KRADServiceLocatorWeb.getDocumentDictionaryService().getDocumentEntryByClass(
                getDocumentClass());

        if (documentEntry == null) {
            throw new RuntimeException(
                    "Unable to find document entry for document class: " + getDocumentClass().getName());
        }

        return documentEntry;
    }

	public Class<? extends Document> getDocumentClass() {
		return this.documentClass;
	}

	public void setDocumentClass(Class<? extends Document> documentClass) {
		this.documentClass = documentClass;
	}

	public boolean isAllowsNoteAttachments() {
		return this.allowsNoteAttachments;
	}

	public void setAllowsNoteAttachments(boolean allowsNoteAttachments) {
		this.allowsNoteAttachments = allowsNoteAttachments;
	}

	public boolean isAllowsNoteFYI() {
		return this.allowsNoteFYI;
	}

	public void setAllowsNoteFYI(boolean allowsNoteFYI) {
		this.allowsNoteFYI = allowsNoteFYI;
	}

	public boolean isDisplayTopicFieldInNotes() {
		return this.displayTopicFieldInNotes;
	}

	public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes) {
		this.displayTopicFieldInNotes = displayTopicFieldInNotes;
	}

	public Class<? extends KeyValuesFinder> getAttachmentTypesValuesFinderClass() {
		return this.attachmentTypesValuesFinderClass;
	}

	public void setAttachmentTypesValuesFinderClass(Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass) {
		this.attachmentTypesValuesFinderClass = attachmentTypesValuesFinderClass;
	}

}
