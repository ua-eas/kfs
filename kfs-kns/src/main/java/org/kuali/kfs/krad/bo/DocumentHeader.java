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
package org.kuali.kfs.krad.bo;

import org.kuali.rice.core.api.exception.RiceRuntimeException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * Business Object representing a document header. The document header contains metadata about a document.
 * This contains a reference to the template associated with the document.
 * This also provides the access to the underlying {@link WorkflowDocument} associated with this document header.
 *
 *
 */
@Entity
@Table(name="KRNS_DOC_HDR_T")
public class DocumentHeader extends org.kuali.kfs.krad.bo.PersistableBusinessObjectBase {

    @Id
	@Column(name="DOC_HDR_ID")
	private String documentNumber;
    @Column(name="FDOC_DESC")
	private String documentDescription;
    @Column(name="ORG_DOC_HDR_ID")
	private String organizationDocumentNumber;
    @Column(name="TMPL_DOC_HDR_ID")
	private String documentTemplateNumber;
    @Column(name="EXPLANATION")
	private String explanation;

    @Transient
    private WorkflowDocument workflowDocument;

    /**
     * Constructor - creates empty instances of dependent objects
     *
     */
    public DocumentHeader() {
        super();
    }

    /**
     * Returns an instance of the the {@link WorkflowDocument} associated with this document header.
     * The workflowDocument provides the core client interface for interacting with the KEW workflow module.
     * @return workflowDocument
     */
    public WorkflowDocument getWorkflowDocument() {
        if (workflowDocument == null) {
            throw new RiceRuntimeException("The workflow document is null.  This indicates that the DocumentHeader has not been initialized properly.  This can be caused by not retrieving a document using the DocumentService.");
        }

        return workflowDocument;
    }

    /**
     * Returns whether this document header has a {@link WorkflowDocument} associated with it.
     * @return true if the workflowDocument is not null
     */
    public boolean hasWorkflowDocument() {
        return (workflowDocument != null);
    }


    /**
     * Associates a {@link WorkflowDocument} with this document header.
     * @param workflowDocument
     */
    public void setWorkflowDocument(WorkflowDocument workflowDocument) {
        this.workflowDocument = workflowDocument;
    }

    /**
     * Returns the documentNumber (also known as the docuementHeaderId). This is a unique identifier for the document.
     * @return the documentNumber
     */
    public String getDocumentNumber() {
        return this.documentNumber;
    }

    /**
     * Sets the documentNumber for this document. It serves as a unique identifier for the document.
     * @param documentNumber the documentNumber to set
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Returns the description text for this document.
     * @return the documentDescription
     */
    public String getDocumentDescription() {
        return this.documentDescription;
    }

    /**
     * Sets the description text for this document.
     * @param documentDescription the documentDescription to set
     */
    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    /**
     * Returns the organizationDocumentNumber. This identifier is one that may be used by a client to refer to the document.
     * @return the organizationDocumentNumber
     */
    public String getOrganizationDocumentNumber() {
        return this.organizationDocumentNumber;
    }

    /**
     * Sets the value of the organizationDocumentNumber
     * @param organizationDocumentNumber the organizationDocumentNumber to set
     */
    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    /**
     * Returns the documentTemplateNumber. It identifies the document template associated with this document.
     * @return the documentTemplateNumber
     */
    public String getDocumentTemplateNumber() {
        return this.documentTemplateNumber;
    }

    /**
     * Associates this document with a document template.
     * @param documentTemplateNumber the id of the documentTemplate associated with this document
     */
    public void setDocumentTemplateNumber(String documentTemplateNumber) {
        this.documentTemplateNumber = documentTemplateNumber;
    }

    /**
     * Gets the explanation attribute. This text provides additional information about the purpose of the document.
     * @return Returns the explanation.
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Sets the explanation attribute value.
     * @param explanation The explanation text string.
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

}
