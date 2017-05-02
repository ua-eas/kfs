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
package org.kuali.kfs.kns.datadictionary;

import org.kuali.kfs.kns.document.authorization.DocumentAuthorizer;
import org.kuali.kfs.kns.document.authorization.DocumentPresentationController;
import org.kuali.kfs.kns.rule.PromptBeforeValidation;
import org.kuali.kfs.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntry;
import org.kuali.kfs.krad.datadictionary.ReferenceDefinition;
import org.kuali.kfs.krad.datadictionary.WorkflowAttributes;
import org.kuali.kfs.krad.datadictionary.WorkflowProperties;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinder;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.List;


@Deprecated
public interface KNSDocumentEntry extends DataDictionaryEntry, Serializable, InitializingBean {

    public Class<? extends Document> getDocumentClass();

    public void setDocumentClass(Class<? extends Document> documentClass);

    public Class<? extends Document> getBaseDocumentClass();

    public void setBaseDocumentClass(Class<? extends Document> baseDocumentClass);

    public Class<? extends BusinessRule> getBusinessRulesClass();

    public void setBusinessRulesClass(Class<? extends BusinessRule> businessRulesClass);

    public String getDocumentTypeName();

    public void setDocumentTypeName(String documentTypeName);

    public boolean getDisplayTopicFieldInNotes();

    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes);

    public Class<? extends KeyValuesFinder> getAttachmentTypesValuesFinderClass();

    public void setAttachmentTypesValuesFinderClass(Class<? extends KeyValuesFinder> attachmentTypesValuesFinderClass);

    public boolean getAllowsCopy();

    public void setAllowsCopy(boolean allowsCopy);

    public boolean getAllowsNoteAttachments();

    public void setAllowsNoteAttachments(boolean allowsNoteAttachments);

    public boolean getAllowsNoteFYI();

    public void setAllowsNoteFYI(boolean allowsNoteFYI);

    public WorkflowProperties getWorkflowProperties();

    public void setWorkflowProperties(WorkflowProperties workflowProperties);

    public WorkflowAttributes getWorkflowAttributes();

    public void setWorkflowAttributes(WorkflowAttributes workflowAttributes);

    public List<ReferenceDefinition> getDefaultExistenceChecks();

    public void setDefaultExistenceChecks(List<ReferenceDefinition> defaultExistenceChecks);

    public boolean isEncryptDocumentDataInPersistentSessionStorage();

    public void setEncryptDocumentDataInPersistentSessionStorage(
        boolean encryptDocumentDataInPersistentSessionStorage);

    List<HeaderNavigation> getHeaderNavigationList();

    List<String> getWebScriptFiles();

    Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass();

    void setPromptBeforeValidationClass(Class<? extends PromptBeforeValidation> preRulesCheckClass);

    void setWebScriptFiles(List<String> webScriptFiles);

    void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList);

    boolean isSessionDocument();

    void setSessionDocument(boolean sessionDocument);

    Class<? extends DerivedValuesSetter> getDerivedValuesSetterClass();

    void setDerivedValuesSetterClass(Class<? extends DerivedValuesSetter> derivedValuesSetter);

    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass();

    public Class<? extends DocumentPresentationController> getDocumentPresentationControllerClass();


}
