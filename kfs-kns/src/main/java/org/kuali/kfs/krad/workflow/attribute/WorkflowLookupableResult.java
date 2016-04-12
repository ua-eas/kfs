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
package org.kuali.kfs.krad.workflow.attribute;

import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This interface is used to get the cglib Enhancer to simulate the returnUrl bean property required by workflow on the
 * BusinessObject proxies returned by the getSearchResults(Map fieldValues, Map fieldConversions) method of WorkflowLookupableImpl.
 * It also extends Map and simulates a bean property of itself on the proxy, because we want a generic getter that returns objects
 * and will allow us to format booleans.
 * 
 * 
 * @see org.kuali.rice.kew.attribute.WorkflowLookupableImpl
 * @see WorkflowLookupableInvocationHandler
 * @deprecated This will go away once workflow supports simple url integration for custom attribute lookups.
 */
public interface WorkflowLookupableResult extends BusinessObject {
    /**
     * Gets the returnUrl attribute.
     * 
     * @return Returns the returnUrl.
     */
    public String getReturnUrl();

    /**
     * Sets the returnUrl attribute.
     * 
     * @param returnUrl The returnUrl to set.
     */
    public void setReturnUrl(String returnUrl);

    /**
     * Gets the workflowLookupableResult attribute.
     * 
     * @return Returns the workflowLookupableResult
     */
    public WorkflowLookupableResult getWorkflowLookupableResult();

    /**
     * Sets the workflowLookupableResult attribute.
     * 
     * @param workflowLookupableResult The workflowLookupableResult to set.
     */
    public void setWorkflowLookupableResult(WorkflowLookupableResult workflowLookupableResult);
}
