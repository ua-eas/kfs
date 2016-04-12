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
package org.kuali.kfs.krad.datadictionary;

import org.kuali.kfs.krad.util.documentserializer.PropertySerializabilityEvaluator;

import java.io.Serializable;

/**
 * This class represents an serializable property when generating workflow routing XML.  The path contained within this object
 * is relative to the basePath in the {@link WorkflowPropertyGroup} that contains this object.  The semantics of the path are determined
 * by the {@link PropertySerializabilityEvaluator} that evaluates whether a property is serializable. 
 */
public class WorkflowProperty implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String path = null;
    
    /**
     * Default constructor, sets path to null
     * 
     */
    public WorkflowProperty() {}

    /**
     * Returns the path to the property that is serializable, relative to the {@link WorkflowPropertyGroup} that contains this object
     * 
     * @return
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Sets the path to the property that is serializable, relative to the {@link WorkflowPropertyGroup} that contains this object
     * 
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
