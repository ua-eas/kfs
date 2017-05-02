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
package org.kuali.kfs.krad.datadictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This element is used to define a set of workflowPropertyGroups, which are used to
 * specify which document properties should be serialized during the document serialization
 * process.
 */
public class WorkflowProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    protected List<WorkflowPropertyGroup> workflowPropertyGroups;

    public WorkflowProperties() {
        workflowPropertyGroups = new ArrayList<WorkflowPropertyGroup>();
    }

    /**
     * Returns a list of workflow property groups, which are used to determine which properties should be serialized when generating
     * routing XML
     *
     * @return a list of {@link WorkflowPropertyGroup} objects, in the order in which they were added
     */
    public List<WorkflowPropertyGroup> getWorkflowPropertyGroups() {
        return this.workflowPropertyGroups;
    }

    /**
     * This element is used to define a set of workflowPropertyGroups, which are used to
     * specify which document properties should be serialized during the document serialization
     * process.
     */
    public void setWorkflowPropertyGroups(List<WorkflowPropertyGroup> workflowPropertyGroups) {
        this.workflowPropertyGroups = workflowPropertyGroups;
    }

}
