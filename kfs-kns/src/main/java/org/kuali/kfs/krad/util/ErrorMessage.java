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
package org.kuali.kfs.krad.util;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Contains the error message key and parameters for a specific instantiation of an error message.
 *
 *
 */
public class ErrorMessage implements Serializable {
    private String errorKey;
    private String[] messageParameters;

    /**
     * Default constructor, required by AutoPopulatingList
     */
    public ErrorMessage() {
    }

    /**
     * Convenience constructor which sets both fields
     *
     * @param errorKey
     * @param messageParameters
     */
    public ErrorMessage(String errorKey, String... messageParameters) {
        if (StringUtils.isBlank(errorKey)) {
            throw new IllegalArgumentException("invalid (blank) errorKey");
        }

        setErrorKey(errorKey);
        setMessageParameters((String[]) ArrayUtils.clone(messageParameters));
    }


    public void setErrorKey(String errorKey) {
        if (StringUtils.isBlank(errorKey)) {
            throw new IllegalArgumentException("invalid (blank) errorKey");
        }

        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }


    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    public String[] getMessageParameters() {
        return messageParameters;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(getErrorKey());

        String[] params = getMessageParameters();
        if (params != null) {
            s.append("(");
            for (int i = 0; i < params.length; ++i) {
                if (i > 0) {
                    s.append(", ");
                }
                s.append(params[i]);
            }
            s.append(")");
        }
        return s.toString();
    }


    /**
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (this == obj) {
            equals = true;
        }
        else if (obj instanceof ErrorMessage) {
            ErrorMessage other = (ErrorMessage) obj;

            if (StringUtils.equals(getErrorKey(), other.getErrorKey())) {
                equals = Arrays.equals(getMessageParameters(), other.getMessageParameters());
            }
        }

        return equals;
    }

    /**
     * Defined because when you redefine equals, you must redefine hashcode.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashCode = 5011966;

        if (getErrorKey() != null) {
            hashCode = getErrorKey().hashCode();
        }

        return hashCode;
    }
}
