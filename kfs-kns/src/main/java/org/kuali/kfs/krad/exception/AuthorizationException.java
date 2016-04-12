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
package org.kuali.kfs.krad.exception;

import org.kuali.rice.core.api.exception.KualiException;
import org.kuali.rice.core.api.util.RiceKeyConstants;

import java.util.Collections;
import java.util.Map;

/**
 * Represents an exception that is thrown when a given user is not authorized to take the given action on the given
 * target type
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AuthorizationException extends KualiException {
    private static final long serialVersionUID = -3874239711783179351L;

    protected final String userId;
    protected final String action;
    protected final String targetType;
    protected final Map<String, Object> additionalDetails;

    public AuthorizationException(String userId, String action, String targetType) {
        this(userId, action, targetType, Collections.<String, Object>emptyMap());
    }

    /**
     * Constructs a exception with a message from the passed in information.
     *
     * @param userId the userid of the user who failed authorization
     * @param action the action the user was trying to take
     * @param targetType what the user was trying to take action on
     * @param additionalDetails additional details about the authorization failure to be passed in and added to the
     * exception message (ex: permission name, qualifiers, etc.)
     */
    public AuthorizationException(String userId, String action, String targetType,
            Map<String, Object> additionalDetails) {
        this(userId, action, targetType, "user '" + userId + "' is not authorized to take action '" + action
                + "' on targets of type '" + targetType + "'"
                + (additionalDetails != null && !additionalDetails.isEmpty() ?
                " Additional Details : " + additionalDetails : ""), additionalDetails);
    }

    /**
     * Allows you to construct the exception message manually
     */
    public AuthorizationException(String userId, String action, String targetType, String message,
            Map<String, Object> additionalDetails) {
        super(message);

        this.userId = userId;
        this.action = action;
        this.targetType = targetType;
        this.additionalDetails = additionalDetails;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getTargetType() {
        return targetType;
    }

    public Map<String, Object> getAdditionalDetails() {
        return additionalDetails;
    }

    /**
     * @return message key used by Struts to select the error message to be displayed
     * @deprecated
     */
    @Deprecated
    public String getErrorMessageKey() {
        return RiceKeyConstants.AUTHORIZATION_ERROR_GENERAL;
    }
}
