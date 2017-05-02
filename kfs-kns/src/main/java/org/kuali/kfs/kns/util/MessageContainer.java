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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An adapter whose subclasses will make either an {@link MessageMap}'s warning or info messages available to the JSP layer
 */
public abstract class MessageContainer implements Serializable {
    private MessageMap errorMap;

    protected MessageContainer(MessageMap errorMap) {
        this.errorMap = errorMap;
    }

    protected MessageMap getMessageMap() {
        return errorMap;
    }

    public ActionMessages getRequestMessages() {
        ActionMessages requestErrors = new ActionMessages();
        for (Iterator<String> iter = getMessagePropertyNames().iterator(); iter.hasNext(); ) {
            String property = iter.next();
            List errorList = (List) getMessagesForProperty(property);

            for (Iterator iterator = errorList.iterator(); iterator.hasNext(); ) {
                ErrorMessage errorMessage = (ErrorMessage) iterator.next();

                // add ActionMessage with any parameters
                requestErrors.add(property, new ActionMessage(errorMessage.getErrorKey(), errorMessage.getMessageParameters()));
            }
        }
        return requestErrors;
    }

    public abstract int getMessageCount();

    public abstract List<String> getMessagePropertyList();

    protected abstract Set<String> getMessagePropertyNames();

    protected abstract List getMessagesForProperty(String propertyName);
}
