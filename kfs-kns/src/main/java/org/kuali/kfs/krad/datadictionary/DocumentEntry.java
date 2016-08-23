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
package org.kuali.kfs.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;
import org.kuali.kfs.krad.datadictionary.exception.DuplicateEntryException;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.document.DocumentAuthorizerBase;
import org.kuali.kfs.krad.document.DocumentPresentationController;
import org.kuali.kfs.krad.document.DocumentPresentationControllerBase;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinder;
import org.kuali.kfs.krad.rules.rule.BusinessRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A single Document entry in the DataDictionary, which contains information relating to the display, validation, and
 * general maintenance of a Document (transactional or maintenance) and its attributes
 *
 * <p>
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process
 * </p>
 *
 *
 */
public abstract class DocumentEntry extends DataDictionaryEntryBase {
    private static final long serialVersionUID = 8231730871830055356L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentEntry.class);

    protected String documentTypeName;

    protected Class<? extends Document> documentClass;
    protected Class<? extends Document> baseDocumentClass;
    protected Class<? extends BusinessRule> businessRulesClass;

    protected boolean allowsNoteAttachments = true;
    protected boolean allowsNoteFYI = false;
    protected Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass;
    protected boolean displayTopicFieldInNotes = false;
    protected boolean encryptDocumentDataInPersistentSessionStorage = false;

    protected boolean allowsCopy = false;
    protected WorkflowProperties workflowProperties;
    protected WorkflowAttributes workflowAttributes;

    protected Class<? extends DocumentAuthorizer> documentAuthorizerClass;
    protected Class<? extends DocumentPresentationController> documentPresentationControllerClass;

    protected List<ReferenceDefinition> defaultExistenceChecks = new ArrayList<ReferenceDefinition>();
    protected Map<String, ReferenceDefinition> defaultExistenceCheckMap =
            new LinkedHashMap<String, ReferenceDefinition>();

    public DocumentEntry() {
        super();

        documentAuthorizerClass = DocumentAuthorizerBase.class;
        documentPresentationControllerClass = DocumentPresentationControllerBase.class;
    }

    /**
     * @see DataDictionaryEntry#getJstlKey()
     */
    public String getJstlKey() {
        return documentTypeName;
    }

    /**
     * The documentClass element is the name of the java class
     * associated with the document.
     */
    public void setDocumentClass(Class<? extends Document> documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }

        this.documentClass = documentClass;
    }

    public Class<? extends Document> getDocumentClass() {
        return documentClass;
    }

    /**
     * The optional baseDocumentClass element is the name of the java base class
     * associated with the document. This gives the data dictionary the ability
     * to index by the base class in addition to the current class.
     */
    public void setBaseDocumentClass(Class<? extends Document> baseDocumentClass) {
        this.baseDocumentClass = baseDocumentClass;
    }

    public Class<? extends Document> getBaseDocumentClass() {
        return baseDocumentClass;
    }

    /**
     * The businessRulesClass element is the full class name of the java
     * class which contains the business rules for a document.
     */
    public void setBusinessRulesClass(Class<? extends BusinessRule> businessRulesClass) {
        this.businessRulesClass = businessRulesClass;
    }

    public Class<? extends BusinessRule> getBusinessRulesClass() {
        return businessRulesClass;
    }

    /**
     * The documentTypeName element is the name of the document
     * as defined in the workflow system.
     * Example: "AddressTypeMaintenanceDocument"
     */
    public void setDocumentTypeName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        this.documentTypeName = documentTypeName;
    }

    public String getDocumentTypeName() {
        return this.documentTypeName;
    }

    /**
     * Validate common fields for subclass' benefit.
     *
     * @see DataDictionaryEntry#completeValidation()
     */
    public void completeValidation() {
        super.completeValidation();

        if (workflowProperties != null && workflowAttributes != null) {
            throw new DataDictionaryException(documentTypeName +
                    ": workflowProperties and workflowAttributes cannot both be defined for a document");
        }
    }

    /**
     * @see DataDictionaryEntry#getFullClassName()
     */
    public String getFullClassName() {
        if (getBaseDocumentClass() != null) {
            return getBaseDocumentClass().getName();
        }
        if (getDocumentClass() != null) {
            return getDocumentClass().getName();
        }
        return "";
    }

    /**
     * @see DataDictionaryEntryBase#getEntryClass()
     */
    public Class getEntryClass() {
        return getDocumentClass();
    }

    public String toString() {
        return "DocumentEntry for documentType " + documentTypeName;
    }

    /**
     * Accessor method for contained displayTopicFieldInNotes
     *
     * @return displayTopicFieldInNotes boolean
     */
    public boolean getDisplayTopicFieldInNotes() {
        return displayTopicFieldInNotes;
    }

    /**
     * This field contains a value of true or false.
     * If true, then the "Notes and Attachments" tab will render a column for a note topic.
     */
    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes) {
        this.displayTopicFieldInNotes = displayTopicFieldInNotes;
    }

    /**
     * The attachmentTypesValuesFinderClass specifies the name of a values finder
     * class. This is used to determine the set of file types that are allowed
     * to be attached to the document.
     */
    public void setAttachmentTypesValuesFinderClass(Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass) {
        if (attachmentTypesValuesFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) attachmentTypesValuesFinderClass");
        }

        this.attachmentTypesValuesFinderClass = attachmentTypesValuesFinderClass;
    }

    /**
     * @see ControlDefinition#getKeyValuesFinder()
     */
    public Class<? extends KeyValuesFinder> getAttachmentTypesValuesFinderClass() {
        return attachmentTypesValuesFinderClass;
    }

    /**
     * The allowsCopy element contains a true or false value.
     * If true, then a user is allowed to make a copy of the
     * record using the maintenance screen.
     */
    public void setAllowsCopy(boolean allowsCopy) {
        this.allowsCopy = allowsCopy;
    }

    public boolean getAllowsCopy() {
        return allowsCopy;
    }

    /**
     * @return the allowsNoteAttachments
     */
    public boolean getAllowsNoteAttachments() {
        return this.allowsNoteAttachments;
    }

    /**
     * The allowsNoteAttachments element contains a true or false value.
     * If true, then a document screen includes notes with attachments. Otherwise,
     * only notes is displayed.
     */
    public void setAllowsNoteAttachments(boolean allowsNoteAttachments) {
        this.allowsNoteAttachments = allowsNoteAttachments;
    }

    /**
     * @return the allowsNoteFYI
     */
    public boolean getAllowsNoteFYI() {
        return allowsNoteFYI;
    }

    /**
     * This is an indicator for determining whether to render the AdHoc FYI recipient box and Send FYI button.
     */
    public void setAllowsNoteFYI(boolean allowsNoteFYI) {
        this.allowsNoteFYI = allowsNoteFYI;
    }

    public WorkflowProperties getWorkflowProperties() {
        return this.workflowProperties;
    }

    /**
     * This element is used to define a set of workflowPropertyGroups, which are used to
     * specify which document properties should be serialized during the document serialization
     * process.
     */
    public void setWorkflowProperties(WorkflowProperties workflowProperties) {
        this.workflowProperties = workflowProperties;
    }

    public WorkflowAttributes getWorkflowAttributes() {
        return this.workflowAttributes;
    }

    public void setWorkflowAttributes(WorkflowAttributes workflowAttributes) {
        this.workflowAttributes = workflowAttributes;
    }

    /**
     * Full class name for the {@link DocumentAuthorizer} that will authorize actions for this document
     *
     * @return class name for document authorizer
     */
    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass() {
        return documentAuthorizerClass;
    }

    /**
     * Setter for the document authorizer class name
     *
     * @param documentAuthorizerClass
     */
    public void setDocumentAuthorizerClass(Class<? extends DocumentAuthorizer> documentAuthorizerClass) {
        this.documentAuthorizerClass = documentAuthorizerClass;
    }

    /**
     * Full class name for the {@link DocumentPresentationController} that will be invoked to implement presentation
     * logic for the document
     *
     * @return class name for document presentation controller
     */
    public Class<? extends DocumentPresentationController> getDocumentPresentationControllerClass() {
        return documentPresentationControllerClass;
    }

    /**
     * Setter for the document presentation controller class name
     *
     * @param documentPresentationControllerClass
     */
    public void setDocumentPresentationControllerClass(
            Class<? extends DocumentPresentationController> documentPresentationControllerClass) {
        this.documentPresentationControllerClass = documentPresentationControllerClass;
    }

    /**
     * @return List of all defaultExistenceCheck ReferenceDefinitions associated with this MaintenanceDocument, in the
     *         order in
     *         which they were added
     */
    public List<ReferenceDefinition> getDefaultExistenceChecks() {
        return defaultExistenceChecks;
    }

    /*
           The defaultExistenceChecks element contains a list of
           reference object names which are required to exist when maintaining a BO.
           Optionally, the reference objects can be required to be active.

           JSTL: defaultExistenceChecks is a Map of Reference elements,
           whose entries are keyed by attributeName
    */
    public void setDefaultExistenceChecks(List<ReferenceDefinition> defaultExistenceChecks) {
        this.defaultExistenceChecks = defaultExistenceChecks;
    }

    /**
     * @return List of all defaultExistenceCheck reference fieldNames associated with this MaintenanceDocument, in the
     *         order in
     *         which they were added
     */
    public List<String> getDefaultExistenceCheckFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.addAll(this.defaultExistenceCheckMap.keySet());

        return fieldNames;
    }

    public boolean isEncryptDocumentDataInPersistentSessionStorage() {
        return this.encryptDocumentDataInPersistentSessionStorage;
    }

    public void setEncryptDocumentDataInPersistentSessionStorage(
            boolean encryptDocumentDataInPersistentSessionStorage) {
        this.encryptDocumentDataInPersistentSessionStorage = encryptDocumentDataInPersistentSessionStorage;
    }

    /**
     * This overridden method ...
     *
     * @see DataDictionaryEntryBase#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (defaultExistenceChecks != null) {
            defaultExistenceCheckMap.clear();
            for (ReferenceDefinition reference : defaultExistenceChecks) {
                if (reference == null) {
                    throw new IllegalArgumentException("invalid (null) defaultExistenceCheck");
                }

                String keyName = reference.isCollectionReference() ?
                        (reference.getCollection() + "." + reference.getAttributeName()) : reference.getAttributeName();
                if (defaultExistenceCheckMap.containsKey(keyName)) {
                    throw new DuplicateEntryException(
                            "duplicate defaultExistenceCheck entry for attribute '" + keyName + "'");
                }
                reference.setBusinessObjectClass(getEntryClass());
                defaultExistenceCheckMap.put(keyName, reference);
            }
        }
    }
}
