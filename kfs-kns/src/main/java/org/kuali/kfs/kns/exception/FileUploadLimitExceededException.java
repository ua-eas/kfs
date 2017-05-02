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
package org.kuali.kfs.kns.exception;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.exception.KualiException;

/**
 * This class represents an FileUploadLimitExceededException.
 */

public class FileUploadLimitExceededException extends KualiException {

    private ActionForm actionForm;
    private ActionMapping actionMapping;

    /**
     * Create an FileUploadLimitExceededException with the given message
     *
     * @param message
     */
    public FileUploadLimitExceededException(String message, ActionForm actionForm, ActionMapping actionMapping) {
        super(message);
        this.actionForm = actionForm;
        this.actionMapping = actionMapping;
    }

    /**
     * Create an FileUploadLimitExceededException with the given message and cause
     *
     * @param message
     * @param cause
     */
    public FileUploadLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @return the actionForm
     */
    public ActionForm getActionForm() {
        return this.actionForm;
    }

    /**
     * @return the actionMapping
     */
    public ActionMapping getActionMapping() {
        return this.actionMapping;
    }

}
