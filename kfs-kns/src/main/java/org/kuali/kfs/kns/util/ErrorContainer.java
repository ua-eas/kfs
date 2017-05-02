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
package org.kuali.kfs.kns.util;


import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.springframework.util.AutoPopulatingList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Provides access to a copy of an ErrorMap and information derived from it. Necessary because ErrorMap implements the Map
 * interface, which for some reason makes JSTL unwilling to translate ErrorMap.errorCount into a call to the getErrorCount method of
 * that ErrorMap instance.
 * <p>
 * Since I had to create this class to provide easy access to the error count (which must be computed as the sum of the sizes of the
 * error message lists of all properties in the ErrorMap), I also moved in the existing code which massaged the contents of the
 * ErrorMap for the purposes of export to the JSP.
 */
public class ErrorContainer implements Serializable {
    private final MessageMap errorMap;
    private final int errorCount;

    /**
     * Constructs an ErrorContainer
     *
     * @param errorMap
     */
    public ErrorContainer(MessageMap errorMap) {
        this.errorMap = errorMap;
        this.errorCount = errorMap.getErrorCount();
    }

    /**
     * @return number of errors in the ErrorMap used to initialize this container
     */
    public int getErrorCount() {
        if (hasFormatterError()) {
            return 0;
        }
        return errorCount;
    }

    /**
     * @return simple List of all properies for which errorMessages exist in the ErrorMap used to initialize this container
     */
    public List getErrorPropertyList() {
        List properties = new ArrayList();

        for (Iterator iter = errorMap.getAllPropertiesWithErrors().iterator(); iter.hasNext(); ) {
            properties.add(iter.next());
        }

        return properties;
    }

    /**
     * This method checks whether the errorMap contains at least a formatter error.
     *
     * @return boolean true if the errorMap contains a formatter error and false otherwise
     */
    private boolean hasFormatterError() {
        if (errorMap.getErrorCount() > 0) {
            for (String errorKey : errorMap.getAllPropertiesWithErrors()) {
                AutoPopulatingList errorValues = errorMap.getMessages(errorKey);
                for (ErrorMessage errorMessage : (List<ErrorMessage>) errorValues) {
                    if (errorMessage.getErrorKey().equals(RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_FORMATTING_ERROR)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return ActionMessages instance containing error messages constructed from the contents of the ErrorMap with which this
     * container was initialized
     */
    public ActionMessages getRequestErrors() {
        ActionMessages requestErrors = new ActionMessages();
        for (Iterator iter = errorMap.getAllPropertiesWithErrors().iterator(); iter.hasNext(); ) {
            String property = (String) iter.next();
            List errorList = (List) errorMap.getErrorMessagesForProperty(property);

            for (Iterator iterator = errorList.iterator(); iterator.hasNext(); ) {
                ErrorMessage errorMessage = (ErrorMessage) iterator.next();

                // add ActionMessage with any parameters
                requestErrors.add(property, new ActionMessage(errorMessage.getErrorKey(), errorMessage.getMessageParameters()));
            }
        }
        return requestErrors;
    }
}
