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
package org.kuali.kfs.kns.datadictionary;

import org.kuali.kfs.kns.document.authorization.DocumentAuthorizer;
import org.kuali.kfs.kns.document.authorization.DocumentPresentationController;
import org.kuali.kfs.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.kfs.kns.document.authorization.TransactionalDocumentPresentationControllerBase;
import org.kuali.kfs.kns.rule.PromptBeforeValidation;
import org.kuali.kfs.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.ReferenceDefinition;
import org.kuali.kfs.krad.document.Document;

import java.util.ArrayList;
import java.util.List;


@Deprecated
public class TransactionalDocumentEntry extends org.kuali.kfs.krad.datadictionary.TransactionalDocumentEntry implements KNSDocumentEntry {

    protected Class<? extends PromptBeforeValidation> promptBeforeValidationClass;
    protected Class<? extends DerivedValuesSetter> derivedValuesSetterClass;
    protected List<String> webScriptFiles = new ArrayList<String>(3);
    protected List<HeaderNavigation> headerNavigationList = new ArrayList<HeaderNavigation>();

    protected boolean sessionDocument = false;

    public TransactionalDocumentEntry() {
        super();

        documentAuthorizerClass = TransactionalDocumentAuthorizerBase.class;
        documentPresentationControllerClass = TransactionalDocumentPresentationControllerBase.class;
    }

    @Override
    public List<HeaderNavigation> getHeaderNavigationList() {
        return headerNavigationList;
    }

    @Override
    public List<String> getWebScriptFiles() {
        return webScriptFiles;
    }

    /**
     * @return Returns the preRulesCheckClass.
     */
    @Override
    public Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass() {
        return promptBeforeValidationClass;
    }

    /**
     * The promptBeforeValidationClass element is the full class name of the java
     * class which determines whether the user should be asked any questions prior to running validation.
     *
     * @see KualiDocumentActionBase#promptBeforeValidation(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, String)
     */
    @Override
    public void setPromptBeforeValidationClass(Class<? extends PromptBeforeValidation> preRulesCheckClass) {
        this.promptBeforeValidationClass = preRulesCheckClass;
    }

    /**
     * The webScriptFile element defines the name of javascript files
     * that are necessary for processing the document.  The specified
     * javascript files will be included in the generated html.
     */
    @Override
    public void setWebScriptFiles(List<String> webScriptFiles) {
        this.webScriptFiles = webScriptFiles;
    }

    /**
     * The headerNavigation element defines a set of additional
     * tabs which will appear on the document.
     */
    @Override
    public void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList) {
        this.headerNavigationList = headerNavigationList;
    }

    @Override
    public boolean isSessionDocument() {
        return this.sessionDocument;
    }

    @Override
    public void setSessionDocument(boolean sessionDocument) {
        this.sessionDocument = sessionDocument;
    }

    /**
     * @return the derivedValuesSetter
     */
    @Override
    public Class<? extends DerivedValuesSetter> getDerivedValuesSetterClass() {
        return this.derivedValuesSetterClass;
    }

    /**
     * @param derivedValuesSetter the derivedValuesSetter to set
     */
    @Override
    public void setDerivedValuesSetterClass(Class<? extends DerivedValuesSetter> derivedValuesSetter) {
        this.derivedValuesSetterClass = derivedValuesSetter;
    }

    /**
     * Returns the document authorizer class for the document.  Only framework code should be calling this method.
     * Client devs should use {@link DocumentTypeService#getDocumentAuthorizer(Document)}
     * or
     * {@link DocumentTypeService#getDocumentAuthorizer(String)}
     *
     * @return a document authorizer class
     */
    @Override
    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass() {
        return (Class<? extends DocumentAuthorizer>) super.getDocumentAuthorizerClass();
    }

    /**
     * Returns the document presentation controller class for the document.  Only framework code should be calling
     * this
     * method.
     * Client devs should use {@link DocumentTypeService#getDocumentPresentationController(Document)}
     * or
     * {@link DocumentTypeService#getDocumentPresentationController(String)}
     *
     * @return the documentPresentationControllerClass
     */
    @Override
    public Class<? extends DocumentPresentationController> getDocumentPresentationControllerClass() {
        return (Class<? extends DocumentPresentationController>) super.getDocumentPresentationControllerClass();
    }

    /**
     * @see DocumentEntry#completeValidation()
     */
    @Override
    public void completeValidation() {
        super.completeValidation();
        for (ReferenceDefinition reference : defaultExistenceChecks) {
            reference.completeValidation(documentClass, null);
        }
    }

    @Override
    public String toString() {
        return "TransactionalDocumentEntry for documentType " + getDocumentTypeName();
    }
}
