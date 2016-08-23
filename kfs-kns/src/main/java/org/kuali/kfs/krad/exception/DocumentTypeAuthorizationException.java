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
package org.kuali.kfs.krad.exception;

import org.kuali.rice.core.api.util.RiceKeyConstants;

import java.util.Collections;
import java.util.Map;

/**
 * This class represents an exception that is thrown when a given user is not authorized to take a given action on the given
 * document type.
 */
public class DocumentTypeAuthorizationException extends AuthorizationException {
    private static final long serialVersionUID = 1548057953855152103L;

    public DocumentTypeAuthorizationException(String userId, String action, String documentType) {
        this(userId, action, documentType, Collections.<String, Object>emptyMap());
    }

    public DocumentTypeAuthorizationException(String userId, String action, String documentType, Map<String, Object> requestAuthDetails) {
        super(userId, action, documentType, requestAuthDetails);
    }

    /**
     * @see AuthorizationException#getErrorMessageKey()
     */
    public String getErrorMessageKey() {
        return RiceKeyConstants.AUTHORIZATION_ERROR_DOCTYPE;
    }
}
