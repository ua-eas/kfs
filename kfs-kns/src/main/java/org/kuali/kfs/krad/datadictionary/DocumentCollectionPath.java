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

import java.util.List;

/**
 * This is a description of what this class does - mpham don't forget to fill this in.
 */
public class DocumentCollectionPath extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = -8165456163213868710L;

    private String collectionPath;
    private List<String> paths;
    private DocumentCollectionPath nestedCollection;

    /**
     * @return the documentValues
     */
    public List<String> getDocumentValues() {
        return this.paths;
    }

    /**
     * @return the documentCollectionPath
     */
    public DocumentCollectionPath getNestedCollection() {
        return this.nestedCollection;
    }

    /**
     * @param documentValues the documentValues to set
     */
    public void setDocumentValues(List<String> paths) {
        this.paths = paths;
    }

    /**
     * @param documentCollectionPath the documentCollectionPath to set
     */
    public void setNestedCollection(
        DocumentCollectionPath documentCollectionPath) {
        this.nestedCollection = documentCollectionPath;
    }

    /**
     * This overridden method ...
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass,
                                   Class otherBusinessObjectClass) {
        // TODO mpham - THIS METHOD NEEDS JAVADOCS

    }

    /**
     * @return the collectionPath
     */
    public String getCollectionPath() {
        return this.collectionPath;
    }

    /**
     * @param collectionPath the collectionPath to set
     */
    public void setCollectionPath(String collectionPath) {
        this.collectionPath = collectionPath;
    }
}
