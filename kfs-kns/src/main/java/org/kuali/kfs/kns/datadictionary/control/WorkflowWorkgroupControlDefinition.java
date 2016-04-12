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
package org.kuali.kfs.kns.datadictionary.control;

/**
 *                         The workflowWorkgroup element control is used to identify
                        the field as being a Workgroup Name field.  The magnifying
                        glass will do a WorkGroup Lookup into the workflow system.
                        The Workgroup Name will be returned from the lookup.

                        This control also displays some special icons next to the
                        magnifying glass.

 */
@Deprecated
public class WorkflowWorkgroupControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -7423968769245455072L;

	public WorkflowWorkgroupControlDefinition() {
    }

    /**
     * @see ControlDefinitionBase#isWorkgroup()
     */
    @Override
    public boolean isWorkflowWorkgroup() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "WorkflowWorkgroupControlDefinition";
    }
}
