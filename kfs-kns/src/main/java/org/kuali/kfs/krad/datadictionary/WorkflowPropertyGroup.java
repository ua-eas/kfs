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

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.KRADConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This object allows for grouping of related {@link WorkflowProperty} objects.  It defines a base path to which all {@link WorkflowProperty} are
 * relative. See {@link #getBasePath()} for a explanation of the meaning of the base path
 *
 *                 This element is used to define a set of workflowProperty tags, which are used to
                specify which document properties should be serialized during the document serialization
                process.  This element allows for all the nested workflowProperty tags to be relative
                to some base path.  This base path itself is relative to the object being serialized
                during the document serialization process (which is not necessarily the document itself,
                but a wrapper around the document).

                If blank/missing, the base path will be assumed to be the property path to the document
 */
public class WorkflowPropertyGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String basePath = KRADConstants.EMPTY_STRING;
    protected List<WorkflowProperty> workflowProperties = new ArrayList<WorkflowProperty>();

    /**
     * Returns the list of added {@link WorkflowProperty} objects.
     *
     * @return list of {@link WorkflowProperty} objects.
     */
    public List<WorkflowProperty> getWorkflowProperties() {
        return workflowProperties;
    }

    /**
     * Returns the base path of the group, which represents the path that all {@link WorkflowProperty} objects are relative to.  The base path
     * itself should be relative from the object being serialized, which may not necessarily be the document, see {@link Document#wrapDocumentWithMetadataForXmlSerialization()}
     * and {@link Document#getBasePathToDocumentDuringSerialization()}
     *
     * @return the base path
     */
    public String getBasePath() {
        return this.basePath;
    }

    /**
     * This element allows for all the nested workflowProperty tags to be relative
                to some base path.  This base path itself is relative to the object being serialized
                during the document serialization process (which is not necessarily the document itself,
                but a wrapper around the document).

                If blank/missing, the base path will be assumed to be the property path to the document
     *
     * @param basePath see description of {@link #getBasePath()}
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setWorkflowProperties(List<WorkflowProperty> workflowProperties) {
        this.workflowProperties = workflowProperties;
    }

}
